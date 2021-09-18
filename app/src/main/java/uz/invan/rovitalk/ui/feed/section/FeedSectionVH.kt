package uz.invan.rovitalk.ui.feed.section

import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.FeedSectionBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.decoration.ItemFeedSectionItemDecoration
import uz.invan.rovitalk.util.ktx.px
import uz.invan.rovitalk.util.ktx.roviClear

class FeedSectionVH(private val binding: FeedSectionBinding) : BaseVH<FeedConfig>(binding.root) {
    private lateinit var params: FeedConfig.FeedSectionParams
    private val sectionItems = arrayListOf<FeedCategory>()
    private val adapter by lazy { ItemFeedSectionAdapter(sectionItems, params.callback) }

    override fun onInit(item: FeedConfig) {
        params = item.items[bindingAdapterPosition].feedSectionParams!!

        with(binding.sectionItems) {
            layoutManager =
                ItemFeedLayoutManager(params.itemsPerPage, params.itemsOffset, context)
            adapter = this@FeedSectionVH.adapter
            isNestedScrollingEnabled = false
            addItemDecoration(ItemFeedSectionItemDecoration(context.px(params.itemsOffset)))
        }
    }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        params = item.items[bindingAdapterPosition].feedSectionParams!!

        sectionItems.roviClear().addAll(params.section.categories)
        adapter.notifyDataSetChanged()

        binding.textSectionName.text = params.section.title
/*        sectionItems.roviClear().addAll(params.section.categories)
        adapter.notifyDataSetChanged()*/
        binding.textMore.setOnClickListener { params.moreCallback.onSectionMoreClick() }
    }
}