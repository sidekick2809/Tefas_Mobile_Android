package com.fontakip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.data.remote.TefasApiService
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetType
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.FavoriteRepository
import com.fontakip.domain.repository.PortfolioRepository
import com.fontakip.presentation.screens.portfolio.TransactionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class FonVerileriUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val todayDate: String = "",
    val sevenDaysAgoDate: String = "",
    val yesterdayDate: String = "",
    val fundCount: Int = 0,
    val selectedFontip: String = "YAT", // YAT, EMK, or ALL
    val fetchAllTypes: Boolean = false, // When true, fetches both YAT and EMK
    val funds: List<Asset> = emptyList(),
    val searchQuery: String = "",
    val portfolios: List<Portfolio> = emptyList()
)

@HiltViewModel
class FonVerileriViewModel @Inject constructor(
    private val tefasApiService: TefasApiService,
    private val assetRepository: AssetRepository,
    private val portfolioRepository: PortfolioRepository,
    private val transactionDao: TransactionDao,
    private val favoriteRepository: FavoriteRepository
) : ViewModel(), TransactionHandler {

    private val _uiState = MutableStateFlow(FonVerileriUiState())
    val uiState: StateFlow<FonVerileriUiState> = _uiState.asStateFlow()

    init {
        initializeDates()
        loadFunds()
        loadPortfolios()
    }

    private fun loadPortfolios() {
        viewModelScope.launch {
            portfolioRepository.getAllPortfolios().collect { entities ->
                val portfolios = entities.map { entity ->
                    Portfolio(
                        id = entity.id,
                        name = entity.name,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
                _uiState.value = _uiState.value.copy(portfolios = portfolios)
            }
        }
    }

    /**
     * Initializes the date parameters for TEFAS data fetching and updates the UI state.
     *
     * This function calculates three key dates:
     * 1. **Today**: The current system date.
     * 2. **Yesterday**: The previous business day. If today is Monday, it rolls back 3 days
     *    to Friday to account for the weekend gap in financial data.
     * 3. **Seven Days Ago**: The date exactly one week prior to today.
     *
     * All dates are formatted using the "dd.MM.yyyy" pattern with a Turkish locale ("tr-TR")
     * to ensure compatibility with the TEFAS API requirements.
     */
    private fun initializeDates() {
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))

        // Eğer bugün pazartesi ise, "Dün" olarak 3 gün öncesini (cuma) kullan
        // Aksi halde dünün tarihini kullan
        val yesterday = Calendar.getInstance().apply {
            if (get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                add(Calendar.DAY_OF_YEAR, -3) // Pazartesi ise cumaya git
            } else {
                add(Calendar.DAY_OF_YEAR, -1)
            }
        }
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }

        _uiState.value = _uiState.value.copy(
            todayDate = dateFormat.format(today.time),
            yesterdayDate = dateFormat.format(yesterday.time),
            sevenDaysAgoDate = dateFormat.format(sevenDaysAgo.time)
        )
    }

    fun updateDate(type: String, newDate: String) {
        when (type) {
            "today" -> _uiState.value = _uiState.value.copy(todayDate = newDate)
            "yesterday" -> _uiState.value = _uiState.value.copy(yesterdayDate = newDate)
            "sevenDaysAgo" -> _uiState.value = _uiState.value.copy(sevenDaysAgoDate = newDate)
        }
    }

    fun setFontip(fontip: String) {
        val fetchAll = fontip == "ALL"
        val actualFontip = if (fetchAll) "YAT" else fontip
        _uiState.value = _uiState.value.copy(
            selectedFontip = fontip,
            fetchAllTypes = fetchAll,
            searchQuery = ""
        )
        loadFunds()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadFunds()
    }

    private fun loadFunds() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // If ALL is selected, get all TEFAS data
            val allAssets = if (state.fetchAllTypes) {
                val yatAssets = assetRepository.getAssetsByFontip("YAT").first()
                val emkAssets = assetRepository.getAssetsByFontip("EMK").first()
                yatAssets + emkAssets
            } else {
                assetRepository.getAssetsByFontip(state.selectedFontip).first()
            }
            
            // Get all favorites from FavoriteRepository
            val favoriteList = favoriteRepository.getAllFavorites().first()
            val favoriteCodes = favoriteList.map { it.code }.toSet()
            
            // Update isFavorite for each asset
            val assetsWithFavorites = allAssets.map { asset ->
                asset.copy(isFavorite = favoriteCodes.contains(asset.code))
            }
            
            val filteredAssets = if (state.searchQuery.isNotEmpty()) {
                assetsWithFavorites.filter { it.code.contains(state.searchQuery, ignoreCase = true) }
            } else {
                assetsWithFavorites
            }
            _uiState.value = _uiState.value.copy(
                funds = filteredAssets,
                fundCount = allAssets.size
            )
        }
    }

    fun toggleFavorite(code: String, portfolioId: Long) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(code, portfolioId)
            // Reload funds to update favorite status
            loadFunds()
        }
    }

    fun deleteAllAssets() {
        viewModelScope.launch {
            try {
                assetRepository.deleteAllAssets()
                _uiState.value = _uiState.value.copy(
                    funds = emptyList(),
                    fundCount = 0,
                    message = "Tüm fon verileri silindi!"
                )
                // Clear message after 3 seconds
                viewModelScope.launch {
                    delay(3000)
                    _uiState.value = _uiState.value.copy(message = "")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    message = "Silme hatası: ${e.message}"
                )
            }
        }
    }

    fun fetchTefasData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "Veriler çekiliyor...")

            try {
                val state = _uiState.value
                
                // Determine which fontip types to fetch
                val fontipTypes = if (state.fetchAllTypes) {
                    listOf("YAT", "EMK")
                } else {
                    listOf(state.selectedFontip)
                }

                var totalAssetsSaved = 0
                
                for (fontip in fontipTypes) {
                    // Get current favorites before deleting (for TEFAS data only, isUserAsset = false)
                    val currentFavoriteFunds = assetRepository.getFavoriteFunds().first()
                    val favoriteCodes = currentFavoriteFunds.map { it.code }.toSet()

                    // Delete existing TEFAS data only (not user assets)
                    assetRepository.deleteAssetsByFontip(fontip)

                    // Fetch today's data
                    val todayResponse = tefasApiService.getFundHistory(
                        fontip = fontip,
                        startDate = state.todayDate,
                        endDate = state.todayDate
                    )

                    // Fetch yesterday's data
                    val yesterdayResponse = tefasApiService.getFundHistory(
                        fontip = fontip,
                        startDate = state.yesterdayDate,
                        endDate = state.yesterdayDate
                    )

                    // Fetch 7 days ago data
                    val sevenDaysAgoResponse = tefasApiService.getFundHistory(
                        fontip = fontip,
                        startDate = state.sevenDaysAgoDate,
                        endDate = state.sevenDaysAgoDate
                    )

                    // Fetch returns data
                    val returnsResponse = tefasApiService.getFundReturns(
                        fontip = fontip,
                        startDate = state.todayDate,
                        endDate = state.todayDate
                    )

                    // Fetch TEFAS status (open/closed)
                    val statusOpenResponse = tefasApiService.getFundStatus(
                        fontip = fontip,
                        startDate = "01.01.2020",
                        endDate = "31.12.2030",
                        islemdurum = "1"
                    )
                    val statusClosedResponse = tefasApiService.getFundStatus(
                        fontip = fontip,
                        startDate = "01.01.2020",
                        endDate = "31.12.2030",
                        islemdurum = "0"
                    )

                    // Create status map
                    val statusMap = mutableMapOf<String, String>()
                    (statusOpenResponse.data ?: emptyList()).forEach { fund ->
                        statusMap[fund.code] = "EVET"
                    }
                    (statusClosedResponse.data ?: emptyList()).forEach { fund ->
                        statusMap[fund.code] = "HAYIR"
                    }

                    // Create price maps
                    val todayPrices = (todayResponse.data ?: emptyList()).associateBy { it.code }
                    val yesterdayPrices = (yesterdayResponse.data ?: emptyList()).associateBy { it.code }
                    val sevenDaysAgoPrices = (sevenDaysAgoResponse.data ?: emptyList()).associateBy { it.code }
                    val returnsMap = (returnsResponse.data ?: emptyList()).associateBy { it.code }

                    // Process and merge data
                    val assets = mutableListOf<Asset>()
                    val currentTime = System.currentTimeMillis()

                todayPrices.forEach { (code, todayFund) ->
                    val yesterdayFund = yesterdayPrices[code]
                    val sevenDaysAgoFund = sevenDaysAgoPrices[code]
                    val returnsFund = returnsMap[code]

                    // Skip if no price or price is 0
                    if (todayFund.price == 0.0 || (yesterdayFund?.price ?: 0.0) == 0.0) {
                        return@forEach
                    }

                    // Skip OKS funds
                    if (todayFund.name.contains("OKS ")) {
                        return@forEach
                    }

                    val priceYesterday = yesterdayFund?.price ?: todayFund.price
                    val priceSevenDaysAgo = sevenDaysAgoFund?.price ?: todayFund.price

                    // Calculate percentage changes
                    val dailyChange = if (priceYesterday > 0) {
                        (todayFund.price - priceYesterday) / priceYesterday
                    } else 0.0

                    val weeklyChange = if (priceSevenDaysAgo > 0) {
                        (todayFund.price - priceSevenDaysAgo) / priceSevenDaysAgo
                    } else 0.0

                    // Extract company name
                    var company = ""
                    if (todayFund.name.contains("HSBC")) {
                        company = "HSBC"
                    } else {
                        val parts = todayFund.name.split("PORTFÖY")
                        company = parts.getOrNull(0)?.trim() ?: ""
                        val pyParts = company.split("PYŞ")
                        company = pyParts.getOrNull(0)?.trim() ?: ""
                    }

                    // Determine tur-C
                    var turC = ""
                    when {
                        todayFund.name.contains("ALTIN ") -> turC = "ALTIN"
                        todayFund.name.contains("YABANCI") -> turC = "YABANCI"
                        todayFund.name.contains("MADEN") -> turC = "MADEN"
                        todayFund.name.contains("GÜMÜŞ") -> turC = "GÜMÜŞ"
                        todayFund.name.contains("BANKA") -> turC = "BANKA"
                    }

                    val asset = Asset(
                        id = 0,
                        portfolioId = getOrCreateDefaultPortfolio(), // Get or create default portfolio
                        code = code,
                        name = todayFund.name,
                        type = AssetType.MUTUAL_FUND,
                        units = 0.0,
                        purchasePrice = 0.0,
                        purchaseDate = currentTime,
                        currentPrice = todayFund.price,
                        lastUpdateDate = currentTime,
                        fontip = fontip,
                        dailyChangePercent = dailyChange * 100,
                        weeklyChangePercent = weeklyChange * 100,
                        monthlyChangePercent = (returnsFund?.return1Month ?: 0.0),
                        threeMonthChangePercent = (returnsFund?.return3Months ?: 0.0),
                        sixMonthChangePercent = (returnsFund?.return6Months ?: 0.0),
                        yearToDateChangePercent = (returnsFund?.returnYearToDate ?: 0.0),
                        oneYearChangePercent = (returnsFund?.return1Year ?: 0.0),
                        threeYearChangePercent = (returnsFund?.return3Years ?: 0.0),
                        fiveYearChangePercent = (returnsFund?.return5Years ?: 0.0),
                        fundType = todayFund.fundType,
                        turC = turC,
                        company = company,
                        tefasStatus = statusMap[code] ?: "Bilinmiyor",
                        priceYesterday = priceYesterday,
                        priceSevenDaysAgo = priceSevenDaysAgo,
                        isFavorite = favoriteCodes.contains(code) // Preserve favorite status
                    )
                    assets.add(asset)
                }

                // Insert into database
                assetRepository.insertAssets(assets)
                totalAssetsSaved += assets.size

                // Log for debugging
                println("Fontip: $fontip - Assets saved: ${assets.size}")
                }

                // Clear message after 2 seconds
                viewModelScope.launch {
                    delay(2000)
                    _uiState.value = _uiState.value.copy(message = "")
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "$totalAssetsSaved fon verisi başarıyla kaydedildi!",
                    fundCount = totalAssetsSaved,
                    funds = assetRepository.getAssetsByFontip(state.selectedFontip).first()
                )

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> "İnternet bağlantısı yok veya sunucuya ulaşılamıyor"
                    e.message?.contains("timeout") == true -> "Bağlantı zaman aşımı"
                    e.message?.contains("network") == true -> "Ağ hatası"
                    else -> "Hata: ${e.message}"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = errorMessage
                )
            }
        }
    }

    private suspend fun getOrCreateDefaultPortfolio(): Long {
        // Get all portfolios and use the first one, or create a default one if none exist
        val portfolios = portfolioRepository.getAllPortfolios().first()
        return if (portfolios.isNotEmpty()) {
            portfolios.first().id
        } else {
            // Create a default portfolio
            val defaultPortfolio = Portfolio(
                id = 0,
                name = "Portföyüm",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            portfolioRepository.insertPortfolio(defaultPortfolio)
        }
    }

    fun buyFund(fund: Asset, quantity: Double, price: Double, date: Long, portfolioId: Long) {
        viewModelScope.launch {
            // Save transaction — portfolio state is calculated from transactions by PortfolioViewModel
            val transaction = TransactionEntity(
                portfolioId = portfolioId,
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = "BUY",
                price = price,
                quantity = quantity,
                date = date
            )
            transactionDao.insertTransaction(transaction)
            _uiState.value = _uiState.value.copy(message = "${fund.code} fonu alındı!")
            // Clear message after 3 seconds
            viewModelScope.launch {
                delay(3000)
                _uiState.value = _uiState.value.copy(message = "")
            }
        }
    }

    fun sellFund(fund: Asset, quantity: Double, price: Double, date: Long, portfolioId: Long) {
        viewModelScope.launch {
            // Satıştan önce ortalama fiyatı ve gerçekleşen kar/zararı hesapla
            val (avgPrice, realizedProfit, profitPercent) = getAveragePriceForSell(fund.code, portfolioId, quantity, price, date)
            
            // Save transaction — portfolio state is calculated from transactions by PortfolioViewModel
            val transaction = TransactionEntity(
                portfolioId = portfolioId,
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = "SELL",
                price = price,
                quantity = quantity,
                date = date
            )
            transactionDao.insertTransaction(transaction)
            _uiState.value = _uiState.value.copy(message = "${fund.code} fonu satıldı! Gerçekleşen Kar/Zarar: ${String.format("%.2f", realizedProfit)} TL (%${String.format("%.2f", profitPercent)})")
            // Clear message after 3 seconds
            viewModelScope.launch {
                delay(3000)
                _uiState.value = _uiState.value.copy(message = "")
            }
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
