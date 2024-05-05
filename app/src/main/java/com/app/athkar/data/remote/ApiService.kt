package com.app.athkar.data.remote

import com.app.athkar.data.model.network.encrypted_response.EncryptedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

/**
 * ApiService is an interface that defines the endpoints of the API.
 */
interface ApiService {
    @GET("/android/cities.json")
    suspend fun getCities(): Response<EncryptedResponse>

    @GET("/android/{city}.json")
    suspend fun getPrayerTimes(@Path("city") city: String): Response<EncryptedResponse>

    @GET("/android/athkar.json")
    suspend fun getAthkar(): Response<EncryptedResponse>
}