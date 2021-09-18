package uz.invan.rovitalk.ui.price

import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.network.responses.BuyTotalResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithClickResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithPaymeResponseData
import uz.invan.rovitalk.data.models.network.responses.BuyWithRoboCassaResponseData
import uz.invan.rovitalk.data.models.price.*
import uz.invan.rovitalk.databinding.ScreenPaymentSystemsBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.money
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class PaymentSystemsScreen : BaseScreen<ScreenPaymentSystemsBinding>(false, null) {
    private val viewModel: PaymentSystemsViewModel by viewModels()
    private val args: PaymentSystemsScreenArgs by navArgs()
    private val systems = fetchRoviPaymentSystems()
    private val adapter by lazy { PaymentSystemsAdapter(systems) }

    private var system: PaymentSystems? = null

    override fun setBinding() = ScreenPaymentSystemsBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.total.observe(viewLifecycleOwner, totalObserver)
        viewModel.buyWithPayme.observe(viewLifecycleOwner, buyWithPaymeObserver)
        viewModel.buyWithClick.observe(viewLifecycleOwner, buyWithClickObserver)
        viewModel.buyWithRoboCassa.observe(viewLifecycleOwner, buyWithRoboCassaObserver)
        viewModel.open.observe(viewLifecycleOwner, openObserver)
        viewModel.call.observe(viewLifecycleOwner, callObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        viewModel.fetchTotalAmount(args.prices.toPricesList())

        binding.back.setOnClickListener { exit() }
        with(binding.paymentSystems) {
            layoutManager = GridLayoutManager(context, 3)
            adapter = this@PaymentSystemsScreen.adapter
            addItemDecoration(PaymentSystemsAdapter.PaymentSystemsItemDecoration(px(16)))
        }
        adapter.setOnPaymentSystemClick {
            system = it.system
            viewModel.paymentSelected(it.system, args.prices.toPricesList())
        }
        binding.support.setOnClickListener {
            viewModel.call()
        }
    }

    private val totalObserver: EventObserver<BuyTotalResponseData> = EventObserver { amount ->
        binding.amount.isVisible = true

        binding.textAmountUSD.text = getString(R.string.usd_x, amount.totalUsd.money())
        binding.textAmountRub.text = getString(R.string.rub_x, amount.totalRuble.money())
        binding.textAmountSum.text = getString(R.string.sum_x, amount.total.money())
    }
    private val buyWithPaymeObserver: EventObserver<BuyWithPaymeResponseData> =
        EventObserver { data ->
//            val link = generatePaymeLink(data.id, data.amount)
            viewModel.open(data.paymeLink)
        }
    private val buyWithClickObserver: EventObserver<BuyWithClickResponseData> = EventObserver {data->
        viewModel.open(data.clickLink)
    }
    private val buyWithRoboCassaObserver: EventObserver<BuyWithRoboCassaResponseData> =
        EventObserver { data ->
            viewModel.open(data.paymentUrl)
        }
    private val openObserver: EventObserver<String> = EventObserver { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
    private val callObserver: EventObserver<String?> = EventObserver { phone ->
        val uri = getString(R.string.call_center_number_x, phone ?: getString(R.string.call_center_number))
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
        startActivity(intent)
    }
    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        controller.navigate(RoviNavigationDirections.splash())
    }
    private val roleNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_role_not_found))
    }
    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
    }
    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
    }
    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
    }
    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    override fun onStart() {
        super.onStart()
        viewModel.retrieveActiveSections()
    }
}