package uz.invan.rovitalk.ui.favorites

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.ItemBookBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

/**
 * Created by Abdulaziz Rasulbek on 14/10/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class BooksAdapter : ListAdapter<FeedCategory, BooksVH>(COMPARATOR) {
    private val clickListener: BookClickListener? = null

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<FeedCategory>() {
            override fun areItemsTheSame(oldItem: FeedCategory, newItem: FeedCategory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedCategory, newItem: FeedCategory): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksVH {
        val binding = ItemBookBinding.inflate(parent.layoutInflater, parent, false)
        return BooksVH(binding, clickListener)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: BooksVH, position: Int) {

    }
}

class BooksVH(
    private val binding: ItemBookBinding,
    private val clickListener: BookClickListener?
) : BaseVH<FeedCategory>(binding.root) {

    override fun onBind(item: FeedCategory) {
        super.onBind(item)

    }
}

typealias BookClickListener = (item: FeedCategory) -> Unit