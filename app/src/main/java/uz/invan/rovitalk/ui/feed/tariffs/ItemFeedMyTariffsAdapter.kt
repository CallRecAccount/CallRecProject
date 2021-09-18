package uz.invan.rovitalk.ui.feed.tariffs

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.feed.FeedSection
import uz.invan.rovitalk.databinding.ItemFeedMyTariffsBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater
import java.util.*

class ItemFeedMyTariffsAdapter(private val sections: List<FeedSection>) :
    RecyclerView.Adapter<ItemFeedMyTariffsAdapter.ItemFeedMyTariffsVH>() {
    inner class ItemFeedMyTariffsVH(private val binding: ItemFeedMyTariffsBinding) :
        BaseVH<FeedSection>(binding.root) {
        override fun onBind(item: FeedSection) {
            super.onBind(item)

            binding.textSectionName.text = item.title
            binding.startDate.text = item.boughtDate?.toUzDate(context)
            binding.endDate.text = item.expireDate?.toUzDate(context)
        }

        private fun Long.toUzDate(context: Context): String {
            val date = Date(this)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val year = calendar[Calendar.YEAR]

            val monthName = context.resources.getStringArray(R.array.months)[month]
            return context.getString(R.string.months_x_x, monthName, day, year)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFeedMyTariffsVH {
        val binding = ItemFeedMyTariffsBinding.inflate(parent.layoutInflater, parent, false)
        return ItemFeedMyTariffsVH(binding)
    }

    override fun getItemCount() = sections.size

    override fun onBindViewHolder(holder: ItemFeedMyTariffsVH, position: Int) =
        holder.onBind(sections[position])
}