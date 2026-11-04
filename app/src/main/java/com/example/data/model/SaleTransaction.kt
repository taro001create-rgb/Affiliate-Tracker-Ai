package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "sale_transactions")
data class SaleTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val commissionRate: Double,
    val commissionEarned: Double,
    val platform: AffiliatePlatform,
    val saleDateTimestamp: Long,
    val status: CommissionStatus,
    val notes: String = ""
) {
    val totalSalesAmount: Double
        get() = productPrice * quantity

    fun formattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(saleDateTimestamp))
    }

    fun formattedShortDate(): String {
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        return sdf.format(Date(saleDateTimestamp))
    }
}
