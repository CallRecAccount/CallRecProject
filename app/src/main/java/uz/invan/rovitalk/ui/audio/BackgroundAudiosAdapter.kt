package uz.invan.rovitalk.ui.audio

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.invan.rovitalk.data.models.audio.BMState
import uz.invan.rovitalk.data.models.audio.BMVisibility
import uz.invan.rovitalk.data.models.audio.PlayerBM
import uz.invan.rovitalk.databinding.ItemBackgroundAudioBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

class BackgroundAudiosAdapter(private val audios: ArrayList<PlayerBM>) :
    RecyclerView.Adapter<BackgroundAudiosAdapter.BackgroundAudioVH>() {
    private var currentItem = -1

    inner class BackgroundAudioVH(private val binding: ItemBackgroundAudioBinding) :
        BaseVH<PlayerBM>(binding.root) {
        override fun onBind(item: PlayerBM) {
            super.onBind(item)

            if (bindingAdapterPosition == 0) {
                binding.imageMute.isVisible = true
                binding.textMute.isVisible = true
                binding.imageBackgroundAudio.setImageDrawable(null)
                return
            }
            Glide.with(context)
                .load(item.image)
                .circleCrop()
                .into(binding.imageBackgroundAudio)

            when (item.state) {
                BMState.LOADING -> {
                    binding.itemBackgroundAudio.alpha = 0.5f
                    binding.progress.isVisible = true
                }
                BMState.LOADED -> {
                    binding.itemBackgroundAudio.alpha = 1f
                    binding.progress.isVisible = false
                }
            }
            binding.containerBm.isVisible = item.visibility == BMVisibility.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundAudioVH {
        val binding = ItemBackgroundAudioBinding.inflate(parent.layoutInflater)
        return BackgroundAudioVH(binding)
    }

    override fun getItemCount() = audios.size
    override fun onBindViewHolder(holder: BackgroundAudioVH, position: Int) =
        holder.onBind(audios[position])

    fun setCurrentItem(index: Int) {
        currentItem = index
    }

    fun showAll() {
        audios.forEachIndexed { index, bm ->
            bm.visibility = BMVisibility.VISIBLE
            if (currentItem != index)
                notifyItemChanged(index)
        }
    }

    fun dismiss() {
        for (i in audios.indices) {
            audios[i].visibility =
                if (currentItem == i) BMVisibility.VISIBLE else BMVisibility.INVISIBLE
            if (currentItem != i)
                notifyItemChanged(i)
        }
    }
}