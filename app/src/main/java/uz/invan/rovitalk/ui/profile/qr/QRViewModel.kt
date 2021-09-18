package uz.invan.rovitalk.ui.profile.qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.validation.NetworkResults
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkException
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptions
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkPodcasts
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _activated = MutableLiveData<Event<Unit>>()
    val activated: LiveData<Event<Unit>> get() = _activated
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
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
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun qr(qr: String) = viewModel {
        podcasts.qr(qr).catch { exception ->
            Timber.d(exception)
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

    private fun sendNetworkPodcastsExceptionToUi(networkPodcastsExceptionData: NetworkPodcasts.NetworkPodcastsExceptionData) {
        Timber.d("data: $networkPodcastsExceptionData")
        when (networkPodcastsExceptionData.exception) {
            NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED -> {
                _unauthorized.postValue(Event(Unit))
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
}