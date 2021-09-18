package uz.invan.rovitalk.ui.feed.podcasts.icons

import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.FeedPodcastsIconsBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.ui.feed.section.ItemFeedLayoutManager
import uz.invan.rovitalk.util.custom.decoration.ItemFeedPodcastsIconsItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear

class FeedPodcastsIconsVH(
    private val binding: FeedPodcastsIconsBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedPodcastsIconsParams!! }
    private val categories = arrayListOf<FeedCategory>()
    private val adapter by lazy { ItemFeedPodcastsIconsAdapter(categories, params.callback) }

    override fun onInit(item: FeedConfig) {
        categories.roviClear().addAll(params.categories)

        with(binding.itemsPodcastsIcons) {
            layoutManager = ItemFeedLayoutManager(params.itemsPerPage, params.itemsOffset, context)
            adapter = this@FeedPodcastsIconsVH.adapter
            isNestedScrollingEnabled = false
            addItemDecoration(ItemFeedPodcastsIconsItemDecoration(context.px(params.itemsOffset)))
        }
    }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        binding.textTitle.text = params.title
    }
}