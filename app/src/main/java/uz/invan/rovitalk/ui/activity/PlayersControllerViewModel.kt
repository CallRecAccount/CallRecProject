package uz.invan.rovitalk.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import uz.invan.rovitalk.data.models.audio.*
import uz.invan.rovitalk.data.models.audio.PlayersController.Companion.NONE
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.util.ktx.roviClear

class PlayersControllerViewModel : ViewModel(), PlayersController {
    override val preparingPlayers: HashMap<Int, PlayersController.PlayerControllerData> =
        hashMapOf()
    override val players: HashMap<Int, PlayersController.PlayerControllerData> = hashMapOf()

    override val preparingBMs: ArrayList<PlayerBM> = arrayListOf()
    override val bms: ArrayList<PlayerBM> = arrayListOf()

    override val preparingAudios: ArrayList<PlayerAudio> = arrayListOf()

    override var preparingCategory: FeedCategory? = null
    override var currentCategory: FeedCategory? = null

    override var player: Player? = null

    private var currentPlayer = NONE
    private var currentBM = 0

    private val _onImage = MutableLiveData<String>()
    val onImage: LiveData<String> get() = _onImage

    /**
     * Updates on every control
     * First param current index
     * Second param player weather playing or not
     * */
    private val _control = MutableLiveData<Pair<Int, Boolean>>()
    val control: LiveData<Pair<Int, Boolean>> get() = _control
    private val _controlBM = MutableLiveData<Int>()
    val controlBM: LiveData<Int> get() = _controlBM

    override fun submitPlayer(newPlayer: Player?) {
        player = newPlayer
    }

    override fun prepareAudios(audios: List<PlayerAudio>, category: FeedCategory) {
        preparingAudios.roviClear().addAll(audios)

        preparingCategory = category
        preparingPlayers.clear()
        audios.forEachIndexed { index, playerAudio ->
            preparingPlayers[index] = PlayersController.PlayerControllerData(playerAudio)
        }

        if (currentCategory == null || currentCategory?.id == category.id)
            submitAudios(audios, category)
    }

    override fun submitAudios(audios: List<PlayerAudio>, category: FeedCategory) {
        if (audios.isEmpty()) return
        if (players.isNotEmpty() && currentPlayer != NONE && currentCategory?.id == category.id) {
            updateControlStateToUI(category.id)
            return
        } else if (currentCategory != null && currentCategory?.id != category.id) forceReleasePlayer()
        if (players.any { it.value.audio.category in audios.map { audio -> audio.category } }) return

        currentCategory = category
        players.clear()
        audios.forEachIndexed { index, playerAudio ->
            players[index] = PlayersController.PlayerControllerData(playerAudio)
        }
        player?.initializePlayers(audios, this)
    }

    override fun submitBMs(newBMs: List<PlayerBM>, category: FeedCategory) {
        if (category.id != currentCategory?.id && category.id == preparingCategory?.id) {
            preparingBMs.roviClear().addAll(newBMs)
            return
        }

//        if (bms.isNotEmpty() && currentPlayer != NONE && currentCategory == category) return
        bms.roviClear().addAll(newBMs)
        player?.initializeBMs(bms)

        if (category.id == currentCategory?.id)
            _controlBM.postValue(currentBM)
    }

    override fun updateBM(index: Int, bm: PlayerBM) {
        if (index >= bms.size) return
        bms[index] = bm
        player?.updateBM(index, bms[index])
        if (currentBM == index) player?.startBM(currentBM)
    }

    fun controlBM(index: Int, category: String) {
        if (category != currentCategory?.id) return
        if (index >= bms.size || index == currentBM) return

        if (index == 0) player?.releaseBM()
        else if (bms[index].file != null) player?.startBM(index)

        currentBM = index
    }

    fun updateVolume(playerVolume: Int) {
        player?.updateVolume(playerVolume)
    }

    override fun submitPlayerItemListener(
        position: Int,
        category: String,
        listener: PlayerItemListener,
    ) {
        Timber.d("listener: $listener, player: $player")
        if (preparingPlayers[position] == null && players[position] == null) return

        val playerController =
            (if (category == currentCategory?.id) players[position] else preparingPlayers[position])
                ?: return
        playerController.listener = listener

        playerController.listener?.image(playerController.audio.image)
        playerController.listener?.title(playerController.audio.title)
        playerController.listener?.author(playerController.audio.author)
        if (playerController.loading) listener.load()
        if (playerController.duration != null) listener.ready(playerController.duration!!)
        if (playerController.control != null) {
            if (playerController.control == PlayersController.ControlState.PLAY) listener.play()
            else listener.pause()
        }
        if (playerController.seek != null) listener.seek(playerController.seek!!)
        if (playerController.loaderSeek != null) listener.loaderSeek(playerController.loaderSeek!!)
    }

    fun control(index: Int, category: String? = currentCategory?.id) {
        if (category == null) return

        if (category == preparingCategory?.id && category != currentCategory?.id) {
            reLaunch(index)
            return
        }

        if (preparingPlayers[index] == null && players[index] == null) return
        if (currentPlayer != index) releasePreviousPlayer()

        currentPlayer = index
        when (players[index]?.control) {
            PlayersController.ControlState.PLAY -> player?.pauseAt(index)
            PlayersController.ControlState.PAUSE -> player?.playAt(index)
            null -> {
                players[index]?.listener?.load()
                player?.prepareAt(index)
            }
        }
        updateControlStateToUI(category)
    }

    private fun reLaunch(index: Int) {
        forceReleasePlayer()

        submitAudios(preparingAudios, preparingCategory ?: return)
        bms.roviClear().addAll(preparingBMs)

        players.clear()
        players.putAll(preparingPlayers)

        control(index, preparingCategory?.id)

        preparingPlayers.clear()
        preparingBMs.clear()
        preparingCategory = null
    }

    private fun updateControlStateToUI(category: String? = currentCategory?.id) {
        if (category != currentCategory?.id) return

        // calls to post controlled index and playing state
        val isPlaying = players[currentPlayer]?.control == PlayersController.ControlState.PLAY
        _control.postValue(currentPlayer to isPlaying)
    }

    fun seekTo(index: Int, position: Int) {
        if (players[index] == null) return
        if (currentPlayer != index) return

        player?.seekAt(index, position)
    }

    /**
     * Releases player if not playing any
     * */
    fun releasePlayerIfNotPlaying(category: FeedCategory) {
        if (currentCategory?.id != category.id) return
        if (players[currentPlayer]?.control == PlayersController.ControlState.PLAY) return

        forceReleasePlayer()
    }

    fun forceReleasePlayer() {
        // sets as not save current audio
        // WARNING!!! Don't delete it
        _control.postValue(currentBM to false)

        player?.releasePlayers()
        players.clear()
        bms.clear()
        currentCategory = null
        currentPlayer = -1
        currentBM = 0
    }

    private fun releasePreviousPlayer() {
        players[currentPlayer]?.apply {
            listener?.release()
            loading = false
            duration = null
            control = null
            seek = null
            loaderSeek = null
        }
    }

    override fun preparedAt(index: Int, duration: Int) {
        if (players[index] == null) return

        val playerController = players[index]!!
        playerController.duration = duration
        playerController.listener?.ready(duration)
        player?.playAt(index)
        // setting image to player
        _onImage.postValue(playerController.audio.image)
        Timber.d("Prepared at: $index, listener: ${playerController.listener}")
    }

    override fun bufferUpdatedAt(index: Int, position: Int) {
        if (players[index] == null) return

        val playerController = players[index]!!
        playerController.loaderSeek = position
        playerController.listener?.loaderSeek(position)
    }

    override fun playedAt(index: Int) {
        if (players[index] == null) return

        val playerController = players[index]!!
        playerController.control = PlayersController.ControlState.PLAY
        playerController.listener?.play()

        updateControlStateToUI()
    }

    override fun pausedAt(index: Int) {
        if (players[index] == null) return

        val playerController = players[index]!!
        playerController.control = PlayersController.ControlState.PAUSE
        playerController.listener?.pause()

        updateControlStateToUI()
    }

    override fun tickAt(index: Int, position: Int) {
        if (players[index] == null) return
        Timber.d("Tick: $index, $position")

        val playerController = players[index]!!
        playerController.seek = position
        playerController.listener?.seek(position)
    }

    override fun onPrev(): Boolean {
        if (currentPlayer == NONE) return false
        if (players[currentPlayer - 1] == null) return false
        control(currentPlayer - 1)

        return true
    }

    override fun onNext(): Boolean {
        if (currentPlayer == NONE) return false
        if (players[currentPlayer + 1] == null) return false
        control(currentPlayer + 1)

        return true
    }

    override fun complete(): Boolean {
        // de initialize current player data
        players[currentPlayer]?.audio?.apply {
            lastPlayedPosition = null
        }

        return onNext()
    }

    override fun onControl(position: Int) {
        if (currentPlayer == NONE || players[currentPlayer] == null) return
        control(currentPlayer)
        if (position != NONE)
            seekTo(currentPlayer, position)
        Timber.d("Control: $currentPlayer")
    }
}