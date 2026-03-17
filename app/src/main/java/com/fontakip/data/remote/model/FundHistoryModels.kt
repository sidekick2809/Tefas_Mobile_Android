package com.fontakip.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Fund history data model for chart display
 * Represents a single data point in the fund's historical performance
 */
data class FundHistoryItem(
    @SerializedName("TARIH")
    val date: String = "",           // Date in format: dd.MM.yyyy
    
    @SerializedName("FIYAT")
    val price: Double = 0.0,         // Fund price
    
    @SerializedName("GETIRI")
    val returnPercent: Double = 0.0, // Return percentage
    
    @SerializedName("FONKODU")
    val fundCode: String = ""        // Fund code
)

/**
 * Response wrapper for fund history API
 */
data class FundHistoryResponse(
    @SerializedName("data")
    val data: List<FundHistoryItem>? = null,
    
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String = ""
)

/**
 * Chart data point for Compose LineChart
 */
data class ChartDataPoint(
    val timestamp: Long,     // Unix timestamp in milliseconds
    val value: Double,       // Price or return value
    val label: String        // Display label (date)
)

/**
 * Available time periods for chart
 */
enum class ChartPeriod(val label: String, val days: Int) {
    ONE_MONTH("1A", 30),
    THREE_MONTHS("3A", 90),
    SIX_MONTHS("6A", 180),
    ONE_YEAR("1Y", 365),
    THREE_YEARS("3Y", 1095),
    FIVE_YEARS("5Y", 1825)
}
