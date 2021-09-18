package uz.invan.rovitalk.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.invan.rovitalk.util.ktx.addStateChangeListener
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard
import java.util.concurrent.atomic.AtomicBoolean


abstract class RoundedBottomSheetDialogFragment<T : ViewBinding>(@StyleRes val sheetTheme: Int) :
    BottomSheetDialogFragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    private val visibility = AtomicBoolean(false)
    private var progress: RoviProgressDialog? = null

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val controller by lazy { findNavController() }

    override fun getTheme(): Int = sheetTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.addStateChangeListener { newState ->
                if (newState == BottomSheetBehavior.STATE_HIDDEN) visibility.set(false)
                onSheetStateChanged(newState)
            }
            onDialogShow(it)
        }

        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = setBinding()
        return binding.root
    }

    abstract fun setBinding(): T

    @Deprecated(
        "This method deprecated due to clean code reasons",
        ReplaceWith("onViewReady()"),
        DeprecationLevel.ERROR
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onObserve()
        onViewReady()
    }

    @CallSuper
    override fun onDismiss(dialog: DialogInterface) {
        dismissProgress()
        visibility.set(false)
        super.onDismiss(dialog)
    }

    open fun onObserve() {}

    open fun onViewReady() {}

    open fun onDialogShow(dialogInterface: DialogInterface) {}

    abstract fun onSheetStateChanged(newState: Int)

    override fun show(manager: FragmentManager, tag: String?) {
        if (visibility.compareAndSet(false, true)) super.show(manager, tag)
    }

    /**
     * Show's full screen progress in [RoundedBottomSheetDialogFragment] children
     * */
    fun showProgress() {
        progress = RoviProgressDialog()
        progress?.show(parentFragmentManager, null)
    }

    /**
     * Dismisses full screen dialog if [progress] not null and hides soft keyboard
     * */
    fun dismissProgress() {
        progress?.dismiss()
        progress = null
        hideSoftKeyboard()
    }
}