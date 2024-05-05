package com.app.athkar.home.presentation

sealed class HomeUIEvent {
    data class ShowMessage(val message: String) : HomeUIEvent()
}