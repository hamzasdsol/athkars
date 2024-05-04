package com.app.athkar.domain.repository

import com.app.athkar.data.model.network.GetAthkarResponse
import com.app.athkar.data.model.network.GetCitiesResponse
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.domain.Result

interface AppRepository {
    suspend fun getAthkar(): Result<GetAthkarResponse>
    suspend fun getPrayerTimes(city: String): Result<GetPrayerTimesResponse>
    suspend fun getCities(): Result<GetCitiesResponse>
}