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

    @Test
    fun testValidGmailAddress() {
        val res = com.example.util.EmailValidator.validateEmail("sarah.creator@gmail.com")
        assertTrue(res.isValid)
        assertEquals(null, res.errorMessage)
    }

    @Test
    fun testWrongGmailDomainTypoIdentified() {
        // Common typo: @gmai.com instead of @gmail.com
        val res1 = com.example.util.EmailValidator.validateEmail("creator@gmai.com")
        org.junit.Assert.assertFalse(res1.isValid)
        assertTrue(res1.isGmailTypo)
        assertEquals("creator@gmail.com", res1.suggestedEmail)
        assertTrue(res1.errorMessage?.contains("Did you mean '@gmail.com'?") == true)

        // Common typo: @gamil.com
        val res2 = com.example.util.EmailValidator.validateEmail("affiliate@gamil.com")
        org.junit.Assert.assertFalse(res2.isValid)
        assertTrue(res2.isGmailTypo)
        assertEquals("affiliate@gmail.com", res2.suggestedEmail)

        // Common typo: @gmail.con
        val res3 = com.example.util.EmailValidator.validateEmail("user@gmail.con")
        org.junit.Assert.assertFalse(res3.isValid)
        assertTrue(res3.isGmailTypo)
        assertEquals("user@gmail.com", res3.suggestedEmail)
    }

    @Test
    fun testWrongEmailFormatIdentified() {
        // Missing @
        val res1 = com.example.util.EmailValidator.validateEmail("creatorgmail.com")
        org.junit.Assert.assertFalse(res1.isValid)
        assertTrue(res1.errorMessage?.contains("Missing '@'") == true)

        // Missing username
        val res2 = com.example.util.EmailValidator.validateEmail("@gmail.com")
        org.junit.Assert.assertFalse(res2.isValid)
        assertTrue(res2.errorMessage?.contains("Missing username") == true)

        // Missing domain
        val res3 = com.example.util.EmailValidator.validateEmail("creator@")
        org.junit.Assert.assertFalse(res3.isValid)
        assertTrue(res3.errorMessage?.contains("Missing domain") == true)

        // Spaces in email
        val res4 = com.example.util.EmailValidator.validateEmail("creator user@gmail.com")
        org.junit.Assert.assertFalse(res4.isValid)
        assertTrue(res4.errorMessage?.contains("spaces") == true)
    }
}

