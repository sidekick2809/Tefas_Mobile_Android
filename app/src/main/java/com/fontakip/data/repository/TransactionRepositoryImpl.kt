package com.fontakip.data.repository

import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.domain.model.Transaction
import com.fontakip.domain.model.TransactionType
import com.fontakip.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    
    override fun getTransactionsByPortfolio(portfolioId: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByPortfolio(portfolioId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTransactionsByFundCode(fundCode: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByFundCode(fundCode).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getTransactionsByPortfolioAndFund(portfolioId: Long, fundCode: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByPortfolioAndFund(portfolioId, fundCode).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }
    
    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }
    
    override suspend fun deleteTransactionsByPortfolio(portfolioId: Long) {
        transactionDao.deleteTransactionsByPortfolio(portfolioId)
    }
    
    private fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = id,
            portfolioId = portfolioId,
            fundCode = fundCode,
            fundName = fundName,
            transactionType = if (transactionType == "BUY") TransactionType.BUY else TransactionType.SELL,
            price = price,
            quantity = quantity,
            date = date,
            createdAt = createdAt
        )
    }
    
    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            portfolioId = portfolioId,
            fundCode = fundCode,
            fundName = fundName,
            transactionType = if (transactionType == TransactionType.BUY) "BUY" else "SELL",
            price = price,
            quantity = quantity,
            date = date,
            createdAt = createdAt
        )
    }
}
