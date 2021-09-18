package uz.invan.rovitalk.ui.price

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestorePriceViewModel @Inject constructor(
     private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _exit = MutableLiveData<Unit>()
    val exit: LiveData<Unit> get() = _exit

    fun exit() {
        _exit.value = Unit
    }
}