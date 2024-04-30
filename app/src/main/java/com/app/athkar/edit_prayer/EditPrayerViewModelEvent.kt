package com.app.athkar.edit_prayer

sealed class EditPrayerViewModelEvent {
    data object Save: EditPrayerViewModelEvent()
}