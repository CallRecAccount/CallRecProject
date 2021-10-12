package uz.invan.rovitalk.ui.section

import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.databinding.ScreenPodcastsBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.ui.feed.FeedAdapter
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.ui.feed.FeedConstructor
import uz.invan.rovitalk.ui.price.PriceDialog
import uz.invan.rovitalk.util.custom.decoration.FeedItemDecoration
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.lifecycle.EventObserver
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class PodcastsScreen : BaseScreen<ScreenPodcastsBinding>(true, direction = MainDirections.SECTION) {
    private val viewModel by viewModels<PodcastsViewModel>()
    private val config = FeedConfig(arrayListOf())
    private val adapter by lazy { FeedAdapter(config) }
    private val podcastsArgs by navArgs<PodcastsScreenArgs>()

    private var isAnimated = AtomicBoolean(false)

    override fun setBinding() = ScreenPodcastsBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.exit.observe(viewLifecycleOwner, exitObserver)
        viewModel.onCategories.observe(viewLifecycleOwner, onCategoriesObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateSmallLoader.observe(viewLifecycleOwner, updateSmallLoaderObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
    }

    override fun onViewAttach() {
        with(binding.feedPodcasts) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PodcastsScreen.adapter
            addItemDecoration(FeedItemDecoration(1f))
        }
        binding.back.setOnClickListener { viewModel.exit() }
        viewModel.retrieveCategories(podcastsArgs.categoryId)
    }

    private val exitObserver: EventObserver<Unit> = EventObserver {
        exit()
    }

    private val onCategoriesObserver: Observer<FeedSection> = Observer { sectionAndItsCategories ->
        val builderData = FeedConstructor.FeedInSectionBuilder(
            section = sectionAndItsCategories,
            callback = { category ->
                if (category.count == 0) {
                    roviError(getString(R.string.category_empty))
                    return@FeedInSectionBuilder
                }
                if (category.isActive) {
                    when (category.type) {
                        Category.PLAYER.type -> controller.navigate(
                            PodcastsScreenDirections.navigatePlayerFromPodcasts(category)
                        )
                        Category.LIST.type -> controller.navigate(
                            PodcastsScreenDirections.navigateAudiosFromPodcasts(category)
                        )
                    }
                } else PriceDialog().apply {
                    setOnPricesSelectedListener {
                        this@PodcastsScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                            it.toPricesString())
                        )
                    }
                }.show(parentFragmentManager, null)
            }
        )
        val configItems = FeedConstructor.buildFeedInSection(builderData)
        config.items.roviClear().addAll(configItems)
        adapter.notifyDataSetChanged()
        if (isAnimated.compareAndSet(false, true)) {
            val animation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)
            binding.feedPodcasts.layoutAnimation = animation
        }
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

    private val unknownErrorObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_unknown_exception))
    }

    private val noInternetObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_no_internet))
    }

    private val timeOutObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_network_time_out))
    }

    private val updateSmallLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) binding.podcasts.setText(R.string.updating)
        else binding.podcasts.setText(R.string.podcasts)
    }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    override fun onBackPressed(): Boolean {
        controller.navigate(RoviNavigationDirections.moveSectionMenuFromProfile())
        return true
    }
}