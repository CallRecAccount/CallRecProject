package uz.invan.rovitalk.ui.favorites

import com.google.android.material.tabs.TabLayoutMediator
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.favorites.Favorites
import uz.invan.rovitalk.databinding.ScreenFavoritesBinding
import uz.invan.rovitalk.ui.base.BaseScreen

/**
 * Created by Abdulaziz Rasulbek on 13/10/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class FavoritesScreen : BaseScreen<ScreenFavoritesBinding>(true, null) {
    override fun setBinding() = ScreenFavoritesBinding.inflate(layoutInflater)
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onViewAttach() {
        favoritesAdapter = FavoritesAdapter(this, Favorites.values().toList())
        binding.pager.adapter = favoritesAdapter
        TabLayoutMediator(binding.favoriteTabs, binding.pager) { tab, pos ->
            tab.text = resources.getStringArray(R.array.favorite_types)[pos]
        }.attach()
    }


}