package uz.invan.rovitalk.ui.profile.language

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.user.LanguageData
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.databinding.ItemLanguageBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater
import uz.invan.rovitalk.util.ktx.roviEnable

class LanguagesAdapter(private val languages: ArrayList<LanguageData>) :
    RecyclerView.Adapter<LanguagesAdapter.LanguageVH>() {
    private var listener: LanguageClickListener? = null

    fun setOnLanguageClickListener(onLanguageClick: LanguageClickListener) {
        listener = onLanguageClick
    }

    inner class LanguageVH(private val binding: ItemLanguageBinding) :
        BaseVH<LanguageData>(binding.root) {
        override fun onInit(item: LanguageData) {
            binding.itemLanguage.setOnClickListener {
                val language = languages[bindingAdapterPosition]
                changeLanguage(language, bindingAdapterPosition)
            }
        }

        override fun onBind(item: LanguageData) {
            super.onBind(item)

            binding.itemLanguage.roviEnable = !item.isCurrent
            when (item.language) {
                RoviLanguage.UZ -> {
                    val uz = ContextCompat.getDrawable(context, R.drawable.ic_uzbekistan)
                    binding.language.setCompoundDrawableLeft(uz)
                    binding.language.setText(R.string.language_uz)
                }
                RoviLanguage.RU -> {
                    val ru = ContextCompat.getDrawable(context, R.drawable.ic_russia)
                    binding.language.setCompoundDrawableLeft(ru)
                    binding.language.setText(R.string.language_ru)
                }
                RoviLanguage.EN -> {
                    val en = ContextCompat.getDrawable(context, R.drawable.ic_us)
                    binding.language.setCompoundDrawableLeft(en)
                    binding.language.setText(R.string.language_en)
                }
            }
            if (item.isChosen)
                binding.tick.isVisible = true
        }

        private fun TextView.setCompoundDrawableLeft(drawable: Drawable?) {
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    private fun changeLanguage(language: LanguageData, position: Int) {
        // return if this language chosen or current rovi language
        if (language.isChosen or language.isCurrent) return

        // changes languages isChosen state
        languages.forEachIndexed { index, languageData ->
            languages[index] = languageData.copy(isChosen = index == position)
        }
        // notifies adapter
        repeat(languages.size) { notifyItemChanged(it) }
        listener?.onLanguageClick(language)
    }

    fun interface LanguageClickListener {
        fun onLanguageClick(language: LanguageData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageVH {
        val binding = ItemLanguageBinding.inflate(parent.layoutInflater, parent, false)
        return LanguageVH(binding)
    }

    override fun onBindViewHolder(holder: LanguageVH, position: Int) =
        holder.onBind(languages[position])

    override fun getItemCount() = languages.size
}