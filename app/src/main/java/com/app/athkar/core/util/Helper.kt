package com.app.athkar.core.util

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