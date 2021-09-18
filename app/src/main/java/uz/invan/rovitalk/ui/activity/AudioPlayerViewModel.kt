package uz.invan.rovitalk.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.invan.rovitalk.data.models.audio.AudioData
import uz.invan.rovitalk.data.models.audio.AudioState
import uz.invan.rovitalk.data.models.audio.SeekState
import uz.invan.rovitalk.util.IAudioPlayer

@Deprecated("Deprecated due to new player", ReplaceWith("PlayersControllerViewModel"))
class AudioPlayerViewModel : ViewModel(), IAudioPlayer {
    private val _prepare = MutableLiveData<AudioData>()
    val prepare: LiveData<AudioData> get() = _prepare
    private val _updateAudio = MutableLiveData<AudioData>()
    val updateAudio: LiveData<AudioData> get() = _updateAudio
    private val _play = MutableLiveData<Unit>()
    val play: LiveData<Unit> get() = _play
    private val _pause = MutableLiveData<Unit>()
    val pause: LiveData<Unit> get() = _pause
    private val _stop = MutableLiveData<Unit>()
    val stop: LiveData<Unit> get() = _stop
    private val _seekTo = MutableLiveData<Int>()
    val seekTo: LiveData<Int> get() = _seekTo
    private val _showNotification = MutableLiveData<Unit>()
    val showNotification: LiveData<Unit> get() = _showNotification
    private val _hideNotification = MutableLiveData<Unit>()
    val hideNotification: LiveData<Unit> get() = _hideNotification

    override fun prepare(audio: AudioData) {
        _prepare.value = audio
    }

    override fun updateAudio(audio: AudioData) {
        _updateAudio.value = audio
    }

    override fun play() {
        _play.value = Unit
    }

    override fun pause() {
        _pause.value = Unit
    }

    override fun stop() {
        _stop.value = Unit
    }

    override fun seekTo(millis: Int) {
        _seekTo.value = millis
    }

    override fun showOrUpdateNotification() {
        _showNotification.value = Unit
    }

    override fun hideNotification() {
        _hideNotification.value = Unit
    }

    // `observe from service`
    private val _audioReady = MutableLiveData<Int>()
    val audioReady: LiveData<Int> get() = _audioReady
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    private val _bufferUpdated = MutableLiveData<Int>()
    val bufferUpdated: LiveData<Int> get() = _bufferUpdated
    private val _audioUpdated = MutableLiveData<Pair<Int, SeekState>>()
    val audioUpdated: LiveData<Pair<Int, SeekState>> get() = _audioUpdated
    private val _audioStateUpdated = MutableLiveData<AudioState>()
    val audioStateUpdated: LiveData<AudioState> get() = _audioStateUpdated

    // `call from service`
    fun audioReady(duration: Int) {
        _audioReady.postValue(duration)
    }

    fun loading(isLoading: Boolean) {
        _loading.postValue(isLoading)
    }

    fun bufferUpdated(value: Int) {
        _bufferUpdated.postValue(value)
    }

    fun audioUpdated(millis: Int, seekState: SeekState) {
        _audioUpdated.postValue(millis to seekState)
    }

    fun audioStateUpdated(audioState: AudioState) {
        _audioStateUpdated.postValue(audioState)
    }
}