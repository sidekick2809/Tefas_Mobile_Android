package com.fontakip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetType
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.model.PortfolioSummary
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Grafik verileri için yardımcı data class
 */
data class AssetChartData(
    val code: String,
    val name: String,
    val value: Double,      // Yüzde değeri
    val absoluteValue: Double // Mutlak değer (pie chart için)
)

data class PortfolioAnalyticsUiState(
    val isLoading: Boolean = true,
    val portfolios: List<Portfolio> = emptyList(),
    val currentPortfolioIndex: Int = 0,
    val assets: List<Asset> = emptyList(),
    val summary: PortfolioSummary = PortfolioSummary(
        totalValue = 0.0,
        totalCost = 0.0,
        profitLossTL = 0.0,
        profitLossPercent = 0.0
    ),
    // Grafik verileri
    val weightChartData: List<AssetChartData> = emptyList(),  // Ağırlık grafiği
    val profitLossChartData: List<AssetChartData> = emptyList(), // Kar grafiği
    val dailyChangeChartData: List<AssetChartData> = emptyList(), // Günlük değişim grafiği
    val pieChartData: List<AssetChartData> = emptyList(), // Pie chart verileri
    val error: String? = null
)

@HiltViewModel
class PortfolioAnalyticsViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val assetRepository: AssetRepository,
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioAnalyticsUiState())
    val uiState: StateFlow<PortfolioAnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadPortfolios()
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            portfolioRepository.getAllPortfolios().collectLatest { portfolios ->
                _uiState.update { state ->
                    state.copy(
                        portfolios = portfolios,
                        isLoading = false
                    )
                }
                if (portfolios.isNotEmpty()) {
                    // Her zaman "Ana Portföy"ü seç (varsa), yoksa ilk portföyü seç
                    val anaPortfolioIndex = portfolios.indexOfFirst { it.name == "Ana Portföy" }
                    val defaultIndex = if (anaPortfolioIndex >= 0) anaPortfolioIndex else 0
                    _uiState.update { it.copy(currentPortfolioIndex = defaultIndex) }
                    loadAssetsForCurrentPortfolio()
                }
            }
        }
    }

    fun selectPortfolio(index: Int) {
        if (index in _uiState.value.portfolios.indices) {
            _uiState.update { it.copy(currentPortfolioIndex = index) }
            loadAssetsForCurrentPortfolio()
        }
    }

    private fun loadAssetsForCurrentPortfolio() {
        val portfolio = _uiState.value.portfolios.getOrNull(_uiState.value.currentPortfolioIndex)
        portfolio?.let { p ->
            viewModelScope.launch {
                val transactionsFlow = transactionDao.getTransactionsByPortfolio(p.id)
                
                transactionsFlow.collectLatest { transactions ->
                    if (transactions.isEmpty()) {
                        _uiState.update { state ->
                            state.copy(
                                assets = emptyList(),
                                summary = PortfolioSummary(
                                    totalValue = 0.0,
                                    totalCost = 0.0,
                                    profitLossTL = 0.0,
                                    profitLossPercent = 0.0
                                ),
                                weightChartData = emptyList(),
                                profitLossChartData = emptyList(),
                                dailyChangeChartData = emptyList(),
                                pieChartData = emptyList()
                            )
                        }
                        return@collectLatest
                    }
                    
                    val uniqueFundCodes = transactions.map { it.fundCode }.distinct()
                    
                    val calculatedAssets = uniqueFundCodes.mapNotNull { fundCode ->
                        try {
                            val fundTransactions = transactions.filter { it.fundCode == fundCode }
                            if (fundTransactions.isEmpty()) return@mapNotNull null
                            
                            val fundInfo = assetRepository.getAssetByCodeGlobal(fundCode)
                            val currentPrice = fundInfo?.currentPrice ?: 0.0
                            
                            val avgResult = calculateAveragePrice(fundCode, p.id, currentPrice)
                            
                            if (avgResult.totalLots > 0) {
                                Asset(
                                    id = fundCode.hashCode().toLong(),
                                    portfolioId = p.id,
                                    code = fundCode,
                                    name = fundTransactions.firstOrNull()?.fundName ?: "",
                                    type = AssetType.MUTUAL_FUND,
                                    units = avgResult.totalLots,
                                    purchasePrice = avgResult.averagePrice,
                                    purchaseDate = fundTransactions.minOfOrNull { it.date } ?: 0L,
                                    currentPrice = currentPrice,
                                    lastUpdateDate = System.currentTimeMillis(),
                                    fontip = fundInfo?.fontip ?: "",
                                    dailyChangePercent = fundInfo?.dailyChangePercent ?: 0.0,
                                    weeklyChangePercent = fundInfo?.weeklyChangePercent ?: 0.0,
                                    monthlyChangePercent = fundInfo?.monthlyChangePercent ?: 0.0,
                                    threeMonthChangePercent = fundInfo?.threeMonthChangePercent ?: 0.0,
                                    sixMonthChangePercent = fundInfo?.sixMonthChangePercent ?: 0.0,
                                    yearToDateChangePercent = fundInfo?.yearToDateChangePercent ?: 0.0,
                                    oneYearChangePercent = fundInfo?.oneYearChangePercent ?: 0.0,
                                    threeYearChangePercent = fundInfo?.threeYearChangePercent ?: 0.0,
                                    fiveYearChangePercent = 0.0,
                                    fundType = fundInfo?.fundType ?: "",
                                    turC = fundInfo?.turC ?: "",
                                    company = fundInfo?.company ?: "",
                                    tefasStatus = fundInfo?.tefasStatus ?: "",
                                    priceYesterday = fundInfo?.priceYesterday ?: 0.0,
                                    priceSevenDaysAgo = fundInfo?.priceSevenDaysAgo ?: 0.0,
                                    isFavorite = false
                                )
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    val summary = calculatePortfolioSummary(calculatedAssets)
                    
                    // Grafik verilerini hazırla
                    val portfolioTotalValue = summary.totalValue
                    
                    // Ağırlık grafiği (büyükten küçüğe sıralı)
                    val weightData = calculatedAssets
                        .filter { it.totalValue > 0 }
                        .map { asset ->
                            AssetChartData(
                                code = asset.code,
                                name = asset.name,
                                value = if (portfolioTotalValue > 0) (asset.totalValue / portfolioTotalValue) * 100 else 0.0,
                                absoluteValue = asset.totalValue
                            )
                        }
                        .sortedByDescending { it.value }
                    
                    // Kar grafiği (büyükten küçüğe sıralı)
                    val profitLossData = calculatedAssets
                        .map { asset ->
                            AssetChartData(
                                code = asset.code,
                                name = asset.name,
                                value = asset.profitLossPercent,
                                absoluteValue = asset.profitLossTL
                            )
                        }
                        .sortedByDescending { it.value }
                    
                    // Günlük % değişim grafiği (büyükten küçüğe sıralı)
                    val dailyChangeData = calculatedAssets
                        .map { asset ->
                            AssetChartData(
                                code = asset.code,
                                name = asset.name,
                                value = asset.dailyChangePercent,
                                absoluteValue = asset.totalValue * (asset.dailyChangePercent / 100)
                            )
                        }
                        .sortedByDescending { it.value }
                    
                    // Pie chart verileri (ağırlık verileri ile aynı)
                    val pieData = weightData
                    
                    _uiState.update { state ->
                        state.copy(
                            assets = calculatedAssets,
                            summary = summary,
                            weightChartData = weightData,
                            profitLossChartData = profitLossData,
                            dailyChangeChartData = dailyChangeData,
                            pieChartData = pieData
                        )
                    }
                }
            }
        }
    }

    private fun calculatePortfolioSummary(assets: List<Asset>): PortfolioSummary {
        val totalValue = assets.sumOf { it.totalValue }
        val totalCost = assets.sumOf { it.totalCost }
        val profitLossTL = totalValue - totalCost
        val profitLossPercent = if (totalCost > 0) (profitLossTL / totalCost) * 100 else 0.0

        val dailyChangeTL = assets.sumOf { asset ->
            (asset.units * asset.currentPrice) - (asset.units * asset.priceYesterday)
        }

        val dailyChangePercent = if (totalValue > 0) {
            (dailyChangeTL / totalValue) * 100
        } else {
            0.0
        }

        // Haftalık TL değişimini hesapla
        val weeklyChangeTL = assets.sumOf { asset ->
            (asset.units * asset.currentPrice) - (asset.units * asset.priceSevenDaysAgo)
        }

        // Ağırlıklı ortalama haftalık yüzde değişimi
        val weeklyChangePercent = if (totalValue > 0) {
            (weeklyChangeTL / totalValue) * 100
        } else {
            0.0
        }

        return PortfolioSummary(
            totalValue = totalValue,
            totalCost = totalCost,
            profitLossTL = profitLossTL,
            profitLossPercent = profitLossPercent,
            dailyChangePercent = dailyChangePercent,
            dailyChangeTL = dailyChangeTL,
            weeklyChangePercent = weeklyChangePercent,
            weeklyChangeTL = weeklyChangeTL,
            lastUpdateTime = System.currentTimeMillis()
        )
    }

    suspend fun calculateAveragePrice(fundCode: String, portfolioId: Long, currentPrice: Double = 0.0): AveragePriceResult {
        val transactions = transactionDao.getTransactionsByPortfolioAndFundOrderedByDate(portfolioId, fundCode)
            .filter { it.fundCode == fundCode }

        if (transactions.isEmpty()) {
            return AveragePriceResult(
                averagePrice = 0.0,
                totalLots = 0.0,
                totalCost = 0.0,
                realizedProfit = 0.0,
                currentValue = 0.0,
                unrealizedProfit = 0.0,
                profitPercent = 0.0
            )
        }

        var totalLots = 0.0
        var totalCost = 0.0
        var realizedProfit = 0.0

        for (transaction in transactions) {
            val quantity = transaction.quantity
            val price = transaction.price
            val transactionValue = quantity * price

            if (transaction.transactionType == "BUY") {
                totalLots += quantity
                totalCost += transactionValue
            } else {
                val prevLots = totalLots
                val prevCost = totalCost

                if (prevLots > 0) {
                    val averagePrice = prevCost / prevLots
                    val sellValue = quantity * averagePrice
                    val profit = transactionValue - sellValue

                    realizedProfit += profit
                    totalLots -= quantity
                    totalCost -= sellValue

                    if (totalLots <= 0) {
                        totalLots = 0.0
                        totalCost = 0.0
                    }
                }
            }
        }

        val averagePrice = if (totalLots > 0) totalCost / totalLots else 0.0
        val currentValue = totalLots * currentPrice
        val unrealizedProfit = currentValue - totalCost
        val profitPercent = if (totalCost > 0) (unrealizedProfit / totalCost) * 100 else 0.0

        return AveragePriceResult(
            averagePrice = averagePrice,
            totalLots = totalLots,
            totalCost = totalCost,
            realizedProfit = realizedProfit,
            currentValue = currentValue,
            unrealizedProfit = unrealizedProfit,
            profitPercent = profitPercent
        )
    }

    data class AveragePriceResult(
        val averagePrice: Double,
        val totalLots: Double,
        val totalCost: Double,
        val realizedProfit: Double,
        val currentValue: Double,
        val unrealizedProfit: Double,
        val profitPercent: Double
    )
}
