package com.app.athkar.edit_prayer.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class EditPrayerViewModel: ViewModel() {
    private val _state = mutableStateOf(EditPrayerState())
    val state: State<EditPrayerState> = _state

    fun onEvent(event: EditPrayerViewModelEvent) {
        when(event) {
            EditPrayerViewModelEvent.Save -> {}
        }

    }
}