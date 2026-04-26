package com.fontakip.data.repository

import com.fontakip.data.local.dao.AssetDao
import com.fontakip.data.remote.TefasApiService
import com.fontakip.data.remote.model.FonGnlBlgSiraliGetirRequest
import com.fontakip.data.remote.model.FundHistoryItem
import com.fontakip.data.remote.model.TefasFund
import com.fontakip.domain.model.Asset
import com.fontakip.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao,
    private val tefasApiService: TefasApiService
) : AssetRepository {

    private val _assetUpdateEvent = MutableSharedFlow<String>()
    override fun getAssetUpdateEvent(): SharedFlow<String> = _assetUpdateEvent.asSharedFlow()

    suspend fun emitAssetUpdateEvent(message: String) {
        _assetUpdateEvent.emit(message)
    }

    override fun getAssetsByPortfolio(portfolioId: Long): Flow<List<Asset>> {
        return assetDao.getAssetsByPortfolio(portfolioId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAssetById(id: Long): Asset? {
        return assetDao.getAssetById(id)?.toDomain()
    }

    override suspend fun getAssetByCode(code: String, portfolioId: Long): Asset? {
        return assetDao.getAssetByCode(code, portfolioId)?.toDomain()
            ?: assetDao.getAssetByCodeGlobal(code)?.toDomain()
    }

    override suspend fun getAssetByCodeGlobal(code: String): Asset? {
        return assetDao.getAssetByCodeGlobal(code)?.toDomain()
    }

    override suspend fun insertAsset(asset: Asset): Long {
        return assetDao.insertAsset(asset.toEntity())
    }

    override suspend fun updateAsset(asset: Asset) {
        assetDao.updateAsset(asset.toEntity())
    }

    override suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(asset.toEntity())
    }

    override suspend fun deleteAssetsByPortfolio(portfolioId: Long) {
        assetDao.deleteAssetsByPortfolio(portfolioId)
    }

    override suspend fun insertAssets(assets: List<Asset>) {
        assetDao.insertAssets(assets.map { it.toEntity() })
        _assetUpdateEvent.emit("assets_updated")
    }

    override fun getAssetsByFontip(fontip: String): Flow<List<Asset>> {
        return assetDao.getAssetsByFontip(fontip).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteAssetsByFontip(fontip: String) {
        assetDao.deleteAssetsByFontip(fontip)
    }

    override suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        assetDao.updateFavorite(id, isFavorite)
    }

    override fun getFavoriteAssetsByFontip(fontip: String): Flow<List<Asset>> {
        return assetDao.getFavoriteAssetsByFontip(fontip).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllFavoriteAssets(): Flow<List<Asset>> {
        return assetDao.getAllFavoriteAssets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun searchAllFunds(query: String): List<Asset> {
        return assetDao.searchAllFunds(query).map { it.toDomain() }
    }

    override suspend fun updateFavoriteByCode(code: String, isFavorite: Boolean) {
        assetDao.updateFavoriteByCode(code, isFavorite)
    }

    override fun getFavoriteFunds(): Flow<List<Asset>> {
        return assetDao.getFavoriteFunds().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteAllAssets() {
        assetDao.deleteAllAssets()
    }
      
    override suspend fun getFundHistory(
        fundCode: String,
        startDate: String,
        endDate: String
    ): List<FundHistoryItem> {
        return try {
            val response = tefasApiService.getFundHistory(
                FonGnlBlgSiraliGetirRequest(
                    fonTipi = "",
                    fonKodu = fundCode,
                    basTarih = startDate,
                    bitTarih = endDate
                )
            )
            // Map TefasFund to FundHistoryItem
            response.data?.map { fund ->
                FundHistoryItem(
                    date = fund.date,
                    price = fund.price,
                    returnPercent = fund.return1Month ?: 0.0,
                    fundCode = fund.code
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
