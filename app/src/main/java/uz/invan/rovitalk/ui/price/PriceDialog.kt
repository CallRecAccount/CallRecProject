package uz.invan.rovitalk.ui.price

import android.content.DialogInterface
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.price.TariffData
import uz.invan.rovitalk.databinding.DialogPriceBinding
import uz.invan.rovitalk.ui.base.RoundedBottomSheetDialogFragment
import uz.invan.rovitalk.util.ktx.*
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class PriceDialog :
    RoundedBottomSheetDialogFragment<DialogPriceBinding>(R.style.BottomSheetDialogTheme) {
    private val viewModel by viewModels<PriceViewModel>()
    private val tariffs = arrayListOf<TariffData>()
    private val adapter by lazy { TariffsPickerAdapter(tariffs) }

    private var listener: PricesSelectedListener? = null
    fun setOnPricesSelectedListener(onPricesSelected: PricesSelectedListener) {
        listener = onPricesSelected
    }

    override fun setBinding() = DialogPriceBinding.inflate(layoutInflater)
    override fun onObserve() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.onPrices.observe(viewLifecycleOwner, onPricesObserver)
        viewModel.onPricesSelected.observe(viewLifecycleOwner, onPricesSelectedObserver)
        viewModel.tariffNotChosen.observe(viewLifecycleOwner, tariffNotChosenObserver)
        viewModel.bought.observe(viewLifecycleOwner, boughtObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.balanceNotEnough.observe(viewLifecycleOwner, balanceNotEnoughObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewReady() {
        binding.priceContainer.roviAnimateLayoutChanges()
        binding.close.setOnClickListener { viewModel.exit() }
        with(binding.tariffsPicker) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PriceDialog.adapter.apply {
                viewModel.onPricesSelected(retrieveChosenTariffs())
            }
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        adapter.setOnTariffClick { prices -> viewModel.onPricesSelected(prices) }
        binding.totalCost.setOnClickListener {
            val prices = adapter.retrieveChosenTariffs()
            if (prices.isNotEmpty()) {
                listener?.onPricesSelected(prices)
                dismiss()
            }
//            viewModel.pay(adapter.retrieveChosenTariffs())
        }
        viewModel.retrievePrices()
    }

    override fun onDialogShow(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        bottomSheetDialog.setupFullHeight(activity)
    }

    override fun onSheetStateChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_EXPANDED)
            binding.toolbarGroup.isVisible = true
        else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
            viewModel.exit()
        if (newState == BottomSheetBehavior.STATE_HIDDEN) viewModel.exit()
    }

    private val exitObserver: Observer<Unit> = Observer {
        dismiss()
    }
    private val onPricesObserver: Observer<List<TariffData>> = Observer { newTariffs ->
        tariffs.roviClear().addAll(newTariffs)
        adapter.notifyDataSetChanged()
        val animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.tariffsPicker.layoutAnimation = animation
    }

    private val onPricesSelectedObserver: Observer<List<String>> = Observer { prices ->
        if (prices.isEmpty()) {
            binding.total.text = getString(R.string.x_sum_str, "0")
            return@Observer
        }
        var total = 0
        prices.forEach { price ->
            val oneMonth = tariffs.firstOrNull { it.oneMonthTariff?.id == price }?.oneMonthTariff
            val threeMonth =
                tariffs.firstOrNull { it.threeMonthTariff?.id == price }?.threeMonthTariff
            val sixMonth = tariffs.firstOrNull { it.sixMonthTariff?.id == price }?.sixMonthTariff
            when {
                oneMonth != null -> {
                    total += oneMonth.price
                }
                threeMonth != null -> {
                    total += threeMonth.price
                }
                sixMonth != null -> {
                    total += sixMonth.price
                }
            }

            binding.total.text = getString(R.string.x_sum_str, total.money())
        }
    }

    private val tariffNotChosenObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_tariff_not_chosen))
    }

    private val boughtObserver: EventObserver<Unit> = EventObserver {
        roviSuccess(getString(R.string.success_bought))
        viewModel.exit()
    }

    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        viewModel.exit()
    }

    private val roleNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_role_not_found))
        viewModel.exit()
    }

    private val balanceNotEnoughObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_balance_not_enough))
    }

    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
        viewModel.exit()
    }

    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
        viewModel.exit()
    }

    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
        viewModel.exit()
    }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    fun interface PricesSelectedListener {
        fun onPricesSelected(prices: List<String>)
    }
}
