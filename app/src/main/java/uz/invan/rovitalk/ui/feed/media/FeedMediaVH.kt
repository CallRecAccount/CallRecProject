package uz.invan.rovitalk.ui.feed.media

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import uz.invan.rovitalk.databinding.FeedMediaBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.ktx.coroutineIO
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.retrieveVideoThumbnail
import uz.invan.rovitalk.util.ktx.setDimensionRatioTo

class FeedMediaVH(
    private val binding: FeedMediaBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedMediaParams!! }

    override fun onInit(item: FeedConfig) {
        binding.media.setOnClickListener { params.callback.onMediaClick(params.category) }
    }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        binding.mediaContainer.setDimensionRatioTo(binding.media, params.type.ratio)
        val requestOptions =
            RequestOptions().transform(CenterCrop(), RoundedCorners(context.px(12)))
        if (params.type == FeedConfig.FeedMediaTypes.VIDEO)
            coroutineIO {
                val thumbnail = context.retrieveVideoThumbnail(params.cover)
                withContext(Main) {
                    try {
                        Glide.with(binding.media)
                            .load(thumbnail ?: params.cover)
                            .apply(requestOptions)
                            .into(binding.media)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        else
            Glide.with(context)
                .load(params.cover)
                .apply(requestOptions)
                .into(binding.media)
        binding.textMediaTitle.text = params.title
        binding.textMediaSubtitle.text = params.subtitle
        binding.textMediaTitle.isVisible = params.title != null
        binding.textMediaSubtitle.isVisible = params.subtitle != null
        binding.playButton.isVisible = params.type == FeedConfig.FeedMediaTypes.VIDEO
        binding.media.setOnClickListener { params.callback.onMediaClick(params.category) }
    }
}