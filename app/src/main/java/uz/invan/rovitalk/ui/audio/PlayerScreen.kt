package uz.invan.rovitalk.ui.audio

import android.content.pm.PackageManager
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.audio.PlayerAudio
import uz.invan.rovitalk.data.models.audio.PlayerBM
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.databinding.ScreenPlayerBinding
import uz.invan.rovitalk.ui.activity.PlayersControllerViewModel
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.*
import uz.invan.rovitalk.util.lifecycle.EventObserver

@AndroidEntryPoint
class PlayerScreen : BaseScreen<ScreenPlayerBinding>(false, null) {
    private val viewModel: PlayerViewModel by viewModels()
    private val playersControllerViewModel: PlayersControllerViewModel by activityViewModels()
    private val playerArgs by navArgs<PlayerScreenArgs>()
    private val audios = arrayListOf<PlayerAudio>()
    private val playerAdapter by lazy { PlayerAdapter(roviActivity, audios) }
    private val bmsData = arrayListOf<PlayerBM>()
    private val bmsAdapter by lazy { BackgroundAudiosAdapter(bmsData) }

    override fun setBinding() = ScreenPlayerBinding.inflate(layoutInflater)

    override fun attachObservers() {
        playersControllerViewModel.control.observe(viewLifecycleOwner, controlObserver)
        playersControllerViewModel.controlBM.observe(viewLifecycleOwner, controlBMObserver)

        viewModel.onAudios.observe(viewLifecycleOwner, onAudiosObserver)
//        viewModel.onImage.observe(viewLifecycleOwner, onImageObserver)
        viewModel.initializeBMs.observe(viewLifecycleOwner, initializeBMsObserver)
        viewModel.updateBM.observe(viewLifecycleOwner, updateBMObserver)
        viewModel.askPermissions.observe(viewLifecycleOwner, askPermissionsObserver)
        viewModel.showPermissionDialog.observe(viewLifecycleOwner, showPermissionDialogObserver)
        viewModel.requestPermissions.observe(viewLifecycleOwner, requestPermissionsObserver)
        viewModel.permissionRecommended.observe(viewLifecycleOwner, permissionRecommendedObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.forbidden.observe(viewLifecycleOwner, forbiddenObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        viewModel.loadPlayer(playerArgs.category.id, playerArgs.subCategory?.id)
        binding.close.setOnClickListener {
            exit()
        }
        binding.scrollLayout.listen(
            onShow = { binding.scrollBackgroundAudios.isEnabled = true },
            onDismiss = { binding.scrollBackgroundAudios.isEnabled = false }
        )
        binding.voiceController.setOnSeekingListener { volume ->
            playersControllerViewModel.updateVolume(volume)
        }
        binding.voiceReset.setOnClickListener {
            binding.voiceController.setProgress(Credentials.MAX_VOLUME / 2f)
        }

        val isPermissionGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        viewModel.permission(isPermissionGranted)
    }

    private val controlObserver: Observer<Pair<Int, Boolean>> = Observer { pair ->
        val categoryId = playerArgs.subCategory?.category()?.id ?: playerArgs.category.id
        if (playersControllerViewModel.currentCategory?.id != categoryId) return@Observer

        val isPlaying = pair.second
        if (isPlaying) viewModel.saveForCurrent(
            playerArgs.subCategory?.category()
                ?: playerArgs.category
        )
        else viewModel.deleteForCurrent()

        // move if position changed
        val position = pair.first
        if (position >= audios.size || binding.player.currentItem == position) return@Observer

        binding.player.currentItem = position
    }
    private val controlBMObserver: Observer<Int> = Observer { index ->
        if (bmsData.isNotEmpty()) {
            bmsAdapter.setCurrentItem(index)
            binding.scrollBackgroundAudios.scrollToPosition(index)
        }
    }

    private val onAudiosObserver: Observer<List<PlayerAudio>> = Observer { newAudios ->
        audios.roviClear().addAll(newAudios)
        playersControllerViewModel.prepareAudios(
            audios,
            playerArgs.subCategory?.category() ?: playerArgs.category
        )
        with(binding.player) {
            adapter = playerAdapter
            orientation = ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                val offset: Float = position * -(2 * px(30) + px(30))
                if (ViewCompat.getLayoutDirection(page) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            }
        }
    }

    private val initializeBMsObserver: Observer<List<PlayerBM>> = Observer { bms ->
        // making voice invisible
        binding.textAmbient.isInvisible = true
        binding.textVocal.isInvisible = true
        binding.voiceReset.isInvisible = true
        binding.voiceController.isInvisible = true

        bmsData.roviClear().addAll(bms)
        playersControllerViewModel.submitBMs(bmsData, playerArgs.category)
        with(binding.scrollBackgroundAudios) {
            adapter = bmsAdapter
            setOrientation(DSVOrientation.HORIZONTAL)
            setItemTransformer(
                ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.8f)
                    .setPivotX(Pivot.X.CENTER)
                    .setPivotY(Pivot.Y.CENTER)
                    .build()
            )
            addOnItemChangedListener { _, adapterPosition ->
                binding.textAmbient.isInvisible = adapterPosition == 0
                binding.textVocal.isInvisible = adapterPosition == 0
                binding.voiceReset.isInvisible = adapterPosition == 0
                binding.voiceController.isInvisible = adapterPosition == 0
                playersControllerViewModel.controlBM(adapterPosition, playerArgs.category.id)
                bmsAdapter.setCurrentItem(adapterPosition)
            }
            layoutAnimation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_right)
            addScrollStateChangeListener<PlayerBM>(
                onScrollStart = { binding.scrollLayout.onScrollStart() },
                onScrollEnd = { binding.scrollLayout.onScrollFinish() }
            )
        }
        // setup scroll layout
        binding.scrollLayout.listen(
            onShow = { bmsAdapter.showAll() },
            onDismiss = { bmsAdapter.dismiss() }
        )
    }

    private val updateBMObserver: Observer<PlayerBM> = Observer { bm ->
        val index = bmsData.indexOfFirst { it.id == bm.id }
        bmsData[index] = bm/*.copy(visibility = BMVisibility.INVISIBLE)*/
        /*bmsAdapter.notifyItemChanged(index)*/
        playersControllerViewModel.updateBM(index, bm)
    }

    private val askPermissionsObserver: EventObserver<Unit> = EventObserver {
        val shouldShowRationale = REQUIRED_PERMISSIONS.all { shouldShowRequestPermissionRationale(it) }

        if (shouldShowRationale) viewModel.showPermissionDialog()
        else viewModel.requestPermissions()
    }

    private val showPermissionDialogObserver: EventObserver<Unit> = EventObserver {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.enable_permissions))
            .setContentText(getString(R.string.enable_call_state_permissions))
            .setCancelText(getString(R.string.not_enable_permissions))
            .setConfirmText(getString(R.string.enable))
            .setCancelButtonBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange)
            )
            .setCancelClickListener {
                it.dismissWithAnimation()
                viewModel.permissionRecommended()
            }
            .setConfirmButtonBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.fern)
            )
            .setConfirmClickListener {
                it.dismissWithAnimation()
                viewModel.requestPermissions()
            }
            .show()
    }

    private val requestPermissionsObserver: EventObserver<Unit> = EventObserver {
        requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
    }

    private val permissionRecommendedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_permissions_recommended))
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_could_not_retrieve_data))
        exit()
    }

    private val unauthorizedObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unauthorized))
        controller.navigate(RoviNavigationDirections.splash())
    }

    private val roleNotFoundObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_role_not_found))
//        if (audios.isEmpty())
//            exit()
    }

    private val forbiddenObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_forbidden))
        exit()
    }

    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
//        if (audios.isEmpty())
//            exit()
    }

    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
//        if (audios.isEmpty())
//            exit()
    }

    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
//        if (audios.isEmpty())
//            exit()
    }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    private val onImageObserver: Observer<String> = Observer { image ->
        Glide.with(binding.blur)
            .asBitmap()
            .load(image)
            .onBitmap { bitmap ->
                Blurry.with(context)
                    .radius(32)
                    .sampling(16)
                    .animate()
                    .async()
                    .from(bitmap)
                    .into(binding.blur)
                Palette.from(bitmap).generate { palette ->
                    palette?.getDominantColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )?.let { color -> updateColors(color) }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        roviActivity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop() {
        super.onStop()
        if (playerArgs.subCategory == null)
            roviActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onBackPressed(): Boolean {
        playersControllerViewModel.releasePlayerIfNotPlaying(playerArgs.category)
        viewModel.onRelease()
        return super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            val isPermissionGranted = REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
            }

            if (!isPermissionGranted)
                viewModel.permissionRecommended()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 23
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.READ_PHONE_STATE)
    }
}