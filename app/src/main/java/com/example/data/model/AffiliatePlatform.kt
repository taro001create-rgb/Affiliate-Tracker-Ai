package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmazonOrange
import com.example.ui.theme.ClickBankBlue
import com.example.ui.theme.OtherPlatformPurple
import com.example.ui.theme.ShopifyGreen
import com.example.ui.theme.TikTokPink

enum class AffiliatePlatform(
    val id: String,
    val displayName: String,
    val shortName: String,
    val defaultCommissionRate: Double,
    val brandColor: Color
) {
    TIKTOK_SHOP(
        id = "tiktok_shop",
        displayName = "TikTok Shop Affiliate",
        shortName = "TikTok Shop",
        defaultCommissionRate = 15.0,
        brandColor = TikTokPink
    ),
    AMAZON(
        id = "amazon_associates",
        displayName = "Amazon Associates",
        shortName = "Amazon",
        defaultCommissionRate = 4.0,
        brandColor = AmazonOrange
    ),
    SHOPIFY(
        id = "shopify_affiliate",
        displayName = "Shopify Affiliate",
        shortName = "Shopify",
        defaultCommissionRate = 20.0,
        brandColor = ShopifyGreen
    ),
    CLICKBANK(
        id = "clickbank",
        displayName = "ClickBank",
        shortName = "ClickBank",
        defaultCommissionRate = 50.0,
        brandColor = ClickBankBlue
    ),
    OTHER(
        id = "other",
        displayName = "Other Affiliate Program",
        shortName = "Other",
        defaultCommissionRate = 10.0,
        brandColor = OtherPlatformPurple
    );

    companion object {
        fun fromId(id: String): AffiliatePlatform {
            return entries.find { it.id == id || it.name.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
