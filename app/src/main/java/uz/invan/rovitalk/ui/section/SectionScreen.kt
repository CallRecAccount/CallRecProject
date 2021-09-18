package uz.invan.rovitalk.ui.section

import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.databinding.ScreenSectionBinding
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
class SectionScreen : BaseScreen<ScreenSectionBinding>(true, direction = MainDirections.SECTION) {
    private val viewModel: SectionViewModel by viewModels()
    private val config = FeedConfig(arrayListOf())
    private val adapter by lazy { FeedAdapter(config) }

    private var isAnimated = AtomicBoolean(false)

    override fun setBinding() = ScreenSectionBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.onSectionsAndCategories.observe(viewLifecycleOwner, onSectionsAndCategories)
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
        binding.search.setOnClickListener { controller.navigate(SectionScreenDirections.navigateSearch()) }
        with(binding.feedSection) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SectionScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }
        viewModel.retrieveSectionsAndCategories()
    }

    private val onSectionsAndCategories: Observer<List<FeedSection>> = Observer { sections ->
        val builderData = FeedConstructor.FeedPodcastsBuilderData(sections.map { section ->
            FeedConstructor.FeedSectionBuilderData(
                section = section,
                moreCallback = { controller.navigate(SectionScreenDirections.movePodcasts(section.id)) },
                callback = { category ->
                    if (category.count == 0) {
                        roviError(getString(R.string.category_empty))
                        return@FeedSectionBuilderData
                    }
                    if (category.isActive) {
                        when (category.type) {
                            Category.PLAYER.type -> controller.navigate(
                                SectionScreenDirections.navigatePlayerFromSection(category)
                            )
                            Category.LIST.type -> controller.navigate(
                                SectionScreenDirections.navigateAudiosFromSection(category)
                            )
                        }
                    } else PriceDialog().apply {
                        setOnPricesSelectedListener {
                            this@SectionScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                it.toPricesString())
                            )
                        }
                    }.show(parentFragmentManager, null)
                },
                buySectionCallback = {
                    PriceDialog().apply {
                        setOnPricesSelectedListener {
                            this@SectionScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                it.toPricesString())
                            )
                        }
                    }.show(parentFragmentManager, null)
                }
            )
        })
        val configItems = FeedConstructor.buildFeedSection(builderData)
        config.items.roviClear().addAll(configItems)
        adapter.notifyDataSetChanged()
        if (isAnimated.compareAndSet(false, true)) {
            val animation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            binding.feedSection.layoutAnimation = animation
        }
    }

    private val incorrectValidationObserver: EventObserver<Unit> = EventObserver {
        roviError(getString(R.string.exception_could_not_retrieve_data))
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

    private val updateSmallLoaderObserver: EventObserver<Boolean> =
        EventObserver { isLoaderVisible ->
            if (isLoaderVisible) binding.podcasts.setText(R.string.updating)
            else binding.podcasts.setText(R.string.podcasts)
        }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }

    override fun onBackPressed(): Boolean {
        controller.navigate(RoviNavigationDirections.moveHomeMenu())
        return true
    }
}