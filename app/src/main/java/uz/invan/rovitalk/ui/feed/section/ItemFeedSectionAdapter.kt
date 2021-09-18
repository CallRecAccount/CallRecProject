package uz.invan.rovitalk.ui.feed.section

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.Purchase
import uz.invan.rovitalk.databinding.ItemFeedSectionBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px

class ItemFeedSectionAdapter(
    val sectionItems: List<FeedCategory>,
    val callback: FeedConfig.ItemSectionClickListener,
) :
    RecyclerView.Adapter<ItemFeedSectionAdapter.ItemFeedSectionVH>() {
    inner class ItemFeedSectionVH(private val binding: ItemFeedSectionBinding) :
        BaseVH<FeedCategory>(binding.root) {
        override fun onInit(item: FeedCategory) {
            super.onInit(item)
            binding.imageSectionItem.setOnClickListener {
                callback.onItemSectionClick(sectionItems[bindingAdapterPosition])
            }
        }

        override fun onBind(item: FeedCategory) {
            super.onBind(item)

            val requestOptions =
                RequestOptions().transform(CenterCrop(), RoundedCorners(context.px(12)))
            Glide.with(context)
                .load(item.image)
                .apply(requestOptions)
                .into(binding.imageSectionItem)
            binding.textSectionItemTitle.text = item.title
            binding.textSectionName.text = item.sectionTitle
            binding.textSectionItemLength.text =
                context.getString(R.string.x_count, item.count)
            when (item.purchaseType) {
                Purchase.FREE.type -> {
                    binding.free.isVisible = true
                    binding.lock.isGone = true
                }
                Purchase.PAID.type -> {
                    if (!item.isActive) binding.lock.isVisible = true
                    else binding.lock.isGone = true
                    binding.free.isGone = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedSectionVH {
        val binding = ItemFeedSectionBinding.inflate(parent.layoutInflater, parent, false)
        return ItemFeedSectionVH(binding)
    }

    override fun getItemCount() = sectionItems.size

    override fun onBindViewHolder(holder: ItemFeedSectionVH, position: Int) =
        holder.onBind(sectionItems[position])
}