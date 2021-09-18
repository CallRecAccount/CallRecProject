package uz.invan.rovitalk.ui.feed.podcasts.vertical

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.ItemFeedPodcastsVerticalBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px

class ItemFeedPodcastsVerticalAdapter(
    val podcasts: List<FeedCategory>,
    private val callback: FeedConfig.PodcastsVerticalClickListener,
) :
    RecyclerView.Adapter<ItemFeedPodcastsVerticalAdapter.ItemFeedPodcastsVerticalVH>() {
    inner class ItemFeedPodcastsVerticalVH(private val binding: ItemFeedPodcastsVerticalBinding) :
        BaseVH<FeedCategory>(binding.root) {
        override fun onInit(item: FeedCategory) {
            binding.feedPodcastsVertical.setOnClickListener {
                callback.onPodcastsVerticalClick(podcasts[bindingAdapterPosition])
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
            binding.textPodcastLength.text = context.getString(R.string.x_count, item.count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedPodcastsVerticalVH {
        val binding = ItemFeedPodcastsVerticalBinding.inflate(parent.layoutInflater, parent, false)
        return ItemFeedPodcastsVerticalVH(binding)
    }

    override fun getItemCount() = podcasts.size

    override fun onBindViewHolder(holder: ItemFeedPodcastsVerticalVH, position: Int) =
        holder.onBind(podcasts[position])
}