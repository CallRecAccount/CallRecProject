package uz.invan.rovitalk.ui.feed.tariffs

import androidx.recyclerview.widget.LinearLayoutManager
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.databinding.FeedMyTariffsBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.ui.feed.FeedConfig
import uz.invan.rovitalk.util.custom.decoration.ItemFeedMyTariffsItemDecoration
import uz.invan.rovitalk.util.ktx.roviClear

class FeedMyTariffsVH(private val binding: FeedMyTariffsBinding, private val config: FeedConfig) :
    BaseVH<FeedConfig>(binding.root) {
    private val params by lazy { config.items[bindingAdapterPosition].feedMyTariffsParams!! }
    private val sections = arrayListOf<FeedSection>()
    private val adapter by lazy { ItemFeedMyTariffsAdapter(sections) }

    override fun onInit(item: FeedConfig) {
        sections.roviClear().addAll(params.sections)

        with(binding.tariffs) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FeedMyTariffsVH.adapter
            isNestedScrollingEnabled = false
            addItemDecoration(ItemFeedMyTariffsItemDecoration(params.itemsOffset))
        }
    }

    override fun onBind(item: FeedConfig) {
        super.onBind(item)
        binding.textTariffsHeader.text = params.myTariffsHeader
    }
}