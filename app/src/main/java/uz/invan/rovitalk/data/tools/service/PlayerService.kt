package uz.invan.rovitalk.data.tools.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.audio.Player
import uz.invan.rovitalk.data.models.audio.Player.Companion.BACK_AFTER_CALL_DURATION
import uz.invan.rovitalk.data.models.audio.Player.Companion.MEDIA_NOTIFICATION_CHANNEL
import uz.invan.rovitalk.data.models.audio.Player.Companion.MEDIA_NOTIFICATION_ID
import uz.invan.rovitalk.data.models.audio.PlayerAudio
import uz.invan.rovitalk.data.models.audio.PlayerBM
import uz.invan.rovitalk.data.models.audio.PlayersController
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.util.ktx.*
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class PlayerService : Service(), Player {
    override var player: MediaPlayer? = null
    override var playerTimer: Player.PlayerTimer? = null
    override val playerAudios: ArrayList<PlayerAudio> = arrayListOf()
    override val bms: ArrayList<PlayerBM> = arrayListOf()
    override var playersController: PlayersController? = null
    override var bm: MediaPlayer? = null

    private val binder = PlayerBinder()
    private val session by lazy { MediaSessionCompat(this, javaClass.name) }
    private var volume: Int = 50
    private var poster: Bitmap? = null
    private var currentBM: Int = -1
    private var currentPlayer: Int = -1

    private val wasMusicPlayingOnCall = AtomicBoolean(false)
    private val isFocusLoss = AtomicBoolean(false)

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                val pendingIntent = createPendingIntent(Player.Actions.AUDIO_FOCUS_GAIN)
                pendingIntent?.send()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                val pendingIntent = createPendingIntent(Player.Actions.AUDIO_FOCUS_LOSS)
                pendingIntent?.send()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                val pendingIntent = createPendingIntent(Player.Actions.AUDIO_FOCUS_LOSS)
                pendingIntent?.send()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                val pendingIntent = createPendingIntent(Player.Actions.AUDIO_FOCUS_LOSS)
                pendingIntent?.send()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAudioFocus() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(audioFocusChangeListener)
            build()
        }
        audioManager.requestAudioFocus(focusRequest)
    }

    @Suppress("DEPRECATION")
    fun requestAudioFocusPreO() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    override fun initializePlayers(audios: List<PlayerAudio>, controller: PlayersController) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) requestAudioFocus()
//        else requestAudioFocusPreO()

        playersController = controller
        playerAudios.roviClear().addAll(audios)
    }

    override fun initializeBMs(newBMs: List<PlayerBM>) {
        bms.roviClear().addAll(newBMs)
    }

    override fun updateBM(index: Int, bm: PlayerBM) {
        if (index >= bms.size) return
        bms[index] = bm
    }

    override fun startBM(index: Int) {
        if (index >= bms.size || (index == currentBM && bm?.isPlaying == true)) return

        val bmAudio = bms[index]
        // releasing bm if exists
        releaseBM()
        // initializing bm
        if (bmAudio.file?.absolutePath == null) return
        bm = MediaPlayer.create(this, Uri.parse(bmAudio.file.absolutePath))
        bm?.isLooping = true
        updateVolume(volume)
        if (player?.isPlaying == true) bm?.start()

        currentBM = index
    }

    override fun releaseBM() {
        bm?.stop()
        bm?.release()
        bm = null
    }

    override fun prepareAt(index: Int) {
        if (index >= playerAudios.size) return

        val playerAudio = playerAudios[index]
        // releasing player if exists
        releasePlayer()
        // downloading poster
        Glide.with(this)
            .asBitmap()
            .load(playerAudio.image)
            .onBitmap { bitmap ->
                poster = bitmap
                if (currentPlayer != -1) notify(index)
            }
        // notify
        currentPlayer = index
        session.bufferingState(player, playerAudio)
        notify(index)
        // initializing player
        player = MediaPlayer()
        player?.setDataSource(playerAudio.file?.absolutePath ?: playerAudio.url ?: return)
        player?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        // set player timer
        playerTimer = Player.PlayerTimer(player!!, callback = { position ->
            playersController?.tickAt(index, position)
            playerAudio.lastPlayedPosition = position
            session.seekState(player, playerAudio)
        })
        // preparing player
        player?.prepareAsync()
        player?.setOnPreparedListener { media ->
            playerAudio.lastPlayedPosition?.let { position ->
                player?.seekTo(position)
            }
            updateVolume(volume)
            playersController?.preparedAt(index, media.duration)
            // setting buffer update to max if loads from file
            if (playerAudio.file?.absolutePath != null) {
                playersController?.bufferUpdatedAt(index, Credentials.MAX_BUFFER_POSITION)
                playerAudio.bufferedPosition = Credentials.MAX_BUFFER_POSITION
            }
            // set current player
            currentPlayer = index
        }
        player?.setOnBufferingUpdateListener { _, percent ->
//            playersController?.bufferUpdatedAt(index, player.percentToNumbers(percent))
//            playerAudio.bufferedPosition = player.percentToNumbers(percent)
            playersController?.bufferUpdatedAt(index, percent)
            playerAudio.bufferedPosition = percent
        }
        player?.setOnCompletionListener {
            // playersController's complete() method returns boolean that we have new player or not
            if (playersController?.complete() == false)
                playersController?.onControl()
        }
    }

    override fun playAt(index: Int) {
        if (index >= playerAudios.size || isFocusLoss.get()) return

        player?.start()
        bm?.start()
        playerTimer?.start()
        playersController?.playedAt(index)
        // notifying playing state
        session.playingState(player, playerAudios[index])
        notify(index)
    }

    override fun pauseAt(index: Int) {
        if (index >= playerAudios.size) return

        player?.pause()
        bm?.pause()
        playerTimer?.stop()
        playersController?.pausedAt(index)
        // notifying playing state
        session.pausedState(player, playerAudios[index])
        notify(index)
    }

    override fun seekAt(index: Int, position: Int) {
        if (index >= playerAudios.size) return

        val audio = playerAudios[index]
        if (player?.toPercent(position) ?: return > audio.bufferedPosition ?: return) return

        playerTimer?.stop()
        player?.seekTo(position)
        playerTimer?.start()
    }

    override fun updateVolume(bmVolume: Int) {
//        player?.setRoviVolume(playerVolume)
        bm?.setRoviVolume(bmVolume)
        volume = bmVolume
    }

    private fun notify(index: Int) {
        if (index >= playerAudios.size || index == -1) return
        val audio = playerAudios[index]

        val style =
            androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(session.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)

        val notification = NotificationCompat.Builder(this, MEDIA_NOTIFICATION_CHANNEL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setContentTitle(audio.title)
            .setContentText(audio.categoryTitle)
            .addAction(previousMediaButton(index != 0, getString(R.string.previous)))
            .addAction(
                controlMediaButton(
                    isPlaying = player?.isPlaying ?: false,
                    play = getString(R.string.play),
                    pause = getString(R.string.pause)
                )
            )
            .addAction(nextMediaButton(index != playerAudios.lastIndex, getString(R.string.next)))
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setLargeIcon(poster)
            .setStyle(style)
            .build()

        if (player?.isPlaying == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) buildMediaNotificationChannel()
            startForeground(MEDIA_NOTIFICATION_ID, notification)
        } else {
            stopForeground(false)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(MEDIA_NOTIFICATION_ID, notification)
        }
    }

    private fun releasePlayer() {
        // stopping notification
        playerTimer?.stop()
        notify(currentPlayer)
        stopForeground(false)
        // releasing poster
        poster = null
        // releasing timer
        currentPlayer = -1
        playerTimer = null
        // releasing media player
        player?.stop()
        player?.release()
        player = null
        // stop bm
        bm?.stop()
    }

    override fun releasePlayers() {
        // stopping notification
        playerTimer?.stop()
        notify(currentPlayer)
        stopForeground(true)
        // releasing poster
        poster = null
        // releasing bm
        releaseBM()
        // releasing timer
        playerTimer = null
        // releasing media player
        currentPlayer = -1
        player?.stop()
        player?.release()
        player = null
        // clearing audios
        playerAudios.clear()
        // removing controller
        playersController = null
    }


    inner class PlayerBinder : Binder() {
        fun player() = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = Player.Actions.values().firstOrNull { intent?.action == it.action }
        if (action != null) {
            when (action) {
                Player.Actions.PREV -> playersController?.onPrev()
                Player.Actions.PAUSE -> playersController?.onControl()
                Player.Actions.PLAY -> playersController?.onControl()
                Player.Actions.NEXT -> playersController?.onNext()
                Player.Actions.AUDIO_FOCUS_LOSS -> {
                    if (isFocusLoss.compareAndSet(false, true)) {
                        if (player?.isPlaying == true) {
                            wasMusicPlayingOnCall.set(true)
                            playersController?.onControl()
                        }
                    }
                }
                Player.Actions.AUDIO_FOCUS_GAIN -> {
                    if (isFocusLoss.compareAndSet(true, false)) {
                        if (wasMusicPlayingOnCall.compareAndSet(true, false)
                            && player?.isPlaying == false && player != null
                        ) {
                            val seekDuration = (player?.currentPosition ?: 0) - BACK_AFTER_CALL_DURATION
                            playersController?.onControl(seekDuration)
                        }
                    }
                }
                Player.Actions.NONE -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}