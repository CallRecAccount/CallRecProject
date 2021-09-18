package uz.invan.rovitalk.ui.profile.settings

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.databinding.ItemReminderBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

class RemindersAdapter(
    private val reminders: List<ReminderEntity>,
) : RecyclerView.Adapter<RemindersAdapter.ReminderVH>() {
    private var listener: ReminderClickListener? = null
    private var removeListener: ReminderRemoveListener? = null

    fun setOnReminderClickListener(onReminderClick: ReminderClickListener) {
        listener = onReminderClick
    }

    fun setOnReminderRemoveListener(onReminderRemove: ReminderRemoveListener) {
        removeListener = onReminderRemove
    }

    inner class ReminderVH(private val binding: ItemReminderBinding) :
        BaseVH<ReminderEntity>(binding.root) {
        override fun onInit(item: ReminderEntity) {
            val reminder = reminders[bindingAdapterPosition]
            binding.itemReminder.setOnClickListener { listener?.onReminderClick(reminder) }
            binding.remove.setOnClickListener { removeListener?.onReminderRemove(reminder) }
        }

        override fun onBind(item: ReminderEntity) {
            super.onBind(item)

            binding.textTitle.text = item.title
            binding.textTime.text = item.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderVH {
        val binding = ItemReminderBinding.inflate(parent.layoutInflater, parent, false)
        return ReminderVH(binding)
    }

    override fun onBindViewHolder(holder: ReminderVH, position: Int) =
        holder.onBind(reminders[position])

    override fun getItemCount() = reminders.size

    fun interface ReminderClickListener {
        fun onReminderClick(reminder: ReminderEntity)
    }

    fun interface ReminderRemoveListener {
        fun onReminderRemove(reminder: ReminderEntity)
    }
}
