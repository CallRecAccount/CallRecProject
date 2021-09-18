package uz.invan.rovitalk.ui.profile

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
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _onProfile = MutableLiveData<Event<Profile>>()
    val onProfile: LiveData<Event<Profile>> get() = _onProfile
    private val _language = MutableLiveData<RoviLanguage>()
    val language: LiveData<RoviLanguage> get() = _language
    private val _onSections = MutableLiveData<List<FeedSection>>()
    val onSections: LiveData<List<FeedSection>> get() = _onSections
    private val _openLink = MutableLiveData<Event<String>>()
    val openLink: LiveData<Event<String>> get() = _openLink
    private val _navigateSettings = MutableLiveData<Event<Unit>>()
    val navigateSettings: LiveData<Event<Unit>> get() = _navigateSettings
    private val _navigateQR = MutableLiveData<Event<Unit>>()
    val navigateQR: LiveData<Event<Unit>> get() = _navigateQR
    private val _navigateEditProfile = MutableLiveData<Event<Unit>>()
    val navigateEditProfile: LiveData<Event<Unit>> get() = _navigateEditProfile
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _roleNotFound = MutableLiveData<Event<Unit>>()
    val roleNotFound: LiveData<Event<Unit>> get() = _roleNotFound
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateSmallLoader = MutableLiveData<Event<Boolean>>()
    val updateSmallLoader: LiveData<Event<Boolean>> get() = _updateSmallLoader
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private fun onProfile(profile: Profile?) {
        if (profile != null) _onProfile.postValue(Event(profile))
        else _unknownError.postValue(Event(Unit))
        retrieveSections()
    }

    fun navigateSettings() {
        _navigateSettings.value = Event(Unit)
    }

    fun navigateQR() {
        _navigateQR.value = Event(Unit)
    }

    fun navigateEditProfile() {
        _navigateEditProfile.value = Event(Unit)
    }

    private var sections: List<FeedSection>?
        set(value) {
            savedStateHandle.set(SECTIONS_AND_CATEGORIES, value)
        }
        get() = savedStateHandle[SECTIONS_AND_CATEGORIES]

    private fun onSectionsAndCategories(newSections: List<FeedSection>?) {
        if (!newSections.isNullOrEmpty()) {
            sections = newSections
            sections?.let { sections -> _onSections.postValue(sections) }
        }
    }

    private fun showLoader() {
        if (!sections.isNullOrEmpty()) _updateSmallLoader.postValue(Event(true))
        else _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        if (!sections.isNullOrEmpty()) _updateSmallLoader.postValue(Event(false))
        else _updateLoader.postValue(false)
    }

    fun loadProfile() = viewModel {
        auth.retrieveProfile().collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            onProfile(profile = resource.data)
        }
    }

    fun fetchLanguage() {
        val roviLang = auth.fetchLanguage()
        _language.value = roviLang
    }

    fun openLink(url: String) {
        _openLink.postValue(Event(url))
    }

    private fun retrieveSections() = viewModel {
        podcasts.retrieveSectionsAndCategories().catch { exception ->
            Timber.e(exception)
            dismissLoader()
            when (exception) {
                is SectionsException -> {
                }
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                onSectionsAndCategories(resource.data)
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
            NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION -> {
                _unknownError.postValue(Event(Unit))
            }
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

    companion object {
        private const val SECTIONS_AND_CATEGORIES =
            "uz.invan.rovitalk.ui.profile.ProfileViewModel.saveStateHandle.sectionsAndCategories"
    }
}