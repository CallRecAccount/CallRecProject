package uz.invan.rovitalk.ui.home

import android.os.Environment
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.databinding.ScreenHomeBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.base.MainDirections
import uz.invan.rovitalk.ui.feed.FeedAdapter
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.ui.feed.FeedConstructor
import uz.invan.rovitalk.ui.price.PriceDialog
import uz.invan.rovitalk.util.custom.decoration.FeedItemDecoration
import uz.invan.rovitalk.util.ktx.into
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.roviSuccess
import uz.invan.rovitalk.util.lifecycle.EventObserver
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class HomeScreen : BaseScreen<ScreenHomeBinding>(true, MainDirections.HOME) {
    private val viewModel by viewModels<HomeViewModel>()
    private val config = FeedConfig(arrayListOf())
    private val adapter by lazy { FeedAdapter(config) }
    private val storyAdapter = StoryAdapter()
    private val bannerAdapter = BannerAdapter()
    private var introduction: String? = null

    private var isAnimated = AtomicBoolean(false)

    override fun setBinding() = ScreenHomeBinding.inflate(layoutInflater)
    override fun attachObservers() {
        viewModel.setPoster.observe(viewLifecycleOwner, setPosterObserver)
        viewModel.activated.observe(viewLifecycleOwner, activatedObserver)
        viewModel.onSections.observe(viewLifecycleOwner, onSectionsObserver)
        viewModel.onIntroduction.observe(viewLifecycleOwner, onIntroductionObserver)
        viewModel.incorrectValidation.observe(viewLifecycleOwner, incorrectValidationObserver)
        viewModel.unauthorized.observe(viewLifecycleOwner, unauthorizedObserver)
        viewModel.roleNotFound.observe(viewLifecycleOwner, roleNotFoundObserver)
        viewModel.qrNotFound.observe(viewLifecycleOwner, qrNotFoundObserver)
        viewModel.qrAlreadyExists.observe(viewLifecycleOwner, qrAlreadyExistsObserver)
        viewModel.unknownError.observe(viewLifecycleOwner, unknownErrorObserver)
        viewModel.noInternet.observe(viewLifecycleOwner, noInternetObserver)
        viewModel.timeOut.observe(viewLifecycleOwner, timeOutObserver)
        viewModel.updateSmallLoader.observe(viewLifecycleOwner, updateSmallLoaderObserver)
        viewModel.updateLoader.observe(viewLifecycleOwner, updateLoaderObserver)
        viewModel.toCategories.observe(viewLifecycleOwner, toCategoriesObserver)
    }

    override fun onViewAttach() {
        viewModel.setPoster()
        with(binding.feed) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }
        with(binding.story) {
            adapter = storyAdapter
        }
        with(binding.banner) {
            adapter = bannerAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
        viewModel.loadHome()
        binding.books.setOnClickListener {
            viewModel.toBooks()
        }
        storyAdapter.onClick {
            controller.navigate(HomeScreenDirections.toStoryScreen())
        }
    }

    private val setPosterObserver: Observer<String> = Observer { posterUrl ->
        Glide.with(this)
            .load(posterUrl)
            .into(binding.assalamuAlaykum)
    }

    private val activatedObserver: EventObserver<Unit> = EventObserver {
        roviSuccess(getString(R.string.success_qr))
        controller.navigate(RoviNavigationDirections.moveProfileMenu())
    }

    private val onSectionsObserver: Observer<List<FeedSection>> = Observer { sections ->
        val poster = sections.flatMap { it.categories }.firstOrNull { it.isBanner }

        val builderData = FeedConstructor.buildFeedHome(
            builderHome = FeedConstructor.FeedHomeBuilderData(
//                stories = FeedConstructor.StoryBuilderData(),
                gridSections = FeedConstructor.FeedSectionsGridBuilderData(
                    sections = sections,
                    gridSectionCallback = { section ->
                        controller.navigate(HomeScreenDirections.movePodcasts(section.id))
                    }
                ),
                buy = FeedConstructor.FeedBuyBuilderData(
                    sections = sections,
                    buyCallback = {
                        PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@HomeScreen.controller.navigate(
                                    RoviNavigationDirections.paymentSystems(
                                        it.toPricesString()
                                    )
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                ),
                video = FeedConstructor.FeedPodcastsVideoBuilderData(
                    cover = introduction ?: "",
                    title = getString(R.string.new_to_rovi),
                    subtitle = getString(R.string.learn_how_to_use),
                    callback = {
                        controller.navigate(HomeScreenDirections.moveVideoScreen(introduction))
                    }
                ),
                boughtSections = sections.map { section ->
                    FeedConstructor.FeedSectionBuilderData(
                        section = section,
                        moreCallback = {
                            controller.navigate(HomeScreenDirections.movePodcasts(section.id))
                        },
                        callback = { category ->
                            if (category.count == 0) {
                                roviError(getString(R.string.category_empty))
                                return@FeedSectionBuilderData
                            }
                            if (category.isActive) {
                                when (category.type) {
                                    Category.PLAYER.type -> controller.navigate(
                                        HomeScreenDirections.navigatePlayerFromHome(category)
                                    )
                                    Category.LIST.type -> controller.navigate(
                                        HomeScreenDirections.navigateAudiosFromHome(category)
                                    )
                                }
                            } else PriceDialog().apply {
                                setOnPricesSelectedListener {
                                    this@HomeScreen.controller.navigate(
                                        RoviNavigationDirections.paymentSystems(
                                            it.toPricesString()
                                        )
                                    )
                                }
                            }.show(parentFragmentManager, null)
                        },
                        buySectionCallback = {}
                    )
                },
                bigMedia = FeedConstructor.FeedBigMediaBuilderData(
                    category = poster,
                    cover = poster?.image ?: "",
                    title = poster?.title ?: "",
                    subtitle = poster?.subHeader,
                    callback = { category ->
                        if (category == null) return@FeedBigMediaBuilderData

                        if (category.count == 0) {
                            roviError(getString(R.string.category_empty))
                            return@FeedBigMediaBuilderData
                        }
                        if (category.isActive) {
                            when (category.type) {
                                Category.PLAYER.type -> controller.navigate(
                                    HomeScreenDirections.navigatePlayerFromHome(category)
                                )
                                Category.LIST.type -> controller.navigate(
                                    HomeScreenDirections.navigateAudiosFromHome(category)
                                )
                            }
                        } else PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@HomeScreen.controller.navigate(
                                    RoviNavigationDirections.paymentSystems(
                                        it.toPricesString()
                                    )
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                ),
                mostListened = FeedConstructor.FeedPodcastsIconsBuilderData(
                    title = getString(R.string.most_listened),
                    categories = sections.flatMap { it.categories }.filter { it.mostListened },
                    callback = { category ->
                        if (category.count == 0) {
                            roviError(getString(R.string.category_empty))
                            return@FeedPodcastsIconsBuilderData
                        }
                        if (category.isActive) {
                            when (category.type) {
                                Category.PLAYER.type -> controller.navigate(
                                    HomeScreenDirections.navigatePlayerFromHome(category)
                                )
                                Category.LIST.type -> controller.navigate(
                                    HomeScreenDirections.navigateAudiosFromHome(category)
                                )
                            }
                        } else PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@HomeScreen.controller.navigate(
                                    RoviNavigationDirections.paymentSystems(
                                        it.toPricesString()
                                    )
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                )
            )
        )
        config.items.roviClear().addAll(builderData)
        adapter.notifyDataSetChanged()
        if (isAnimated.compareAndSet(false, true)) {
            val animation =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            binding.feed.layoutAnimation = animation
        }
    }

    private val onIntroductionObserver: Observer<String> = Observer {
        introduction = it
    }

/*    private val initializeFeedObserver: Observer<Unit> = Observer {

        *//*config.items.roviClear().addAll(
            arrayListOf(
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.SECTIONS_GRID,
                    feedSectionsGridParams = FeedConfig.FeedSectionGridParams(callback = {
//                        controller.navigate(HomeScreenDirections.movePodcasts())
                    }, sections = emptyList())
                ),
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.BUY,
                    feedBuyParams = FeedConfig.FeedBuyParams(callback = {
                        PriceDialog.getInstance().show(parentFragmentManager, null)
//                        controller.navigate(RoviNavigationDirections.openPrice())
                    })
                ),
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.MEDIA,
                    feedMediaParams = FeedConfig.FeedMediaParams(
                        FeedConfig.FeedMediaTypes.VIDEO,
                        "",
                        "Roviga qaytganingiz bilan",
                        "Qanday foydalanish kerak?", callback = {
//                            controller.navigate(RoviNavigationDirections.moveVideoScreen())
                        }
                    )
                ),
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.SECTION,
                    feedSectionParams = FeedConfig.FeedSectionParams(moreCallback = {
//                        controller.navigate(HomeScreenDirections.movePodcasts())
                    })
                ),
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.MEDIA,
                    feedMediaParams = FeedConfig.FeedMediaParams(
                        FeedConfig.FeedMediaTypes.IMAGE,
                        "",
                        "Tongi mashq, har kun uchun"
                    )
                ),
                FeedConfig.FeedItems(
                    FeedConfig.FeedTypes.PODCASTS_ICONS,
                    feedPodcastsIconsParams = FeedConfig.FeedPodcastsIconsParams(
                        title = getString(R.string.most_listened)
                    )
                )
            )
        )
        with(binding.feed) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }*//*
    }*/

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

    private val updateSmallLoaderObserver: EventObserver<Boolean> =
        EventObserver { isLoaderVisible ->
            if (isLoaderVisible) roviSuccess(getString(R.string.updating))
        }

    private val updateLoaderObserver: Observer<Boolean> = Observer { isLoaderVisible ->
        if (isLoaderVisible) showProgress()
        else dismissProgress()
    }
    private val toCategoriesObserver = EventObserver<Unit> {
        controller.navigate(HomeScreenDirections.toCategoriesScreen())
    }
}