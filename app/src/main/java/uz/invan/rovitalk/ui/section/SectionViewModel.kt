package uz.invan.rovitalk.ui.section

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
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class SectionViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _onSectionsAndCategories = MutableLiveData<List<FeedSection>>()
    val onSectionsAndCategories: LiveData<List<FeedSection>> get() = _onSectionsAndCategories
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

    private var sections: List<FeedSection>?
        set(value) {
            savedStateHandle.set(SECTIONS_AND_CATEGORIES, value)
        }
        get() = savedStateHandle[SECTIONS_AND_CATEGORIES]

    private fun onSectionsAndCategories(newSections: List<FeedSection>?) {
        if (!newSections.isNullOrEmpty()) {
            sections = newSections
            sections?.let { sections -> _onSectionsAndCategories.postValue(sections) }
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

    fun retrieveSectionsAndCategories() = viewModel {
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
            "uz.invan.rovitalk.ui.section.SectionViewModel.saveStateHandle.sectionsAndCategories"
    }
}