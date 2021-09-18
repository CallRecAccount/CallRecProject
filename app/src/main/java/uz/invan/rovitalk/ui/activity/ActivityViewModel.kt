package uz.invan.rovitalk.ui.activity

import android.net.Uri
import android.net.UrlQuerySanitizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.local.prefs.RoviPrefsImpl
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.data.repository.MainRepository
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.util.ktx.coroutineIO
import uz.invan.rovitalk.util.ktx.viewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val main: MainRepository,
    private val reminder: ReminderRepository,
) : ViewModel() {
    private val _onNotification = MutableLiveData<ReminderEntity>()
    val onNotification: LiveData<ReminderEntity> get() = _onNotification
    private val _initBottomBar = MutableLiveData<Unit>()
    val initBottomBar: LiveData<Unit> get() = _initBottomBar
    private val _changeBottomBarVisibility = MutableLiveData<Boolean>()
    val changeBottomBarVisibility: LiveData<Boolean> get() = _changeBottomBarVisibility
    private val _selectBottomBar = MutableLiveData<MainDirections>()
    val selectBottomBar: LiveData<MainDirections> get() = _selectBottomBar
    private val _showPlayerFooter = MutableLiveData<FeedCategory>()
    val showPlayerFooter: LiveData<FeedCategory> get() = _showPlayerFooter
    private val _hidePlayerFooter = MutableLiveData<Unit>()
    val hidePlayerFooter: LiveData<Unit> get() = _hidePlayerFooter

    private var isBottomBarInitialized = false
    private var lastBackPressedMillis: Long = System.currentTimeMillis()

    init {
        retrieveNotifications()
    }

    fun qr(uri: Uri?) {
        if (uri == null) return
        val value = UrlQuerySanitizer(uri.toString()).getValue(Credentials.QR_QUERY_DATA) ?: return

        main.saveQR(value)
    }

    fun retrieveNotifications() = viewModel {
        reminder.retrieveReminderNotifications().catch { exception ->
            Timber.d(exception)
        }.collect {
            _onNotification.postValue(it)
        }
    }

    fun initBottomBar() {
        if (isBottomBarInitialized) return

        isBottomBarInitialized = true
        _initBottomBar.value = Unit
    }

    fun changeBottomBarVisibility(isVisible: Boolean) {
        _changeBottomBarVisibility.value = isVisible
    }

    fun selectBottomBar(direction: MainDirections) {
        _selectBottomBar.value = direction
    }

    fun onBackPressed(): Boolean {
        val backPressMillis = System.currentTimeMillis()
        return (lastBackPressedMillis + BACK_PRESS_TIMEOUT > backPressMillis).apply {
            lastBackPressedMillis = System.currentTimeMillis()
        }
    }

    fun showPlayerFooter() {
        _showPlayerFooter.value = RoviPrefsImpl.currentAudio ?: return
    }

    fun disableFooter() {
        _hidePlayerFooter.postValue(Unit)
    }

    fun disableCurrentAudio() = viewModel {
        main.disableCurrentAudio()
    }

    override fun onCleared() {
        coroutineIO { main.clearTemporaryConfigurations() }
        super.onCleared()
    }

    companion object {
        private const val BACK_PRESS_TIMEOUT = 2 * 1000
    }
}