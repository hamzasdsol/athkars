package com.app.athkar.data.model.network

data class GetPrayerTimesResponse(
    val prayerTimes: Map<String, List<String>>
)