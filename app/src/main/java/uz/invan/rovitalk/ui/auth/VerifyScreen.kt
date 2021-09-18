package uz.invan.rovitalk.ui.auth

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.validation.exceptions.LengthCredentialData
import uz.invan.rovitalk.databinding.ScreenVerifyBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.ktx.stringText
import uz.invan.rovitalk.util.lifecycle.EventObserver

@Suppress("DEPRECATION")
@AndroidEntryPoint
class VerifyScreen : BaseScreen<ScreenVerifyBinding>(isBottomBarVisible = false, direction = null) {
    private val viewModel by viewModels<VerifyViewModel>()
    private val verifyArgs by navArgs<VerifyScreenArgs>()

    override fun setBinding() = ScreenVerifyBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.initializeVerification.observe(viewLifecycleOwner, initializeVerificationObserver)
        viewModel.initializeTimer.observe(viewLifecycleOwner, initializeTimerObserver)
        viewModel.dismissVerification.observe(viewLifecycleOwner, dismissVerificationObserver)
        viewModel.refreshVerification.observe(viewLifecycleOwner, refreshVerificationObserver)
        viewModel.navigateHome.observe(viewLifecycleOwner, navigateHomeObserver)
        viewModel.codeSent.observe(viewLifecycleOwner, codeSentObserver)
        viewModel.incorrectPhone.observe(viewLifecycleOwner, incorrectPhoneObserver)
        viewModel.incorrectOtp.observe(viewLifecycleOwner, incorrectOtpObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.userNotFound.observe(viewLifecycleOwner, userNotFoundObserver)
        viewModel.tooManyAttempts.observe(viewLifecycleOwner, tooManyAttemptsObserver)
        viewModel.wrongOtp.observe(viewLifecycleOwner, wrongOtpObserver)
        viewModel.otpExpired.observe(viewLifecycleOwner, otpExpiredObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        binding.otp.setOtpCompletionListener { viewModel.verifyOtp(verifyArgs.phone, it) }
        binding.verify.setOnClickListener {
            viewModel.verifyOtp(
                phone = verifyArgs.phone,
                otp = binding.otp.stringText
            )
        }
        binding.resend.setOnClickListener { viewModel.resend(phone = verifyArgs.phone) }
    }

    private val initializeVerificationObserver: Observer<Unit> = Observer {
/*        // sets resend code text with html in k-code, because html used in R.string.resend_code
        binding.textResendCode.text =
            HtmlCompat.fromHtml(getString(R.string.resend_code), HtmlCompat.FROM_HTML_MODE_LEGACY)*/
        binding.otp.isVisible = true
        binding.timer.isVisible = true
        binding.resend.isGone = true
    }

    private val initializeTimerObserver: Observer<Long> = Observer { timeInMillis ->
        binding.timer.start(timeInMillis, binding.timerText) { viewModel.dismissVerification() }
    }

    private val dismissVerificationObserver: Observer<Unit> = Observer {
        binding.resend.isVisible = true
        binding.otp.isGone = true
        binding.timer.isGone = true
    }

    private val refreshVerificationObserver: Observer<Long> = Observer { timeInMillis ->
        binding.timer.start(timeInMillis, binding.timerText) { viewModel.dismissVerification() }
        binding.otp.isVisible = true
        binding.timer.isVisible = true
        binding.resend.isGone = true
    }

    private val navigateHomeObserver: EventObserver<Unit> = EventObserver {
        activityViewModel.retrieveNotifications()
        hideSoftKeyboard()
        controller.navigate(VerifyScreenDirections.navigateHome())
    }

    private val codeSentObserver: EventObserver<String> = EventObserver { phone ->
        roviSuccess(getString(R.string.success_otp_sent, phone))
    }

    private val incorrectPhoneObserver: EventObserver<LengthCredentialData> = EventObserver {
        roviError(getString(R.string.exception_wrong_phone_length))
    }

    private val incorrectOtpObserver: EventObserver<LengthCredentialData> = EventObserver { otp ->
        roviError(getString(R.string.exception_wrong_otp_length, otp.length))
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_validation))
    }

    private val userNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_user_not_found))
    }

    private val tooManyAttemptsObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_too_many_attempts))
    }

    private val wrongOtpObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_wrong_otp))
        binding.otp.text = null
        // animate shake otp
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        binding.otp.startAnimation(shake)
        // make vibrate phone
        val v = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v?.vibrate(1000)
        }
    }

    private val otpExpiredObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_otp_expired))
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