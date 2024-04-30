package com.app.athkar.home.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.SelectAutoLocation -> {}
            HomeViewModelEvent.SelectManualLocation -> {}
        }
    }
}