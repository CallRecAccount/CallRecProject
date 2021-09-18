package uz.invan.rovitalk.ui.feed.podcasts.vertical

import androidx.recyclerview.widget.LinearLayoutManager
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.FeedPodcastsVerticalBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.decoration.ItemFeedPodcastsVerticalItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear

class FeedPodcastsVerticalVH(
    private val binding: FeedPodcastsVerticalBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedPodcastsVerticalParams!! }
    private val podcasts = arrayListOf<FeedCategory>()
    private val adapter by lazy { ItemFeedPodcastsVerticalAdapter(podcasts, params.callback) }

    override fun onInit(item: FeedConfig) {
        super.onInit(item)
        podcasts.roviClear().addAll(params.categories)

        with(binding.podcasts) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FeedPodcastsVerticalVH.adapter
            isNestedScrollingEnabled = false
            addItemDecoration(ItemFeedPodcastsVerticalItemDecoration(context.px(params.itemsOffset)))
        }
    }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        binding.textPodcastsHeader.text = params.title
    }
}