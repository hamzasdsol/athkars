package com.app.athkar.data.model.network

data class GetPrayerTimesResponse(
    val schedules: Map<String, Schedule>,
)