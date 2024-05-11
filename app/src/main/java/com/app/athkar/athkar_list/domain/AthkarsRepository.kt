package com.app.athkar.athkar_list.domain

import com.app.athkar.athkar_list.data.GetAthkarResponse
import com.app.athkar.core.network.NetworkResult

interface AthkarsRepository {
    suspend fun getAthkars(): NetworkResult<GetAthkarResponse>
}