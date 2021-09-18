package uz.invan.rovitalk.ui.auth

import android.telephony.PhoneNumberUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.validation.ISO3166
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class EnterViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
) : ViewModel() {
    private val _navigateVideo = MutableLiveData<Event<String>>()
    val navigateVideo: LiveData<Event<String>> get() = _navigateVideo
    private val _navigateRegister = MutableLiveData<Event<Unit>>()
    val navigateRegister: LiveData<Event<Unit>> get() = _navigateRegister
    private val _navigateVerify = MutableLiveData<Event<Unit>>()
    val navigateVerify: LiveData<Event<Unit>> get() = _navigateVerify
    private val _codeSent = MutableLiveData<Event<String>>()
    val codeSent: LiveData<Event<String>> get() = _codeSent
    private val _incorrectPhone = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectPhone: LiveData<Event<LengthCredentialData>> get() = _incorrectPhone
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _tooManyAttempts = MutableLiveData<Event<Unit>>()
    val tooManyAttempts: LiveData<Event<Unit>> get() = _tooManyAttempts
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    fun introduction() {
        val introduction = auth.fetchIntroduction()
        introduction?.let {
            _navigateVideo.postValue(Event(introduction))
        }
    }

    private fun navigateRegister() {
        _navigateRegister.postValue(Event(Unit))
    }

    private fun codeSent(phone: String) {
        _codeSent.postValue(Event(PhoneNumberUtils.formatNumber(phone, ISO3166.UZ)))
    }

    private fun navigateVerify() {
        _navigateVerify.postValue(Event(Unit))
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun login(phone: String, isValidFormattedNumber: Boolean) = viewModel {
        auth.login(phone, isValidFormattedNumber).catch { exception ->
            dismissLoader()
            when (exception) {
                is LoginException -> sendLoginExceptionToUi(exception.data)
                is NetworkAuthException -> sendNetworkAuthExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success) {
                codeSent(phone)
                navigateVerify()
            }
        }
    }

    private fun sendLoginExceptionToUi(loginExceptionData: LoginExceptionData) {
        when (loginExceptionData.exception) {
            LoginExceptions.PHONE_LENGTH_EXCEPTION -> {
                _incorrectPhone.postValue(Event(loginExceptionData.phone))
            }
        }
    }

    private fun sendNetworkAuthExceptionToUi(networkAuthExceptionData: NetworkAuthExceptionData) {
        when (networkAuthExceptionData.exception) {
            NetworkAuthExceptions.VALIDATION_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
            NetworkAuthExceptions.USER_NOT_FOUND_EXCEPTION -> {
                navigateRegister()
            }
            NetworkAuthExceptions.TOO_MANY_ATTEMPTS_EXCEPTION -> {
                _tooManyAttempts.postValue(Event(Unit))
            }
            NetworkAuthExceptions.UNKNOWN_EXCEPTION -> {
                _unknownError.postValue(Event(Unit))
            }
            else->Unit
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