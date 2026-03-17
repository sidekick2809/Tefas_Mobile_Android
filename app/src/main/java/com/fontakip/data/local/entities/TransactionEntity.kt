package com.fontakip.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = PortfolioEntity::class,
            parentColumns = ["id"],
            childColumns = ["portfolioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("portfolioId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portfolioId: Long,
    val fundCode: String,
    val fundName: String,
    val transactionType: String, // "BUY" or "SELL"
    val price: Double,
    val quantity: Double,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
