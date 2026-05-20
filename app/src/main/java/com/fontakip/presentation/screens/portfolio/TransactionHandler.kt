package com.fontakip.presentation.screens.portfolio

import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.data.remote.model.FundHistoryItem

/**
 * Interface for ViewModels that handle transaction operations.
 * Used to support multiple ViewModel types in screens that need transaction functionality.
 */
interface TransactionHandler {
    fun getTransactionsByFundCode(fundCode: String, onResult: (List<TransactionEntity>) -> Unit)
    fun getTransactionsByFundCodeAndPortfolioId(fundCode: String, portfolioId: Long, onResult: (List<TransactionEntity>) -> Unit)
    fun deleteTransaction(transaction: TransactionEntity)
    fun getFundPriceHistory(fundCode: String, period: Int, onResult: (List<FundHistoryItem>) -> Unit)
    fun getFundDistribution(fundCode: String, onResult: (com.fontakip.data.remote.model.FundDistributionResponse?) -> Unit)
}
