package uz.invan.rovitalk.ui.feed.sections

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.databinding.ItemFeedSectionsGridBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig

class ItemFeedSectionsGridAdapter(
    val sections: List<FeedSection>,
    val callback: FeedConfig.SectionGridClickListener
) :
    RecyclerView.Adapter<ItemFeedSectionsGridAdapter.ItemFeedSectionsGridVH>() {
    inner class ItemFeedSectionsGridVH(private val binding: ItemFeedSectionsGridBinding) :
        BaseVH<FeedSection>(binding.root) {
        override fun onInit(item: FeedSection) {
            binding.itemFeedSectionGrid.setOnClickListener {
                callback.onSectionGridClick(sections[bindingAdapterPosition])
            }
        }

        override fun onBind(item: FeedSection) {
            super.onBind(item)

            Glide.with(context)
                .load(item.image)
                .into(binding.imageSection)
            binding.textSectionName.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedSectionsGridVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFeedSectionsGridBinding.inflate(inflater, parent, false)
        return ItemFeedSectionsGridVH(binding)
    }

    override fun getItemCount() = sections.size

    override fun onBindViewHolder(holder: ItemFeedSectionsGridVH, position: Int) =
        holder.onBind(sections[position])
}