package uz.invan.rovitalk.ui.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.feed.FeedSubCategory
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkException
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptions
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkPodcasts
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class AudiosViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _onSubCategories = MutableLiveData<List<FeedSubCategory>>()
    val onSubCategories: LiveData<List<FeedSubCategory>> get() = _onSubCategories
    private val _animateSubCategories = MutableLiveData<Event<Unit>>()
    val animateSubCategories: LiveData<Event<Unit>> get() = _animateSubCategories
    private val _onImage = MutableLiveData<String>()
    val onImage: LiveData<String> get() = _onImage
    private val _sub = MutableLiveData<Event<FeedSubCategory>>()
    val sub: LiveData<Event<FeedSubCategory>> get() = _sub
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _roleNotFound = MutableLiveData<Event<Unit>>()
    val roleNotFound: LiveData<Event<Unit>> get() = _roleNotFound
    private val _forbidden = MutableLiveData<Event<Unit>>()
    val forbidden: LiveData<Event<Unit>> get() = _forbidden
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private fun onSubCategories(newSubCategories: List<FeedSubCategory>?) {
        if (!newSubCategories.isNullOrEmpty()) {
            _onSubCategories.postValue(newSubCategories)
        }
    }

    fun animateSubCategories() {
        _animateSubCategories.postValue(Event(Unit))
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun image(image: String) {
        _onImage.postValue(image)
    }

    fun sub(subCategory: FeedSubCategory) {
        _sub.postValue(Event(subCategory))
    }

    fun retrieveAudios(category: String) = viewModel {
        podcasts.retrieveSubCategories(category).catch { exception ->
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
            Timber.d(exception)
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                onSubCategories(resource.data?.subCategories)
        }
    }

    private fun sendNetworkPodcastsExceptionToUi(networkPodcastsExceptionData: NetworkPodcasts.NetworkPodcastsExceptionData) {
        when (networkPodcastsExceptionData.exception) {
            NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED -> {
                _unauthorized.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION -> {
                _roleNotFound.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.FORBIDDEN -> {
                _forbidden.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION -> {
                _unknownError.postValue(Event(Unit))
            }
            else -> Unit
        }
    }

    private fun sendNetworkExceptionToUi(networkExceptionData: NetworkExceptionData) {
        when (networkExceptionData.exception) {
            NetworkExceptions.IO_EXCEPTION -> {
                _noInternet.postValue(Event(Unit))
            }
            NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION -> {
                _timeOut.postValue(Event(Unit))
            }
        }
    }
}