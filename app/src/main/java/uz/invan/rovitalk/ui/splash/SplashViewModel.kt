package uz.invan.rovitalk.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val auth: AuthRepository,
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _moveToEnter = MutableLiveData<Event<Unit>>()
    val moveToEnter: LiveData<Event<Unit>> get() = _moveToEnter
    private val _moveToHome = MutableLiveData<Event<Unit>>()
    val moveToHome: LiveData<Event<Unit>> get() = _moveToHome

    init {
        viewModel {
            val isAuthenticated = auth.isUserAuthenticated()
            podcasts.retrieveIntroductions().catch { exception ->
                Timber.d(exception)
            }.onCompletion {
                if (isAuthenticated)
                    retrieveInitialDataAndMoveHome()
                else moveToEnter()
            }.collect()
        }
    }

    private fun moveToEnter() {
        _moveToEnter.postValue(Event(Unit))
    }

    private fun moveToHome() {
        _moveToHome.postValue(Event(Unit))
    }

    private suspend fun retrieveInitialDataAndMoveHome() {
        podcasts.retrieveSectionsAndCategories(ignoreCache = true).catch { exception ->
            moveToHome()
        }.collect { resource ->
            if (resource is Resource.Success)
                moveToHome()
        }
    }

    fun navigate() = viewModel {
    }
}