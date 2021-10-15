package uz.invan.rovitalk.ui.favorites

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.invan.rovitalk.data.models.favorites.Favorites

/**
 * Created by Abdulaziz Rasulbek on 14/10/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class FavoritesAdapter(fragment: Fragment, private val list: List<Favorites>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return FavoritesPage().apply {
            arguments = FavoritesPageArgs(list[position]).toBundle()
        }
    }

}