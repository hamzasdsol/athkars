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


val list = listOf(
    Prayer("Fajr", "6:14", "6:35", true),
    Prayer("Duhur", "6:14", "6:35", true),
    Prayer("Asr", "6:14", "6:35", true),
    Prayer("Maghrib", "6:14", "6:35", false),
    Prayer("Isha", "6:14", "6:35", false),
)