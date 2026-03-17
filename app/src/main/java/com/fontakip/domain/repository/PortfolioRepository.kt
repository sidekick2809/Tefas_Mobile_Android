package com.fontakip.domain.repository

import com.fontakip.domain.model.Portfolio
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun getAllPortfolios(): Flow<List<Portfolio>>
    suspend fun getPortfolioById(id: Long): Portfolio?
    suspend fun insertPortfolio(portfolio: Portfolio): Long
    suspend fun updatePortfolio(portfolio: Portfolio)
    suspend fun deletePortfolio(portfolio: Portfolio)
    suspend fun getPortfolioCount(): Int
}
