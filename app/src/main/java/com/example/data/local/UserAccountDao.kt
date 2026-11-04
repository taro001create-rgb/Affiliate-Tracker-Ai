package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    fun getAccountById(id: String): Flow<UserAccount?>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountByIdDirect(id: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<UserAccount>)

    @Update
    suspend fun updateAccount(account: UserAccount)

    @Query("UPDATE user_accounts SET dailyGoal = :daily, weeklyGoal = :weekly, monthlyGoal = :monthly WHERE id = :id")
    suspend fun updateGoals(id: String, daily: Double, weekly: Double, monthly: Double)

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteAccount(id: String)
}
