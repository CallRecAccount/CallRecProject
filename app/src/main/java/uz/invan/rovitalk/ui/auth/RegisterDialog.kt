package uz.invan.rovitalk.ui.auth

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.DialogRegisterBinding
import uz.invan.rovitalk.ui.base.RoundedBottomSheetDialogFragment
import uz.invan.rovitalk.util.lifecycle.EventObserver

class RegisterDialog :
    RoundedBottomSheetDialogFragment<DialogRegisterBinding>(R.style.SharkBottomSheetDialogTheme) {
    private val viewModel by viewModels<RegisterViewModel>()

    override fun setBinding() = DialogRegisterBinding.inflate(layoutInflater)

    override fun onObserve() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
//        viewModel.register.observe(viewLifecycleOwner, registerObserver)
//        viewModel.navigateRegister.observe(viewLifecycleOwner, navigateRegisterObserver)
    }

    override fun onViewReady() {
        binding.back.setOnClickListener { viewModel.exit() }
        binding.textCancel.setOnClickListener { viewModel.exit() }
//        binding.register.setOnClickListener { viewModel.register() }
    }

    override fun onSheetStateChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
    }

    private val exitObserver: Observer<Unit> = Observer {
        dismiss()
    }

    private val registerObserver: Observer<Unit> = Observer {}
//    private val navigateRegisterObserver: EventObserver<Unit> =
//        EventObserver { controller.navigate(RegisterScreenDirections.moveHomeFromAuth()) }
}