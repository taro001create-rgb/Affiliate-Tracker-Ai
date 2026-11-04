package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.AffiliateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String) {
    DASHBOARD("Dashboard"),
    TRANSACTIONS("Transactions"),
    ADD_SALE("Add Sale"),
    ANALYTICS("Analytics"),
    GOALS_REPORTS("Goals & Reports")
}

enum class AnalyticsTimeframe {
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class TransactionSort {
    DATE_DESC,
    DATE_ASC,
    COMMISSION_DESC,
    COMMISSION_ASC
}

class AffiliateViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AffiliateRepository(application)

    private val authPrefs = application.getSharedPreferences("affiliate_auth", android.content.Context.MODE_PRIVATE)
    private val _isSignedIn = MutableStateFlow(authPrefs.getBoolean("is_signed_in", false))
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterPlatform = MutableStateFlow<AffiliatePlatform?>(null)
    val filterPlatform: StateFlow<AffiliatePlatform?> = _filterPlatform.asStateFlow()

    private val _filterStatus = MutableStateFlow<CommissionStatus?>(null)
    val filterStatus: StateFlow<CommissionStatus?> = _filterStatus.asStateFlow()

    private val _sortOrder = MutableStateFlow(TransactionSort.DATE_DESC)
    val sortOrder: StateFlow<TransactionSort> = _sortOrder.asStateFlow()

    private val _analyticsTimeframe = MutableStateFlow(AnalyticsTimeframe.DAILY)
    val analyticsTimeframe: StateFlow<AnalyticsTimeframe> = _analyticsTimeframe.asStateFlow()

    private val _editingTransaction = MutableStateFlow<SaleTransaction?>(null)
    val editingTransaction: StateFlow<SaleTransaction?> = _editingTransaction.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    val allAccounts: StateFlow<List<UserAccount>> = repository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentAccount: StateFlow<UserAccount?> = repository.currentAccountId
        .flatMapLatest { id -> repository.getCurrentAccount(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val rawTransactions: StateFlow<List<SaleTransaction>> = repository.currentAccountId
        .flatMapLatest { id -> repository.getTransactionsForAccount(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTransactions: StateFlow<List<SaleTransaction>> = combine(
        rawTransactions,
        _searchQuery,
        _filterPlatform,
        _filterStatus,
        _sortOrder
    ) { txs, query, platform, status, sort ->
        var list = txs
        if (query.isNotBlank()) {
            val q = query.trim()
            list = list.filter {
                it.productName.contains(q, ignoreCase = true) ||
                    it.notes.contains(q, ignoreCase = true) ||
                    it.platform.displayName.contains(q, ignoreCase = true)
            }
        }
        if (platform != null) {
            list = list.filter { it.platform == platform }
        }
        if (status != null) {
            list = list.filter { it.status == status }
        }
        when (sort) {
            TransactionSort.DATE_DESC -> list.sortedByDescending { it.saleDateTimestamp }
            TransactionSort.DATE_ASC -> list.sortedBy { it.saleDateTimestamp }
            TransactionSort.COMMISSION_DESC -> list.sortedByDescending { it.commissionEarned }
            TransactionSort.COMMISSION_ASC -> list.sortedBy { it.commissionEarned }
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardSummary: StateFlow<DashboardSummary> = rawTransactions.map { txs ->
        repository.computeDashboardSummary(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    val goalProgressList: StateFlow<List<GoalProgress>> = combine(
        dashboardSummary,
        currentAccount
    ) { summary, account ->
        if (account != null) {
            repository.computeGoalProgress(summary, account)
        } else {
            emptyList()
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailySeries: StateFlow<List<TimeSeriesPoint>> = rawTransactions.map { txs ->
        repository.computeDailySeries(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklySeries: StateFlow<List<TimeSeriesPoint>> = rawTransactions.map { txs ->
        repository.computeWeeklySeries(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlySeries: StateFlow<List<TimeSeriesPoint>> = rawTransactions.map { txs ->
        repository.computeMonthlySeries(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformBreakdown: StateFlow<List<PlatformBreakdown>> = rawTransactions.map { txs ->
        repository.computePlatformBreakdown(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topProducts: StateFlow<List<ProductPerformance>> = rawTransactions.map { txs ->
        repository.computeTopProducts(txs)
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiInsights: StateFlow<List<AiInsight>> = combine(
        dashboardSummary,
        platformBreakdown,
        topProducts,
        currentAccount
    ) { summary, platforms, products, account ->
        if (account != null) {
            repository.generateAiInsights(summary, platforms, products, account)
        } else {
            emptyList()
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterPlatform(platform: AffiliatePlatform?) {
        _filterPlatform.value = platform
    }

    fun setFilterStatus(status: CommissionStatus?) {
        _filterStatus.value = status
    }

    fun setSortOrder(order: TransactionSort) {
        _sortOrder.value = order
    }

    fun setAnalyticsTimeframe(timeframe: AnalyticsTimeframe) {
        _analyticsTimeframe.value = timeframe
    }

    fun startEditingTransaction(tx: SaleTransaction?) {
        _editingTransaction.value = tx
    }

    fun dismissUserFeedback() {
        _userFeedbackMessage.value = null
    }

    fun signInWithAccount(accountId: String) {
        viewModelScope.launch {
            repository.switchAccount(accountId)
            authPrefs.edit().putBoolean("is_signed_in", true).apply()
            _isSignedIn.value = true
            _userFeedbackMessage.value = "Signed in to workspace!"
        }
    }

    fun signInWithCredentials(emailOrHandle: String, pass: String): Boolean {
        val accounts = allAccounts.value
        val matched = accounts.find {
            it.email.equals(emailOrHandle, ignoreCase = true) ||
                it.handle.equals(emailOrHandle, ignoreCase = true) ||
                it.name.contains(emailOrHandle, ignoreCase = true)
        }
        val targetId = matched?.id ?: accounts.firstOrNull()?.id ?: "sarah_creator"
        signInWithAccount(targetId)
        return true
    }

    fun signOut() {
        authPrefs.edit().putBoolean("is_signed_in", false).apply()
        _isSignedIn.value = false
        _userFeedbackMessage.value = "Signed out of workspace"
    }

    fun switchAccount(accountId: String) {
        viewModelScope.launch {
            repository.switchAccount(accountId)
            _userFeedbackMessage.value = "Switched affiliate workspace"
        }
    }

    fun createAccountAndSignIn(name: String, handle: String, email: String, pass: String, niche: String, monthlyGoal: Double) {
        viewModelScope.launch {
            val newAcc = UserAccount(
                id = "acc_${UUID.randomUUID().toString().take(8)}",
                name = name,
                handle = if (handle.startsWith("@")) handle else "@$handle",
                email = email,
                niche = niche,
                dailyGoal = (monthlyGoal / 30.0).coerceAtLeast(50.0),
                weeklyGoal = (monthlyGoal / 4.0).coerceAtLeast(300.0),
                monthlyGoal = monthlyGoal,
                currencySymbol = "$",
                isDemoAccount = false,
                avatarEmoji = "🚀"
            )
            repository.createAccount(newAcc)
            authPrefs.edit().putBoolean("is_signed_in", true).apply()
            _isSignedIn.value = true
            _userFeedbackMessage.value = "Welcome to Affiliate Tracker AI, ${newAcc.name}!"
        }
    }

    fun createNewAccount(name: String, handle: String, niche: String, dailyGoal: Double, weeklyGoal: Double, monthlyGoal: Double) {
        viewModelScope.launch {
            val newAcc = UserAccount(
                id = "acc_${UUID.randomUUID().toString().take(8)}",
                name = name,
                handle = if (handle.startsWith("@")) handle else "@$handle",
                email = "${handle.replace("@", "").lowercase()}@creator.io",
                niche = niche,
                dailyGoal = dailyGoal,
                weeklyGoal = weeklyGoal,
                monthlyGoal = monthlyGoal,
                currencySymbol = "$",
                isDemoAccount = false,
                avatarEmoji = "💼"
            )
            repository.createAccount(newAcc)
            _userFeedbackMessage.value = "Created workspace for ${newAcc.name}"
        }
    }

    fun updateGoals(daily: Double, weekly: Double, monthly: Double) {
        val acc = currentAccount.value ?: return
        viewModelScope.launch {
            repository.updateGoals(acc.id, daily, weekly, monthly)
            _userFeedbackMessage.value = "Goals updated successfully!"
        }
    }

    fun saveSale(
        id: Long = 0,
        productName: String,
        productPrice: Double,
        quantity: Int,
        commissionRate: Double,
        platform: AffiliatePlatform,
        saleDateTimestamp: Long,
        status: CommissionStatus,
        notes: String
    ) {
        val accId = repository.currentAccountId.value
        val commissionEarned = productPrice * quantity * (commissionRate / 100.0)

        val tx = SaleTransaction(
            id = id,
            accountId = accId,
            productName = productName,
            productPrice = productPrice,
            quantity = quantity,
            commissionRate = commissionRate,
            commissionEarned = commissionEarned,
            platform = platform,
            saleDateTimestamp = saleDateTimestamp,
            status = status,
            notes = notes
        )

        viewModelScope.launch {
            if (id == 0L) {
                repository.addTransaction(tx)
                _userFeedbackMessage.value = "Sale recorded! Earned +$${String.format(java.util.Locale.US, "%.2f", commissionEarned)}"
            } else {
                repository.updateTransaction(tx)
                _userFeedbackMessage.value = "Transaction updated!"
            }
            _editingTransaction.value = null
        }
    }

    fun deleteTransaction(tx: SaleTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            _userFeedbackMessage.value = "Sale deleted"
        }
    }

    fun resetDemoData() {
        val accId = repository.currentAccountId.value
        viewModelScope.launch {
            repository.resetDemoData(accId)
            _userFeedbackMessage.value = "Sample data restored!"
        }
    }

    fun generateCsvContent(): String {
        val acc = currentAccount.value ?: return ""
        val txs = rawTransactions.value
        return repository.exportTransactionsToCsv(txs, acc)
    }
}
