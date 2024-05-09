package com.app.athkar.core.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

inline fun <reified T> String.toResponseModel(): T {
    val typeToken = object : TypeToken<T>() {}.type
    return Gson().fromJson(this, typeToken)
}

fun String.toPrayerDate(): Date {
    return try {
        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(this) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}

fun Context.canScheduleExactAlarm(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if(alarmManager.canScheduleExactAlarms())
            return true
        else{
            Intent().also {
                it.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                startActivity(it)
            }
            return false
        }
    } else {
        true
    }
}