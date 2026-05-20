package com.fontakip.data.remote

import com.fontakip.data.remote.model.FundDistributionResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FvtApiService {
    @GET("funds/{fundCode}/distribution")
    suspend fun getFundDistribution(
        @Path("fundCode") fundCode: String
    ): FundDistributionResponse
}
