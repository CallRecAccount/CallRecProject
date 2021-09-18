package uz.invan.rovitalk.ui.profile.qr

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.databinding.ScreenQrBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.coroutineIO
import uz.invan.rovitalk.util.ktx.roviActivity
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class QRScreen : BaseScreen<ScreenQrBinding>(false, null) {
    private val viewModel by viewModels<QRViewModel>()
    private lateinit var scanner: CodeScanner

    override fun setBinding() = ScreenQrBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.activated.observe(viewLifecycleOwner, activatedObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.qrNotFound.observe(viewLifecycleOwner, qrNotFoundObserver)
        viewModel.qrAlreadyExists.observe(viewLifecycleOwner, qrAlreadyExistsObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        scanner = CodeScanner(roviActivity, binding.scanner)
        scanner.decodeCallback = DecodeCallback {
            viewModel.qr(it.text)
        }
        binding.scanner.setOnClickListener { scanner.startPreview() }
        coroutineIO {
            delay(1000)
            scanner.startPreview()
        }
    }

    private val activatedObserver: EventObserver<Unit> = EventObserver {
        roviSuccess(getString(R.string.success_qr))
        exit()
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_qr_not_found))
    }

    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        controller.navigate(RoviNavigationDirections.splash())
    }

    private val qrNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_qr_not_found))
    }

    private val qrAlreadyExistsObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_qr_already_exists))
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