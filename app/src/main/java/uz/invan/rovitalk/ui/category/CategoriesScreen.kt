package uz.invan.rovitalk.ui.category

import androidx.fragment.app.viewModels
import uz.invan.rovitalk.databinding.ScreenCategoriesBinding
import uz.invan.rovitalk.ui.base.BaseScreen

/**
 * Created by Abdulaziz Rasulbek on 25/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class CategoriesScreen : BaseScreen<ScreenCategoriesBinding>(false, null) {
    override fun setBinding(): ScreenCategoriesBinding = ScreenCategoriesBinding.inflate(layoutInflater)
    private val viewModel by viewModels<CategoriesViewModel>()

    private val categoriesAdapter = CategoriesAdapter()

    override fun onViewAttach() {
        binding.categories.adapter = categoriesAdapter
    }

}