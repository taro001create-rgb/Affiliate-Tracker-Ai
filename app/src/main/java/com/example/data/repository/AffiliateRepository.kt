package com.example.data.repository

import android.content.Context
import com.example.data.SampleDataProvider
import com.example.data.local.AppDatabase
import com.example.data.model.AffiliatePlatform
import com.example.data.model.AiInsight
import com.example.data.model.CommissionStatus
import com.example.data.model.DashboardSummary
import com.example.data.model.GoalProgress
import com.example.data.model.PlatformBreakdown
import com.example.data.model.ProductPerformance
import com.example.data.model.SaleTransaction
import com.example.data.model.TimeSeriesPoint
import com.example.data.model.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AffiliateRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val transactionDao = database.transactionDao()
    private val userAccountDao = database.userAccountDao()

    private val _currentAccountId = MutableStateFlow("sarah_creator")
    val currentAccountId: StateFlow<String> = _currentAccountId.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val accounts = userAccountDao.getAllAccounts().first()
        if (accounts.isEmpty()) {
            userAccountDao.insertAccounts(SampleDataProvider.defaultAccounts)
            for (acc in SampleDataProvider.defaultAccounts) {
                val demoTransactions = SampleDataProvider.generateSampleTransactions(acc.id)
                transactionDao.insertTransactions(demoTransactions)
            }
        }
    }

    fun getAllAccounts(): Flow<List<UserAccount>> = userAccountDao.getAllAccounts()

    fun getCurrentAccount(accountId: String): Flow<UserAccount?> = userAccountDao.getAccountById(accountId)

    fun getTransactionsForAccount(accountId: String): Flow<List<SaleTransaction>> =
        transactionDao.getTransactionsForAccount(accountId)

    suspend fun switchAccount(accountId: String) {
        _currentAccountId.value = accountId
    }

    suspend fun createAccount(account: UserAccount) = withContext(Dispatchers.IO) {
        userAccountDao.insertAccount(account)
        _currentAccountId.value = account.id
    }

    suspend fun updateGoals(accountId: String, daily: Double, weekly: Double, monthly: Double) =
        withContext(Dispatchers.IO) {
            userAccountDao.updateGoals(accountId, daily, weekly, monthly)
        }

    suspend fun addTransaction(transaction: SaleTransaction): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: SaleTransaction) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: SaleTransaction) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun resetDemoData(accountId: String) = withContext(Dispatchers.IO) {
        transactionDao.deleteAllForAccount(accountId)
        val freshSamples = SampleDataProvider.generateSampleTransactions(accountId)
        transactionDao.insertTransactions(freshSamples)
    }

    // Computation helpers
    fun computeDashboardSummary(transactions: List<SaleTransaction>): DashboardSummary {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // Start of today (00:00:00)
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        // Start of week (7 days ago)
        cal.timeInMillis = now
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val startOfWeek = cal.timeInMillis

        // Start of month (30 days ago)
        cal.timeInMillis = now
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val startOfMonth = cal.timeInMillis

        var totalEarnings = 0.0
        var todayEarnings = 0.0
        var weeklyEarnings = 0.0
        var monthlyEarnings = 0.0
        var totalSalesCount = 0
        var totalSalesVolume = 0.0
        var pendingCommissions = 0.0
        var approvedCommissions = 0.0
        var paidCommissions = 0.0

        for (tx in transactions) {
            totalEarnings += tx.commissionEarned
            totalSalesCount += tx.quantity
            totalSalesVolume += tx.totalSalesAmount

            if (tx.saleDateTimestamp >= startOfToday) {
                todayEarnings += tx.commissionEarned
            }
            if (tx.saleDateTimestamp >= startOfWeek) {
                weeklyEarnings += tx.commissionEarned
            }
            if (tx.saleDateTimestamp >= startOfMonth) {
                monthlyEarnings += tx.commissionEarned
            }

            when (tx.status) {
                CommissionStatus.PENDING -> pendingCommissions += tx.commissionEarned
                CommissionStatus.APPROVED -> approvedCommissions += tx.commissionEarned
                CommissionStatus.PAID -> paidCommissions += tx.commissionEarned
            }
        }

        val avgRate = if (totalSalesVolume > 0) (totalEarnings / totalSalesVolume) * 100 else 0.0

        return DashboardSummary(
            totalEarnings = totalEarnings,
            todayEarnings = todayEarnings,
            weeklyEarnings = weeklyEarnings,
            monthlyEarnings = monthlyEarnings,
            totalSalesCount = totalSalesCount,
            totalSalesVolume = totalSalesVolume,
            pendingCommissions = pendingCommissions,
            approvedCommissions = approvedCommissions,
            paidCommissions = paidCommissions,
            avgCommissionRate = avgRate
        )
    }

    fun computeGoalProgress(
        summary: DashboardSummary,
        account: UserAccount
    ): List<GoalProgress> {
        return listOf(
            GoalProgress(
                title = "Daily Target",
                current = summary.todayEarnings,
                target = account.dailyGoal,
                timeframe = "Today"
            ),
            GoalProgress(
                title = "Weekly Target",
                current = summary.weeklyEarnings,
                target = account.weeklyGoal,
                timeframe = "Last 7 Days"
            ),
            GoalProgress(
                title = "Monthly Target",
                current = summary.monthlyEarnings,
                target = account.monthlyGoal,
                timeframe = "Last 30 Days"
            )
        )
    }

    fun computeDailySeries(transactions: List<SaleTransaction>): List<TimeSeriesPoint> {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val points = mutableListOf<TimeSeriesPoint>()

        for (i in 6 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val dayStart = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            val dayEnd = cal.timeInMillis

            val matching = transactions.filter { it.saleDateTimestamp in dayStart..dayEnd }
            val earnings = matching.sumOf { it.commissionEarned }
            val count = matching.sumOf { it.quantity }
            val label = if (i == 0) "Today" else sdf.format(dayStart)

            points.add(TimeSeriesPoint(label, earnings, count, dayStart))
        }
        return points
    }

    fun computeWeeklySeries(transactions: List<SaleTransaction>): List<TimeSeriesPoint> {
        val cal = Calendar.getInstance()
        val points = mutableListOf<TimeSeriesPoint>()

        for (w in 3 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -((w + 1) * 7))
            val weekStart = cal.timeInMillis

            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -(w * 7))
            val weekEnd = cal.timeInMillis

            val matching = transactions.filter { it.saleDateTimestamp in weekStart..weekEnd }
            val earnings = matching.sumOf { it.commissionEarned }
            val count = matching.sumOf { it.quantity }
            val label = if (w == 0) "This Wk" else "Wk -${w}"

            points.add(TimeSeriesPoint(label, earnings, count, weekStart))
        }
        return points
    }

    fun computeMonthlySeries(transactions: List<SaleTransaction>): List<TimeSeriesPoint> {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        val points = mutableListOf<TimeSeriesPoint>()

        for (m in 5 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.MONTH, -m)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            val monthStart = cal.timeInMillis

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            val monthEnd = cal.timeInMillis

            val matching = transactions.filter { it.saleDateTimestamp in monthStart..monthEnd }
            val earnings = matching.sumOf { it.commissionEarned }
            val count = matching.sumOf { it.quantity }
            val label = sdf.format(monthStart)

            points.add(TimeSeriesPoint(label, earnings, count, monthStart))
        }
        return points
    }

    fun computePlatformBreakdown(transactions: List<SaleTransaction>): List<PlatformBreakdown> {
        val total = transactions.sumOf { it.commissionEarned }
        return AffiliatePlatform.entries.map { platform ->
            val matching = transactions.filter { it.platform == platform }
            val earnings = matching.sumOf { it.commissionEarned }
            val count = matching.sumOf { it.quantity }
            val pct = if (total > 0) (earnings / total).toFloat() else 0f
            PlatformBreakdown(platform, earnings, count, pct)
        }.sortedByDescending { it.totalEarnings }
    }

    fun computeTopProducts(transactions: List<SaleTransaction>): List<ProductPerformance> {
        return transactions.groupBy { it.productName }
            .map { (name, group) ->
                val platform = group.first().platform
                val comms = group.sumOf { it.commissionEarned }
                val count = group.sumOf { it.quantity }
                val rev = group.sumOf { it.totalSalesAmount }
                ProductPerformance(name, platform, comms, count, rev)
            }
            .sortedByDescending { it.totalCommissions }
            .take(6)
    }

    fun generateAiInsights(
        summary: DashboardSummary,
        platforms: List<PlatformBreakdown>,
        topProducts: List<ProductPerformance>,
        account: UserAccount
    ): List<AiInsight> {
        val insights = mutableListOf<AiInsight>()

        val topPlatform = platforms.firstOrNull { it.totalEarnings > 0 }
        if (topPlatform != null) {
            val pctFormatted = (topPlatform.percentageOfTotal * 100).toInt()
            insights.add(
                AiInsight(
                    headline = "${topPlatform.platform.displayName} is your #1 Revenue Engine",
                    description = "Driving $pctFormatted% of your total earnings ($${String.format(Locale.US, "%.2f", topPlatform.totalEarnings)}). Doubling down on ${topPlatform.platform.shortName} short-form content can accelerate weekly returns.",
                    badge = "Top Performer",
                    iconType = "growth"
                )
            )
        }

        if (summary.pendingCommissions > 0) {
            insights.add(
                AiInsight(
                    headline = "$${String.format(Locale.US, "%.2f", summary.pendingCommissions)} in Commissions Awaiting Clearance",
                    description = "You have affiliate payouts pending merchant audit. Platforms typically clear pending earnings between the 15th and 30th of the month.",
                    badge = "Pending Payout",
                    iconType = "alert"
                )
            )
        }

        val bestProduct = topProducts.firstOrNull()
        if (bestProduct != null) {
            insights.add(
                AiInsight(
                    headline = "High Conversion Winner: ${bestProduct.productName}",
                    description = "Generated $${String.format(Locale.US, "%.2f", bestProduct.totalCommissions)} across ${bestProduct.salesCount} units. Consider contacting the merchant for a tiered commission increase or exclusive promo code.",
                    badge = "Product Opportunity",
                    iconType = "tip"
                )
            )
        }

        if (summary.todayEarnings >= account.dailyGoal) {
            insights.add(
                AiInsight(
                    headline = "🔥 Daily Target Smashed!",
                    description = "You've earned $${String.format(Locale.US, "%.2f", summary.todayEarnings)} today, exceeding your $${String.format(Locale.US, "%.2f", account.dailyGoal)} daily goal by +${((summary.todayEarnings / account.dailyGoal - 1) * 100).toInt()}%.",
                    badge = "Goal Reached",
                    iconType = "growth"
                )
            )
        }

        return insights
    }

    fun exportTransactionsToCsv(transactions: List<SaleTransaction>, account: UserAccount): String {
        val sb = StringBuilder()
        sb.append("AFFILIATE TRACKER AI - COMMISSION & SALES EXPORT\n")
        sb.append("Account: ${account.name} (${account.handle})\n")
        sb.append("Generated At: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)}\n")
        sb.append("Note: Manually entered self-reported affiliate earnings report.\n\n")

        sb.append("ID,Product Name,Platform,Price,Units Sold,Gross Sales,Commission Rate (%),Commission Earned,Status,Date,Notes\n")

        for (tx in transactions) {
            val dateStr = tx.formattedDate()
            val notesEscaped = "\"${tx.notes.replace("\"", "\"\"")}\""
            val productEscaped = "\"${tx.productName.replace("\"", "\"\"")}\""
            sb.append("${tx.id},${productEscaped},\"${tx.platform.displayName}\",${String.format(Locale.US, "%.2f", tx.productPrice)},${tx.quantity},${String.format(Locale.US, "%.2f", tx.totalSalesAmount)},${tx.commissionRate},${String.format(Locale.US, "%.2f", tx.commissionEarned)},${tx.status.displayName},\"$dateStr\",$notesEscaped\n")
        }

        val totalEarnings = transactions.sumOf { it.commissionEarned }
        val totalVolume = transactions.sumOf { it.totalSalesAmount }
        val totalUnits = transactions.sumOf { it.quantity }
        sb.append("\nTOTALS,,,")
        sb.append("$totalUnits,${String.format(Locale.US, "%.2f", totalVolume)},,${String.format(Locale.US, "%.2f", totalEarnings)},,\n")

        return sb.toString()
    }
}
