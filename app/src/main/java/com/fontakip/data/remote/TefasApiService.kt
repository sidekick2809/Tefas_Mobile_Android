package com.fontakip.data.remote

import com.fontakip.data.remote.model.FonGetiriBazliBilgiGetirRequest
import com.fontakip.data.remote.model.FonGnlBlgSiraliGetirRequest
import com.fontakip.data.remote.model.TefasResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TefasApiService {
    
    @POST("DB/SearchFunds")
    @FormUrlEncoded
    suspend fun searchFunds(
        @Field("searchText") searchText: String
    ): TefasResponse
    
    @POST("funds/fonGnlBlgSiraliGetir")
    suspend fun getFundHistory(
        @Body request: FonGnlBlgSiraliGetirRequest
    ): TefasResponse

    @POST("funds/fonGetiriBazliBilgiGetir")
    suspend fun getFundReturns(
        @Body request: FonGetiriBazliBilgiGetirRequest
    ): TefasResponse

    @POST("funds/fonGetiriBazliBilgiGetir")
    suspend fun getFundStatus(
        @Body request: FonGetiriBazliBilgiGetirRequest
    ): TefasResponse
}
