package com.app.athkar.athkar_list.presentation

sealed class AthkarsViewModelEvent {
    data object Next: AthkarsViewModelEvent()
    data object Back: AthkarsViewModelEvent()
    data object Export: AthkarsViewModelEvent()
}