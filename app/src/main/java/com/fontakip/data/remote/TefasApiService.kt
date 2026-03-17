package com.fontakip.data.remote

import com.fontakip.data.remote.model.TefasResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TefasApiService {
    
    @POST("SearchFunds")
    @FormUrlEncoded
    suspend fun searchFunds(
        @Field("searchText") searchText: String
    ): TefasResponse
    
    @POST("BindHistoryInfo")
    @FormUrlEncoded
    suspend fun getFundHistory(
        @Field("fontip") fontip: String,
        @Field("sfontur") sfontur: String = "",
        @Field("fonkod") fonkod: String = "",
        @Field("fongrup") fongrup: String = "",
        @Field("bastarih") startDate: String,
        @Field("bittarih") endDate: String,
        @Field("fontkod") fontkod: String = "",
        @Field("fonunvantip") fonunvantip: String = "",
        @Field("kurucukod") kurucukod: String = ""
    ): TefasResponse

    @POST("BindComparisonFundReturns")
    @FormUrlEncoded
    suspend fun getFundReturns(
        @Field("calismatipi") calismatipi: Int = 2,
        @Field("fontip") fontip: String,
        @Field("sfontur") sfontur: String = "",
        @Field("kurucukod") kurucukod: String = "",
        @Field("fongrup") fongrup: String = "",
        @Field("bastarih") startDate: String,
        @Field("bittarih") endDate: String,
        @Field("fontkod") fontkod: String = "",
        @Field("fonunvantip") fonunvantip: String = "",
        @Field("strperiod") strperiod: String = "1,1,1,1,1,1,1",
        @Field("islemdurum") islemdurum: String = ""
    ): TefasResponse

    @POST("BindComparisonFundReturns")
    @FormUrlEncoded
    suspend fun getFundStatus(
        @Field("calismatipi") calismatipi: Int = 2,
        @Field("fontip") fontip: String,
        @Field("sfontur") sfontur: String = "",
        @Field("kurucukod") kurucukod: String = "",
        @Field("fongrup") fongrup: String = "",
        @Field("bastarih") startDate: String,
        @Field("bittarih") endDate: String,
        @Field("fontkod") fontkod: String = "",
        @Field("fonunvantip") fonunvantip: String = "",
        @Field("strperiod") strperiod: String = "1,1,1,1,1,1,1",
        @Field("islemdurum") islemdurum: String
    ): TefasResponse
}
