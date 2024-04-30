package com.app.athkar.edit_prayer.presentation

sealed class EditPrayerViewModelEvent {
    data object Save: EditPrayerViewModelEvent()
}