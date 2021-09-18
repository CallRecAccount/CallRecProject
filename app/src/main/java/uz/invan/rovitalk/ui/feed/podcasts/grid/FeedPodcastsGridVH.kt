package uz.invan.rovitalk.ui.feed.podcasts.grid

import androidx.recyclerview.widget.GridLayoutManager
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.FeedPodcastsGridBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.decoration.ItemFeedPodcastsGridItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear

class FeedPodcastsGridVH(
    private val binding: FeedPodcastsGridBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedPodcastsGridParams!! }
    private val podcasts = arrayListOf<FeedCategory>()
    private val adapter by lazy { ItemFeedPodcastsGridAdapter(podcasts, params.callback) }

    override fun onInit(item: FeedConfig) {
        podcasts.roviClear().addAll(params.categories)

        with(binding.podcasts) {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = this@FeedPodcastsGridVH.adapter
            isNestedScrollingEnabled = false
            addItemDecoration(ItemFeedPodcastsGridItemDecoration(context.px(params.itemsOffset)))
        }
    }
}