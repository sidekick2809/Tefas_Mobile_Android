package com.fontakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fontakip.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE portfolioId = :portfolioId ORDER BY date DESC")
    fun getTransactionsByPortfolio(portfolioId: Long): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE fundCode = :fundCode ORDER BY date DESC")
    fun getTransactionsByFundCode(fundCode: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode ORDER BY date DESC")
    fun getTransactionsByPortfolioAndFund(portfolioId: Long, fundCode: String): Flow<List<TransactionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions WHERE portfolioId = :portfolioId")
    suspend fun deleteTransactionsByPortfolio(portfolioId: Long)
    
    @Query("SELECT * FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode ORDER BY date ASC")
    suspend fun getTransactionsByPortfolioAndFundOrderedByDate(portfolioId: Long, fundCode: String): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    // ===== DİNAMİK HESAPLAMA FONKSİYONLARI =====
    
    /**
     * Belirli bir portföydeki tüm benzersiz fon kodlarını döndürür
     */
    @Query("SELECT DISTINCT fundCode FROM transactions WHERE portfolioId = :portfolioId")
    suspend fun getUniqueFundCodesByPortfolio(portfolioId: Long): List<String>
    
    /**
     * Tüm portföylerdeki tüm benzersiz fon kodlarını döndürür
     */
    @Query("SELECT DISTINCT fundCode FROM transactions")
    suspend fun getAllUniqueFundCodes(): List<String>
    
    /**
     * Belirli bir fon koduna ait tüm işlemleri tarih sırasına göre döndürür
     */
    @Query("SELECT * FROM transactions WHERE fundCode = :fundCode ORDER BY date ASC")
    suspend fun getTransactionsByFundCodeOrderedByDate(fundCode: String): List<TransactionEntity>
    
    /**
     * Belirli bir portföy ve fon koduna ait işlemleri tarih sırasına göre döndürür
     */
    @Query("""
        SELECT * FROM transactions 
        WHERE portfolioId = :portfolioId AND fundCode = :fundCode 
        ORDER BY date ASC
    """)
    suspend fun getTransactionsByPortfolioAndFundOrdered(portfolioId: Long, fundCode: String): List<TransactionEntity>
    
    /**
     * Toplam işlem sayısını ve toplam değeri döndürür
     */
    @Query("SELECT COUNT(*) as count, COALESCE(SUM(quantity * price), 0) as totalValue FROM transactions WHERE portfolioId = :portfolioId")
    suspend fun getPortfolioTransactionStats(portfolioId: Long): TransactionStats?
    
    /**
     * Belirli bir fon kodunun toplam alım miktarını döndürür (sadece BUY işlemleri)
     */
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode AND transactionType = 'BUY'")
    suspend fun getTotalBuyQuantity(portfolioId: Long, fundCode: String): Double
    
    /**
     * Belirli bir fon kodunun toplam satış miktarını döndürür (sadece SELL işlemleri)
     */
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode AND transactionType = 'SELL'")
    suspend fun getTotalSellQuantity(portfolioId: Long, fundCode: String): Double
    
    /**
     * Belirli bir fon kodunun toplam alım maliyetini döndürür
     */
    @Query("SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode AND transactionType = 'BUY'")
    suspend fun getTotalBuyCost(portfolioId: Long, fundCode: String): Double
    
    /**
     * Belirli bir fon kodunun toplam satış gelirini döndürür
     */
    @Query("SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE portfolioId = :portfolioId AND fundCode = :fundCode AND transactionType = 'SELL'")
    suspend fun getTotalSellRevenue(portfolioId: Long, fundCode: String): Double
}

/**
 * İşlem istatistikleri için veri sınıfı
 */
data class TransactionStats(
    val count: Int,
    val totalValue: Double
)
