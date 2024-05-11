package com.app.athkar.shared.data

data class GetPrayerTimesResponse(
    val prayerTimes: Map<String, List<String>>
)