package uz.invan.rovitalk.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import uz.invan.rovitalk.R
import uz.invan.rovitalk.ui.activity.ActivityViewModel
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard

abstract class BaseScreen<T : ViewBinding> constructor(
    private val isBottomBarVisible: Boolean,
    private val direction: MainDirections?,
) : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    private var progress: RoviProgressDialog? = null

    protected val activityViewModel by activityViewModels<ActivityViewModel>()
    protected val controller by lazy { findNavController() }

    private val updateStatusBarColor = MutableLiveData<Int>()
    private val updateNavigationColor = MutableLiveData<Int>()

    private fun initBottomBar() {
        if (isBottomBarVisible && direction != null) activityViewModel.initBottomBar()
    }

    private fun changeBottomBarVisibility() {
        activityViewModel.changeBottomBarVisibility(isBottomBarVisible)
    }

    private fun selectBottomBar() {
        direction?.let { activityViewModel.selectBottomBar(it) }
    }

    private fun initPlayer() {
        if (isBottomBarVisible)
            activityViewModel.showPlayerFooter()
        else
            activityViewModel.disableFooter()
    }

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
        savedInstanceState: Bundle?,
    ): View? {
        initBottomBar()
        changeBottomBarVisibility()
        selectBottomBar()
        initPlayer()
        _binding = setBinding()
        return binding.root
    }

    /**
     * [BaseScreen] instance have to override this method and return binding,
     * binding used in [onCreateView] while creating view
     * @return [T] - child of [ViewBinding] generated by AS
     * */
    abstract fun setBinding(): T

    @Deprecated(
        "This method deprecated due to clean code reasons",
        ReplaceWith("onViewReady()"),
        DeprecationLevel.ERROR
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateColors()
        attachObservers()
        onViewAttach()
        updateStatusBarColor.observe(viewLifecycleOwner, { color ->
            activity?.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = color
            }
        })
        updateNavigationColor.observe(viewLifecycleOwner, { color ->
            activity?.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                navigationBarColor = color
            }
        })
    }

    /**
     * if instance of [BaseScreen] sets observers, writes inside this method
     * called [onViewCreated]
     * */
    open fun attachObservers() {}

    /**
     * Sets all [Fragment] start operations and click listeners inside this method
     * called [onViewCreated], after [attachObservers]
     * */
    open fun onViewAttach() {}

    /**
     * If [BaseScreen] instance overrides below two methods, status bar changes to return type of this methods
     * @return: Color of status bar and navigation bar
     * called first when [onViewCreated]
     * */
    open fun statusColor(): Int = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
    open fun navigationColor(): Int =
        ContextCompat.getColor(requireContext(), R.color.black)

    /**
     * Updates status and navigation bar colors to same color
     * @param color - color which status and navigation bars should be colored
     * */
    fun updateColors(@ColorInt color: Int) {
        updateStatusBarColor.value = color
        updateNavigationColor.value = color
    }

    /**
     * Updates status bar and navigation bar colors
     * sets color which returned in [statusColor] and [navigationColor]
     * */
    private fun updateColors() {
        updateStatusBarColor.value = statusColor()
        updateNavigationColor.value = navigationColor()
    }

    /**
     * Show's full screen progress in [BaseScreen] children
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

    /**
     * This return type indicated, whether back button should be blocked or not
     * @return [Boolean] - if (true) blocks back press else not
     * @default returns false
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /**
     * Calls [getActivity]'s [onBackPressed] method
     * */
    fun exit() = activity?.onBackPressed()

    override fun onDestroyView() {
        super.onDestroyView()
        progress = null
        _binding = null
    }
}