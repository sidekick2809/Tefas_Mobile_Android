package com.fontakip.domain.repository

import com.fontakip.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionsByPortfolio(portfolioId: Long): Flow<List<Transaction>>
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByFundCode(fundCode: String): Flow<List<Transaction>>
    fun getTransactionsByPortfolioAndFund(portfolioId: Long, fundCode: String): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionsByPortfolio(portfolioId: Long)
}
