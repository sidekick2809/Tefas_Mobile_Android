package com.fontakip.data.repository

import com.fontakip.data.local.dao.PortfolioDao
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioDao
) : PortfolioRepository {

    override fun getAllPortfolios(): Flow<List<Portfolio>> {
        return portfolioDao.getAllPortfolios().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPortfolioById(id: Long): Portfolio? {
        return portfolioDao.getPortfolioById(id)?.toDomain()
    }

    override suspend fun insertPortfolio(portfolio: Portfolio): Long {
        return portfolioDao.insertPortfolio(portfolio.toEntity())
    }

    override suspend fun updatePortfolio(portfolio: Portfolio) {
        portfolioDao.updatePortfolio(portfolio.toEntity())
    }

    override suspend fun deletePortfolio(portfolio: Portfolio) {
        portfolioDao.deletePortfolio(portfolio.toEntity())
    }

    override suspend fun getPortfolioCount(): Int {
        return portfolioDao.getPortfolioCount()
    }
}
