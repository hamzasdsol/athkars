package com.app.athkar.athkar_list.presentation

sealed class AthkarListUIEvent {
    data class ShowMessage(val message: String) : AthkarListUIEvent()
}