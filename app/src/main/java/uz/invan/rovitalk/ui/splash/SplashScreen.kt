package uz.invan.rovitalk.ui.splash

import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.ScreenSplashBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.roviActivity
import uz.invan.rovitalk.util.ktx.roviContext
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class SplashScreen : BaseScreen<ScreenSplashBinding>(false, null) {
    private val viewModel by viewModels<SplashViewModel>()

    override fun setBinding() = ScreenSplashBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.moveToEnter.observe(viewLifecycleOwner, moveToEnterObserver)
        viewModel.moveToHome.observe(viewLifecycleOwner, moveToHomeObserver)
    }

    override fun onViewAttach() {
        /*roviActivity.window?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(roviContext, R.color.colorPrimaryDark))
        )*/
        viewModel.navigate()
    }

    override fun statusColor() = ContextCompat.getColor(requireContext(), R.color.deluge)
    override fun navigationColor() =
        ContextCompat.getColor(requireContext(), R.color.victoria)

    private val moveToEnterObserver: EventObserver<Unit> = EventObserver {
        binding.roviAnimation.isGone = true
        /*val extras = FragmentNavigatorExtras(
            binding.imageLogo to getString(R.string.rovi_animation)
        )*/
        controller.navigate(SplashScreenDirections.toAuthScreen())
    }

    private val moveToHomeObserver: EventObserver<Unit> = EventObserver {
        controller.navigate(SplashScreenDirections.moveHome())
    }
}