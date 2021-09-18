package uz.invan.rovitalk.ui.feed

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.*
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.buy.FeedBuyVH
import uz.invan.rovitalk.ui.feed.header.FeedHeaderVH
import uz.invan.rovitalk.ui.feed.media.FeedMediaVH
import uz.invan.rovitalk.ui.feed.podcasts.grid.FeedPodcastsGridVH
import uz.invan.rovitalk.ui.feed.podcasts.icons.FeedPodcastsIconsVH
import uz.invan.rovitalk.ui.feed.podcasts.vertical.FeedPodcastsVerticalVH
import uz.invan.rovitalk.ui.feed.section.FeedSectionVH
import uz.invan.rovitalk.ui.feed.sections.FeedSectionsGridVH
import uz.invan.rovitalk.ui.feed.tariffs.FeedMyTariffsVH
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.px

class FeedAdapter(private val config: FeedConfig) : RecyclerView.Adapter<BaseVH<FeedConfig>>() {
    override fun getItemViewType(position: Int) = config.items[position].type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH<FeedConfig> {
        when (viewType) {
            FeedConfig.FeedTypes.SECTIONS_GRID.ordinal -> {
                val binding = FeedSectionsGridBinding.inflate(parent.layoutInflater, parent, false)
                return FeedSectionsGridVH(binding, config)
            }
            FeedConfig.FeedTypes.BUY.ordinal -> {
                val binding = FeedBuyBinding.inflate(parent.layoutInflater, parent, false)
                return FeedBuyVH(binding, config)
            }
            FeedConfig.FeedTypes.SECTION.ordinal -> {
                val binding = FeedSectionBinding.inflate(parent.layoutInflater, parent, false)
                return FeedSectionVH(binding)
            }
            FeedConfig.FeedTypes.MEDIA.ordinal -> {
                val binding = FeedMediaBinding.inflate(parent.layoutInflater, parent, false)
                return FeedMediaVH(binding, config)
            }
            FeedConfig.FeedTypes.PODCASTS_ICONS.ordinal -> {
                val binding = FeedPodcastsIconsBinding.inflate(parent.layoutInflater, parent, false)
                return FeedPodcastsIconsVH(binding, config)
            }
            FeedConfig.FeedTypes.PODCASTS_GRID.ordinal -> {
                val binding = FeedPodcastsGridBinding.inflate(parent.layoutInflater, parent, false)
                return FeedPodcastsGridVH(binding, config)
            }
            FeedConfig.FeedTypes.HEADER.ordinal -> {
                val binding = FeedHeaderBinding.inflate(parent.layoutInflater, parent, false)
                return FeedHeaderVH(binding, config)
            }
            FeedConfig.FeedTypes.MY_TARIFFS.ordinal -> {
                val binding = FeedMyTariffsBinding.inflate(parent.layoutInflater, parent, false)
                return FeedMyTariffsVH(binding, config)
            }
            FeedConfig.FeedTypes.PODCASTS_VERTICAL.ordinal -> {
                val binding =
                    FeedPodcastsVerticalBinding.inflate(parent.layoutInflater, parent, false)
                return FeedPodcastsVerticalVH(binding, config)
            }
            else -> {
                return object : BaseVH<FeedConfig>(FrameLayout(parent.context).apply {
                    val width = ViewGroup.LayoutParams.WRAP_CONTENT
                    val height =
                        context.px(56) - context.resources.getDimension(R.dimen.item_inner_space)
                    layoutParams = ViewGroup.LayoutParams(width, height.toInt())
                }) {}
            }
        }
    }

    override fun getItemCount() = config.items.size

    override fun onBindViewHolder(holder: BaseVH<FeedConfig>, position: Int) = holder.onBind(config)
}