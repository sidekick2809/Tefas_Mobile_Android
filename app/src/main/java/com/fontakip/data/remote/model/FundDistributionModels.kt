package com.fontakip.data.remote.model

import com.google.gson.annotations.SerializedName

data class FundDistributionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: FundDistributionData?
)

data class FundDistributionData(
    @SerializedName("items") val items: List<FundDistributionItem>?,
    @SerializedName("meta") val meta: FundDistributionMeta?
)

data class FundDistributionItem(
    @SerializedName("yabanci") val yabanci: Int?,
    @SerializedName("etf") val etf: Int?,
    @SerializedName("fiyat") val fiyat: String?,
    @SerializedName("degisim") val degisim: String?,
    @SerializedName("agirlik") val agirlik: String, // E.g., "26.62"
    @SerializedName("oran") val oran: String?,
    @SerializedName("hisseKodu") val hisseKodu: String,
    @SerializedName("fiyatCanli") val fiyatCanli: String?,
    @SerializedName("degisimCanli") val degisimCanli: String?,
    @SerializedName("oranCanli") val oranCanli: String?,
    @SerializedName("sirketAdi") val sirketAdi: String?,
    @SerializedName("fonAdi2") val fonAdi2: String?,
    @SerializedName("fonFiyat") val fonFiyat: String?,
    @SerializedName("fonGetiri") val fonGetiri: String?,
    @SerializedName("sirketKodu") val sirketKodu: String?,
    @SerializedName("hisseKategori") val hisseKategori: Int?,
    @SerializedName("sektorAdi") val sektorAdi: String?,
    @SerializedName("eskiAgirlik") val eskiAgirlik: String?,
    @SerializedName("fark") val fark: String?
)

data class FundDistributionMeta(
    @SerializedName("aciklamaTarihi") val aciklamaTarihi: String?,
    @SerializedName("oncekiAy") val oncekiAy: Int?,
    @SerializedName("oncekiYil") val oncekiYil: Int?
)
