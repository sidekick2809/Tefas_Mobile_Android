package com.fontakip.di

import com.fontakip.data.repository.AssetRepositoryImpl
import com.fontakip.data.repository.FavoriteRepositoryImpl
import com.fontakip.data.repository.PortfolioRepositoryImpl
import com.fontakip.data.repository.TransactionRepositoryImpl
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.FavoriteRepository
import com.fontakip.domain.repository.PortfolioRepository
import com.fontakip.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(
        portfolioRepositoryImpl: PortfolioRepositoryImpl
    ): PortfolioRepository

    @Binds
    @Singleton
    abstract fun bindAssetRepository(
        assetRepositoryImpl: AssetRepositoryImpl
    ): AssetRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
