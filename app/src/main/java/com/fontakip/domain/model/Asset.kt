package com.fontakip.domain.model

enum class AssetType {
    MUTUAL_FUND,
    STOCK
}

data class Asset(
    val id: Long = 0,
    val portfolioId: Long,
    val code: String,
    val name: String,
    val type: AssetType,
    val units: Double,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val currentPrice: Double = 0.0,
    val lastUpdateDate: Long = 0,
    // TEFAS data fields
    val fontip: String = "", // YAT or EMK
    val dailyChangePercent: Double = 0.0,
    val weeklyChangePercent: Double = 0.0,
    val monthlyChangePercent: Double = 0.0,
    val threeMonthChangePercent: Double = 0.0,
    val sixMonthChangePercent: Double = 0.0,
    val yearToDateChangePercent: Double = 0.0,
    val oneYearChangePercent: Double = 0.0,
    val threeYearChangePercent: Double = 0.0,
    val fiveYearChangePercent: Double = 0.0,
    val fundType: String = "",
    val turC: String = "",
    val company: String = "",
    val tefasStatus: String = "",
    val priceYesterday: Double = 0.0,
    val priceSevenDaysAgo: Double = 0.0,
    val isFavorite: Boolean = false
) {
    val totalValue: Double
        get() = units * currentPrice

    val totalCost: Double
        get() = units * purchasePrice

    val averagePurchasePrice: Double
        get() = if (units > 0) totalCost / units else 0.0

    val profitLossTL: Double
        get() = totalValue - totalCost

    val profitLossPercent: Double
        get() = if (totalCost > 0) ((totalValue - totalCost) / totalCost) * 100 else 0.0
}
