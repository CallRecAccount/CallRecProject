package uz.invan.rovitalk.data.tools.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.audio.*
import uz.invan.rovitalk.ui.activity.AudioPlayerViewModel
import uz.invan.rovitalk.util.IAudioPlayer
import uz.invan.rovitalk.util.ktx.AudioPlayerTimer
import uz.invan.rovitalk.util.ktx.setRoviVolume

@Deprecated("Deprecated due to new player", ReplaceWith("PlayerService"))
@AndroidEntryPoint
class AudioPlayerService : Service(), IAudioPlayer {
    private val binder = AudioPlayerBinder()
    private val player by lazy { MediaPlayer() }
    private val backgroundPlayer by lazy { MediaPlayer() }
    private var audioTimer: AudioPlayerTimer? = null

    // Only `call from service` methods should be called
    var playerVM: AudioPlayerViewModel? = null

    private var duration = 0
    private var maxLoadedPosition = 0
    private var lastPlayedPosition = 0
    private var currentAudio: AudioData? = null
    private var currentBackgroundAudio: BackgroundAudioData? = null

    inner class AudioPlayerBinder : Binder() {
        fun getService() = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = PlayerActions.values().firstOrNull { intent?.action == it.title }
        action?.let { onPlayerAction(it) }
        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun prepare(audio: AudioData) {
        if (checkIfExists(audio)) return
        loading()
        try {
            player.setDataSource(audio.url)
            player.prepareAsync()
            player.setOnPreparedListener {
                audioPrepared(audio)
            }
            player.setOnCompletionListener { backgroundPlayer.stop() }
            prepareAudioBackground()
        } catch (e: Exception) {
            Timber.e(e, "Error occurred while preparing media player")
        }
    }

    private fun checkIfExists(audio: AudioData): Boolean {
        if (audio.id != currentAudio?.id)
            return false
        audioPrepared(audio)
        return true
    }

    private fun audioPrepared(audio: AudioData) {
        currentAudio = audio
        currentBackgroundAudio = audio.backgroundMusic
        setAudioAttrs(player)
        setPlayersVolume()
        setBufferListener()
        setAudioUpdateListener()
        // update audio state
        playerVM?.audioReady(player.duration)
        duration = player.duration
    }

    private fun setAudioAttrs(player: MediaPlayer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
        } else {
            @Suppress("DEPRECATION")
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }

    private fun setPlayersVolume() {
        player.setRoviVolume((MAX_VOLUME_PERCENT - (currentBackgroundAudio?.volumePercent
            ?: DEFAULT_VOLUME_PERCENT)))
        backgroundPlayer.setRoviVolume(currentBackgroundAudio?.volumePercent ?: 0)
    }

    private fun setBufferListener() {
        player.setOnBufferingUpdateListener { _, value ->
            maxLoadedPosition = duration * value / 100
            playerVM?.bufferUpdated(maxLoadedPosition)
        }
    }

    private fun setAudioUpdateListener() {
        if (audioTimer != null) return
        audioTimer = AudioPlayerTimer(player) { currentPosition ->
            loaded()
            lastPlayedPosition = currentPosition
            playerVM?.audioUpdated(currentPosition, SeekState.UPDATE)
        }
    }

    private fun loading() {
        playerVM?.loading(true)
    }

    private fun loaded() {
        playerVM?.loading(false)
    }

    private fun prepareAudioBackground() {
        if (currentBackgroundAudio == null || currentBackgroundAudio?.url == null) {
            backgroundPlayer.stop()
            return
        }
        if (backgroundPlayer.isPlaying) backgroundPlayer.reset()
        setAudioAttrs(backgroundPlayer)
        try {
            backgroundPlayer.setDataSource(currentBackgroundAudio!!.url)
            backgroundPlayer.prepareAsync()
            backgroundPlayer.setOnPreparedListener {
                if (player.isPlaying)
                    backgroundPlayer.start()
            }
            backgroundPlayer.setOnCompletionListener {
                if (player.isPlaying) {
                    backgroundPlayer.seekTo(0)
                    backgroundPlayer.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e, "Error occurred while preparing background media player")
        }
    }

    override fun updateAudio(audio: AudioData) {
        if (audio.updateReason == null || returnIfNull()) return
        when (audio.updateReason) {
            AudioUpdateReasons.VOLUME_UPDATE -> {
                currentAudio?.backgroundMusic?.volumePercent =
                    audio.backgroundMusic?.volumePercent ?: DEFAULT_VOLUME_PERCENT
                currentBackgroundAudio?.volumePercent =
                    currentAudio?.backgroundMusic?.volumePercent ?: DEFAULT_VOLUME_PERCENT
                setPlayersVolume()
            }
            AudioUpdateReasons.BACKGROUND_AUDIO_UPDATE -> {
                backgroundPlayer.reset()
                currentAudio?.backgroundMusic = audio.backgroundMusic
                currentBackgroundAudio = currentAudio?.backgroundMusic
                prepareAudioBackground()
            }
            AudioUpdateReasons.BACKGROUND_AUDIO_STOP -> {
                currentAudio?.backgroundMusic = null
                currentBackgroundAudio = null
                backgroundPlayer.reset()
            }
        }
    }

    override fun play() {
        if (returnIfNull()) return
        player.start()
        backgroundPlayer.start()
        audioTimer?.start()
        playerVM?.audioStateUpdated(AudioState.PLAY)
        showOrUpdateNotification()
    }

    override fun pause() {
        if (returnIfNull()) return
        player.pause()
        backgroundPlayer.pause()
        audioTimer?.cancel()
        playerVM?.audioStateUpdated(AudioState.PAUSE)
        showOrUpdateNotification()
    }

    override fun stop() {
        if (returnIfNull()) return
        playerVM?.audioStateUpdated(AudioState.PAUSE)
        player.stop()
        backgroundPlayer.stop()
        player.reset()
        backgroundPlayer.reset()
        audioTimer?.cancel()
        audioTimer = null
        duration = 0
        maxLoadedPosition = 0
        lastPlayedPosition = 0
        currentAudio = null
        currentBackgroundAudio = null
        hideNotification()
    }

    override fun seekTo(millis: Int) {
        if (returnIfNull()) return
        if (millis - lastPlayedPosition == 0) return
        when {
            maxLoadedPosition < millis -> {
                forceUpdateAudio(lastPlayedPosition, SeekState.FORCE)
            }
            maxLoadedPosition == millis -> {
                forceUpdateAudio(millis - SECOND_IN_MILLIS, SeekState.UPDATE)
                player.seekTo(millis - SECOND_IN_MILLIS)
            }
            else -> {
                forceUpdateAudio(millis, SeekState.UPDATE)
                player.seekTo(millis)
            }
        }
    }

    private fun forceUpdateAudio(millis: Int, seekState: SeekState) {
        if (returnIfNull()) return
        playerVM?.audioUpdated(millis, seekState)
    }

    override fun showOrUpdateNotification() {
        if (returnIfNull()) return
        val isPreviousAvailable = currentBackgroundAudio?.prev != null
        val isNextAvailable = currentBackgroundAudio?.next != null
        createOrUpdateNotification(
            player.isPlaying,
            isPreviousAvailable,
            isNextAvailable,
            currentAudio?.album,
            currentAudio?.audioTitle)
    }

    private fun returnIfNull() = currentAudio == null

    private fun createOrUpdateNotification(
        isPlaying: Boolean,
        isPreviousAvailable: Boolean,
        isNextAvailable: Boolean,
        album: String?,
        audioTitle: String?,
    ) {
        // session
        val mediaSession = MediaSessionCompat(this, javaClass.name)
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, audioTitle)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, album)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI,
                    "android.resource://uz.invan.rovitalk/R.drawable.image")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, /*player.duration.toLong()*/-1L)
                .build()
        )
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, player.currentPosition.toLong(), 1f)
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .build())
        val mediaStyle = MediaStyle().setMediaSession(mediaSession.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)

        val previousAction =
            NotificationCompat.Action(if (isPreviousAvailable) R.drawable.ic_baseline_skip_previous_notification else R.drawable.ic_baseline_skip_previous_notification_not_clickable,
                getString(R.string.previous),
                actionPendingIntent(if (isPreviousAvailable) PlayerActions.PREVIOUS else PlayerActions.EMPTY))
        val pauseAction =
            NotificationCompat.Action(if (isPlaying) R.drawable.ic_pause_notification else R.drawable.ic_play_arrow_notification,
                getString(if (isPlaying) R.string.pause else R.string.play),
                actionPendingIntent(if (isPlaying) PlayerActions.PAUSE else PlayerActions.PLAY))
        val nextAction =
            NotificationCompat.Action(if (isNextAvailable) R.drawable.ic_baseline_skip_next_notification else R.drawable.ic_baseline_skip_next_notification_not_clickable,
                getString(R.string.next),
                actionPendingIntent(if (isNextAvailable) PlayerActions.NEXT else PlayerActions.EMPTY))

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setContentTitle(album ?: getString(R.string.unknown_album))
            .setContentText(audioTitle ?: getString(R.string.unknown_music))
//            .setSubText(getString(R.string.x_minutes_left, leftMinutes))
            .addAction(previousAction)
            .addAction(pauseAction)
            .addAction(nextAction)
            .setShowWhen(false)
//            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.image))
/*            .setStyle(MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                    PlaybackStateCompat.ACTION_STOP))
                .setMediaSession(mediaSession.sessionToken)
            )*/
            .setStyle(mediaStyle)
/*            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_STOP))*/
            .build()

        if (isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createAudioPlayerNotificationChannel()
            startForeground(NOTIFICATION_ID, notification)
        } else {
            stopForeground(false)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun actionPendingIntent(action: PlayerActions): PendingIntent? {
        val intent = Intent(this, AudioPlayerService::class.java)
        intent.action = action.title
        return PendingIntent.getService(this, 0, intent, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAudioPlayerNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID,
            getString(R.string.player_notification_channel_name), importance).apply {
            description = getString(R.string.player_notification_channel_description)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun hideNotification() {
        stopForeground(true)
    }

    private fun onPlayerAction(action: PlayerActions) {
        when (action) {
            PlayerActions.PREVIOUS -> {
            }
            PlayerActions.PAUSE -> pause()
            PlayerActions.PLAY -> play()
            PlayerActions.NEXT -> {
            }
            PlayerActions.EMPTY -> {
            }
        }
    }

    companion object {
        const val MAX_VOLUME = 1.0F
        const val MAX_VOLUME_PERCENT = 100
        const val DEFAULT_VOLUME_PERCENT = 50
        private const val SECOND_IN_MILLIS = 1000 * 1
        private const val CHANNEL_ID = "uz.invan.rovitalk.data.tools.service.AudioPlayerNotification"
        private const val NOTIFICATION_ID = 123
    }

    enum class PlayerActions(val title: String) {
        PREVIOUS("previous"), PAUSE("pause"), PLAY("play"), NEXT("next"), EMPTY("empty")
    }
}