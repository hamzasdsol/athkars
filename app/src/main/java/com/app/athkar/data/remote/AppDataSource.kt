package com.app.athkar.data.remote

import com.app.athkar.data.model.network.encrypted_response.EncryptedResponse
import javax.inject.Inject
import com.app.athkar.domain.Result
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AppDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getCities(): Result<EncryptedResponse> {
        return try {
            val response = apiService.getCities()
            handleResponse(response)
        } catch (e: HttpException) {
            Result.Failure(e)
        } catch (e: IOException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun getPrayerTimes(city: String): Result<EncryptedResponse> {
        return try {
            val response = apiService.getPrayerTimes(city)
            handleResponse(response)
        } catch (e: HttpException) {
            Result.Failure(e)
        } catch (e: IOException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun getAthkar(): Result<EncryptedResponse> {
        return try {
            val response = apiService.getAthkar()
            handleResponse(response)
        } catch (e: HttpException) {
            Result.Failure(e)
        } catch (e: IOException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    private inline fun <reified T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Failure(NullPointerException("Response body is null"))
            }
        } else {
            Result.Failure(HttpException(response))
        }
    }
}