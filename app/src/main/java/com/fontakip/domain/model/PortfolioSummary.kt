package com.fontakip.domain.model

data class PortfolioSummary(
    val totalValue: Double,
    val totalCost: Double,
    val profitLossTL: Double,
    val profitLossPercent: Double,
    val dailyChangePercent: Double = 0.0,
    val weeklyChangePercent: Double = 0.0,
    val dailyChangeTL: Double = 0.0,
    val weeklyChangeTL: Double = 0.0,
    val lastUpdateTime: Long = System.currentTimeMillis()
)
