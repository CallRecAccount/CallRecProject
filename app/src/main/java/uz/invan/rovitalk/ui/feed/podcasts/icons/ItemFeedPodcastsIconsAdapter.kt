package uz.invan.rovitalk.ui.feed.podcasts.icons

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.Purchase
import uz.invan.rovitalk.databinding.ItemFeedPodcastsIconsBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px

class ItemFeedPodcastsIconsAdapter(
    private val categories: List<FeedCategory>,
    private val callback: FeedConfig.PodcastsIconClickListener,
) :
    RecyclerView.Adapter<ItemFeedPodcastsIconsAdapter.ItemFeedPodcastsItemsVH>() {
    inner class ItemFeedPodcastsItemsVH(private val binding: ItemFeedPodcastsIconsBinding) :
        BaseVH<FeedCategory>(binding.root) {
        override fun onInit(item: FeedCategory) {
            binding.podcastImage.setOnClickListener {
                callback.onPodcastsIconClick(categories[bindingAdapterPosition])
            }
        }

        override fun onBind(item: FeedCategory) {
            super.onBind(item)

            val requestOptions =
                RequestOptions().transform(CenterCrop(), RoundedCorners(context.px(12)))
            Glide.with(context)
                .load(item.image)
                .apply(requestOptions)
                .into(binding.podcastImage)
            when (item.purchaseType) {
                Purchase.FREE.type -> binding.free.isVisible = true
                Purchase.PAID.type -> if (!item.isActive) binding.lock.isVisible = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedPodcastsItemsVH {
        val binding = ItemFeedPodcastsIconsBinding.inflate(parent.layoutInflater, parent, false)
        return ItemFeedPodcastsItemsVH(binding)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ItemFeedPodcastsItemsVH, position: Int) =
        holder.onBind(categories[position])
}