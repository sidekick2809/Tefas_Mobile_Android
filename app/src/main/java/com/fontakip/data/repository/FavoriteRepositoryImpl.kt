package com.fontakip.data.repository

import com.fontakip.data.local.dao.FavoriteDao
import com.fontakip.data.local.entities.FavoriteEntity
import com.fontakip.domain.model.Favorite
import com.fontakip.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoritesByPortfolio(portfolioId: Long): Flow<List<Favorite>> {
        return favoriteDao.getFavoritesByPortfolio(portfolioId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun isFavorite(code: String, portfolioId: Long): Boolean {
        return favoriteDao.isFavorite(code, portfolioId)
    }

    override suspend fun addFavorite(code: String, portfolioId: Long): Long {
        val favorite = FavoriteEntity(
            code = code,
            portfolioId = portfolioId
        )
        return favoriteDao.insertFavorite(favorite)
    }

    override suspend fun removeFavorite(code: String, portfolioId: Long) {
        favoriteDao.deleteFavoriteByCodeAndPortfolio(code, portfolioId)
    }

    override suspend fun toggleFavorite(code: String, portfolioId: Long): Boolean {
        val isFav = favoriteDao.isFavorite(code, portfolioId)
        if (isFav) {
            favoriteDao.deleteFavoriteByCodeAndPortfolio(code, portfolioId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(code = code, portfolioId = portfolioId))
        }
        return !isFav
    }

    private fun FavoriteEntity.toDomain(): Favorite {
        return Favorite(
            id = id,
            code = code,
            portfolioId = portfolioId,
            createdAt = createdAt
        )
    }
}
