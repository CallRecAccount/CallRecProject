package uz.invan.rovitalk.data.models.audio

import uz.invan.rovitalk.data.models.feed.FeedCategory

interface PlayersController {
    val preparingPlayers: HashMap<Int, PlayerControllerData>
    val players: HashMap<Int, PlayerControllerData>

    val preparingBMs: ArrayList<PlayerBM>
    val bms: ArrayList<PlayerBM>

    val preparingAudios: ArrayList<PlayerAudio>

    var preparingCategory: FeedCategory?
    var currentCategory: FeedCategory?

    var player: Player?

    fun submitPlayer(newPlayer: Player?)

    fun prepareAudios(audios: List<PlayerAudio>, category: FeedCategory)

    fun submitAudios(audios: List<PlayerAudio>, category: FeedCategory)

    fun submitBMs(newBMs: List<PlayerBM>, category: FeedCategory)

    fun updateBM(index: Int, bm: PlayerBM)

    fun submitPlayerItemListener(position: Int, category: String, listener: PlayerItemListener)

    fun preparedAt(index: Int, duration: Int)

    fun bufferUpdatedAt(index: Int, position: Int)

    fun playedAt(index: Int)

    fun pausedAt(index: Int)

    fun tickAt(index: Int, position: Int)

    fun onPrev(): Boolean

    fun onNext(): Boolean

    fun complete(): Boolean

    fun onControl(position: Int = NONE)

    data class PlayerControllerData(
        val audio: PlayerAudio,
        var listener: PlayerItemListener? = null,
        var loading: Boolean = false,
        var duration: Int? = null,
        var control: ControlState? = null,
        var seek: Int? = null,
        var loaderSeek: Int? = null,
    )

    enum class ControlState {
        PLAY, PAUSE
    }

    companion object {
        const val NONE = -1
    }
}