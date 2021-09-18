package uz.invan.rovitalk.ui.profile.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
) : ViewModel() {
    private val _initializeEditable = MutableLiveData<String>()
    val initializeEditable: LiveData<String> get() = _initializeEditable
    private val _navigateSettings = MutableLiveData<Event<Unit>>()
    val navigateSettings: LiveData<Event<Unit>> get() = _navigateSettings
    private val _profileUpdated = MutableLiveData<Event<Profile>>()
    val profileUpdated: LiveData<Event<Profile>> get() = _profileUpdated
    private val _incorrectFirstName = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectFirstName: LiveData<Event<LengthCredentialData>> get() = _incorrectFirstName
    private val _incorrectLastName = MutableLiveData<Event<LengthCredentialData>>()
    val incorrectLastName: LiveData<Event<LengthCredentialData>> get() = _incorrectLastName
    private val _incorrectValidation = MutableLiveData<Event<Unit>>()
    val incorrectValidation: LiveData<Event<Unit>> get() = _incorrectValidation
    private val _userNotFound = MutableLiveData<Event<Unit>>()
    val userNotFound: LiveData<Event<Unit>> get() = _userNotFound
    private val _userAlreadyExists = MutableLiveData<Event<Unit>>()
    val userAlreadyExists: LiveData<Event<Unit>> get() = _userAlreadyExists
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private var phone = ""

    init {
        viewModel {
            auth.retrieveProfile().collect { resource ->
                if (resource is Resource.Loading) showLoader()
                else dismissLoader()
                profileUpdated(profile = resource.data)
            }
        }
        _initializeEditable.postValue(auth.phoneFormatMask)
    }

    fun onPhoneChanged(phoneExtracted: String) {
        phone = phoneExtracted
    }

    fun navigateSettings() {
        _navigateSettings.value = Event(Unit)
    }

    private fun profileUpdated(profile: Profile?) {
        if (profile != null) _profileUpdated.postValue(Event(profile))
        else _unknownError.postValue(Event(Unit))
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun editProfile(firstName: String, lastName: String, photo: File?) = viewModel {
        auth.editProfile(firstName, lastName, photo).catch { exception ->
            dismissLoader()
            when (exception) {
                is EditProfileException -> sendEditProfileExceptionToUi(exception.data)
                is NetworkAuthException -> sendNetworkAuthExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                profileUpdated(resource.data)
        }
    }

    private fun sendEditProfileExceptionToUi(editProfileExceptionData: EditProfileExceptionData) {
        when (editProfileExceptionData.exception) {
            EditProfileExceptions.FIRST_NAME_LENGTH_EXCEPTION -> {
                _incorrectFirstName.postValue(Event(editProfileExceptionData.firstName))
            }
            EditProfileExceptions.LAST_NAME_LENGTH_EXCEPTION -> {
                _incorrectLastName.postValue(Event(editProfileExceptionData.lastName))
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
            NetworkAuthExceptions.USER_ALREADY_EXISTS -> {
                _userAlreadyExists.postValue(Event(Unit))
            }
            NetworkAuthExceptions.UNAUTHORIZED -> {
                _unauthorized.postValue(Event(Unit))
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