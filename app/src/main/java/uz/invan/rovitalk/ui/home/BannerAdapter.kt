package uz.invan.rovitalk.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import uz.invan.rovitalk.databinding.ItemBannerBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

/**
 * Created by Abdulaziz Rasulbek on 19/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class BannerAdapter : ListAdapter<String, BannerVH>(COMPARATOR) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerVH {
        val binding = ItemBannerBinding.inflate(parent.layoutInflater, parent, false)
        return BannerVH(binding)
    }

    override fun onBindViewHolder(holder: BannerVH, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }
}

class BannerVH(private val binding: ItemBannerBinding) : BaseVH<String>(binding.root) {
    override fun onBind(item: String) {
        super.onBind(item)

    }
}