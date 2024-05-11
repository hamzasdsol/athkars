package com.app.athkar.home.data

import com.app.athkar.core.network.NetworkResult
import com.app.athkar.core.util.CryptLib
import com.app.athkar.core.util.toResponseModel
import com.app.athkar.core.network.remote.AppDataSource
import com.app.athkar.home.domain.HomeRepository
import javax.inject.Inject

class DefaultHomeRepository @Inject constructor(
    private val dataSource: AppDataSource
) : HomeRepository {

    override suspend fun getCities(): NetworkResult<GetCitiesResponse> {
        return when (val response = dataSource.getCities()) {
            is NetworkResult.Success -> {
                try {
                    val decryptedResponse = CryptLib.decryptData(response.data.data)
                    val getCitiesResponse: GetCitiesResponse = decryptedResponse.toResponseModel()
                    NetworkResult.Success(getCitiesResponse)
                } catch (e: Exception) {
                    NetworkResult.Failure(e)
                }
            }

            is NetworkResult.Failure -> {
                NetworkResult.Failure(response.exception)
            }
        }
    }
}