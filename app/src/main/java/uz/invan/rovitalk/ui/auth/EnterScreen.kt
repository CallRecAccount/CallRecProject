package uz.invan.rovitalk.ui.auth

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.validation.exceptions.LengthCredentialData
import uz.invan.rovitalk.databinding.ScreenEnterBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.custom.setOnDebounceClickListener
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class EnterScreen : BaseScreen<ScreenEnterBinding>(false, null) {
    private val viewModel by viewModels<EnterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun setBinding() = ScreenEnterBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.navigateVideo.observe(viewLifecycleOwner, navigateVideoObserver)
        viewModel.navigateRegister.observe(viewLifecycleOwner, navigateRegisterObserver)
        viewModel.navigateVerify.observe(viewLifecycleOwner, navigateVerifyObserver)
        viewModel.codeSent.observe(viewLifecycleOwner, codeSentObserver)
        viewModel.incorrectPhone.observe(viewLifecycleOwner, incorrectPhoneObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.tooManyAttempts.observe(viewLifecycleOwner, tooManyAttemptsObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        binding.ccp.registerCarrierNumberEditText(binding.editablePhoneNumber)
        binding.login.setOnDebounceClickListener {
            viewModel.login(binding.ccp.fullNumber, binding.ccp.isValidFullNumber)
        }
        binding.textStart.setOnClickListener {
            viewModel.introduction()
        }
        binding.version.text = getString(R.string.app_version_x, BuildConfig.VERSION_NAME)
    }

    private val navigateVideoObserver: EventObserver<String> = EventObserver { video ->
        controller.navigate(RoviNavigationDirections.moveVideoScreen(video))
    }

    private val navigateRegisterObserver: EventObserver<Unit> = EventObserver {
        hideSoftKeyboard()
        controller.navigate(EnterScreenDirections.moveRegister(binding.ccp.fullNumber))
    }

    private val navigateVerifyObserver: EventObserver<Unit> = EventObserver {
        hideSoftKeyboard()
        controller.navigate(EnterScreenDirections.navigateVerify(binding.ccp.fullNumber))
    }

    private val codeSentObserver: EventObserver<String> = EventObserver { phone ->
        roviSuccess(getString(R.string.success_otp_sent, phone))
    }

    private val incorrectPhoneObserver: EventObserver<LengthCredentialData> = EventObserver {
        roviError(getString(R.string.exception_wrong_phone_length))
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_validation))
    }

    private val tooManyAttemptsObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_too_many_attempts))
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
}