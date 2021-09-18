package uz.invan.rovitalk.ui.price

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.network.responses.BuyTotalResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithClickResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithPaymeResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithRoboCassaResponseData
import uz.invan.rovitalk.data.models.price.PaymentSystems
import uz.invan.rovitalk.data.models.price.PaymentSystems.*
import uz.invan.rovitalk.data.models.settings.ResourceLinks
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkException
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptionData
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkExceptions
import uz.invan.rovitalk.data.models.validation.exceptions.NetworkPodcasts
import uz.invan.rovitalk.data.repository.AuthRepository
import uz.invan.rovitalk.data.repository.PodcastsRepository
import uz.invan.rovitalk.util.ktx.viewModel
import uz.invan.rovitalk.util.lifecycle.Event
import javax.inject.Inject

@HiltViewModel
class PaymentSystemsViewModel @Inject constructor(
    private val podcasts: PodcastsRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _total = MutableLiveData<Event<BuyTotalResponseData>>()
    val total: LiveData<Event<BuyTotalResponseData>> get() = _total
    private val _buyWithPayme = MutableLiveData<Event<BuyWithPaymeResponseData>>()
    val buyWithPayme: LiveData<Event<BuyWithPaymeResponseData>> get() = _buyWithPayme
    private val _buyWithClick = MutableLiveData<Event<BuyWithClickResponseData>>()
    val buyWithClick: LiveData<Event<BuyWithClickResponseData>> get() = _buyWithClick
    private val _buyWithRoboCassa = MutableLiveData<Event<BuyWithRoboCassaResponseData>>()
    val buyWithRoboCassa: LiveData<Event<BuyWithRoboCassaResponseData>> get() = _buyWithRoboCassa
    private val _open = MutableLiveData<Event<String>>()
    val open: LiveData<Event<String>> get() = _open
    private val _call = MutableLiveData<Event<String?>>()
    val call: LiveData<Event<String?>> get() = _call
    private val _unauthorized = MutableLiveData<Event<Unit>>()
    val unauthorized: LiveData<Event<Unit>> get() = _unauthorized
    private val _roleNotFound = MutableLiveData<Event<Unit>>()
    val roleNotFound: LiveData<Event<Unit>> get() = _roleNotFound
    private val _unknownError = MutableLiveData<Event<Unit>>()
    val unknownError: LiveData<Event<Unit>> get() = _unknownError
    private val _noInternet = MutableLiveData<Event<Unit>>()
    val noInternet: LiveData<Event<Unit>> get() = _noInternet
    private val _timeOut = MutableLiveData<Event<Unit>>()
    val timeOut: LiveData<Event<Unit>> get() = _timeOut
    private val _updateLoader = MutableLiveData<Boolean>()
    val updateLoader: LiveData<Boolean> get() = _updateLoader

    private fun showLoader() {
        _updateLoader.postValue(true)
    }

    private fun dismissLoader() {
        _updateLoader.postValue(false)
    }

    fun fetchTotalAmount(prices: List<String>) = viewModel {
        podcasts.fetchTotalAmount(prices).catch { exception ->
            Timber.d(exception)
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success && resource.data != null)
                _total.postValue(Event(resource.data))
        }
    }

    fun paymentSelected(system: PaymentSystems, prices: List<String>) = viewModel {
        when (system) {
            PAYME -> buyWithPayme(prices)
            CLICK -> buyWithClick(prices)
            ROBO_CASSA -> buyWithRoboCassa(prices)
            PAYPAL -> buyWithRoboCassa(prices)
            VISA -> buyWithRoboCassa(prices)
            MASTERCARD -> buyWithRoboCassa(prices)
            IO_MONEY -> buyWithRoboCassa(prices)
            QIWI -> buyWithRoboCassa(prices)
            APPLE_PAY -> buyWithRoboCassa(prices)
            OTHER -> buyWithRoboCassa(prices)
        }
    }

    private fun buyWithPayme(prices: List<String>) = viewModel {
        podcasts.buyWithPayme(prices).catch { exception ->
            Timber.d(exception)
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success && resource.data != null)
                _buyWithPayme.postValue(Event(resource.data))
        }
    }

    private fun buyWithClick(prices: List<String>) = viewModel {
        podcasts.buyWithClick(prices).catch { exception ->
            Timber.d(exception)
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success && resource.data != null)
                _buyWithClick.postValue(Event(resource.data))
        }
    }

    private fun buyWithRoboCassa(prices: List<String>) = viewModel {
        podcasts.buyWithRoboCassa(prices).catch { exception ->
            Timber.d(exception)
            dismissLoader()
            when (exception) {
                is NetworkPodcasts.NetworkPodcastsException ->
                    sendNetworkPodcastsExceptionToUi(exception.data)
                is NetworkException -> sendNetworkExceptionToUi(exception.data)
            }
        }.collect { resource ->
            if (resource is Resource.Loading) showLoader()
            else dismissLoader()
            if (resource is Resource.Success && resource.data != null)
                _buyWithRoboCassa.postValue(Event(resource.data))
        }
    }

    fun retrieveActiveSections() = viewModel {
        podcasts.retrieveActiveSections(emptyList()).catch { exception ->
            Timber.d(exception)
        }.collect()
    }

    fun open(url: String) {
        _open.postValue(Event(url))
    }

    fun call() {
        val settingsLinks = auth.fetchSettingsLinks()
        val phone = settingsLinks?.links?.firstOrNull { it.type == ResourceLinks.CALL_CENTER.title }?.url
        _call.postValue(Event(phone))
    }

    private fun sendNetworkPodcastsExceptionToUi(networkPodcastsExceptionData: NetworkPodcasts.NetworkPodcastsExceptionData) {
        when (networkPodcastsExceptionData.exception) {
            NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED -> {
                _unauthorized.postValue(Event(Unit))
            }
            NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION -> {
                _roleNotFound.postValue(Event(Unit))
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