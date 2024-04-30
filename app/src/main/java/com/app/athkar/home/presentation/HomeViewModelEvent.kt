package com.app.athkar.home.presentation

sealed class HomeViewModelEvent {
    data object SelectAutoLocation: HomeViewModelEvent()
    data object SelectManualLocation: HomeViewModelEvent()
}