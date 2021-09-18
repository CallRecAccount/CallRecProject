package uz.invan.rovitalk.ui.price

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.price.TariffData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkException
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptions
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkPodcasts
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class PriceViewModel @Inject constructor(
    private val podcasts: PodcastsRepository,
) : ViewModel() {
    private val _exit = MutableLiveData<Unit>()
    val exit: LiveData<Unit> get() = _exit
    private val _onPrices = MutableLiveData<List<TariffData>>()
    val onPrices: LiveData<List<TariffData>> get() = _onPrices
    private val _onPricesSelected = MutableLiveData<List<String>>()
    val onPricesSelected: LiveData<List<String>> get() = _onPricesSelected
    private val _tariffNotChosen = MutableLiveData<Event<Unit>>()
    val tariffNotChosen: LiveData<Event<Unit>> get() = _tariffNotChosen
    private val _bought = MutableLiveData<Event<Unit>>()
    val bought: LiveData<Event<Unit>> get() = _bought
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _roleNotFound = MutableLiveData<Event<Unit>>()
    val roleNotFound: LiveData<Event<Unit>> get() = _roleNotFound
    private val _balanceNotEnough = MutableLiveData<Event<Unit>>()
    val balanceNotEnough: LiveData<Event<Unit>> get() = _balanceNotEnough
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

    private fun onPrices(newPrices: List<TariffData>?) {
        if (newPrices != null) {
            if (newPrices.isEmpty()) {
                _unknownError.postValue(Event(Unit))
                return
            }
            _onPrices.postValue(newPrices ?: return)
        }
    }

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun retrievePrices() = viewModel {
        podcasts.retrievePrices().catch { exception ->
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success)
                onPrices(resource.data)
        }
    }

    fun onPricesSelected(prices: List<String>) {
        _onPricesSelected.postValue(prices)
    }

    private fun sendNetworkPodcastsExceptionToUi(networkPodcastsExceptionData: NetworkPodcasts.NetworkPodcastsExceptionData) {
        when (networkPodcastsExceptionData.exception) {
            NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED -> {
                _unauthorized.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION -> {
                _roleNotFound.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.BALANCE_NOT_ENOUGH -> {
                _balanceNotEnough.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION -> {
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