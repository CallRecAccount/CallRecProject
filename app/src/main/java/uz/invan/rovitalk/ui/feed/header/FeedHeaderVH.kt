package uz.invan.rovitalk.ui.feed.header

import uz.invan.rovitalk.databinding.FeedHeaderBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig

class FeedHeaderVH(
    private val binding: FeedHeaderBinding,
    private val config: FeedConfig,
) : BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedHeaderParams!! }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        binding.header.text = params.title
    }
}