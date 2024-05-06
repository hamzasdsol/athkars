package com.app.athkar.home.presentation

import com.app.athkar.R
import com.app.athkar.data.model.CurrentPrayerDetails
import com.app.athkar.data.model.network.City

data class HomeState(
    val isFirstTime: Boolean = true,
    val location: String = "",
    val cities: List<City> = emptyList(),
    val currentPrayer: CurrentPrayerDetails = CurrentPrayerDetails(
        name = "Duhur",
        time = "01:15 PM",
        nextPrayer = "Asr",
        nextPrayerTime = "05:30 PM"
    ),
)

data class PrayerItem(
    val name: String,
    val iconRes: Int,
    val isSelected: Boolean = false
)


val list = listOf(
    PrayerItem(name = "Fajr", iconRes = R.drawable.ic_fajr, isSelected = false),
    PrayerItem(name = "Shuruq", iconRes = R.drawable.ic_shuruq, isSelected = false),
    PrayerItem(name = "Duhur", iconRes = R.drawable.ic_duhur, isSelected = true),
    PrayerItem(name = "Asr", iconRes = R.drawable.ic_asr, isSelected = false),
    PrayerItem(name = "Maghrib", iconRes = R.drawable.ic_maghrib, isSelected = false),
    PrayerItem(name = "Isha", iconRes = R.drawable.ic_isha, isSelected = false),
)