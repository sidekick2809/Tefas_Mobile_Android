package com.fontakip.domain.repository

import com.fontakip.data.remote.model.FundHistoryItem
import com.fontakip.domain.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AssetRepository {
    fun getAssetsByPortfolio(portfolioId: Long): Flow<List<Asset>>
    suspend fun getAssetById(id: Long): Asset?
    suspend fun getAssetByCode(code: String, portfolioId: Long): Asset?
    suspend fun getAssetByCodeGlobal(code: String): Asset?
    suspend fun searchAllFunds(query: String): List<Asset>
    suspend fun insertAsset(asset: Asset): Long
    suspend fun updateAsset(asset: Asset)
    suspend fun deleteAsset(asset: Asset)
    suspend fun deleteAssetsByPortfolio(portfolioId: Long)
    suspend fun insertAssets(assets: List<Asset>)
    fun getAssetsByFontip(fontip: String): Flow<List<Asset>>
    suspend fun deleteAssetsByFontip(fontip: String)
    suspend fun deleteAllAssets()
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
    fun getFavoriteAssetsByFontip(fontip: String): Flow<List<Asset>>
    fun getAllFavoriteAssets(): Flow<List<Asset>>
    suspend fun updateFavoriteByCode(code: String, isFavorite: Boolean)
    fun getFavoriteFunds(): Flow<List<Asset>>
    
    /**
     * Flow that emits when assets are updated from TEFAS
     */
    fun getAssetUpdateEvent(): SharedFlow<String>
    
    /**
     * Get fund history data for chart display
     * @param fundCode The TEFAS fund code (e.g., "DOH")
     * @param startDate Start date in dd.MM.yyyy format
     * @param endDate End date in dd.MM.yyyy format
     * @return List of FundHistoryItem containing date, price, and return data
     */
    suspend fun getFundHistory(
        fundCode: String,
        startDate: String,
        endDate: String
    ): List<FundHistoryItem>
}
