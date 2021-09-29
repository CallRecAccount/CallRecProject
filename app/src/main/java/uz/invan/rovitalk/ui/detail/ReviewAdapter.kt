package uz.invan.rovitalk.ui.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.invan.rovitalk.data.models.review.Review
import uz.invan.rovitalk.databinding.ItemReviewBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

/**
 * Created by Abdulaziz Rasulbek on 29/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class ReviewAdapter : ListAdapter<Review, ReviewVH>(COMPARATOR) {
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        val binding = ItemReviewBinding.inflate(parent.layoutInflater, parent, false)
        return ReviewVH(binding)
    }

    override fun onBindViewHolder(holder: ReviewVH, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }
}

class ReviewVH(private val binding: ItemReviewBinding) : BaseVH<Review>(binding.root) {

    override fun onBind(item: Review) {
        super.onBind(item)

    }
}