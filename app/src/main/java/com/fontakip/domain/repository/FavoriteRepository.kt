package com.fontakip.domain.repository

import com.fontakip.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Favorite>>
    fun getFavoritesByPortfolio(portfolioId: Long): Flow<List<Favorite>>
    suspend fun isFavorite(code: String, portfolioId: Long): Boolean
    suspend fun addFavorite(code: String, portfolioId: Long): Long
    suspend fun removeFavorite(code: String, portfolioId: Long)
    suspend fun toggleFavorite(code: String, portfolioId: Long): Boolean
}
