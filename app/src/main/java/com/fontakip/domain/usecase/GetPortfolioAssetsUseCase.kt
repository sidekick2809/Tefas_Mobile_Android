package com.fontakip.domain.usecase

import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.TransactionType
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPortfolioAssetsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository
) {
    operator fun invoke(portfolioId: Long): Flow<List<Asset>> {
        return transactionRepository.getTransactionsByPortfolio(portfolioId).map { transactions ->
            val fundCodes = transactions.map { it.fundCode }.distinct()
            
            fundCodes.mapNotNull { code ->
                val fundTransactions = transactions.filter { it.fundCode == code }.sortedBy { it.date }
                
                // Get fund info from AssetRepository (TEFAS data)
                val fundInfo = assetRepository.getAssetByCodeGlobal(code)
                
                // AĞIRLIKLI ORTALAMA HESAPLAMA (FIFO - First In First Out)
                // Satışlarda önce alınan lotlar satılır
                var totalLots = 0.0
                var totalCost = 0.0
                var realizedProfit = 0.0
                
                // Her işlemi sırayla işle
                for (transaction in fundTransactions) {
                    val quantity = transaction.quantity
                    val price = transaction.price
                    val value = quantity * price
                    
                    if (transaction.transactionType == TransactionType.BUY) {
                        // Alım: lot ve maliyeti ekle
                        totalLots += quantity
                        totalCost += value
                    } else {
                        // Satış: Önceki ortalama fiyat üzerinden kar/zarar hesapla
                        if (totalLots > 0) {
                            val averagePrice = totalCost / totalLots
                            val sellValue = quantity * averagePrice
                            realizedProfit += value - sellValue
                            
                            // Lot ve maliyeti azalt
                            totalLots -= quantity
                            totalCost -= sellValue
                            
                            // Eğer lot sıfırın altına düştüyse düzelt
                            if (totalLots <= 0) {
                                totalLots = 0.0
                                totalCost = 0.0
                            }
                        }
                    }
                }
                
                // Eğer mevcut lot yoksa fonu dahil etme
                if (totalLots <= 0) return@mapNotNull null
                
                val averagePrice = if (totalLots > 0) totalCost / totalLots else 0.0
                val currentPrice = fundInfo?.currentPrice ?: 0.0
                val currentValue = totalLots * currentPrice

                
                Asset(
                    id = fundInfo?.id ?: 0,
                    portfolioId = portfolioId,
                    code = code,
                    name = fundInfo?.name ?: code,
                    type = fundInfo?.type ?: com.fontakip.domain.model.AssetType.MUTUAL_FUND,
                    units = totalLots,
                    purchasePrice = averagePrice,
                    purchaseDate = fundTransactions.firstOrNull()?.date ?: System.currentTimeMillis(),
                    currentPrice = currentPrice,
                    lastUpdateDate = fundInfo?.lastUpdateDate ?: System.currentTimeMillis(),
                    fontip = fundInfo?.fontip ?: "",
                    dailyChangePercent = fundInfo?.dailyChangePercent ?: 0.0,
                    weeklyChangePercent = fundInfo?.weeklyChangePercent ?: 0.0,
                    monthlyChangePercent = fundInfo?.monthlyChangePercent ?: 0.0,
                    threeMonthChangePercent = fundInfo?.threeMonthChangePercent ?: 0.0,
                    sixMonthChangePercent = fundInfo?.sixMonthChangePercent ?: 0.0,
                    yearToDateChangePercent = fundInfo?.yearToDateChangePercent ?: 0.0,
                    oneYearChangePercent = fundInfo?.oneYearChangePercent ?: 0.0,
                    threeYearChangePercent = fundInfo?.threeYearChangePercent ?: 0.0,
                    fiveYearChangePercent = fundInfo?.fiveYearChangePercent ?: 0.0,
                    fundType = fundInfo?.fundType ?: "",
                    turC = fundInfo?.turC ?: "",
                    company = fundInfo?.company ?: "",
                    tefasStatus = fundInfo?.tefasStatus ?: "",
                    priceYesterday = fundInfo?.priceYesterday ?: 0.0,
                    priceSevenDaysAgo = fundInfo?.priceSevenDaysAgo ?: 0.0,
                    isFavorite = fundInfo?.isFavorite ?: false
                )
            }
        }
    }
}
