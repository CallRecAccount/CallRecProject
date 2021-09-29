package uz.invan.rovitalk.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.invan.rovitalk.databinding.ItemStoryBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

/**
 * Created by Abdulaziz Rasulbek on 19/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class StoryAdapter : ListAdapter<String, StoryVH>(COMPARATOR) {
    private var clickListener: StoryClickListener? = null

    fun onClick(listener: StoryClickListener) {
        clickListener = listener
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryVH {
        val binding = ItemStoryBinding.inflate(parent.layoutInflater, parent, false)
        return StoryVH(binding, clickListener)
    }

    override fun onBindViewHolder(holder: StoryVH, position: Int) {
        holder.onBind("")
    }

    override fun getItemCount(): Int {
        return 10
    }
}

class StoryVH(
    private val binding: ItemStoryBinding,
    private val clickListener: StoryClickListener?
) : BaseVH<String>(binding.root) {

    override fun itemClick(item: String) {
        binding.root.setOnClickListener {
            clickListener?.invoke(item)
        }
    }

    override fun onBind(item: String) {
        super.onBind(item)

    }
}

typealias StoryClickListener = (item: String) -> Unit