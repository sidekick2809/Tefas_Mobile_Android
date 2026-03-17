package com.fontakip.domain.model

data class Transaction(
    val id: Long = 0,
    val portfolioId: Long,
    val portfolioName: String = "",
    val fundCode: String,
    val fundName: String,
    val transactionType: TransactionType,
    val price: Double,
    val quantity: Double,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    BUY,
    SELL
}
