package uz.invan.rovitalk.ui.profile.settings

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.invan.rovitalk.data.models.settings.RoviSettings
import uz.invan.rovitalk.databinding.ItemSettingsBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

class SettingsAdapter(private val settings: List<RoviSettings>) :
    RecyclerView.Adapter<SettingsAdapter.SettingsVH>() {
    private var callback: ((RoviSettings) -> Unit)? = null

    fun setOnNavigateSettings(f: (RoviSettings) -> Unit) {
        callback = f
    }

    inner class SettingsVH(private val binding: ItemSettingsBinding) :
        BaseVH<RoviSettings>(binding.root) {
        init {
            binding.itemSettingsLayout.setOnClickListener { callback?.invoke(settings[bindingAdapterPosition]) }
        }

        override fun onBind(item: RoviSettings) {
            super.onBind(item)

            binding.imageItemSettings.setImageResource(item.image)
            binding.textItemSettings.setText(item.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsVH {
        val binding = ItemSettingsBinding.inflate(parent.layoutInflater, parent, false)
        return SettingsVH(binding)
    }

    override fun getItemCount() = settings.size

    override fun onBindViewHolder(holder: SettingsVH, position: Int) =
        holder.onBind(settings[position])
}