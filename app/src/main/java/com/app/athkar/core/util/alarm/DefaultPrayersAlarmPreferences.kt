package com.app.athkar.core.util.alarm

import android.content.SharedPreferences
import com.app.athkar.edit_prayer.presentation.PrayerName

class DefaultPrayersAlarmPreferences(
    private val sharedPrefs: SharedPreferences
): PrayersAlarmPreferences {
    override fun savePrayerNotification(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    override fun prayerAlarmState(): List<Pair<String, Boolean>> {
        val resultList = mutableListOf<Pair<String, Boolean>>()
        PrayerName.entries.forEach { key ->
            val value = sharedPrefs.getBoolean(key.name, true)
            savePrayerNotification(key.name, value)
            resultList.add(Pair(key.name, value))
        }

        return resultList.toList()
    }

    override fun shouldShowNotification(key: String) = sharedPrefs.getBoolean(key, false)
}