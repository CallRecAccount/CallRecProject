package uz.invan.rovitalk.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.settings.ResourceLinks
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.util.ktx.phone
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
     val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
) : ViewModel() {
    private val _exit = MutableLiveData<Unit>()
    val exit: LiveData<Unit> get() = _exit
    private val _terms = MutableLiveData<Event<String>>()
    val terms: LiveData<Event<String>> get() = _terms
    private val _navigateVideo = MutableLiveData<Event<String>>()
    val navigateVideo: LiveData<Event<String>> get() = _navigateVideo
    private val _navigateVerify = MutableLiveData<Event<Unit>>()
    val navigateVerify: LiveData<Event<Unit>> get() = _navigateVerify
    private val _codeSent = MutableLiveData<Event<String>>()
    val codeSent: LiveData<Event<String>> get() = _codeSent
    private val _incorrectPhone = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectPhone: LiveData<Event<LengthCredentialData>> get() = _incorrectPhone
    private val _incorrectFirstName = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectFirstName: LiveData<Event<LengthCredentialData>> get() = _incorrectFirstName
    private val _incorrectLastName = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectLastName: LiveData<Event<LengthCredentialData>> get() = _incorrectLastName
    private val _incorrectAgreement = MutableLiveData<Event<Unit>>()
    val incorrectAgreement: LiveData<Event<Unit>> get() = _incorrectAgreement
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _userAlreadyExists = MutableLiveData<Event<Unit>>()
    val userAlreadyExists: LiveData<Event<Unit>> get() = _userAlreadyExists
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    fun exit() {
        _exit.value = Unit
    }

    fun termsAndConditions() {
        val terms =
            auth.fetchSettingsLinks()?.links?.firstOrNull { it.type == ResourceLinks.TERMS_OF_USE.title }
        if (terms != null)
            _terms.value = Event(terms.url)
    }

    fun introduction() {
        val introduction = auth.fetchIntroduction()
        introduction?.let {
            _navigateVideo.postValue(Event(introduction))
        }
    }

    private fun navigateVerify() {
        _navigateVerify.postValue(Event(Unit))
    }

    private fun codeSent(phone: String?) {
        if (phone != null)
            _codeSent.postValue(Event(phone.phone() ?: phone))
//            _codeSent.postValue(Event(PhoneNumberUtils.formatNumber(phone, ISO3166.UZ)))
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun register(phone: String, firstName: String, lastName: String, isAgreed: Boolean) =
        viewModel {
            auth.register(phone, firstName, lastName, isAgreed).catch { exception ->
                dismissLoader()
                when (exception) {
                    is RegisterException -> sendRegisterExceptionToUi(exception.data)
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

    private fun sendRegisterExceptionToUi(registerExceptionData: RegisterExceptionData) {
        when (registerExceptionData.exception) {
            RegisterExceptions.PHONE_LENGTH_EXCEPTION -> {
                _incorrectPhone.postValue(Event(registerExceptionData.phone))
            }
            RegisterExceptions.FIRST_NAME_LENGTH_EXCEPTION -> {
                _incorrectFirstName.postValue(Event(registerExceptionData.firstName))
            }
            RegisterExceptions.LAST_NAME_LENGTH_EXCEPTION -> {
                _incorrectLastName.postValue(Event(registerExceptionData.lastName))
            }
            RegisterExceptions.AGREEMENT_EXCEPTION -> {
                _incorrectAgreement.postValue(Event(Unit))
            }
        }
    }

    private fun sendNetworkAuthExceptionToUi(networkAuthExceptionData: NetworkAuthExceptionData) {
        when (networkAuthExceptionData.exception) {
            NetworkAuthExceptions.VALIDATION_EXCEPTION -> {
                _incorrectValidation.postValue(Event(Unit))
            }
            NetworkAuthExceptions.USER_ALREADY_EXISTS -> {
                _userAlreadyExists.postValue(Event(Unit))
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