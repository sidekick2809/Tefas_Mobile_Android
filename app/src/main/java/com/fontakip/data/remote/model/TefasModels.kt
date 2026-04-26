package com.fontakip.data.remote.model

import com.google.gson.annotations.SerializedName

data class FonGnlBlgSiraliGetirRequest(
    val fonTipi: String = "YAT",
    val fonKodu: String? = null,
    val aramaMetni: String? = null,
    val fonTurKod: String? = null,
    val fonGrubu: String? = null,
    val sfonTurKod: String? = null,
    val basTarih: String,
    val bitTarih: String,
    val basSira: Int = 1,
    val bitSira: Int = 2100,
    val fonTurAciklama: String? = null,
    val dil: String = "TR",
    val kurucuKod: String? = null
)

data class FonGetiriBazliBilgiGetirRequest(
    val dil: String = "TR",
    val fonTipi: String = "YAT",
    val kurucuKodu: String? = null,
    val sfonTurKod: String? = null,
    val fonTurAciklama: String? = null,
    val islem: Int? = null,
    val fonTurKod: String? = null,
    val fonGrubu: String? = null,
    val donemGetiri1a: String = "1",
    val donemGetiri3a: String = "1",
    val donemGetiri6a: String = "1",
    val donemGetiri1y: String = "1",
    val donemGetiriyb: String = "1",
    val donemGetiri3y: String = "1",
    val donemGetiri5y: String = "1",
    val basTarih: String? = null,
    val bitTarih: String? = null,
    val calismaTipi: Int = 2,
    val getiriOrani: String = "1"
)
// Response wrapper
data class TefasResponse(
    @SerializedName("resultList", alternate = ["data", "Data", "result"])
    val data: List<TefasFund>? = null,
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("errorCode")
    val errorCode: String? = null,
    @SerializedName("errorMessage")
    val errorMessage: String? = null
)

// Fund data model - matching TEFAS API response fields
data class TefasFund(
    @SerializedName("FONKODU", alternate = ["fonKodu"])
    val code: String = "",
    
    @SerializedName("FONUNVAN", alternate = ["fonUnvan"])
    val name: String = "",
    
    @SerializedName("FIYAT", alternate = ["fiyat"])
    val price: Double = 0.0,
    
    @SerializedName("TARIH", alternate = ["tarih"])
    val date: String = "",
    
    @SerializedName("GETIRI1A", alternate = ["getiri1a"])
    val return1Month: Double? = null,
    
    @SerializedName("GETIRI3A", alternate = ["getiri3a"])
    val return3Months: Double? = null,
    
    @SerializedName("GETIRI6A", alternate = ["getiri6a"])
    val return6Months: Double? = null,
    
    @SerializedName("GETIRIYB", alternate = ["getiriyb"])
    val returnYearToDate: Double? = null,
    
    @SerializedName("GETIRI1Y", alternate = ["getiri1y"])
    val return1Year: Double? = null,
    
    @SerializedName("GETIRI3Y", alternate = ["getiri3y"])
    val return3Years: Double? = null,
    
    @SerializedName("GETIRI5Y", alternate = ["getiri5y"])
    val return5Years: Double? = null,
    
    @SerializedName("FONTURACIKLAMA", alternate = ["fonTurAciklama"])
    val fundType: String = "",
    
    @SerializedName("tefasDurum")
    val tefasDurum: Boolean? = null
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
