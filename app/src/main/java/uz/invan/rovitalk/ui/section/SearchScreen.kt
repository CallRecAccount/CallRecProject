package uz.invan.rovitalk.ui.section

import androidx.annotation.RawRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.RoviNavigationDirections
import uz.invan.rovitalk.data.models.feed.Category
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.price.toPricesString
import uz.invan.rovitalk.databinding.ScreenSearchBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.ui.feed.FeedAdapter
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.ui.feed.FeedConstructor
import uz.invan.rovitalk.ui.price.PriceDialog
import uz.invan.rovitalk.util.custom.decoration.FeedItemDecoration
import uz.invan.rovitalk.util.ktx.hideSoftKeyboard
import uz.invan.rovitalk.util.ktx.roviClear
import uz.invan.rovitalk.util.ktx.roviError
import uz.invan.rovitalk.util.ktx.showSoftKeyboard

@AndroidEntryPoint
class SearchScreen : BaseScreen<ScreenSearchBinding>(false, null) {
    private val viewModel: SearchViewModel by viewModels()
    private val config = FeedConfig(arrayListOf())
    private val adapter by lazy { FeedAdapter(config) }

    @RawRes
    private var anim = R.raw.search

    override fun setBinding() = ScreenSearchBinding.inflate(layoutInflater)

    override fun attachObservers() {
        viewModel.onQuery.observe(viewLifecycleOwner, onQueryObserver)
        viewModel.empty.observe(viewLifecycleOwner, emptyObserver)
        viewModel.notFound.observe(viewLifecycleOwner, notFoundObserver)
    }

    override fun onViewAttach() {
        binding.editableSearch.requestFocus()
        showSoftKeyboard()

        binding.editableSearch.addTextChangedListener {
            viewModel.onQuery(it.toString())
        }

        with(binding.feedSearch) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchScreen.adapter
            addItemDecoration(FeedItemDecoration(48f))
        }
    }

    private val onQueryObserver: Observer<Pair<String, List<FeedSection>>> = Observer { pair ->
        val query = pair.first
        val sections = pair.second
        Timber.tag("MY_TAG").d("Sections: $sections")

        val builderData = FeedConstructor.FeedPodcastsBuilderData(
            sections.map { section ->
                FeedConstructor.FeedSectionBuilderData(
                    section = section,
                    moreCallback = {
                        controller.navigate(
                            SearchScreenDirections.navigatePodcastsFromSearch(section.id)
                        )
                    },
                    callback = { category ->
                        if (category.count == 0) {
                            roviError(getString(R.string.category_empty))
                            return@FeedSectionBuilderData
                        }
                        if (category.isActive) {
                            when (category.type) {
                                Category.PLAYER.type -> controller.navigate(
                                    SearchScreenDirections.navigatePlayerFromSearch(category)
                                )
                                Category.LIST.type -> controller.navigate(
                                    SearchScreenDirections.navigateAudiosFromSearch(category)
                                )
                            }
                        } else PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@SearchScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                    it.toPricesString())
                                )
                            }
                        }.show(parentFragmentManager, null)
                    },
                    buySectionCallback = {
                        PriceDialog().apply {
                            setOnPricesSelectedListener {
                                this@SearchScreen.controller.navigate(RoviNavigationDirections.paymentSystems(
                                    it.toPricesString())
                                )
                            }
                        }.show(parentFragmentManager, null)
                    }
                )
            }
        )
        val configItems = FeedConstructor.buildFeedSearch(builderData, query)
        config.items.roviClear().addAll(configItems)
        with(binding.feedSearch) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SearchScreen.adapter
        }

        binding.lottie.isVisible = configItems.isEmpty()
        binding.feedSearch.isVisible = configItems.isNotEmpty()
        if (configItems.isEmpty()) {
            if (binding.editableSearch.text?.isEmpty() == true) viewModel.empty()
            else viewModel.notFound()
        }
    }
    private val emptyObserver: Observer<Unit> = Observer {
        binding.lottie.isVisible = true
        if (anim == R.raw.search) return@Observer

        anim = R.raw.search
        with(binding.lottie) {
            setAnimation(anim)
            playAnimation()
        }
    }
    private val notFoundObserver: Observer<Unit> = Observer {
        binding.lottie.isVisible = true
        if (anim == R.raw.search_not_found) return@Observer

        anim = R.raw.search_not_found
        with(binding.lottie) {
            isVisible = true
            setAnimation(anim)
            playAnimation()
        }
    }

    override fun onStop() {
        super.onStop()
        hideSoftKeyboard()
    }
}