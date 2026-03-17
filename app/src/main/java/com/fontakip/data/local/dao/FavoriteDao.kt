package com.fontakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fontakip.data.local.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE portfolioId = :portfolioId ORDER BY createdAt DESC")
    fun getFavoritesByPortfolio(portfolioId: Long): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE code = :code AND portfolioId = :portfolioId LIMIT 1")
    suspend fun getFavoriteByCodeAndPortfolio(code: String, portfolioId: Long): FavoriteEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE code = :code AND portfolioId = :portfolioId)")
    suspend fun isFavorite(code: String, portfolioId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE code = :code AND portfolioId = :portfolioId")
    suspend fun deleteFavoriteByCodeAndPortfolio(code: String, portfolioId: Long)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
