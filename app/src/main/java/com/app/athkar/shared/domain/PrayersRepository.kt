package com.app.athkar.shared.domain

import com.app.athkar.core.network.NetworkResult
import com.app.athkar.shared.data.GetPrayerTimesResponse

interface PrayersRepository {
    suspend fun getPrayerTimes(city: String): NetworkResult<GetPrayerTimesResponse>
}