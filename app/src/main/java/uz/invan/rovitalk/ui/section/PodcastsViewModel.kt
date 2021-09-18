package uz.invan.rovitalk.ui.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class PodcastsViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val podcasts: PodcastsRepository
) : ViewModel() {
    private val _exit = MutableLiveData<Event<Unit>>()
    val exit: LiveData<Event<Unit>> get() = _exit
    private val _onCategories = MutableLiveData<FeedSection>()
    val onCategories: LiveData<FeedSection> get() = _onCategories
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
    private val _updateSmallLoader = MutableLiveData<Boolean>()
    val updateSmallLoader: LiveData<Boolean> get() = _updateSmallLoader
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private var section: FeedSection?
        set(value) {
            savedStateHandle[SECTION] = Gson().toJson(value)
        }
        get() = Gson().fromJson(savedStateHandle.get<String?>(SECTION), FeedSection::class.java)
    private var categories: List<FeedCategory>?
        set(value) {
            savedStateHandle[CATEGORIES] = value
        }
        get() = savedStateHandle[CATEGORIES]

    fun exit() {
        _exit.value = Event(Unit)
    }

    private fun onCategories(newSectionAndItsCategories: FeedSection?) {
        if (newSectionAndItsCategories != null && newSectionAndItsCategories.categories.isNotEmpty()) {
            section = newSectionAndItsCategories
            categories = newSectionAndItsCategories.categories
            val sectionsAndItsCategories = mergeSectionAndItsCategories()
            sectionsAndItsCategories?.let { _onCategories.postValue(sectionsAndItsCategories) }
        }
    }

    private fun mergeSectionAndItsCategories(): FeedSection? {
        return section?.copy(categories = categories ?: return null)
    }

    private fun showLoader() {
        if (!categories.isNullOrEmpty()) _updateSmallLoader.postValue(true)
        else _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        if (!categories.isNullOrEmpty()) _updateSmallLoader.postValue(false)
        else _updateLoader.postValue(false)
    }

    fun retrieveCategories(sectionId: String) = viewModel {
        podcasts.retrieveCategories(sectionId).catch { exception ->
            Timber.e(exception)
            dismissLoader()
            when (exception) {
                is SectionsException -> sendSectionsExceptionToUi(exception.data)
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                onCategories(resource.data)
        }
    }

    private fun sendSectionsExceptionToUi(sectionsExceptionData: SectionsExceptionData) {
        when (sectionsExceptionData.exception) {
            SectionsExceptions.SECTION_BY_ID_NOT_FOUND_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
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
        private const val SECTION = "uz.invan.rovitalk.ui.section.PodcastsViewModel.section"
        private const val CATEGORIES = "uz.invan.rovitalk.ui.section.PodcastsViewModel.categories"
    }
}