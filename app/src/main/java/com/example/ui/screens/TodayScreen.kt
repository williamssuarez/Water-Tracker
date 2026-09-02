package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeverageType
import com.example.data.model.WaterLog
import com.example.ui.WaterTrackerUiState
import com.example.ui.components.BeverageSelectorDialog
import com.example.ui.components.CustomLogDialog
import com.example.ui.components.FluidWaveProgress
import com.example.ui.components.QuickLogSection
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AquaLight
import com.example.ui.theme.OceanBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    uiState: WaterTrackerUiState,
    onQuickLog: (Int) -> Unit,
    onLogCustom: (Int, BeverageType, String) -> Unit,
    onDeleteLog: (WaterLog) -> Unit,
    onSelectBeverage: (BeverageType) -> Unit,
    onNavigateToReminders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showBeverageSelector by remember { mutableStateOf(false) }
    var showCustomLogDialog by remember { mutableStateOf(false) }

    val locale = remember(strings.languageCode) { Locale(strings.languageCode) }
    val todayDateString = remember(strings.languageCode) {
        SimpleDateFormat("EEEE, MMMM d", locale).format(Date()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
        }
    }

    if (showBeverageSelector) {
        BeverageSelectorDialog(
            selectedBeverage = uiState.selectedBeverage,
            onBeverageSelected = { onSelectBeverage(it) },
            onDismissRequest = { showBeverageSelector = false }
        )
    }

    if (showCustomLogDialog) {
        CustomLogDialog(
            initialBeverage = uiState.selectedBeverage,
            onLogConfirmed = { amount, beverage, note ->
                onLogCustom(amount, beverage, note)
            },
            onDismissRequest = { showCustomLogDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar Greeting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.todayTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = todayDateString,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Quick reminder status button
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onNavigateToReminders)
                            .testTag("header_notifications_button"),
                        shape = CircleShape,
                        color = if (uiState.userSettings.notificationsEnabled) OceanBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = strings.remindersTooltip,
                                tint = if (uiState.userSettings.notificationsEnabled) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Hero Liquid Wave Progress
            item {
                FluidWaveProgress(
                    currentIntakeMl = uiState.todayIntakeMl,
                    dailyGoalMl = uiState.userSettings.dailyGoalMl,
                    streakDays = uiState.streakDays
                )
            }

            // Quick Log Section
            item {
                QuickLogSection(
                    currentBeverage = uiState.selectedBeverage,
                    onOpenBeverageSelector = { showBeverageSelector = true },
                    onQuickLog = onQuickLog,
                    onOpenCustomLog = { showCustomLogDialog = true }
                )
            }

            // Today's Timeline Log Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(strings.todaysLogTitle, uiState.todayLogs.size),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    if (uiState.todayLogs.isNotEmpty()) {
                        Text(
                            text = String.format(strings.totalIntakePrefix, uiState.todayIntakeMl),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = OceanBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Empty State
            if (uiState.todayLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("empty_today_logs_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(OceanBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = strings.emptyLogTitle,
                                    tint = OceanBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.emptyLogTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.emptyLogSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(uiState.todayLogs, key = { it.id }) { log ->
                    LogItemCard(
                        log = log,
                        onDelete = { onDeleteLog(log) }
                    )
                }
            }
        }

        // Floating Action Button for Custom Drink
        FloatingActionButton(
            onClick = { showCustomLogDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_custom_log_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = strings.logCustomActionFab
            )
        }
    }
}

@Composable
private fun LogItemCard(
    log: WaterLog,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val locale = remember(strings.languageCode) { Locale(strings.languageCode) }
    val timeFormat = remember(locale) { SimpleDateFormat("h:mm a", locale) }
    val formattedTime = remember(log.timestamp, locale) { timeFormat.format(Date(log.timestamp)) }
    val beverageLocalizedName = log.beverageType.getLocalizedName(strings)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_item_${log.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(log.beverageType.getColor().copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = log.beverageType.getIcon(),
                        contentDescription = beverageLocalizedName,
                        tint = log.beverageType.getColor(),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+${log.amountMl} ml",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = beverageLocalizedName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = log.beverageType.getColor(),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        if (log.note.isNotEmpty()) {
                            Text(
                                text = " • ${log.note}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        if (log.beverageType.hydrationFactor != 1.0f) {
                            Text(
                                text = " (${log.effectiveHydrationMl}ml)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_log_${log.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete entry",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
