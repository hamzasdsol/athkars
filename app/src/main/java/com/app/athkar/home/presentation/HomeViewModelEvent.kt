package com.app.athkar.home.presentation

import com.app.athkar.data.model.network.City

sealed class HomeViewModelEvent {
    data object SelectAutoLocation: HomeViewModelEvent()
    data object SelectManualLocation: HomeViewModelEvent()
    data class UpdateLocation(val city: City): HomeViewModelEvent()
    data class UpdateShowDialog(val showDialog: Boolean): HomeViewModelEvent()
}