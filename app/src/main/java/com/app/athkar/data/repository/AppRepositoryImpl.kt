package com.app.athkar.data.repository

import com.app.athkar.core.util.CryptLib.decryptData
import com.app.athkar.core.util.toResponseModel
import com.app.athkar.data.model.network.GetAthkarResponse
import com.app.athkar.data.model.network.GetCitiesResponse
import com.app.athkar.data.model.network.GetPrayerTimesResponse
import com.app.athkar.data.remote.AppDataSource
import com.app.athkar.domain.Result
import com.app.athkar.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dataSource: AppDataSource
) : AppRepository {
    override suspend fun getAthkar(): Result<GetAthkarResponse> {
        when (val response = dataSource.getAthkar()) {
            is Result.Success -> {

                try {
                    val decryptedResponse = decryptData(response.data.data)
                    val getAthkarResponse: GetAthkarResponse = decryptedResponse.toResponseModel()
                    return Result.Success(getAthkarResponse)
                } catch (e: Exception) {
                    return Result.Failure(e)
                }
            }

            is Result.Failure -> {
                return Result.Failure(response.exception)
            }
        }
    }

    override suspend fun getPrayerTimes(city: String): Result<GetPrayerTimesResponse> {
        when (val response = dataSource.getPrayerTimes(city)) {
            is Result.Success -> {
                try {
                    val decryptedResponse = decryptData(response.data.data)
                    val getPrayerTimesResponse: GetPrayerTimesResponse =
                        decryptedResponse.toResponseModel()
                    return Result.Success(getPrayerTimesResponse)
                } catch (e: Exception) {
                    return Result.Failure(e)
                }
            }

            is Result.Failure -> {
                return Result.Failure(response.exception)
            }
        }
    }

    override suspend fun getCities(): Result<GetCitiesResponse> {
        when (val response = dataSource.getCities()) {
            is Result.Success -> {
                try {
                    val decryptedResponse = decryptData(response.data.data)
                    val getCitiesResponse: GetCitiesResponse = decryptedResponse.toResponseModel()
                    return Result.Success(getCitiesResponse)
                } catch (e: Exception) {
                    return Result.Failure(e)
                }
            }

            is Result.Failure -> {
                return Result.Failure(response.exception)
            }
        }
    }
}
