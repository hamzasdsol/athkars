package com.app.athkar.home

sealed class HomeViewModelEvent {
    data object SelectAutoLocation: HomeViewModelEvent()
    data object SelectManualLocation: HomeViewModelEvent()
}