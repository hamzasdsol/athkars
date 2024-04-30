package com.app.athkar.edit_prayer

data class EditPrayerState(
    val prayers: List<Prayer> = emptyList()
)

data class Prayer(
    val name: String,
    val prayerTime: String,
    val iqamaTime: String,
    val isAlarmEnabled: Boolean,
)
