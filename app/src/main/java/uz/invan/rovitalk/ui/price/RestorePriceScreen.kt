package uz.invan.rovitalk.ui.price

import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.databinding.ScreenRestorePriceBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.roviActivity

@AndroidEntryPoint
class RestorePriceScreen :
    BaseScreen<ScreenRestorePriceBinding>(false, direction = null) {
    private val viewModel by viewModels<RestorePriceViewModel>()

    override fun setBinding() = ScreenRestorePriceBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
    }

    override fun onViewAttach() {
        binding.close.setOnClickListener { viewModel.exit() }
    }

    private val exitObserver: Observer<Unit> = Observer {
        exit()
    }

    override fun onResume() {
        super.onResume()
        roviActivity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()
        roviActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}