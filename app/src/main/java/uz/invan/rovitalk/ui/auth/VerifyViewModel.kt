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
class VerifyViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    private val _initializeVerification = MutableLiveData<Unit>()
    val initializeVerification: LiveData<Unit> get() = _initializeVerification
    private val _initializeTimer = MutableLiveData<Long>()
    val initializeTimer: LiveData<Long> get() = _initializeTimer
    private val _dismissVerification = MutableLiveData<Unit>()
    val dismissVerification: LiveData<Unit> get() = _dismissVerification
    private val _refreshVerification = MutableLiveData<Long>()
    val refreshVerification: LiveData<Long> get() = _refreshVerification
    private val _navigateHome = MutableLiveData<Event<Unit>>()
    val navigateHome: LiveData<Event<Unit>> get() = _navigateHome
    private val _codeSent = MutableLiveData<Event<String>>()
    val codeSent: LiveData<Event<String>> get() = _codeSent
    private val _incorrectPhone = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectPhone: LiveData<Event<LengthCredentialData>> get() = _incorrectPhone
    private val _incorrectOtp = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectOtp: LiveData<Event<LengthCredentialData>> get() = _incorrectOtp
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _userNotFound = MutableLiveData<Event<Unit>>()
    val userNotFound: LiveData<Event<Unit>> get() = _userNotFound
    private val _tooManyAttempts = MutableLiveData<Event<Unit>>()
    val tooManyAttempts: LiveData<Event<Unit>> get() = _tooManyAttempts
    private val _wrongOtp = MutableLiveData<Event<Unit>>()
    val wrongOtp: LiveData<Event<Unit>> get() = _wrongOtp
    private val _otpExpired = MutableLiveData<Event<Unit>>()
    val otpExpired: LiveData<Event<Unit>> get() = _otpExpired
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    // converts `auth.resendIntervalInMinutes` to millis and sends it to ui
    private val resendInterValInMillis = auth.resendIntervalInMinutes * 60 * 1000L

    init {
        _initializeVerification.value = Unit
        _initializeTimer.value = resendInterValInMillis
    }

    fun dismissVerification() {
        _dismissVerification.postValue(Unit)
    }

    private fun refreshVerification() {
        _refreshVerification.postValue(resendInterValInMillis)
    }

    private fun navigateHome() {
        _navigateHome.postValue(Event(Unit))
    }

    private fun codeSent(phone: String) {
        _codeSent.postValue(Event(PhoneNumberUtils.formatNumber(phone, ISO3166.UZ)))
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun verifyOtp(phone: String, otp: String) = viewModel {
        auth.verify(phone, otp).catch { exception ->
            dismissLoader()
            when (exception) {
                is VerifyException -> sendOtpExceptionToUi(exception.data)
                is NetworkAuthException -> sendNetworkAuthExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                navigateHome()
        }
    }

    private fun sendOtpExceptionToUi(verifyExceptionData: VerifyExceptionData) {
        when (verifyExceptionData.exception) {
            VerifyExceptions.VERIFY_PHONE_LENGTH_EXCEPTION -> {
                _incorrectPhone.postValue(Event(verifyExceptionData.phone))
            }
            VerifyExceptions.VERIFY_OTP_LENGTH_EXCEPTION -> {
                _incorrectOtp.postValue(Event(verifyExceptionData.otp))
            }
        }
    }

    fun resend(phone: String) = viewModel {
        auth.resendOtp(phone).catch { exception ->
            dismissLoader()
            when (exception) {
                is ResendException -> sendResendOtpExceptionToUi(exception.data)
                is NetworkAuthException -> sendNetworkAuthExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success) {
                codeSent(phone)
                refreshVerification()
            }
        }
    }

    private fun sendResendOtpExceptionToUi(resendExceptionData: ResendExceptionData) {
        when (resendExceptionData.exception) {
            ResendExceptions.PHONE_LENGTH_EXCEPTION -> {
                _incorrectPhone.postValue(Event(resendExceptionData.phone))
            }
        }
    }

    private fun sendNetworkAuthExceptionToUi(networkAuthExceptionData: NetworkAuthExceptionData) {
        when (networkAuthExceptionData.exception) {
            NetworkAuthExceptions.VALIDATION_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
            NetworkAuthExceptions.USER_NOT_FOUND_EXCEPTION -> {
                _userNotFound.postValue(Event(Unit))
            }
            NetworkAuthExceptions.TOO_MANY_ATTEMPTS_EXCEPTION -> {
                _tooManyAttempts.postValue(Event(Unit))
            }
            NetworkAuthExceptions.INCORRECT_OTP_EXCEPTION -> {
                _wrongOtp.postValue(Event(Unit))
            }
            NetworkAuthExceptions.OTP_EXPIRED_EXCEPTION -> {
                _otpExpired.postValue(Event(Unit))
            }
            NetworkAuthExceptions.UNKNOWN_EXCEPTION -> {
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

}