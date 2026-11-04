package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SaleTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM sale_transactions WHERE accountId = :accountId ORDER BY saleDateTimestamp DESC")
    fun getTransactionsForAccount(accountId: String): Flow<List<SaleTransaction>>

    @Query("SELECT * FROM sale_transactions WHERE accountId = :accountId ORDER BY saleDateTimestamp DESC")
    suspend fun getTransactionsForAccountList(accountId: String): List<SaleTransaction>

    @Query("SELECT * FROM sale_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): SaleTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SaleTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<SaleTransaction>)

    @Update
    suspend fun updateTransaction(transaction: SaleTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: SaleTransaction)

    @Query("DELETE FROM sale_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM sale_transactions WHERE accountId = :accountId")
    suspend fun deleteAllForAccount(accountId: String)
}
