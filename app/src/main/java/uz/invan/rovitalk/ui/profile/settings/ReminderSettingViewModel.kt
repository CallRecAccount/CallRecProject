package uz.invan.rovitalk.ui.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.util.ktx.viewModel
import javax.inject.Inject

@HiltViewModel
class ReminderSettingViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
    private val reminder: ReminderRepository,
) : ViewModel() {
    private val _exit = MutableLiveData<Unit>()
    val exit: LiveData<Unit> get() = _exit
    private val _toggled = MutableLiveData<Boolean>()
    val toggled: LiveData<Boolean> get() = _toggled
    private val _reminders = MutableLiveData<List<ReminderEntity>>()
    val reminders: LiveData<List<ReminderEntity>> get() = _reminders
    private val _reminderEnabled = MutableLiveData<Boolean>()
    val reminderEnabled: LiveData<Boolean> get() = _reminderEnabled
    private val _removeReminder = MutableLiveData<String>()
    val removeReminder: LiveData<String> get() = _removeReminder

    fun exit() {
        _exit.value = Unit
    }

    fun toggled(toggle: Boolean) {
        _toggled.value = toggle
    }

    fun reminderEnabled(enabled: Boolean) = viewModel {
        reminder.switchReminder(enabled)
    }

    fun retrieveReminders() = viewModel {
        val privateReminders = reminder.retrievePrivateReminders()
        _reminders.postValue(privateReminders)
        checkReminder()
    }

    private fun checkReminder() = viewModel {
        val isEnabled = reminder.isReminderEnabled()
        _reminderEnabled.postValue(isEnabled)
    }

    fun removeReminder(reminderData: ReminderEntity) = viewModel {
        val id = reminder.removeReminder(reminderData)
        _removeReminder.postValue(id)
    }
}