package uz.invan.rovitalk.ui.favorites

import uz.invan.rovitalk.databinding.PageFavoritesBinding
import uz.invan.rovitalk.ui.base.BaseScreen

/**
 * Created by Abdulaziz Rasulbek on 14/10/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class FavoritesPage : BaseScreen<PageFavoritesBinding>(true, null) {
    override fun setBinding() = PageFavoritesBinding.inflate(layoutInflater)
    private val booksAdapter = BooksAdapter()

    override fun onViewAttach() {
        binding.books.adapter = booksAdapter
    }

}