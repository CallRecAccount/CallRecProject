package uz.invan.rovitalk.ui.audio

import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.feed.FeedSubCategory
import uz.invan.rovitalk.databinding.ItemAudiosBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviEnable

class AudiosAdapter(
    private val subCategories: List<FeedSubCategory>,
) : RecyclerView.Adapter<AudiosAdapter.AudiosVH>() {
    private var callback: CategoryClickListener? = null

    fun setOnAudioClick(onClick: CategoryClickListener) {
        callback = onClick
    }

    inner class AudiosVH(private val binding: ItemAudiosBinding) :
        BaseVH<FeedSubCategory>(binding.root) {
        override fun onInit(item: FeedSubCategory) {
            binding.itemAudios.setOnClickListener { callback?.onCategoryClick(subCategories[bindingAdapterPosition]) }
        }

        override fun onBind(item: FeedSubCategory) {
            super.onBind(item)

            if (bindingAdapterPosition == 0) binding.up.isInvisible = true
            if (bindingAdapterPosition == subCategories.lastIndex) binding.down.isInvisible = true

            if (!item.isOpen) {
                binding.itemAudios.isClickable = false
                binding.textCounter.roviEnable = false
                binding.image.roviEnable = false
                binding.imageLock.isVisible = true
                binding.indicator.setImageResource(R.drawable.audio_indicator_disabled)
                binding.indicator.roviEnable = false
                binding.textSubCategoryName.roviEnable = false
                binding.textSubCategoryAuthor.roviEnable = false
                binding.textSubCategorysCount.roviEnable = false
                binding.imageTime.roviEnable = false
            }

            binding.textCounter.text = context.getString(R.string.x_order, bindingAdapterPosition + 1)
            binding.textSubCategoryName.text = item.title
            binding.textSubCategoryAuthor.text = item.author
            binding.textSubCategorysCount.text =
                context.getString(R.string.x_count, item.count)
            val requestOptions =
                RequestOptions().transform(CenterCrop(), RoundedCorners(context.px(12)))
            Glide.with(context)
                .load(item.image)
                .apply(requestOptions)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiosVH {
        val binding = ItemAudiosBinding.inflate(parent.layoutInflater, parent, false)
        return AudiosVH(binding)
    }

    override fun onBindViewHolder(holder: AudiosVH, position: Int) =
        holder.onBind(subCategories[position])

    override fun getItemCount() = subCategories.size

    fun interface CategoryClickListener {
        fun onCategoryClick(category: FeedSubCategory)
    }
}