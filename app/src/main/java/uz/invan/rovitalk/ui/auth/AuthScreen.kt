package uz.invan.rovitalk.ui.auth

import androidx.fragment.app.viewModels
import uz.invan.rovitalk.databinding.ScreenAuthBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.lifecycle.EventObserver

/**
 * Created by Abdulaziz Rasulbek on 17/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class AuthScreen : BaseScreen<ScreenAuthBinding>(false, null) {
    private val viewModel: AuthViewModel by viewModels()
    override fun setBinding(): ScreenAuthBinding {
        return ScreenAuthBinding.inflate(layoutInflater)
    }

    override fun attachObservers() {
        viewModel.toEnterScreen.observe(viewLifecycleOwner, toEnterScreenObserver)
    }
    private val toEnterScreenObserver=EventObserver<Unit>{
        controller.navigate(AuthScreenDirections.toEnterScreen())
    }

    override fun onViewAttach() {
        binding.login.setOnClickListener {
            viewModel.toEnterScreen()
        }
    }


}