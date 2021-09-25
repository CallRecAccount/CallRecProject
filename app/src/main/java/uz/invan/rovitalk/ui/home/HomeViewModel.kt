package uz.invan.rovitalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.validation.NetworkResults
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _setPoster = MutableLiveData<String>()
    val setPoster: LiveData<String> get() = _setPoster
    private val _activated = MutableLiveData<Event<Unit>>()
    val activated: LiveData<Event<Unit>> get() = _activated
    private val _onSections = MutableLiveData<List<FeedSection>>()
    val onSections: LiveData<List<FeedSection>> get() = _onSections
    private val _onIntroduction = MutableLiveData<String>()
    val onIntroduction: LiveData<String> get() = _onIntroduction
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _roleNotFound = MutableLiveData<Event<Unit>>()
    val roleNotFound: LiveData<Event<Unit>> get() = _roleNotFound
    private val _qrNotFound = MutableLiveData<Event<Unit>>()
    val qrNotFound: LiveData<Event<Unit>> get() = _qrNotFound
    private val _qrAlreadyExists = MutableLiveData<Event<Unit>>()
    val qrAlreadyExists: LiveData<Event<Unit>> get() = _qrAlreadyExists
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

    private val _toCategories = MutableLiveData<Event<Unit>>()
    val toCategories: LiveData<Event<Unit>> get() = _toCategories

    fun setPoster() {
        _setPoster.value = "https://telegra.ph/file/397198e4861617312c26f.png"
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

    fun loadHome() {
        retrieveIntroduction()
    }

    private fun qr() = viewModel {
        podcasts.qr().catch { exception ->
            // deletes qr if not correct
            podcasts.deleteTemporaryQR()

            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                _activated.postValue(Event(Unit))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
        }.onCompletion {
            qr()
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                onSectionsAndCategories(resource.data)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun retrieveIntroduction() = viewModel {
        podcasts.retrieveIntroductions().onStart {
            showLoader()
        }.catch { exception ->
            dismissLoader()
            Timber.d(exception)
        }.collect { introduction ->
            dismissLoader()
            _onIntroduction.postValue(introduction)
            retrieveSections()
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
                when (networkPodcastsExceptionData.code) {
                    NetworkResults.QR_NOT_FOUND -> _qrNotFound.postValue(Event(Unit))
                    NetworkResults.QR_ALREADY_USED -> _qrAlreadyExists.postValue(Event(Unit))
                    else -> _unknownError.postValue(Event(Unit))
                }
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

    fun toBooks() {
        _toCategories.value = Event(Unit)
    }

    companion object {
        private const val SECTIONS_AND_CATEGORIES =
            "uz.invan.rovitalk.ui.home.HomeViewModel.saveStateHandle.sectionsAndCategories"
    }
}