package com.fontakip.domain.model

data class AssetPerformance(
    val assetId: Long,
    val dailyChangePercent: Double,
    val dailyChangeTL: Double,
    val weeklyChangePercent: Double,
    val weeklyChangeTL: Double,
    val totalGainLossPercent: Double,
    val totalGainLossTL: Double,
    val dailyAverageChangePercent: Double,
    val dailyAverageChangeTL: Double,
    val investmentDays: Int
)
