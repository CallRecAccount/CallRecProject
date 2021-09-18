package uz.invan.rovitalk.util.ktx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

// viewmodel-ktx
fun ViewModel.withViewModelIO(
    block: suspend CoroutineScope.() -> Unit,
) {
    viewModelScope.launch(context = IO, block = block)
}

fun ViewModel.viewModel(block: suspend CoroutineScope.() -> Unit) = withViewModelIO(block)