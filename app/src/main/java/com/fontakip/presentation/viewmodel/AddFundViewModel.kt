package com.fontakip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fontakip.data.remote.TefasApiService
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetType
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.model.Transaction
import com.fontakip.domain.model.TransactionType
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.PortfolioRepository
import com.fontakip.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AddFundUiState(
    val isLoading: Boolean = false,
    val searchResults: List<Asset> = emptyList(),
    val selectedFund: Asset? = null,
    val purchaseDate: Long = System.currentTimeMillis(),
    val error: String? = null,
    val portfolios: List<Portfolio> = emptyList(),
    val selectedPortfolioId: Long = 0
)

@HiltViewModel
class AddFundViewModel @Inject constructor(
    private val tefasApiService: TefasApiService,
    private val assetRepository: AssetRepository,
    private val portfolioRepository: PortfolioRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFundUiState())
    val uiState: StateFlow<AddFundUiState> = _uiState.asStateFlow()

    private var currentPortfolioId: Long = 0
    private var currentPortfolioName: String = "Portföyüm"

    init {
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

    fun setPortfolioId(portfolioId: Long) {
        currentPortfolioId = portfolioId
    }

    fun setPortfolioName(portfolioName: String) {
        currentPortfolioName = portfolioName
    }

    init {
        loadCurrentPortfolio()
    }

    private fun loadCurrentPortfolio() {
        viewModelScope.launch {
            val portfolios = portfolioRepository.getAllPortfolios().first()
            if (portfolios.isNotEmpty()) {
                currentPortfolioId = portfolios.first().id
            }
        }
    }

    fun searchFunds(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Search from local database (Fon Verileri from TEFAS)
                val funds = assetRepository.searchAllFunds(query.uppercase())

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        searchResults = funds
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    fun selectFund(fund: Asset) {
        _uiState.update { 
            it.copy(
                selectedFund = fund,
                searchResults = emptyList()
            )
        }
    }

    fun setPurchaseDate(date: Long) {
        _uiState.update { it.copy(purchaseDate = date) }
    }

    fun buyFund(quantity: Double, price: Double, date: Long, portfolioId: Long) {
        val fund = _uiState.value.selectedFund ?: return

        viewModelScope.launch {
            val effectivePortfolioId = if (portfolioId != 0L) portfolioId else currentPortfolioId

            // Save transaction record ONLY - portfolio values are calculated dynamically from transactions
            saveTransaction(
                portfolioId = effectivePortfolioId,
                portfolioName = currentPortfolioName,
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = TransactionType.BUY,
                price = price,
                quantity = quantity,
                date = date
            )
        }
    }

    private suspend fun saveTransaction(
        portfolioId: Long,
        portfolioName: String,
        fundCode: String,
        fundName: String,
        transactionType: TransactionType,
        price: Double,
        quantity: Double,
        date: Long
    ) {
        val transaction = Transaction(
            portfolioId = portfolioId,
            portfolioName = portfolioName,
            fundCode = fundCode,
            fundName = fundName,
            transactionType = transactionType,
            price = price,
            quantity = quantity,
            date = date
        )
        transactionRepository.insertTransaction(transaction)
    }

    fun sellFund(quantity: Double, price: Double, date: Long, portfolioId: Long) {
        val fund = _uiState.value.selectedFund ?: return

        viewModelScope.launch {
            val effectivePortfolioId = if (portfolioId != 0L) portfolioId else currentPortfolioId
            
            // Save transaction record ONLY - portfolio values are calculated dynamically from transactions
            saveTransaction(
                portfolioId = effectivePortfolioId,
                portfolioName = currentPortfolioName,
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = TransactionType.SELL,
                price = price,
                quantity = quantity,
                date = date
            )
        }
    }

    fun clearSelectedFund() {
        _uiState.update { 
            it.copy(
                selectedFund = null,
                searchResults = emptyList()
            )
        }
    }

    fun quickAddFund(fund: Asset) {
        viewModelScope.launch {
            // Ensure we have a valid portfolio
            if (currentPortfolioId == 0L) {
                val portfolios = portfolioRepository.getAllPortfolios().first()
                if (portfolios.isEmpty()) return@launch
                currentPortfolioId = portfolios.first().id
            }
            
            // Save transaction record ONLY - portfolio values are calculated dynamically from transactions
            saveTransaction(
                portfolioId = currentPortfolioId,
                portfolioName = currentPortfolioName,
                fundCode = fund.code,
                fundName = fund.name,
                transactionType = TransactionType.BUY,
                price = fund.currentPrice,
                quantity = 1.0,
                date = System.currentTimeMillis()
            )
        }
    }
}
