package com.app.athkar.core.network.remote

import com.app.athkar.core.network.NetworkResult
import com.app.athkar.core.network.encrypted_response.EncryptedResponse
import javax.inject.Inject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AppDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getCities(): NetworkResult<EncryptedResponse> {
        return try {
            val response = apiService.getCities()
            handleResponse(response)
        } catch (e: HttpException) {
            NetworkResult.Failure(e)
        } catch (e: IOException) {
            NetworkResult.Failure(e)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    suspend fun getPrayerTimes(city: String): NetworkResult<EncryptedResponse> {
        return try {
            val response = apiService.getPrayerTimes(city)
            handleResponse(response)
        } catch (e: HttpException) {
            NetworkResult.Failure(e)
        } catch (e: IOException) {
            NetworkResult.Failure(e)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    suspend fun getAthkar(): NetworkResult<EncryptedResponse> {
        return try {
            val response = apiService.getAthkar()
            handleResponse(response)
        } catch (e: HttpException) {
            NetworkResult.Failure(e)
        } catch (e: IOException) {
            NetworkResult.Failure(e)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    private inline fun <reified T> handleResponse(response: Response<T>): NetworkResult<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Failure(NullPointerException("Response body is null"))
            }
        } else {
            NetworkResult.Failure(HttpException(response))
        }
    }
}