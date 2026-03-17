package com.fontakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fontakip.data.local.entities.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets WHERE portfolioId = :portfolioId ORDER BY code ASC")
    fun getAssetsByPortfolio(portfolioId: Long): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Long): AssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)

    @Query("DELETE FROM assets WHERE portfolioId = :portfolioId")
    suspend fun deleteAssetsByPortfolio(portfolioId: Long)

    @Query("SELECT * FROM assets WHERE code = :code AND portfolioId = :portfolioId")
    suspend fun getAssetByCode(code: String, portfolioId: Long): AssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<AssetEntity>)

    @Query("SELECT * FROM assets WHERE fontip = :fontip")
    fun getAssetsByFontip(fontip: String): Flow<List<AssetEntity>>

    @Query("DELETE FROM assets WHERE fontip = :fontip")
    suspend fun deleteAssetsByFontip(fontip: String)

    @Query("UPDATE assets SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM assets WHERE isFavorite = 1 AND fontip = :fontip")
    fun getFavoriteAssetsByFontip(fontip: String): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE isFavorite = 1")
    fun getAllFavoriteAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE (code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%') ORDER BY code ASC LIMIT 50")
    suspend fun searchAllFunds(query: String): List<AssetEntity>

    @Query("UPDATE assets SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavoriteByCode(code: String, isFavorite: Boolean)

    @Query("SELECT * FROM assets WHERE isFavorite = 1")
    fun getFavoriteFunds(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE code = :code LIMIT 1")
    suspend fun getAssetByCodeGlobal(code: String): AssetEntity?

    @Query("DELETE FROM assets")
    suspend fun deleteAllAssets()
}
