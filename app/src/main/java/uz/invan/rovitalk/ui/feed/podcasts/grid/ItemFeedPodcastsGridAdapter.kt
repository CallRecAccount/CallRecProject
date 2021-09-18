package uz.invan.rovitalk.ui.feed.podcasts.grid

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.Purchase
import uz.invan.rovitalk.databinding.ItemFeedPodcastsGridBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px

class ItemFeedPodcastsGridAdapter(
    private val podcasts: List<FeedCategory>,
    val callback: FeedConfig.PodcastClickListener,
) : RecyclerView.Adapter<ItemFeedPodcastsGridAdapter.ItemFeedPodcastsGridVH>() {
    inner class ItemFeedPodcastsGridVH(val binding: ItemFeedPodcastsGridBinding) :
        BaseVH<FeedCategory>(binding.root) {
        override fun onInit(item: FeedCategory) {
            super.onInit(item)
            binding.imagePodcast.setOnClickListener {
                if (bindingAdapterPosition >= 0 && podcasts.isNotEmpty()) callback.onPodcastClick(podcasts[bindingAdapterPosition])
            }
        }

        override fun onBind(item: FeedCategory) {
            super.onBind(item)

            val requestOptions =
                RequestOptions().transform(CenterCrop(), RoundedCorners(context.px(12)))
            Glide.with(context)
                .load(item.image)
                .apply(requestOptions)
                .into(binding.imagePodcast)
            binding.textPodcastTitle.text = item.title
            binding.textSectionName.text = item.sectionTitle
            binding.textPodcastLength.text = context.getString(
                R.string.x_count, item.count
            )
            when (item.purchaseType) {
                Purchase.FREE.type -> binding.free.isVisible = true
                Purchase.PAID.type -> if (!item.isActive) binding.lock.isVisible = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedPodcastsGridVH {
        val binding = ItemFeedPodcastsGridBinding.inflate(parent.layoutInflater, parent, false)
        return ItemFeedPodcastsGridVH(binding)
    }

    override fun getItemCount() = podcasts.size

    override fun onBindViewHolder(holder: ItemFeedPodcastsGridVH, position: Int) =
        holder.onBind(podcasts[position])
}