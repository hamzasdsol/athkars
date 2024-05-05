package com.app.athkar.core.util.alarm

import com.app.athkar.edit_prayer.presentation.PrayerName

interface PrayersAlarmPreferences {

    fun savePrayerNotification(key: String, value: Boolean)
    fun prayerAlarmState(): List<Pair<String, Boolean>>
    fun shouldShowNotification(key: String): Boolean

    companion object {
        const val PREFERENCES_NAME = "prayers_alarm_preferences"
    }
}