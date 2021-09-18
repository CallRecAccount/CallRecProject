package uz.invan.rovitalk.ui.feed

import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.data.models.feed.Purchase
import uz.invan.rovitalk.util.SearchManager

object FeedConstructor {
    fun buildFeedHome(builderHome: FeedHomeBuilderData): ArrayList<FeedConfig.FeedItems> {
        val configItems = arrayListOf<FeedConfig.FeedItems>()
        configItems.add(
            FeedConfig.FeedItems(
                type = FeedConfig.FeedTypes.SECTIONS_GRID,
                feedSectionsGridParams = FeedConfig.FeedSectionGridParams(
                    // filter section by main(shows only main)
                    sections = builderHome.gridSections.sections.filter { it.isMain },
                    callback = builderHome.gridSections.gridSectionCallback
                )
            )
        )
        if (builderHome.buy.sections.all { !it.isActive }) {
            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.BUY,
                    feedBuyParams = FeedConfig.FeedBuyParams(
                        callback = builderHome.buy.buyCallback
                    )
                )
            )
        }
        configItems.add(
            FeedConfig.FeedItems(
                type = FeedConfig.FeedTypes.MEDIA,
                feedMediaParams = FeedConfig.FeedMediaParams(
                    type = FeedConfig.FeedMediaTypes.VIDEO,
                    cover = builderHome.video.cover,
                    title = builderHome.video.title,
                    subtitle = builderHome.video.subtitle,
                    callback = builderHome.video.callback
                )
            )
        )
        builderHome.boughtSections.firstOrNull()?.let { builderSection ->
            // filter sections
            val filteredSection = builderSection.section.filter()

            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.SECTION,
                    feedSectionParams = FeedConfig.FeedSectionParams(
                        section = filteredSection,
                        moreCallback = builderSection.moreCallback,
                        callback = builderSection.callback
                    )
                )
            )
        }

        if (builderHome.bigMedia.category != null) {
            configItems.add(FeedConfig.FeedItems(
                type = FeedConfig.FeedTypes.MEDIA,
                feedMediaParams = FeedConfig.FeedMediaParams(
                    type = FeedConfig.FeedMediaTypes.IMAGE,
                    cover = builderHome.bigMedia.cover,
                    title = builderHome.bigMedia.title,
                    subtitle = builderHome.bigMedia.subtitle,
                    category = builderHome.bigMedia.category,
                    callback = builderHome.bigMedia.callback
                )
            ))
        }
        if (builderHome.mostListened.categories.isNotEmpty()) {
            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.PODCASTS_ICONS,
                    feedPodcastsIconsParams = FeedConfig.FeedPodcastsIconsParams(
                        title = builderHome.mostListened.title,
                        categories = builderHome.mostListened.categories,
                        callback = builderHome.mostListened.callback
                    )
                )
            )
        }
        // free space for player
        configItems.add(FeedConfig.FeedItems(type = FeedConfig.FeedTypes.NONE))

        return configItems
    }

    private fun FeedSection.filter(): FeedSection {
        return copy(categories = categories
            // filter not banner
            .filter { !it.isBanner }
            // sort by created time descending
            .sortedBy { it.createdAt }
            .sortedByDescending { it.purchaseType == Purchase.FREE.type }
        )
    }

    fun buildFeedSection(builderPodcasts: FeedPodcastsBuilderData): List<FeedConfig.FeedItems> {
        val configItems = arrayListOf<FeedConfig.FeedItems>()
        builderPodcasts.builderSections.forEachIndexed { index, builderSection ->
            // filter sections
            val filteredSection = builderSection.section.filter()
            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.SECTION,
                    feedSectionParams = FeedConfig.FeedSectionParams(
                        section = filteredSection,
                        moreCallback = builderSection.moreCallback,
                        callback = builderSection.callback
                    )
                )
            )
            if (!filteredSection.isActive && index in 0..1) {
                configItems.add(
                    FeedConfig.FeedItems(
                        type = FeedConfig.FeedTypes.BUY,
                        feedBuyParams = FeedConfig.FeedBuyParams(
                            callback = builderSection.buySectionCallback
                        )
                    )
                )
            }
        }

        // free space for player
        configItems.add(FeedConfig.FeedItems(type = FeedConfig.FeedTypes.NONE))

        return configItems
    }

    fun buildFeedSearch(
        builderPodcasts: FeedPodcastsBuilderData,
        query: String,
    ): List<FeedConfig.FeedItems> {
        val configItems = arrayListOf<FeedConfig.FeedItems>()
        if (query.trim().isEmpty()) return configItems

        builderPodcasts.builderSections.forEach { builderSection ->
            // filter sections
            val filteredSection = builderSection.section.filter()

            val section = search(filteredSection, query)
            if (section.categories.isNotEmpty()) {
                configItems.add(
                    FeedConfig.FeedItems(
                        type = FeedConfig.FeedTypes.SECTION,
                        feedSectionParams = FeedConfig.FeedSectionParams(
                            section = section,
                            moreCallback = builderSection.moreCallback,
                            callback = builderSection.callback
                        )
                    )
                )
            }
        }
        return configItems
    }

    private fun search(section: FeedSection, query: String): FeedSection {
        val categories = arrayListOf<FeedCategory>()
        section.categories.forEach { category ->
            if (SearchManager.search(query, category.title))
                categories.add(category)
        }
//        section.categories.filter { category-> SearchManager.search(query, category.title) }
        return section.copy(categories = categories)
    }

    fun buildFeedInSection(builderInSection: FeedInSectionBuilder): List<FeedConfig.FeedItems> {
        val configItems = arrayListOf<FeedConfig.FeedItems>()
        // filter section
        val filteredSection = builderInSection.section.filter()

        configItems.add(
            FeedConfig.FeedItems(
                FeedConfig.FeedTypes.HEADER,
                feedHeaderParams = FeedConfig.FeedHeaderParams(builderInSection.section.title)
            )
        )
        configItems.add(
            FeedConfig.FeedItems(
                type = FeedConfig.FeedTypes.PODCASTS_GRID,
                feedPodcastsGridParams = FeedConfig.FeedPodcastsGridParams(
                    categories = filteredSection.categories,
                    callback = builderInSection.callback
                )
            )
        )

        return configItems
    }

    fun buildProfile(builderProfile: FeedProfileBuilder): List<FeedConfig.FeedItems> {
        val configItems = arrayListOf<FeedConfig.FeedItems>()

        if (builderProfile.myTariffs.sections.isNotEmpty()) {
            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.MY_TARIFFS,
                    feedMyTariffsParams = FeedConfig.FeedMyTariffsParams(
                        myTariffsHeader = builderProfile.myTariffs.header,
                        sections = builderProfile.myTariffs.sections
                    )
                )
            )
        }
        configItems.add(
            FeedConfig.FeedItems(
                type = FeedConfig.FeedTypes.BUY,
                feedBuyParams = FeedConfig.FeedBuyParams(
                    callback = builderProfile.buy.buyCallback
                )
            )
        )

        if (builderProfile.lastListened.categories.isNotEmpty()) {
            configItems.add(
                FeedConfig.FeedItems(
                    type = FeedConfig.FeedTypes.PODCASTS_VERTICAL,
                    feedPodcastsVerticalParams = FeedConfig.FeedPodcastsVerticalParams(
                        title = builderProfile.lastListened.header,
                        categories = builderProfile.lastListened.categories,
                        callback = builderProfile.lastListened.callback
                    )
                )
            )
        }

        // free space for player
        configItems.add(FeedConfig.FeedItems(type = FeedConfig.FeedTypes.NONE))

        return configItems
    }

    data class FeedHomeBuilderData(
        val gridSections: FeedSectionsGridBuilderData,
        val buy: FeedBuyBuilderData,
        val video: FeedPodcastsVideoBuilderData,
        val boughtSections: List<FeedSectionBuilderData>,
        val bigMedia: FeedBigMediaBuilderData,
        val mostListened: FeedPodcastsIconsBuilderData,
    )

    data class FeedSectionsGridBuilderData(
        val sections: List<FeedSection>,
        val gridSectionCallback: FeedConfig.SectionGridClickListener,
    )

    data class FeedBuyBuilderData(
        val sections: List<FeedSection>,
        val buyCallback: FeedConfig.BuyClickListener,
    )

    data class FeedPodcastsVideoBuilderData(
        val cover: String,
        val title: String?,
        val subtitle: String,
        val callback: FeedConfig.MediaClickListener,
    )

    data class FeedBigMediaBuilderData(
        val category: FeedCategory?,
        val cover: String,
        val title: String,
        val subtitle: String?,
        val callback: FeedConfig.MediaClickListener,
    )

    data class FeedPodcastsIconsBuilderData(
        val title: String,
        val categories: List<FeedCategory>,
        val callback: FeedConfig.PodcastsIconClickListener,
    )

    data class FeedPodcastsBuilderData(
        val builderSections: List<FeedSectionBuilderData>,
    )

    data class FeedSectionBuilderData(
        val section: FeedSection,
        val moreCallback: FeedConfig.SectionMoreClickListener,
        val callback: FeedConfig.ItemSectionClickListener,
        val buySectionCallback: FeedConfig.BuyClickListener,
    )

    data class FeedInSectionBuilder(
        val section: FeedSection,
        val callback: FeedConfig.PodcastClickListener,
    )

    data class FeedProfileBuilder(
        val myTariffs: FeedMyTariffsBuilder,
        val buy: FeedBuyBuilderData,
        val lastListened: FeedLastListenedBuilder,
    )

    data class FeedMyTariffsBuilder(
        val header: String,
        val sections: List<FeedSection>,
    )

    data class FeedLastListenedBuilder(
        val header: String,
        val categories: List<FeedCategory>,
        val callback: FeedConfig.PodcastsVerticalClickListener,
    )
}