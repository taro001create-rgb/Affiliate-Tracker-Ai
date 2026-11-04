package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey
    val id: String,
    val name: String,
    val handle: String,
    val email: String,
    val niche: String,
    val dailyGoal: Double = 150.0,
    val weeklyGoal: Double = 1000.0,
    val monthlyGoal: Double = 4500.0,
    val currencySymbol: String = "$",
    val isDemoAccount: Boolean = false,
    val avatarEmoji: String = "🚀"
)
