package uz.invan.rovitalk.ui.profile.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.util.ktx.viewModel
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val reminder: ReminderRepository,
) : ViewModel() {
    private val _reminderSaved = MutableLiveData<ReminderEntity>()
    val reminderSaved: LiveData<ReminderEntity> get() = _reminderSaved

    fun remind(title: String, content: String, time: String, rm: ReminderEntity?) = viewModel {
        reminder.remind(title, content, time, rm).catch { exception ->
            Timber.d(exception)
        }.collect { reminder ->
            _reminderSaved.postValue(reminder)
        }
    }
}