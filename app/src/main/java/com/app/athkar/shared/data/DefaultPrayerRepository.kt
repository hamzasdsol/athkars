package com.app.athkar.shared.data

import com.app.athkar.core.network.NetworkResult
import com.app.athkar.core.util.CryptLib
import com.app.athkar.core.network.remote.AppDataSource
import com.app.athkar.shared.domain.PrayersRepository
import org.json.JSONObject
import javax.inject.Inject

class DefaultPrayerRepository @Inject constructor(
    private val dataSource: AppDataSource
): PrayersRepository {
    override suspend fun getPrayerTimes(city: String): NetworkResult<GetPrayerTimesResponse> {
        when (val response = dataSource.getPrayerTimes(city)) {
            is NetworkResult.Success -> {
                try {
                    val decryptedResponse = CryptLib.decryptData(response.data.data)
                    val decryptedResponseObject = JSONObject(decryptedResponse)
                    val map = mutableMapOf<String, List<String>>()
                    decryptedResponseObject.keys().forEach { key ->
                        val value = decryptedResponseObject.getJSONArray(key)
                        val list = mutableListOf<String>()
                        for (i in 0 until value.length()) {
                            list.add(value.getString(i))
                        }
                        map[key] = list
                    }
                    val getPrayerTimesResponse = GetPrayerTimesResponse(map.toMap())
                    return NetworkResult.Success(getPrayerTimesResponse)
                } catch (e: Exception) {
                    return NetworkResult.Failure(e)
                }
            }

            is NetworkResult.Failure -> {
                return NetworkResult.Failure(response.exception)
            }
        }
    }
}