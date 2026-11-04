package com.example.data

import com.example.data.model.AffiliatePlatform
import com.example.data.model.CommissionStatus
import com.example.data.model.SaleTransaction
import com.example.data.model.UserAccount
import java.util.Calendar

object SampleDataProvider {
    val defaultAccounts = listOf(
        UserAccount(
            id = "sarah_creator",
            name = "Sarah Jenkins",
            handle = "@sarah_finds",
            email = "sarah@creatortok.com",
            niche = "TikTok Shop & Beauty Lifestyle",
            dailyGoal = 150.0,
            weeklyGoal = 1000.0,
            monthlyGoal = 4500.0,
            currencySymbol = "$",
            isDemoAccount = true,
            avatarEmoji = "✨"
        ),
        UserAccount(
            id = "alex_tech",
            name = "Alex Vance",
            handle = "@tech_affiliate",
            email = "alex@gadgetreview.io",
            niche = "Consumer Tech & Amazon Reviews",
            dailyGoal = 220.0,
            weeklyGoal = 1500.0,
            monthlyGoal = 6000.0,
            currencySymbol = "$",
            isDemoAccount = true,
            avatarEmoji = "⚡"
        ),
        UserAccount(
            id = "growth_guru",
            name = "Marcus King",
            handle = "@click_growth",
            email = "marcus@digitalaffiliate.co",
            niche = "SaaS & Info Products",
            dailyGoal = 350.0,
            weeklyGoal = 2500.0,
            monthlyGoal = 10000.0,
            currencySymbol = "$",
            isDemoAccount = true,
            avatarEmoji = "📈"
        )
    )

    fun generateSampleTransactions(accountId: String): List<SaleTransaction> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        fun daysAgo(days: Int, hour: Int = 14): Long {
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, -days)
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, (10..55).random())
            return cal.timeInMillis
        }

        return listOf(
            SaleTransaction(
                accountId = accountId,
                productName = "Viral Sunset Projection Lamp",
                productPrice = 29.99,
                quantity = 14,
                commissionRate = 20.0,
                commissionEarned = 29.99 * 14 * 0.20,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(0, 11),
                status = CommissionStatus.PAID,
                notes = "Promoted in evening livestream - massive conversion spike"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Matcha Whisk Electric Set",
                productPrice = 22.50,
                quantity = 8,
                commissionRate = 18.0,
                commissionEarned = 22.50 * 8 * 0.18,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(0, 9),
                status = CommissionStatus.APPROVED,
                notes = "Morning routine short video hook"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Ergonomic Memory Foam Pillow",
                productPrice = 49.99,
                quantity = 6,
                commissionRate = 4.0,
                commissionEarned = 49.99 * 6 * 0.04,
                platform = AffiliatePlatform.AMAZON,
                saleDateTimestamp = daysAgo(1, 16),
                status = CommissionStatus.PAID,
                notes = "Storefront bio link click"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Collagen Glow Gummies",
                productPrice = 34.00,
                quantity = 11,
                commissionRate = 25.0,
                commissionEarned = 34.00 * 11 * 0.25,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(2, 18),
                status = CommissionStatus.PAID,
                notes = "Sample review video"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Aesthetic Linen Canvas Tote",
                productPrice = 38.00,
                quantity = 5,
                commissionRate = 20.0,
                commissionEarned = 38.00 * 5 * 0.20,
                platform = AffiliatePlatform.SHOPIFY,
                saleDateTimestamp = daysAgo(3, 13),
                status = CommissionStatus.PENDING,
                notes = "Direct merchant collab program"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Wireless Noise-Cancelling Earbuds",
                productPrice = 89.99,
                quantity = 4,
                commissionRate = 3.5,
                commissionEarned = 89.99 * 4 * 0.035,
                platform = AffiliatePlatform.AMAZON,
                saleDateTimestamp = daysAgo(4, 15),
                status = CommissionStatus.PAID,
                notes = "Desk setup roundup"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "21-Day Smoothie Blueprint E-Book",
                productPrice = 37.00,
                quantity = 7,
                commissionRate = 70.0,
                commissionEarned = 37.00 * 7 * 0.70,
                platform = AffiliatePlatform.CLICKBANK,
                saleDateTimestamp = daysAgo(5, 20),
                status = CommissionStatus.PAID,
                notes = "High commission digital product"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Heatless Hair Curling Ribbon",
                productPrice = 19.99,
                quantity = 18,
                commissionRate = 22.0,
                commissionEarned = 19.99 * 18 * 0.22,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(6, 17),
                status = CommissionStatus.PAID,
                notes = "Tutorial video went viral with 45k views"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Smart Time Marker Water Bottle",
                productPrice = 28.00,
                quantity = 9,
                commissionRate = 15.0,
                commissionEarned = 28.00 * 9 * 0.15,
                platform = AffiliatePlatform.SHOPIFY,
                saleDateTimestamp = daysAgo(8, 12),
                status = CommissionStatus.APPROVED,
                notes = "Fitness challenge reel series"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Portable Foldable Neck Fan",
                productPrice = 26.50,
                quantity = 12,
                commissionRate = 18.0,
                commissionEarned = 26.50 * 12 * 0.18,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(10, 15),
                status = CommissionStatus.PAID,
                notes = "Summer travel gear showcase"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Ring Light with Extendable Tripod",
                productPrice = 42.00,
                quantity = 8,
                commissionRate = 4.0,
                commissionEarned = 42.00 * 8 * 0.04,
                platform = AffiliatePlatform.AMAZON,
                saleDateTimestamp = daysAgo(12, 14),
                status = CommissionStatus.PAID,
                notes = "Content creation starter kit post"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Custom Keto Plan Video Series",
                productPrice = 47.00,
                quantity = 6,
                commissionRate = 75.0,
                commissionEarned = 47.00 * 6 * 0.75,
                platform = AffiliatePlatform.CLICKBANK,
                saleDateTimestamp = daysAgo(15, 19),
                status = CommissionStatus.PAID,
                notes = "Digital info bundle affiliate"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "LED Under-Cabinet Sensor Bars",
                productPrice = 31.99,
                quantity = 15,
                commissionRate = 20.0,
                commissionEarned = 31.99 * 15 * 0.20,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(18, 21),
                status = CommissionStatus.PAID,
                notes = "Kitchen makeover transition"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Creator Video Filter Lightroom Presets",
                productPrice = 45.00,
                quantity = 6,
                commissionRate = 30.0,
                commissionEarned = 45.00 * 6 * 0.30,
                platform = AffiliatePlatform.OTHER,
                saleDateTimestamp = daysAgo(22, 16),
                status = CommissionStatus.PENDING,
                notes = "Gumroad affiliate partner link"
            ),
            SaleTransaction(
                accountId = accountId,
                productName = "Silicone Ice Facial Roller",
                productPrice = 14.99,
                quantity = 24,
                commissionRate = 20.0,
                commissionEarned = 14.99 * 24 * 0.20,
                platform = AffiliatePlatform.TIKTOK_SHOP,
                saleDateTimestamp = daysAgo(25, 11),
                status = CommissionStatus.PAID,
                notes = "Skincare morning ritual"
            )
        )
    }
}
