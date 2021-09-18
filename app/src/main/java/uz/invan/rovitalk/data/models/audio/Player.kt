package uz.invan.rovitalk.data.models.audio

import android.media.MediaPlayer
import android.os.CountDownTimer

interface Player {
    val playerAudios: ArrayList<PlayerAudio>
    val bms: ArrayList<PlayerBM>
    var player: MediaPlayer?
    var playerTimer: PlayerTimer?
    var playersController: PlayersController?
    var bm: MediaPlayer?

    fun initializePlayers(audios: List<PlayerAudio>, controller: PlayersController)

    fun initializeBMs(newBMs: List<PlayerBM>)

    fun updateBM(index: Int, bm: PlayerBM)

    fun startBM(index: Int)

    fun releaseBM()

    fun prepareAt(index: Int)

    fun playAt(index: Int)

    fun pauseAt(index: Int)

    fun seekAt(index: Int, position: Int)

    fun updateVolume(bmVolume: Int)

    fun releasePlayers()

    class PlayerTimer(player: MediaPlayer, callback: OnTickListener) {
        private val counter by lazy {
            object :
                CountDownTimer(player.duration.toLong() - player.currentPosition, TICK_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    try {
                        callback.onTick(player.currentPosition)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFinish() {}
            }
        }

        fun start() {
            counter.start()
        }

        fun stop() {
            counter.cancel()
        }

        fun interface OnTickListener {
            fun onTick(position: Int)
        }

        companion object {
            private const val TICK_INTERVAL = 1000L
        }
    }

    enum class Actions(val action: String) {
        PREV("previous-media"),
        PAUSE("pause-media"),
        PLAY("play-media"),
        NEXT("next-media"),
        AUDIO_FOCUS_LOSS("call-active"),
        AUDIO_FOCUS_GAIN("call-inactive"),
        NONE("none")
    }

    companion object {
        const val MEDIA_NOTIFICATION_ID = 30
        const val MEDIA_NOTIFICATION_CHANNEL =
            "uz.invan.rovitalk.data.models.audio.Player.Companion.MEDiA_NOTIFICATION_CHANNEL"
        const val BACK_AFTER_CALL_DURATION = 3 * 1000
    }
}