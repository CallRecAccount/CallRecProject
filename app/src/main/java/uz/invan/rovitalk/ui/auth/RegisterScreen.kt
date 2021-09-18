package uz.invan.rovitalk.ui.auth

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.validation.exceptions.LengthCredentialData
import uz.invan.rovitalk.databinding.ScreenRegisterBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.custom.setOnDebounceClickListener
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.ktx.stringText
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class RegisterScreen : BaseScreen<ScreenRegisterBinding>(false, null) {
    private val viewModel by viewModels<RegisterViewModel>()
    private val registerArgs by navArgs<RegisterScreenArgs>()

    override fun setBinding() = ScreenRegisterBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.terms.observe(viewLifecycleOwner, termsObserver)
        viewModel.navigateVideo.observe(viewLifecycleOwner, navigateVideoObserver)
        viewModel.navigateVerify.observe(viewLifecycleOwner, navigateVerifyObserver)
        viewModel.codeSent.observe(viewLifecycleOwner, codeSentObserver)
        viewModel.incorrectPhone.observe(viewLifecycleOwner, incorrectPhoneObserver)
        viewModel.incorrectFirstName.observe(viewLifecycleOwner, incorrectFirstNameObserver)
        viewModel.incorrectLastName.observe(viewLifecycleOwner, incorrectLastNameObserver)
        viewModel.incorrectAgreement.observe(viewLifecycleOwner, incorrectAgreementObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.userAlreadyExists.observe(viewLifecycleOwner, userAlreadyExistsObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        binding.registerContainer.setOnClickListener { viewModel.exit() }
        binding.back.setOnClickListener { viewModel.exit() }
        binding.textCancel.setOnClickListener { viewModel.exit() }
        binding.textTermsAndConditions.setOnClickListener { viewModel.termsAndConditions() }
        binding.register.setOnDebounceClickListener {
            viewModel.register(
                phone = registerArgs.phone,
                firstName = binding.editableFirstName.stringText,
                lastName = binding.editableLastName.stringText,
                isAgreed = binding.checkableTermsAndConditions.isChecked
            )
        }
        binding.enter.setOnClickListener { viewModel.exit() }
        binding.textInstruction.setOnClickListener {
            viewModel.introduction()
        }
    }

    private val exitObserver: Observer<Unit> = Observer {
        exit()
    }

    private val termsObserver: EventObserver<String> = EventObserver { terms ->
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(terms))
    }

    private val navigateVideoObserver: EventObserver<String> = EventObserver { video ->
        controller.navigate(RoviNavigationDirections.moveVideoScreen(video))
    }

    private val navigateVerifyObserver: EventObserver<Unit> = EventObserver {
        hideSoftKeyboard()
        controller.navigate(RegisterScreenDirections.navigateVerify(registerArgs.phone))
    }

    private val codeSentObserver: EventObserver<String> = EventObserver { phone ->
        roviSuccess(getString(R.string.success_otp_sent, phone))
    }

    private val incorrectPhoneObserver: EventObserver<LengthCredentialData> = EventObserver {
        roviError(getString(R.string.exception_wrong_phone_length))
    }

    private val incorrectFirstNameObserver: EventObserver<LengthCredentialData> =
        EventObserver { firstName ->
            roviError(getString(R.string.exception_wrong_first_name_length, firstName.length))
        }

    private val incorrectLastNameObserver: EventObserver<LengthCredentialData> =
        EventObserver { lastName ->
            roviError(getString(R.string.exception_wrong_last_name_length, lastName.length))
        }

    private val incorrectAgreementObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_agreement))
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_validation))
    }

    private val userAlreadyExistsObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_user_already_exists))
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