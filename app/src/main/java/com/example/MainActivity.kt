package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AccountSwitchDialog
import com.example.ui.components.AddSaleForm
import com.example.ui.components.AffiliateNavigationRail
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoalsAndReportsScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryPurple
import com.example.ui.viewmodel.AffiliateViewModel
import com.example.ui.viewmodel.AppTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AffiliateTrackerApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffiliateTrackerApp(viewModel: AffiliateViewModel = viewModel()) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentAccount by viewModel.currentAccount.collectAsStateWithLifecycle()
    val allAccounts by viewModel.allAccounts.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()

    var showAccountDialog by remember { mutableStateOf(false) }
    var showAddSaleSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Feedback message display
    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissUserFeedback()
        }
    }

    // Adapt system bar icon contrast dynamically for Light/Dark themes on Android phones
    val activity = LocalActivity.current as? ComponentActivity
    DisposableEffect(isDarkTheme, activity) {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (isDarkTheme) {
                SystemBarStyle.dark(AndroidColor.TRANSPARENT)
            } else {
                SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
            },
            navigationBarStyle = if (isDarkTheme) {
                SystemBarStyle.dark(AndroidColor.TRANSPARENT)
            } else {
                SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
            }
        )
        onDispose {}
    }

    // Handle back button
    BackHandler(enabled = isSignedIn && currentTab != AppTab.DASHBOARD) {
        viewModel.selectTab(AppTab.DASHBOARD)
    }

    MyApplicationTheme(darkTheme = isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            if (!isSignedIn) {
                // Sign In & Onboarding Page (Adaptive center on computer, fluid on mobile)
                SignInScreen(
                    accounts = allAccounts,
                    onSignInWithAccount = { viewModel.signInWithAccount(it) },
                    onSignInWithCredentials = { email, pass ->
                        viewModel.signInWithCredentials(email, pass)
                    },
                    onCreateAccount = { name, handle, email, pass, niche, goal ->
                        viewModel.createAccountAndSignIn(name, handle, email, pass, niche, goal)
                    }
                )
            } else if (isWideScreen) {
                // DESKTOP & TABLET SAAS LAYOUT: Side Navigation Rail + Wide Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AffiliateNavigationRail(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            if (tab == AppTab.ADD_SALE) {
                                showAddSaleSheet = true
                            } else {
                                viewModel.selectTab(tab)
                            }
                        },
                        currentAccount = currentAccount,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { viewModel.toggleTheme() },
                        onSignOut = { viewModel.signOut() },
                        onOpenAccountSwitch = { showAccountDialog = true },
                        modifier = Modifier.statusBarsPadding().navigationBarsPadding()
                    )

                    // Right Main Workspace
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    ) {
                        // Desktop Top Header
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentTab.title.uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { showAccountDialog = true }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${currentAccount?.avatarEmoji ?: "✨"} ${currentAccount?.name ?: "Workspace"} (${currentAccount?.handle ?: "@creator"})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AccentGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "● Live SaaS Mode",
                                            color = AccentGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Content Body
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 12.dp)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "desktopTabContent"
                            ) { tab ->
                                when (tab) {
                                    AppTab.DASHBOARD -> {
                                        val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                        val goals by viewModel.goalProgressList.collectAsStateWithLifecycle()
                                        val rawTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()

                                        DashboardScreen(
                                            currentAccount = currentAccount,
                                            summary = summary,
                                            goals = goals,
                                            recentTransactions = rawTransactions,
                                            onNavigateTab = { target ->
                                                if (target == AppTab.ADD_SALE) {
                                                    showAddSaleSheet = true
                                                } else {
                                                    viewModel.selectTab(target)
                                                }
                                            },
                                            onOpenAccountSwitcher = { showAccountDialog = true },
                                            onEditTransaction = { tx ->
                                                viewModel.startEditingTransaction(tx)
                                                showAddSaleSheet = true
                                            },
                                            isWideScreen = true
                                        )
                                    }

                                    AppTab.TRANSACTIONS -> {
                                        val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
                                        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                                        val filterPlatform by viewModel.filterPlatform.collectAsStateWithLifecycle()
                                        val filterStatus by viewModel.filterStatus.collectAsStateWithLifecycle()
                                        val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

                                        TransactionsScreen(
                                            transactions = filteredTransactions,
                                            searchQuery = searchQuery,
                                            onSearchChange = { viewModel.setSearchQuery(it) },
                                            selectedPlatform = filterPlatform,
                                            onSelectPlatform = { viewModel.setFilterPlatform(it) },
                                            selectedStatus = filterStatus,
                                            onSelectStatus = { viewModel.setFilterStatus(it) },
                                            currentSort = sortOrder,
                                            onSelectSort = { viewModel.setSortOrder(it) },
                                            onEditTransaction = { tx ->
                                                viewModel.startEditingTransaction(tx)
                                                showAddSaleSheet = true
                                            },
                                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                                            onAddNewSale = { showAddSaleSheet = true },
                                            isWideScreen = true
                                        )
                                    }

                                    AppTab.ADD_SALE -> AddSaleForm(
                                        initialTransaction = editingTransaction,
                                        onSave = { id, name, price, qty, rate, platform, date, status, notes ->
                                            viewModel.saveSale(id, name, price, qty, rate, platform, date, status, notes)
                                            viewModel.selectTab(AppTab.TRANSACTIONS)
                                        },
                                        onCancel = {
                                            viewModel.startEditingTransaction(null)
                                            viewModel.selectTab(AppTab.DASHBOARD)
                                        },
                                        modifier = Modifier.widthIn(max = 640.dp)
                                    )

                                    AppTab.ANALYTICS -> {
                                        val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                        val analyticsTimeframe by viewModel.analyticsTimeframe.collectAsStateWithLifecycle()
                                        val dailySeries by viewModel.dailySeries.collectAsStateWithLifecycle()
                                        val weeklySeries by viewModel.weeklySeries.collectAsStateWithLifecycle()
                                        val monthlySeries by viewModel.monthlySeries.collectAsStateWithLifecycle()
                                        val platformBreakdown by viewModel.platformBreakdown.collectAsStateWithLifecycle()
                                        val topProducts by viewModel.topProducts.collectAsStateWithLifecycle()

                                        AnalyticsScreen(
                                            summary = summary,
                                            currentTimeframe = analyticsTimeframe,
                                            onSelectTimeframe = { viewModel.setAnalyticsTimeframe(it) },
                                            dailySeries = dailySeries,
                                            weeklySeries = weeklySeries,
                                            monthlySeries = monthlySeries,
                                            platformBreakdown = platformBreakdown,
                                            topProducts = topProducts,
                                            isWideScreen = true
                                        )
                                    }

                                    AppTab.GOALS_REPORTS -> {
                                        val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                        val goals by viewModel.goalProgressList.collectAsStateWithLifecycle()
                                        val platformBreakdown by viewModel.platformBreakdown.collectAsStateWithLifecycle()
                                        val rawTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()

                                        GoalsAndReportsScreen(
                                            currentAccount = currentAccount,
                                            summary = summary,
                                            goals = goals,
                                            platforms = platformBreakdown,
                                            transactions = rawTransactions,
                                            onUpdateGoals = { d, w, m -> viewModel.updateGoals(d, w, m) },
                                            onGenerateCsv = { viewModel.generateCsvContent() },
                                            isWideScreen = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // MOBILE COMPACT LAYOUT: TopAppBar + Bottom Navigation Bar
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AffiliateTopAppBar(
                            creatorName = currentAccount?.name ?: "Affiliate Tracker",
                            avatarEmoji = currentAccount?.avatarEmoji ?: "✨",
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() },
                            onOpenAccountSwitch = { showAccountDialog = true },
                            onSignOut = { viewModel.signOut() }
                        )
                    },
                    bottomBar = {
                        AffiliateBottomNav(
                            currentTab = currentTab,
                            onTabSelected = { tab ->
                                if (tab == AppTab.ADD_SALE) {
                                    showAddSaleSheet = true
                                } else {
                                    viewModel.selectTab(tab)
                                }
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "mobileTabContent"
                        ) { tab ->
                            when (tab) {
                                AppTab.DASHBOARD -> {
                                    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                    val goals by viewModel.goalProgressList.collectAsStateWithLifecycle()
                                    val rawTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()

                                    DashboardScreen(
                                        currentAccount = currentAccount,
                                        summary = summary,
                                        goals = goals,
                                        recentTransactions = rawTransactions,
                                        onNavigateTab = { target ->
                                            if (target == AppTab.ADD_SALE) {
                                                showAddSaleSheet = true
                                            } else {
                                                viewModel.selectTab(target)
                                            }
                                        },
                                        onOpenAccountSwitcher = { showAccountDialog = true },
                                        onEditTransaction = { tx ->
                                            viewModel.startEditingTransaction(tx)
                                            showAddSaleSheet = true
                                        },
                                        isWideScreen = false
                                    )
                                }

                                AppTab.TRANSACTIONS -> {
                                    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
                                    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                                    val filterPlatform by viewModel.filterPlatform.collectAsStateWithLifecycle()
                                    val filterStatus by viewModel.filterStatus.collectAsStateWithLifecycle()
                                    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

                                    TransactionsScreen(
                                        transactions = filteredTransactions,
                                        searchQuery = searchQuery,
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        selectedPlatform = filterPlatform,
                                        onSelectPlatform = { viewModel.setFilterPlatform(it) },
                                        selectedStatus = filterStatus,
                                        onSelectStatus = { viewModel.setFilterStatus(it) },
                                        currentSort = sortOrder,
                                        onSelectSort = { viewModel.setSortOrder(it) },
                                        onEditTransaction = { tx ->
                                            viewModel.startEditingTransaction(tx)
                                            showAddSaleSheet = true
                                        },
                                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                                        onAddNewSale = { showAddSaleSheet = true },
                                        isWideScreen = false
                                    )
                                }

                                AppTab.ADD_SALE -> AddSaleForm(
                                    initialTransaction = editingTransaction,
                                    onSave = { id, name, price, qty, rate, platform, date, status, notes ->
                                        viewModel.saveSale(id, name, price, qty, rate, platform, date, status, notes)
                                        viewModel.selectTab(AppTab.TRANSACTIONS)
                                    },
                                    onCancel = {
                                        viewModel.startEditingTransaction(null)
                                        viewModel.selectTab(AppTab.DASHBOARD)
                                    }
                                )

                                AppTab.ANALYTICS -> {
                                    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                    val analyticsTimeframe by viewModel.analyticsTimeframe.collectAsStateWithLifecycle()
                                    val dailySeries by viewModel.dailySeries.collectAsStateWithLifecycle()
                                    val weeklySeries by viewModel.weeklySeries.collectAsStateWithLifecycle()
                                    val monthlySeries by viewModel.monthlySeries.collectAsStateWithLifecycle()
                                    val platformBreakdown by viewModel.platformBreakdown.collectAsStateWithLifecycle()
                                    val topProducts by viewModel.topProducts.collectAsStateWithLifecycle()

                                    AnalyticsScreen(
                                        summary = summary,
                                        currentTimeframe = analyticsTimeframe,
                                        onSelectTimeframe = { viewModel.setAnalyticsTimeframe(it) },
                                        dailySeries = dailySeries,
                                        weeklySeries = weeklySeries,
                                        monthlySeries = monthlySeries,
                                        platformBreakdown = platformBreakdown,
                                        topProducts = topProducts,
                                        isWideScreen = false
                                    )
                                }

                                AppTab.GOALS_REPORTS -> {
                                    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
                                    val goals by viewModel.goalProgressList.collectAsStateWithLifecycle()
                                    val platformBreakdown by viewModel.platformBreakdown.collectAsStateWithLifecycle()
                                    val rawTransactions by viewModel.rawTransactions.collectAsStateWithLifecycle()

                                    GoalsAndReportsScreen(
                                        currentAccount = currentAccount,
                                        summary = summary,
                                        goals = goals,
                                        platforms = platformBreakdown,
                                        transactions = rawTransactions,
                                        onUpdateGoals = { d, w, m -> viewModel.updateGoals(d, w, m) },
                                        onGenerateCsv = { viewModel.generateCsvContent() },
                                        isWideScreen = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add / Edit Sale Bottom Sheet Modal
            if (showAddSaleSheet) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = {
                        showAddSaleSheet = false
                        viewModel.startEditingTransaction(null)
                    },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AddSaleForm(
                            initialTransaction = editingTransaction,
                            onSave = { id, name, price, qty, rate, platform, date, status, notes ->
                                viewModel.saveSale(id, name, price, qty, rate, platform, date, status, notes)
                                showAddSaleSheet = false
                            },
                            onCancel = {
                                showAddSaleSheet = false
                                viewModel.startEditingTransaction(null)
                            },
                            modifier = Modifier.widthIn(max = 640.dp)
                        )
                    }
                }
            }

            // Workspace / Account Switch Dialog
            if (showAccountDialog) {
                val allAccounts by viewModel.allAccounts.collectAsStateWithLifecycle()
                AccountSwitchDialog(
                    currentAccount = currentAccount,
                    allAccounts = allAccounts,
                    onSelectAccount = { viewModel.switchAccount(it) },
                    onCreateAccount = { name, handle, niche, d, w, m ->
                        viewModel.createNewAccount(name, handle, niche, d, w, m)
                    },
                    onResetDemoData = { viewModel.resetDemoData() },
                    onDismiss = { showAccountDialog = false }
                )
            }
        }
    }
}

@Composable
fun AffiliateTopAppBar(
    creatorName: String,
    avatarEmoji: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenAccountSwitch: () -> Unit,
    onSignOut: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenAccountSwitch() }
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AFFILIATE TRACKER",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AccentGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "AI",
                                color = AccentGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "$avatarEmoji $creatorName",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons: Theme Toggle & Sign Out
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = if (isDarkTheme) Color(0xFFFBBF24) else PrimaryPurple
                    )
                }

                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AffiliateBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val navItems = listOf(
            Triple(AppTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
            Triple(AppTab.TRANSACTIONS, "History", Icons.Default.ReceiptLong),
            Triple(AppTab.ADD_SALE, "+ Sale", Icons.Default.AddCircle),
            Triple(AppTab.ANALYTICS, "Analytics", Icons.Default.BarChart),
            Triple(AppTab.GOALS_REPORTS, "Goals", Icons.Default.TrackChanges)
        )

        navItems.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            val isAddSale = tab == AppTab.ADD_SALE

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (isAddSale) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isAddSale) Color.White else PrimaryPurple,
                    selectedTextColor = PrimaryPurple,
                    indicatorColor = if (isAddSale) Color.Transparent else PrimaryPurple.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
