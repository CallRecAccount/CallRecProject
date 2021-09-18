package uz.invan.rovitalk.ui.audio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.audio.PlayerAudio
import uz.invan.rovitalk.data.models.audio.PlayerBM
import uz.invan.rovitalk.data.models.audio.fakePlayerBM
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkException
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptions
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkPodcasts
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.hasOneTime
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _onAudios = MutableLiveData<List<PlayerAudio>>()
    val onAudios: LiveData<List<PlayerAudio>> get() = _onAudios
    private val _onImage = MutableLiveData<String>()
    val onImage: LiveData<String> get() = _onImage
    private val _initializeBMs = MutableLiveData<List<PlayerBM>>()
    val initializeBMs: LiveData<List<PlayerBM>> get() = _initializeBMs
    private val _updateBM = MutableLiveData<PlayerBM>()
    val updateBM: LiveData<PlayerBM> get() = _updateBM
    private val _askPermissions = MutableLiveData<Event<Unit>>()
    val askPermissions: LiveData<Event<Unit>> get() = _askPermissions
    private val _showPermissionDialog = MutableLiveData<Event<Unit>>()
    val showPermissionDialog: LiveData<Event<Unit>> get() = _showPermissionDialog
    private val _requestPermissions = MutableLiveData<Event<Unit>>()
    val requestPermissions: LiveData<Event<Unit>> get() = _requestPermissions
    private val _permissionRecommended = MutableLiveData<Event<Unit>>()
    val permissionRecommended: LiveData<Event<Unit>> get() = _permissionRecommended
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

    private fun onAudios(newAudios: List<PlayerAudio>?) {
        if (!newAudios.isNullOrEmpty()) {
            newAudios.firstOrNull()?.categoryImage?.let { image ->
                _onImage.postValue(image)
            }
            _onAudios.postValue(newAudios ?: return)
        }
    }

    private fun onBMs(bms: List<PlayerBM>?) {
        if (!bms.isNullOrEmpty() && initializeBMs.value == null) {
            val initializedBMS = arrayListOf(fakePlayerBM)
            initializedBMS.addAll(bms)
            _initializeBMs.postValue(initializedBMS)
        }
        if (!bms.isNullOrEmpty() && initializeBMs.value != null && bms.hasOneTime()) {
            val bm = bms.firstOrNull()
            bm?.let { _updateBM.postValue(bm ?: return) }
        }
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun loadPlayer(category: String, subCategory: String?) {
        // if subcategory null then retrieve category
        retrieveAudios(subCategory ?: category)
        retrieveBMs(subCategory ?: category)
    }

    private fun retrieveAudios(category: String) = viewModel {
        podcasts.retrieveCategoryAudios(category).catch { exception ->
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
            if (resource is Resource.Success) {
                onAudios(resource.data)
                podcasts.setLastListened(category)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun retrieveBMs(category: String) = viewModel {
        podcasts.retrieveBMs(category).catch { exception ->
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
            Timber.d(exception)
        }.collect { resource ->
//            if (resource is Resource.Loading) showLoader()
//            else dismissLoader()
            if (resource is Resource.Success)
                onBMs(resource.data)
        }
    }

    fun permission(isPermissionGranted: Boolean) {
        if (!isPermissionGranted) _askPermissions.postValue(Event(Unit))
    }

    fun showPermissionDialog() {
        _showPermissionDialog.postValue(Event(Unit))
    }

    fun requestPermissions() {
        _requestPermissions.postValue(Event(Unit))
    }

    fun permissionRecommended() {
        _permissionRecommended.postValue(Event(Unit))
    }

    fun saveForCurrent(category: FeedCategory) = viewModel {
        podcasts.saveCurrentAudio(category)
    }

    fun deleteForCurrent() = viewModel {
        podcasts.deleteCurrentAudio()
    }

    fun onRelease() = viewModel {
        podcasts.setPlayerRelease()
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