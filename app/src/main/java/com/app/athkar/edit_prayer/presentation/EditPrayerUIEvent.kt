package com.app.athkar.edit_prayer.presentation

sealed class EditPrayerUIEvent {
    data class ShowMessage(val message: String) : EditPrayerUIEvent()
    data object RequestAlarmPermission : EditPrayerUIEvent()
}