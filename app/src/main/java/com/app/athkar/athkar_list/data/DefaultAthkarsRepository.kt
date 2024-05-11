package com.app.athkar.athkar_list.data

import com.app.athkar.athkar_list.domain.AthkarsRepository
import com.app.athkar.core.util.CryptLib
import com.app.athkar.core.util.toResponseModel
import com.app.athkar.core.network.remote.AppDataSource
import com.app.athkar.core.network.NetworkResult

import javax.inject.Inject

class DefaultAthkarsRepository @Inject constructor(
    private val dataSource: AppDataSource
): AthkarsRepository {
    override suspend fun getAthkars(): NetworkResult<GetAthkarResponse> {
        when (val response = dataSource.getAthkar()) {
            is NetworkResult.Success -> {

                return try {
                    val decryptedResponse = CryptLib.decryptData(response.data.data)
                    val getAthkarResponse: GetAthkarResponse = decryptedResponse.toResponseModel()
                    NetworkResult.Success(getAthkarResponse)
                } catch (e: Exception) {
                    NetworkResult.Failure(e)
                }
            }

            is NetworkResult.Failure -> {
                return NetworkResult.Failure(response.exception)
            }
        }
    }
}