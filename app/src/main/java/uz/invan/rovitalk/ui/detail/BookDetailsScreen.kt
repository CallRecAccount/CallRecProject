package uz.invan.rovitalk.ui.detail

import uz.invan.rovitalk.databinding.ScreenBookDetailsBinding
import uz.invan.rovitalk.ui.base.BaseScreen

/**
 * Created by Abdulaziz Rasulbek on 28/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class BookDetailsScreen : BaseScreen<ScreenBookDetailsBinding>(false, null) {
    override fun setBinding(): ScreenBookDetailsBinding = ScreenBookDetailsBinding.inflate(layoutInflater)

    private val reviewAdapter = ReviewAdapter()

    override fun onViewAttach() {
        binding.reviews.adapter = reviewAdapter
    }
}