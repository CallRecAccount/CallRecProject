package uz.invan.rovitalk.ui.feed.buy

import uz.invan.rovitalk.databinding.FeedBuyBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.setOnDebounceClickListener

class FeedBuyVH(private val binding: FeedBuyBinding, private val config: FeedConfig) :
    BaseVH<FeedConfig>(binding.root) {

    init {
        binding.feedBuy.setOnDebounceClickListener {
            val params =
                if (bindingAdapterPosition < 0) null else config.items[bindingAdapterPosition].feedBuyParams!!
            params?.let {
                params.callback.onBuyClick()
            }
        }
    }
}