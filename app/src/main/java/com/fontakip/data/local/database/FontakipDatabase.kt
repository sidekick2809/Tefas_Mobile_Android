package com.fontakip.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fontakip.data.local.dao.AssetDao
import com.fontakip.data.local.dao.FavoriteDao
import com.fontakip.data.local.dao.PortfolioDao
import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.data.local.entities.AssetEntity
import com.fontakip.data.local.entities.FavoriteEntity
import com.fontakip.data.local.entities.PortfolioEntity
import com.fontakip.data.local.entities.TransactionEntity

@Database(
    entities = [PortfolioEntity::class, AssetEntity::class, TransactionEntity::class, FavoriteEntity::class],
    version = 9,
    exportSchema = false
)
abstract class FontakipDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "fontakip_database"
    }
}
