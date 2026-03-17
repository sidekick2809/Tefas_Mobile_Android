package com.fontakip.data.remote.model

import com.google.gson.annotations.SerializedName

// Response wrapper
data class TefasResponse(
    @SerializedName("data")
    val data: List<TefasFund>? = null,
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String = ""
)

// Fund data model - matching TEFAS API response fields
data class TefasFund(
    @SerializedName("FONKODU")
    val code: String = "",
    
    @SerializedName("FONUNVAN")
    val name: String = "",
    
    @SerializedName("FIYAT")
    val price: Double = 0.0,
    
    @SerializedName("TARIH")
    val date: String = "",
    
    @SerializedName("GETIRI1A")
    val return1Month: Double? = null,
    
    @SerializedName("GETIRI3A")
    val return3Months: Double? = null,
    
    @SerializedName("GETIRI6A")
    val return6Months: Double? = null,
    
    @SerializedName("GETIRIYB")
    val returnYearToDate: Double? = null,
    
    @SerializedName("GETIRI1Y")
    val return1Year: Double? = null,
    
    @SerializedName("GETIRI3Y")
    val return3Years: Double? = null,
    
    @SerializedName("GETIRI5Y")
    val return5Years: Double? = null,
    
    @SerializedName("FONTURACIKLAMA")
    val fundType: String = ""
)

// Status response for TEFAS open/closed status
data class TefasStatusResponse(
    @SerializedName("data")
    val data: List<TefasStatusFund>? = null
)

data class TefasStatusFund(
    @SerializedName("FONKODU")
    val code: String = ""
)
