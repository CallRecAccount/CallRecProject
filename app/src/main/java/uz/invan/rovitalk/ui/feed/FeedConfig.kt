package uz.invan.rovitalk.ui.feed

import androidx.annotation.FloatRange
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.ui.feed.FeedConfig.*

class FeedConfig(val items: ArrayList<FeedItems>) {
    enum class FeedTypes {
        SECTIONS_GRID, SECTION, PODCASTS_ICONS, PODCASTS_GRID, PODCASTS_VERTICAL, BUY, MEDIA, HEADER, MY_TARIFFS, NONE
    }

    data class FeedItems(
        val type: FeedTypes,
        val feedSectionsGridParams: FeedSectionGridParams? = null,
        val feedSectionParams: FeedSectionParams? = null,
        val feedPodcastsIconsParams: FeedPodcastsIconsParams? = null,
        val feedPodcastsGridParams: FeedPodcastsGridParams? = null,
        val feedBuyParams: FeedBuyParams? = null,
        val feedMediaParams: FeedMediaParams? = null,
        val feedHeaderParams: FeedHeaderParams? = null,
        val feedMyTariffsParams: FeedMyTariffsParams? = null,
        val feedPodcastsVerticalParams: FeedPodcastsVerticalParams? = null,
    )

    fun interface SectionGridClickListener {
        fun onSectionGridClick(section: FeedSection)
    }

    data class FeedSectionGridParams(
        @FloatRange(from = 0.0) val itemsOffset: Float = 16f,
        val sections: List<FeedSection>,
        val callback: SectionGridClickListener = SectionGridClickListener { },
    )

    fun interface SectionMoreClickListener {
        fun onSectionMoreClick()
    }

    fun interface ItemSectionClickListener {
        fun onItemSectionClick(category: FeedCategory)
    }

    data class FeedSectionParams(
        @FloatRange(from = 0.0) val itemsPerPage: Float = 2.5f,
        @FloatRange(from = 0.0) val itemsOffset: Float = 16f,
        val section: FeedSection,
        val moreCallback: SectionMoreClickListener = SectionMoreClickListener { },
        val callback: ItemSectionClickListener = ItemSectionClickListener { },
    )

    fun interface PodcastsIconClickListener {
        fun onPodcastsIconClick(category: FeedCategory)
    }

    data class FeedPodcastsIconsParams(
        @FloatRange(from = 0.0) val itemsPerPage: Float = 2.5f,
        @FloatRange(from = 0.0) val itemsOffset: Float = 16f,
        val title: String,
        val categories: List<FeedCategory>,
        val callback: PodcastsIconClickListener,
    )

    fun interface PodcastClickListener {
        fun onPodcastClick(category: FeedCategory)
    }

    data class FeedPodcastsGridParams(
        @FloatRange(from = 0.0) val itemsOffset: Float = 24f,
        val categories: List<FeedCategory>,
        val callback: PodcastClickListener,
    )

    fun interface BuyClickListener {
        fun onBuyClick()
    }

    data class FeedBuyParams(
        val callback: BuyClickListener = BuyClickListener { },
    )

    enum class FeedMediaTypes(val ratio: String) {
        VIDEO("16:9"), IMAGE("1:1")
    }

    fun interface MediaClickListener {
        fun onMediaClick(category: FeedCategory?)
    }

    data class FeedMediaParams(
        val type: FeedMediaTypes = FeedMediaTypes.VIDEO,
        val cover: String,
        val title: String? = null,
        val subtitle: String? = null,
        val category: FeedCategory? = null,
        val callback: MediaClickListener,
    )

    data class FeedHeaderParams(
        val title: String,
    )

    data class FeedMyTariffsParams(
        @FloatRange(from = 0.0) val itemsOffset: Float = 8f,
        val myTariffsHeader: String,
        val sections: List<FeedSection>,
    )

    data class FeedPodcastsVerticalParams(
        @FloatRange(from = 0.0) val itemsOffset: Float = 0f,
        val title: String,
        val categories: List<FeedCategory>,
        val callback: PodcastsVerticalClickListener,
    )

    fun interface PodcastsVerticalClickListener {
        fun onPodcastsVerticalClick(category: FeedCategory)
    }
}