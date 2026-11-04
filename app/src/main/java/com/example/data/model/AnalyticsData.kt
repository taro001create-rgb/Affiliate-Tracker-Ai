package com.example.data.model

data class DashboardSummary(
    val totalEarnings: Double = 0.0,
    val todayEarnings: Double = 0.0,
    val weeklyEarnings: Double = 0.0,
    val monthlyEarnings: Double = 0.0,
    val totalSalesCount: Int = 0,
    val totalSalesVolume: Double = 0.0,
    val pendingCommissions: Double = 0.0,
    val approvedCommissions: Double = 0.0,
    val paidCommissions: Double = 0.0,
    val avgCommissionRate: Double = 0.0
)

data class GoalProgress(
    val title: String,
    val current: Double,
    val target: Double,
    val timeframe: String
) {
    val progressFraction: Float
        get() = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f

    val percentage: Int
        get() = if (target > 0) ((current / target) * 100).toInt() else 0

    val isAchieved: Boolean
        get() = current >= target

    val remaining: Double
        get() = (target - current).coerceAtLeast(0.0)
}

data class TimeSeriesPoint(
    val label: String,
    val earnings: Double,
    val salesCount: Int,
    val timestamp: Long
)

data class PlatformBreakdown(
    val platform: AffiliatePlatform,
    val totalEarnings: Double,
    val salesCount: Int,
    val percentageOfTotal: Float
)

data class ProductPerformance(
    val productName: String,
    val platform: AffiliatePlatform,
    val totalCommissions: Double,
    val salesCount: Int,
    val totalRevenue: Double
)

data class AiInsight(
    val headline: String,
    val description: String,
    val badge: String,
    val iconType: String // "growth", "tip", "alert"
)
