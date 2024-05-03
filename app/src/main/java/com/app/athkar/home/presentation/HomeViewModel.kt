package com.app.athkar.home.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.athkar.core.util.LocationSelectionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationSelectionPreferences: LocationSelectionPreferences
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        _state.value = state.value.copy(isFirstTime = locationSelectionPreferences.isLocationSelected())
    }

    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.SelectAutoLocation -> {}
            HomeViewModelEvent.SelectManualLocation -> {}
        }
    }
}