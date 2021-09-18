package uz.invan.rovitalk.ui.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.invan.rovitalk.data.models.settings.RoviSettings
import uz.invan.rovitalk.data.models.settings.SettingsLinks
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
) : ViewModel() {
    private val _exit = MutableLiveData<Unit>()
    val exit: LiveData<Unit> get() = _exit
    private val _initializeSettings = MutableLiveData<Unit>()
    val initializeSettings: LiveData<Unit> get() = _initializeSettings
    private val _openLink = MutableLiveData<Event<String>>()
    val openLink: LiveData<Event<String>> get() = _openLink
    private val _navigateSetting = MutableLiveData<Event<Pair<RoviSettings, SettingsLinks?>>>()
    val navigateSetting: LiveData<Event<Pair<RoviSettings, SettingsLinks?>>> get() = _navigateSetting
    private val _logoutWarning = MutableLiveData<Event<Unit>>()
    val logoutWarning: LiveData<Event<Unit>> get() = _logoutWarning
    private val _logout = MutableLiveData<Event<Unit>>()
    val logout: LiveData<Event<Unit>> get() = _logout

    fun exit() {
        _exit.value = Unit
    }

    fun initializeSettings() {
        _initializeSettings.value = Unit
    }

    fun openLink(url: String) {
        _openLink.postValue(Event(url))
    }

    fun navigateSetting(setting: RoviSettings) {
        val settingsLinks = auth.fetchSettingsLinks()
        _navigateSetting.value = Event(setting to settingsLinks)
    }

    fun logoutWarning() {
        _logoutWarning.value = Event(Unit)
    }

    fun logout() {
        viewModel {
            auth.logout()
            _logout.postValue(Event(Unit))
        }
    }
}