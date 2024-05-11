package com.app.athkar.home.domain

import com.app.athkar.core.network.NetworkResult
import com.app.athkar.home.data.GetCitiesResponse

interface HomeRepository {
    suspend fun getCities(): NetworkResult<GetCitiesResponse>
}