package uz.invan.rovitalk.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.invan.rovitalk.ui.activity.ActivityViewModel

abstract class BaseBottomSheetDialogScreen<T : ViewBinding>() : BottomSheetDialogFragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    private var root: View? = null

    protected val activityViewModel by activityViewModels<ActivityViewModel>()
    protected val controller by lazy { findNavController() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this) {
            if (!onBackPressed()) {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return root ?: run {
            _binding = setBinding()
            root = binding.root
            binding.root
        }
    }

    abstract fun setBinding(): T

    @Deprecated(
        "This method deprecated due to clean code reasons",
        ReplaceWith("onViewReady()"),
        DeprecationLevel.ERROR
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewReady()
    }

    abstract fun onViewReady()

    // This return type indicated, whether back button should be blocked or not
    open fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (view?.parent != null) (view?.parent as ViewGroup).removeView(view)
    }
}