package com.app.athkar.data.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class PrayersModel(
    val fajr: Date,
    val shuruq: Date,
    val duhur: Date,
    val asr: Date,
    val maghrib: Date,
    val isha: Date
): Parcelable {
    @IgnoredOnParcel
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getCurrentPrayer(): Pair<Pair<String, String>, Pair<String, String>> {
        val currentTime = Date()
        return when {
            currentTime.after(fajr) && currentTime.before(shuruq) -> Pair(Pair("Fajr", timeFormat.format(fajr)), Pair("Shuruq", timeFormat.format(shuruq)))
            currentTime.after(shuruq) && currentTime.before(duhur) -> Pair(Pair("Shuruq", timeFormat.format(shuruq)), Pair("Duhur", timeFormat.format(duhur)))
            currentTime.after(duhur) && currentTime.before(asr) -> Pair(Pair("Duhur", timeFormat.format(duhur)), Pair("Asr", timeFormat.format(asr)))
            currentTime.after(asr) && currentTime.before(maghrib) -> Pair(Pair("Asr", timeFormat.format(asr)), Pair("Maghrib", timeFormat.format(maghrib)))
            currentTime.after(maghrib) && currentTime.before(isha) -> Pair(Pair("Maghrib", timeFormat.format(maghrib)), Pair("Isha", timeFormat.format(isha)))
            else -> Pair(Pair("Isha", timeFormat.format(isha)), Pair("Fajr", timeFormat.format(fajr)))
        }
    }

    fun toFormattedString(date: String): String {
        return timeFormat.format(date)
    }

}

fun List<String>.toPrayersModel(date:String): PrayersModel {
    val timeFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
    val fajr = timeFormat.parse("$date ${this[0]}")
    val shuruq = timeFormat.parse("$date ${this[1]}")
    val duhur = timeFormat.parse("$date ${this[2]}")
    val asr = timeFormat.parse("$date ${this[3]}")
    val maghrib = timeFormat.parse("$date ${this[4]}")
    val isha = timeFormat.parse("$date ${this[5]}")
    return PrayersModel(fajr, shuruq, duhur, asr, maghrib, isha)
}
