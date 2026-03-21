package com.fontakip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetPerformance
import com.fontakip.domain.model.AssetType
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.model.PortfolioSummary
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.PortfolioRepository
import com.fontakip.presentation.screens.portfolio.TransactionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class PortfolioUiState(
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
    val assetPerformances: Map<Long, AssetPerformance> = emptyMap(),
    val selectedAsset: Asset? = null,
    val transactions: List<TransactionEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val assetRepository: AssetRepository,
    private val transactionDao: TransactionDao
) : ViewModel(), TransactionHandler {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        loadPortfolios()
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Check if portfolios exist, if not create sample data
            val count = portfolioRepository.getPortfolioCount()
            if (count == 0) {
                createSampleData()
            }

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
                    loadTransactionsForCurrentPortfolio()
                }
            }
        }
    }

    private suspend fun createSampleData() {
        // Create sample portfolio
        val portfolioId = portfolioRepository.insertPortfolio(
            Portfolio(name = "Ana Portföy")
        )
    }

    private fun getTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, 0, 0, 0)
        return calendar.timeInMillis
    }

    fun selectPortfolio(index: Int) {
        if (index in _uiState.value.portfolios.indices) {
            _uiState.update { it.copy(currentPortfolioIndex = index) }
            loadAssetsForCurrentPortfolio()
            loadTransactionsForCurrentPortfolio()
        }
    }

    /**
     * Verileri yeniden yükler (Pull-to-refresh için)
     */
    fun refresh() {
        loadPortfolios()
    }

    fun nextPortfolio() {
        val nextIndex = _uiState.value.currentPortfolioIndex + 1
        if (nextIndex < _uiState.value.portfolios.size) {
            selectPortfolio(nextIndex)
        }
    }

    fun previousPortfolio() {
        val prevIndex = _uiState.value.currentPortfolioIndex - 1
        if (prevIndex >= 0) {
            selectPortfolio(prevIndex)
        }
    }

    private fun loadTransactionsForCurrentPortfolio() {
        val portfolio = _uiState.value.portfolios.getOrNull(_uiState.value.currentPortfolioIndex)
        portfolio?.let { p ->
            viewModelScope.launch {
                transactionDao.getTransactionsByPortfolio(p.id).collectLatest { transactions ->
                    _uiState.update { state ->
                        state.copy(transactions = transactions)
                    }
                }
            }
        }
    }

    fun createNewPortfolio(name: String) {
        viewModelScope.launch {
            val newPortfolio = Portfolio(
                id = 0,
                name = name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            portfolioRepository.insertPortfolio(newPortfolio)
        }
    }

    fun updatePortfolioName(portfolioId: Long, newName: String) {
        viewModelScope.launch {
            val portfolio = portfolioRepository.getPortfolioById(portfolioId)
            portfolio?.let {
                val updated = it.copy(name = newName, updatedAt = System.currentTimeMillis())
                portfolioRepository.updatePortfolio(updated)
            }
        }
    }

    fun deleteCurrentPortfolio() {
        viewModelScope.launch {
            val portfolio = _uiState.value.portfolios.getOrNull(_uiState.value.currentPortfolioIndex)
            portfolio?.let {
                portfolioRepository.deletePortfolio(it)
            }
        }
    }

    private fun loadAssetsForCurrentPortfolio() {
        val portfolio = _uiState.value.portfolios.getOrNull(_uiState.value.currentPortfolioIndex)
        portfolio?.let { p ->
            viewModelScope.launch {
                // Get transactions for the specific portfolio only
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
                                assetPerformances = emptyMap()
                            )
                        }
                        return@collectLatest
                    }
                    
                    // Get unique fund codes
                    val uniqueFundCodes = transactions.map { it.fundCode }.distinct()
                    
                    // Calculate assets from transactions for each fund
                    val calculatedAssets = uniqueFundCodes.mapNotNull { fundCode ->
                        try {
                            // Get fund transactions
                            val fundTransactions = transactions.filter { it.fundCode == fundCode }
                            if (fundTransactions.isEmpty()) return@mapNotNull null
                            
                            // Get current price from TEFAS
                            val fundInfo = assetRepository.getAssetByCodeGlobal(fundCode)
                            val currentPrice = fundInfo?.currentPrice ?: 0.0
                            
                            // Calculate average price and holdings from transactions
                            val avgResult = calculateAveragePrice(fundCode, p.id, currentPrice)
                            
                            // Only include funds with units > 0
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
                    val performances = calculateAssetPerformances(calculatedAssets)
                    _uiState.update { state ->
                        state.copy(
                            assets = calculatedAssets.sortedByDescending { it.totalValue },
                            summary = summary,
                            assetPerformances = performances
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

        // Her bir asset için günlük TL değişimini hesapla ve topla
        // Formül: (birim sayısı × bugünkü fiyat) - (birim sayısı × dünkü fiyat)
        val dailyChangeTL = assets.sumOf { asset ->
            (asset.units * asset.currentPrice) - (asset.units * asset.priceYesterday)
        }

        // Ağırlıklı ortalama günlük yüzde değişimi
        val dailyChangePercent = if (totalValue > 0) {
            (dailyChangeTL / totalValue) * 100
        } else {
            0.0
        }

        // Haftalık TL değişimini hesapla
        // Formül: (birim sayısı × bugünkü fiyat) - (birim sayısı × 7 gün önceki fiyat)
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

    private fun calculateAssetPerformances(assets: List<Asset>): Map<Long, AssetPerformance> {
        val currentTime = System.currentTimeMillis()
        
        return assets.associate { asset ->
            val investmentDays = ((currentTime - asset.purchaseDate) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
            
            val performance = AssetPerformance(
                assetId = asset.id,
                dailyChangePercent = if (asset.purchasePrice > 0) 
                    ((asset.currentPrice - asset.purchasePrice) / asset.purchasePrice) * 100 / investmentDays * 30 else 0.0, // Simplified calculation
                dailyChangeTL = asset.totalValue * 0.0276, // Sample: 2.76%
                weeklyChangePercent = asset.weeklyChangePercent,
                weeklyChangeTL = asset.totalValue * (asset.weeklyChangePercent / 100),
                totalGainLossPercent = asset.profitLossPercent,
                totalGainLossTL = asset.profitLossTL,
                dailyAverageChangePercent = if (investmentDays > 0) asset.profitLossPercent / investmentDays else 0.0,
                dailyAverageChangeTL = if (investmentDays > 0) asset.profitLossTL / investmentDays else 0.0,
                investmentDays = investmentDays
            )
            asset.id to performance
        }
    }

    fun addAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.insertAsset(asset)
        }
    }

    fun updateAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.updateAsset(asset)
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.deleteAsset(asset)
        }
    }

    fun selectAssetByCode(code: String) {
        val portfolio = _uiState.value.portfolios.getOrNull(_uiState.value.currentPortfolioIndex)
        portfolio?.let { p ->
            viewModelScope.launch {
                val asset = assetRepository.getAssetByCode(code, p.id)
                _uiState.update { it.copy(selectedAsset = asset) }
            }
        }
    }

    fun clearSelectedAsset() {
        _uiState.update { it.copy(selectedAsset = null) }
    }

    fun buyFund(fund: Asset, quantity: Double, price: Double, date: Long, portfolioId: Long) {
        viewModelScope.launch {
            // Add transaction record - portfolio values are calculated from transactions table
            val transaction = TransactionEntity(
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = "BUY",
                quantity = quantity,
                price = price,
                date = date,
                portfolioId = portfolioId
            )
            transactionDao.insertTransaction(transaction)
        }
    }

    fun sellFund(fund: Asset, quantity: Double, price: Double, date: Long, portfolioId: Long) {
        viewModelScope.launch {
            // Add transaction record - portfolio values are calculated from transactions table
            val transaction = TransactionEntity(
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = "SELL",
                quantity = quantity,
                price = price,
                date = date,
                portfolioId = portfolioId
            )
            transactionDao.insertTransaction(transaction)
        }
    }

    // Get transactions for specific fund
    override fun getTransactionsByFundCode(fundCode: String, onResult: (List<TransactionEntity>) -> Unit) {
        viewModelScope.launch {
            transactionDao.getTransactionsByFundCode(fundCode).collectLatest { transactions ->
                onResult(transactions.take(5)) // Take only last 5 transactions
            }
        }
    }

    override fun getTransactionsByFundCodeAndPortfolioId(fundCode: String, portfolioId: Long, onResult: (List<TransactionEntity>) -> Unit) {
        viewModelScope.launch {
            transactionDao.getTransactionsByPortfolioAndFund(portfolioId, fundCode).collectLatest { transactions ->
                onResult(transactions.take(5)) // Take only last 5 transactions
            }
        }
    }

    // Delete a transaction
    override fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }

    // ===== ORTALAMA ALIŞ FİYATI HESAPLAMA (JavaScript sample_transactions.js mantığı) =====
    // Bu fonksiyon, verilen fon koduna göre tüm işlemleri kronolojik sırayla işleyerek
    // ortalama alış fiyatını (ortFiyat) hesaplar

    data class AveragePriceResult(
        val averagePrice: Double,      // Mevcut ortalama fiyat
        val totalLots: Double,         // Toplam lot
        val totalCost: Double,         // Toplam maliyet
        val realizedProfit: Double,   // Gerçekleşen kar/zarar (satışlardan)
        val currentValue: Double,     // Güncel piyasa değeri
        val unrealizedProfit: Double,  // Gerçekleşmemiş kar/zarar (güncel değer - maliyet)
        val profitPercent: Double     // Kar/zarar yüzdesi
    )

    // Ana Portföy için tüm transaction'lardan ortalama fiyat hesapla
    private suspend fun calculateAveragePriceForMainPortfolio(fundCode: String, transactions: List<TransactionEntity>, currentPrice: Double = 0.0): AveragePriceResult {
        // Fon koduna göre filtrele ve tarihe göre sırala
        val fundTransactions = transactions
            .filter { it.fundCode == fundCode }
            .sortedBy { it.date }

        if (fundTransactions.isEmpty()) {
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

        for (transaction in fundTransactions) {
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

    /**
     * Belirli bir fon için ortalama alış fiyatını ve kar/zarar hesaplar.
     * İşlemler tarih sırasına göre işlenir.
     * 
     * ALIŞ: lot ve maliyet kümülatif olarak eklenir
     * SATIŞ: Önceki ortalama fiyat üzerinden kar/zarar hesaplanır
     * 
     * Formül: ortalamaFiyat = toplamMaliyet / toplamLot
     * Kar%: (GüncelDeğer - Maliyet) / Maliyet * 100
     */
    suspend fun calculateAveragePrice(fundCode: String, portfolioId: Long, currentPrice: Double = 0.0): AveragePriceResult {
        // Tarihe göre sıralı işlemleri al (ASC - eski tarihten yeniye)
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
                // ALIŞ: Kümülatif toplamları güncelle
                totalLots += quantity
                totalCost += transactionValue
            } else {
                // SATIŞ: Önceki ortalama fiyatı kullan
                val prevLots = totalLots
                val prevCost = totalCost

                if (prevLots > 0) {
                    val averagePrice = prevCost / prevLots
                    val sellValue = quantity * averagePrice
                    val profit = transactionValue - sellValue

                    realizedProfit += profit

                    // Kümülatif toplamları güncelle
                    totalLots -= quantity
                    totalCost -= sellValue

                    // Lot 0 veya altına düşerse sıfırla
                    if (totalLots <= 0) {
                        totalLots = 0.0
                        totalCost = 0.0
                    }
                }
            }
        }

        val averagePrice = if (totalLots > 0) totalCost / totalLots else 0.0
        
        // Güncel değer ve kar/zarar hesapla
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

    /**
     * Satış işlemi için kullanılacak ortalama fiyatı ve kar/zararı döndürür.
     * Bu, satıştan önceki son ortalama fiyattır.
     * 
     * @return Triple<averagePrice, realizedProfit, profitPercent>
     */
    suspend fun getAveragePriceForSell(fundCode: String, portfolioId: Long, sellQuantity: Double, sellPrice: Double, sellDate: Long): Triple<Double, Double, Double> {
        // Satıştan önceki işlemleri al
        val transactions = transactionDao.getTransactionsByPortfolioAndFundOrderedByDate(portfolioId, fundCode)
            .filter { it.fundCode == fundCode && it.date <= sellDate }

        if (transactions.isEmpty()) {
            return Triple(0.0, 0.0, 0.0) // averagePrice, realizedProfit, profitPercent
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

        // Şimdi bu satışı simüle et
        if (totalLots > 0) {
            val currentAvgPrice = totalCost / totalLots
            val sellValue = sellQuantity * currentAvgPrice
            val profit = (sellQuantity * sellPrice) - sellValue
            realizedProfit += profit
            
            // Kümülatif toplamları güncelle
            totalLots -= sellQuantity
            totalCost -= sellValue
        }

        val finalAveragePrice = if (totalLots > 0) totalCost / totalLots else 0.0
        
        // Kar yüzdesi hesapla (satış değerine göre)
        val profitPercent = if (totalCost > 0) (realizedProfit / totalCost) * 100 else 0.0

        return Triple(finalAveragePrice, realizedProfit, profitPercent)
    }
}
