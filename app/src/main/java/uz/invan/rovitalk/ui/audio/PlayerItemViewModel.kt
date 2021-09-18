package uz.invan.rovitalk.ui.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import uz.invan.rovitalk.data.models.audio.PlayerItemListener

class PlayerItemViewModel : ViewModel(), PlayerItemListener {
    private val _initializePlayerItems = MutableLiveData<Unit>()
    val initializePlayerItems: LiveData<Unit> get() = _initializePlayerItems
    private val _audioImage = MutableLiveData<String>()
    val audioImage: LiveData<String> get() = _audioImage
    private val _audioTitle = MutableLiveData<String>()
    val audioTitle: LiveData<String> get() = _audioTitle
    private val _audioAuthor = MutableLiveData<String>()
    val audioAuthor: LiveData<String> get() = _audioAuthor
    private val _audioLoading = MutableLiveData<Unit>()
    val audioLoading: LiveData<Unit> get() = _audioLoading
    private val _audioReady = MutableLiveData<Int>()
    val audioReady: LiveData<Int> get() = _audioReady
    private val _audioPlay = MutableLiveData<Unit>()
    val audioPlay: LiveData<Unit> get() = _audioPlay
    private val _audioPause = MutableLiveData<Unit>()
    val audioPause: LiveData<Unit> get() = _audioPause
    private val _audioSeek = MutableLiveData<Int>()
    val audioSeek: LiveData<Int> get() = _audioSeek
    private val _audioLoaderSeek = MutableLiveData<Int>()
    val audioLoaderSeek: LiveData<Int> get() = _audioLoaderSeek
    private val _audioReleased = MutableLiveData<Unit>()
    val audioReleased: LiveData<Unit> get() = _audioReleased

    fun initializePlayerItems() {
        _initializePlayerItems.postValue(Unit)
    }

    override fun image(image: String) {
        _audioImage.postValue(image)
    }

    override fun title(title: String) {
        _audioTitle.postValue(title)
    }

    override fun author(author: String) {
        _audioAuthor.postValue(author)
    }

    override fun load() {
        _audioLoading.postValue(Unit)
    }

    override fun ready(duration: Int) {
        Timber.d("prepared: $duration")
        _audioReady.postValue(duration)
    }

    override fun play() {
        _audioPlay.postValue(Unit)
    }

    override fun pause() {
        _audioPause.postValue(Unit)
    }

    override fun seek(position: Int) {
        _audioSeek.postValue(position)
    }

    override fun loaderSeek(position: Int) {
        _audioLoaderSeek.postValue(position)
    }

    override fun release() {
        _audioReleased.postValue(Unit)
    }
}