package uz.invan.rovitalk.ui.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.invan.rovitalk.data.models.audio.AudioState
import uz.invan.rovitalk.data.models.audio.BackgroundAudioData
import uz.invan.rovitalk.data.tools.service.AudioPlayerService.Companion.DEFAULT_VOLUME_PERCENT
import uz.invan.rovitalk.util.lifecycle.Event
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@Deprecated("Deprecated due to new player", ReplaceWith("PlayerViewModel"))
@HiltViewModel
class AudioViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _initBackgroundAudios = MutableLiveData<List<BackgroundAudioData>>()
    val initBackgroundAudios: LiveData<List<BackgroundAudioData>> get() = _initBackgroundAudios
    private val _playAudio = MutableLiveData<Unit>()
    val playAudio: LiveData<Unit> get() = _playAudio
    private val _pauseAudio = MutableLiveData<Unit>()
    val pauseAudio: LiveData<Unit> get() = _pauseAudio
    private val _updateBackgroundAudio = MutableLiveData<BackgroundAudioData>()
    val updateBackgroundAudio: LiveData<BackgroundAudioData> get() = _updateBackgroundAudio
    private val _stopBackgroundAudio = MutableLiveData<Unit>()
    val stopBackgroundAudio: LiveData<Unit> get() = _stopBackgroundAudio
    private val _updateVolume = MutableLiveData<Int>()
    val updateVolume: LiveData<Int> get() = _updateVolume
    private val _navigateImage = MutableLiveData<Event<Unit>>()
    val navigateImage: LiveData<Event<Unit>> get() = _navigateImage
    private val _close = MutableLiveData<Unit>()
    val close: LiveData<Unit> get() = _close

    private var audioState = AudioState.PAUSE
    private var audioInitiated = AtomicBoolean(false)
    private var lastBackgroundPosition = 0

    fun initBackgroundAudios() {
        _initBackgroundAudios.value = listOf(
            BackgroundAudioData(BACKGROUND_URL, 50, null, null),
            BackgroundAudioData(BACKGROUND_URL, 50, null, null),
            BackgroundAudioData(
                "https://dl3s1.muzofond.fm/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDEvMDAyLzUwNS8xMDAyNTA1Lm1wMw==",
                50,
                null,
                null
            ),
            BackgroundAudioData(
                "https://dl3s1.muzofond.fm/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDQvMTA2Lzc5NS80MTA2Nzk1Lm1wMw==",
                50,
                null,
                null
            ),
            BackgroundAudioData(BACKGROUND_URL, 50, null, null),
            BackgroundAudioData(
                "https://dl3s1.muzofond.fm/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDEvMDAyLzUwNS8xMDAyNTA1Lm1wMw==",
                50,
                null,
                null
            ),
            BackgroundAudioData(
                "https://dl3s1.muzofond.fm/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDQvMTA2Lzc5NS80MTA2Nzk1Lm1wMw==",
                50,
                null,
                null
            )
        )
    }

    fun setAudioState(state: AudioState) {
        audioState = state
    }

    fun updateControl() {
        if (audioState == AudioState.PLAY) pause()
        else play()
    }

    private fun play() {
        _playAudio.value = Unit
    }

    private fun pause() {
        _pauseAudio.value = Unit
    }

    fun updateBackgroundAudio(backgroundAudio: BackgroundAudioData, position: Int) {
        if (lastBackgroundPosition == position) return
        lastBackgroundPosition = position
        _updateBackgroundAudio.value = backgroundAudio
    }

    fun stopBackgroundAudio(position: Int) {
        if (audioInitiated.compareAndSet(false, true) || lastBackgroundPosition == position) return
        lastBackgroundPosition = position
        _stopBackgroundAudio.value = Unit
    }

    fun resetVolume() {
        _updateVolume.value = DEFAULT_VOLUME_PERCENT
    }

    fun updateVolume(volumePercent: Int) {
        _updateVolume.value = volumePercent
    }

    fun navigateImage() {
        _navigateImage.value = Event(Unit)
    }

    fun close() {
        _close.value = Unit
    }

    companion object {
        const val URL = /*"https://www.learnoutloud.com/samples/45338/Will%20to%20Believe.mp3"*/
            "https://play.podtrac.com/510298/ondemand.npr.org/anon.npr-podcasts/podcast/npr/ted/2020/09/20200904_ted_tedpod-a473e921-f999-42d8-baaf-3163db7fdfe4.mp3?orgId=1&d=3101&p=510298&story=909228987&t=podcast&e=909228987&dl=1&siteplayer=true&size=49508114"
        const val BACKGROUND_URL =
            "https://dl3s1.muzofond.fm/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDEvMDAyLzUwNS8xMDAyNTA1Lm1wMw=="
        const val ID = -1
    }
}