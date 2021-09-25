package uz.invan.rovitalk.ui.category

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.databinding.ItemCategoryBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

/**
 * Created by Abdulaziz Rasulbek on 25/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class CategoriesAdapter : ListAdapter<FeedCategory, CategoryVH>(COMPARATOR) {
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FeedCategory>() {
            override fun areItemsTheSame(oldItem: FeedCategory, newItem: FeedCategory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedCategory, newItem: FeedCategory): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val binding = ItemCategoryBinding.inflate(parent.layoutInflater, parent, false)
        return CategoryVH(binding)
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {

    }

    override fun getItemCount(): Int {
        return 12
    }
}

class CategoryVH(
    private val binding: ItemCategoryBinding
) : BaseVH<FeedCategory>(binding.root) {

}