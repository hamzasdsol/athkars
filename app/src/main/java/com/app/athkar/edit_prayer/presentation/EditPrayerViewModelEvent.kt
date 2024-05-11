package com.app.athkar.edit_prayer.presentation

sealed class EditPrayerViewModelEvent {
    data class SetPrayerAlarmPreference(val key: PrayerName, val value: Boolean): EditPrayerViewModelEvent()
}