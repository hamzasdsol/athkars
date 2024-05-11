package com.app.athkar.shared.alarm

interface PrayersAlarmPreferences {

    fun savePrayerNotification(key: String, value: Boolean)
    fun prayerAlarmState(): List<Pair<String, Boolean>>
    fun shouldShowNotification(key: String): Boolean

    companion object {
        const val PREFERENCES_NAME = "prayers_alarm_preferences"
    }
}