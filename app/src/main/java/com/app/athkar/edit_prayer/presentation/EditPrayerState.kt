package com.app.athkar.edit_prayer.presentation

data class EditPrayerState(
    val prayers: List<Prayer> = emptyList()
)

data class Prayer(
    val name: String,
    val prayerTime: String,
    val iqamaTime: String,
    val isAlarmEnabled: Boolean,
)
