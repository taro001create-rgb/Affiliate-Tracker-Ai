package com.example

import com.example.data.model.AffiliatePlatform
import com.example.data.model.CommissionStatus
import com.example.data.model.GoalProgress
import com.example.data.model.SaleTransaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCommissionCalculation() {
        val price = 29.99
        val quantity = 10
        val commissionRate = 20.0 // 20%
        val gross = price * quantity
        val commission = gross * (commissionRate / 100.0)

        assertEquals(299.90, gross, 0.001)
        assertEquals(59.98, commission, 0.001)

        val tx = SaleTransaction(
            id = 1L,
            accountId = "sarah_creator",
            productName = "Sunset Lamp",
            productPrice = price,
            quantity = quantity,
            commissionRate = commissionRate,
            commissionEarned = commission,
            platform = AffiliatePlatform.TIKTOK_SHOP,
            saleDateTimestamp = System.currentTimeMillis(),
            status = CommissionStatus.PAID
        )

        assertEquals(299.90, tx.totalSalesAmount, 0.001)
        assertEquals(59.98, tx.commissionEarned, 0.001)
    }

    @Test
    fun testGoalProgressCalculation() {
        val goal = GoalProgress(
            title = "Daily Target",
            current = 180.0,
            target = 150.0,
            timeframe = "Today"
        )

        assertTrue(goal.isAchieved)
        assertEquals(120, goal.percentage)
        assertEquals(0.0, goal.remaining, 0.001)
    }
}
