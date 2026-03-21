package com.fontakip.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fontakip.data.local.dao.AssetDao
import com.fontakip.data.local.dao.FavoriteDao
import com.fontakip.data.local.dao.PortfolioDao
import com.fontakip.data.local.dao.TransactionDao
import com.fontakip.data.local.database.FontakipDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FontakipDatabase {
        return Room.databaseBuilder(
            context,
            FontakipDatabase::class.java,
            "fontakip_database"
        )
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    @Singleton
    fun providePortfolioDao(database: FontakipDatabase): PortfolioDao {
        return database.portfolioDao()
    }

    @Provides
    @Singleton
    fun provideAssetDao(database: FontakipDatabase): AssetDao {
        return database.assetDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: FontakipDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: FontakipDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}
