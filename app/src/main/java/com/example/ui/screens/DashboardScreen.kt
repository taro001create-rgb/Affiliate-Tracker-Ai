package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardSummary
import com.example.data.model.GoalProgress
import com.example.data.model.SaleTransaction
import com.example.data.model.UserAccount
import com.example.ui.components.GoalProgressBar
import com.example.ui.components.PlatformBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberPending
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PurpleDark
import com.example.ui.viewmodel.AppTab
import java.util.Locale

@Composable
fun DashboardScreen(
    currentAccount: UserAccount?,
    summary: DashboardSummary,
    goals: List<GoalProgress>,
    recentTransactions: List<SaleTransaction>,
    onNavigateTab: (AppTab) -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    onEditTransaction: (SaleTransaction) -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = if (isWideScreen) 24.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item(key = "dashboard_hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PurpleDark.copy(alpha = 0.28f),
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        1.dp,
                        PrimaryPurple.copy(alpha = 0.35f),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isWideScreen) 22.dp else 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentAccount?.avatarEmoji ?: "✨",
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentAccount?.name ?: "Creator Workspace",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${currentAccount?.handle ?: "@creator"} • ${currentAccount?.niche ?: "Affiliate Tracker"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onOpenAccountSwitcher() }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = "Switch Account",
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Workspace",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NET AFFILIATE COMMISSIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", summary.totalEarnings)}",
                                style = if (isWideScreen) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = AccentGreen
                            )
                        }

                        Button(
                            onClick = { onNavigateTab(AppTab.ADD_SALE) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("dashboard_add_sale_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Sale", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Earnings Breakdown
        item(key = "section_earnings") {
            Text(
                text = "Earnings Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Responsive KPI Cards: 4 across on Desktop, 2 across on Mobile
        item(key = "kpi_responsive_grid") {
            if (isWideScreen) {
                // Wide Desktop Layout: 4 columns
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Today's Earnings",
                            value = "$${String.format(Locale.US, "%.2f", summary.todayEarnings)}",
                            icon = Icons.Default.CalendarToday,
                            iconTint = AccentGreen,
                            badgeText = "TODAY",
                            badgeColor = AccentGreen,
                            subtitle = "Real-time daily tracker",
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Weekly Earnings",
                            value = "$${String.format(Locale.US, "%.2f", summary.weeklyEarnings)}",
                            icon = Icons.Default.DateRange,
                            iconTint = PrimaryPurple,
                            badgeText = "7 DAYS",
                            badgeColor = PrimaryPurple,
                            subtitle = "Last 7 days total",
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Monthly Earnings",
                            value = "$${String.format(Locale.US, "%,.2f", summary.monthlyEarnings)}",
                            icon = Icons.Default.MonetizationOn,
                            iconTint = Color(0xFF38BDF8),
                            badgeText = "30 DAYS",
                            badgeColor = Color(0xFF38BDF8),
                            subtitle = "Current run rate",
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Total Sales",
                            value = "${summary.totalSalesCount} units",
                            icon = Icons.Default.ShoppingBag,
                            iconTint = Color(0xFFF472B6),
                            subtitle = "$${String.format(Locale.US, "%,.2f", summary.totalSalesVolume)} gross",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Paid Out (Cleared)",
                            value = "$${String.format(Locale.US, "%.2f", summary.paidCommissions)}",
                            icon = Icons.Default.Paid,
                            iconTint = AccentGreen,
                            badgeText = "CLEARED",
                            badgeColor = AccentGreen,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Pending Audit",
                            value = "$${String.format(Locale.US, "%.2f", summary.pendingCommissions)}",
                            icon = Icons.Default.HourglassTop,
                            iconTint = AmberPending,
                            badgeText = "PENDING",
                            badgeColor = AmberPending,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Average Rate",
                            value = "${String.format(Locale.US, "%.1f", summary.avgCommissionRate)}%",
                            icon = Icons.Default.AutoAwesome,
                            iconTint = PrimaryPurple,
                            badgeText = "AVG RATE",
                            badgeColor = PrimaryPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Mobile Layout: 2 columns
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Today's Earnings",
                            value = "$${String.format(Locale.US, "%.2f", summary.todayEarnings)}",
                            icon = Icons.Default.CalendarToday,
                            iconTint = AccentGreen,
                            badgeText = "TODAY",
                            badgeColor = AccentGreen,
                            subtitle = "Real-time daily tracker",
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Weekly Earnings",
                            value = "$${String.format(Locale.US, "%.2f", summary.weeklyEarnings)}",
                            icon = Icons.Default.DateRange,
                            iconTint = PrimaryPurple,
                            badgeText = "7 DAYS",
                            badgeColor = PrimaryPurple,
                            subtitle = "Last 7 days total",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Monthly Earnings",
                            value = "$${String.format(Locale.US, "%,.2f", summary.monthlyEarnings)}",
                            icon = Icons.Default.MonetizationOn,
                            iconTint = Color(0xFF38BDF8),
                            badgeText = "30 DAYS",
                            badgeColor = Color(0xFF38BDF8),
                            subtitle = "Current run rate",
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Total Sales",
                            value = "${summary.totalSalesCount} units",
                            icon = Icons.Default.ShoppingBag,
                            iconTint = Color(0xFFF472B6),
                            subtitle = "$${String.format(Locale.US, "%,.2f", summary.totalSalesVolume)} gross",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Paid Out",
                            value = "$${String.format(Locale.US, "%.2f", summary.paidCommissions)}",
                            icon = Icons.Default.Paid,
                            iconTint = AccentGreen,
                            badgeText = "CLEARED",
                            badgeColor = AccentGreen,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Pending Audit",
                            value = "$${String.format(Locale.US, "%.2f", summary.pendingCommissions)}",
                            icon = Icons.Default.HourglassTop,
                            iconTint = AmberPending,
                            badgeText = "PENDING",
                            badgeColor = AmberPending,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Dual Pane Section on Desktop vs Stack on Mobile
        if (isWideScreen) {
            item(key = "desktop_dual_pane") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: AI Insights & Goals
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // AI Smart Insights Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            PrimaryPurple.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    PrimaryPurple.copy(alpha = 0.35f),
                                    RoundedCornerShape(18.dp)
                                )
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPurple.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "AFFILIATE AI INSIGHT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryPurple,
                                            letterSpacing = 0.5.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AccentGreen.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Smart Optimizer",
                                                color = AccentGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "TikTok Shop conversions are pacing 3.2x higher than Amazon bio clicks this week. Prioritizing short video hooks on your top 2 beauty products could yield +$320 in commission before Sunday.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // Target Tracker
                        val dailyGoal = goals.firstOrNull()
                        if (dailyGoal != null) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Target Tracker",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "View All Goals →",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PrimaryPurple,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { onNavigateTab(AppTab.GOALS_REPORTS) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                GoalProgressBar(goal = dailyGoal)
                            }
                        }
                    }

                    // Right Column: Recent Transactions
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "See All (${recentTransactions.size}) →",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryPurple,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateTab(AppTab.TRANSACTIONS) }
                            )
                        }

                        val previewItems = recentTransactions.take(5)
                        if (previewItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions logged yet.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            previewItems.forEach { tx ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onEditTransaction(tx) }
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = tx.productName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                PlatformBadge(platform = tx.platform, short = true)
                                                Text(
                                                    text = "${tx.quantity} sold @ $${String.format(Locale.US, "%.2f", tx.productPrice)}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "+$${String.format(Locale.US, "%.2f", tx.commissionEarned)}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = AccentGreen
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            StatusBadge(status = tx.status)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Mobile Stack
            item(key = "goal_preview") {
                val dailyGoal = goals.firstOrNull()
                if (dailyGoal != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View All Goals →",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryPurple,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateTab(AppTab.GOALS_REPORTS) }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        GoalProgressBar(goal = dailyGoal)
                    }
                }
            }

            item(key = "ai_insights") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    PrimaryPurple.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .border(
                            1.dp,
                            PrimaryPurple.copy(alpha = 0.35f),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryPurple.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "AFFILIATE AI INSIGHT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPurple,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Smart Optimizer",
                                        color = AccentGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "TikTok Shop conversions are pacing 3.2x higher than Amazon bio clicks this week. Prioritizing short video hooks on your top 2 beauty products could yield +$320 in commission before Sunday.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item(key = "section_recent") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "See All (${recentTransactions.size}) →",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateTab(AppTab.TRANSACTIONS) }
                    )
                }
            }

            val previewItems = recentTransactions.take(4)
            if (previewItems.isEmpty()) {
                item(key = "empty_preview") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions logged yet. Tap 'Add Sale' above!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(previewItems, key = { it.id }) { tx ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onEditTransaction(tx) }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.productName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PlatformBadge(platform = tx.platform, short = true)
                                    Text(
                                        text = "${tx.quantity} sold @ $${String.format(Locale.US, "%.2f", tx.productPrice)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+$${String.format(Locale.US, "%.2f", tx.commissionEarned)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = AccentGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                StatusBadge(status = tx.status)
                            }
                        }
                    }
                }
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
