package uz.invan.rovitalk.ui.audio

import android.graphics.Color
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.FeedSubCategory
import uz.invan.rovitalk.databinding.ScreenAudiosBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.*
import uz.invan.rovitalk.util.lifecycle.EventObserver
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class AudiosScreen : BaseScreen<ScreenAudiosBinding>(isBottomBarVisible = false, direction = null) {
    private val viewModel by viewModels<AudiosViewModel>()
    private val audiosArgs by navArgs<AudiosScreenArgs>()
    private val subCategories = arrayListOf<FeedSubCategory>()
    private val adapter by lazy { AudiosAdapter(subCategories) }

    private val isAnimatedSubCategories = AtomicBoolean(false)

    override fun setBinding() = ScreenAudiosBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.onSubCategories.observe(viewLifecycleOwner, onAudiosObserver)
        viewModel.animateSubCategories.observe(viewLifecycleOwner, animateCategoriesObserver)
        viewModel.onImage.observe(viewLifecycleOwner, onImageObserver)
        viewModel.sub.observe(viewLifecycleOwner, subObserver)
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
        binding.close.setOnClickListener { exit() }
        viewModel.retrieveAudios(audiosArgs.category.id)
        viewModel.image(audiosArgs.category.image)
        binding.textAbout.text = audiosArgs.category.description
        binding.textHeader.text = audiosArgs.category.header
        binding.textSubHeader.text = audiosArgs.category.subHeader
    }

    private val onAudiosObserver: Observer<List<FeedSubCategory>> = Observer { newSubCategories ->
        subCategories.roviClear().addAll(newSubCategories)
        with(binding.audios) {
            adapter = this@AudiosScreen.adapter
            layoutManager = LinearLayoutManager(context)
        }
        adapter.setOnAudioClick { subCategory -> viewModel.sub(subCategory) }
        if (isAnimatedSubCategories.compareAndSet(false, true))
            viewModel.animateSubCategories()
    }
    private val animateCategoriesObserver: EventObserver<Unit> = EventObserver {
        val animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)
        binding.audios.layoutAnimation = animation
    }

    private val onImageObserver: Observer<String> = Observer { image ->
        Glide.with(binding.image)
            .asBitmap()
            .transition(BitmapTransitionOptions.withCrossFade())
            .load(image)
            .onBitmap { bitmap ->
                binding.image.setImageBitmap(bitmap)
                Palette.from(bitmap).generate { palette ->
                    palette?.getDominantColor(Color.WHITE)?.let { color ->
                        binding.collapsingToolbar.setContentScrimColor(color)
                    }
                }
            }
    }

    private val subObserver: EventObserver<FeedSubCategory> = EventObserver { subCategory ->
        if (subCategory.count == 0) {
            roviError(getString(R.string.sub_category_empty))
            return@EventObserver
        }
        controller.navigate(
            AudiosScreenDirections.navigatePlayerFromAudios(audiosArgs.category, subCategory)
        )
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
    }

    private val forbiddenObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_forbidden))
        exit()
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

    override fun onResume() {
        super.onResume()
        roviActivity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onDestroy() {
        roviActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        super.onDestroy()
    }
}