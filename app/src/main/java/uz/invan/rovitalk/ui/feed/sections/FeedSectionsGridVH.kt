package uz.invan.rovitalk.ui.feed.sections

import androidx.recyclerview.widget.GridLayoutManager
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.databinding.FeedSectionsGridBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.decoration.GridSectionsItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear

class FeedSectionsGridVH(
    private val binding: FeedSectionsGridBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedSectionsGridParams!! }
    private val sections = arrayListOf<FeedSection>()
    private val adapter by lazy { ItemFeedSectionsGridAdapter(sections, params.callback) }

    override fun onInit(item: FeedConfig) {
        sections.roviClear().addAll(params.sections)

        with(binding.gridSections) {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = this@FeedSectionsGridVH.adapter
            addItemDecoration(GridSectionsItemDecoration(context.px(params.itemsOffset)))
            isNestedScrollingEnabled = false
        }
    }
}