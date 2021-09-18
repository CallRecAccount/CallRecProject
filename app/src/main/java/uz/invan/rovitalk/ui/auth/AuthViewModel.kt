package uz.invan.rovitalk.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.invan.rovitalk.util.lifecycle.Event

/**
 * Created by Abdulaziz Rasulbek on 18/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class AuthViewModel : ViewModel() {
    private val _toEnterScreen = MutableLiveData<Event<Unit>>()
    val toEnterScreen: LiveData<Event<Unit>> get() = _toEnterScreen

    fun toEnterScreen() {
        _toEnterScreen.value = Event(Unit)
    }
}