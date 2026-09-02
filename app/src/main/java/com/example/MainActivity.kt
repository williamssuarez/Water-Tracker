/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.NotificationHelper
import com.example.ui.WaterTrackerViewModel
import com.example.ui.i18n.AppStrings
import com.example.ui.i18n.EnglishStrings
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.SpanishStrings
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OceanBlue
import kotlinx.coroutines.launch

enum class ScreenTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TODAY(Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop, "tab_today"),
    STATS(Icons.Filled.BarChart, Icons.Outlined.BarChart, "tab_stats"),
    REMINDERS(Icons.Filled.Notifications, Icons.Outlined.Notifications, "tab_reminders");

    fun getTitle(strings: AppStrings): String = when (this) {
        TODAY -> strings.tabToday
        STATS -> strings.tabStats
        REMINDERS -> strings.tabReminders
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: WaterTrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                WaterTrackerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WaterTrackerApp(viewModel: WaterTrackerViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(ScreenTab.TODAY) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val appStrings = if (uiState.userSettings.language.equals("es", ignoreCase = true)) {
        SpanishStrings
    } else {
        EnglishStrings
    }

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    ScreenTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        val tabTitle = tab.getTitle(appStrings)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tabTitle
                                )
                            },
                            label = {
                                Text(
                                    text = tabTitle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        ScreenTab.TODAY -> {
                            TodayScreen(
                                uiState = uiState,
                                onQuickLog = { amount ->
                                    viewModel.logWater(amount)
                                    coroutineScope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        val bevName = uiState.selectedBeverage.getLocalizedName(appStrings)
                                        snackbarHostState.showSnackbar(
                                            message = String.format(appStrings.addedSnackbar, amount, bevName),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onLogCustom = { amount, beverage, note ->
                                    viewModel.logWater(amount, beverage, note)
                                    coroutineScope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        val bevName = beverage.getLocalizedName(appStrings)
                                        snackbarHostState.showSnackbar(
                                            message = String.format(appStrings.addedSnackbar, amount, bevName),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onDeleteLog = { log ->
                                    viewModel.deleteLog(log)
                                    coroutineScope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        val result = snackbarHostState.showSnackbar(
                                            message = String.format(appStrings.removedSnackbar, log.amountMl),
                                            actionLabel = appStrings.undoLabel,
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.undoDelete()
                                        }
                                    }
                                },
                                onSelectBeverage = { beverage ->
                                    viewModel.setSelectedBeverage(beverage)
                                },
                                onNavigateToReminders = {
                                    currentTab = ScreenTab.REMINDERS
                                }
                            )
                        }

                        ScreenTab.STATS -> {
                            StatsScreen(uiState = uiState)
                        }

                        ScreenTab.REMINDERS -> {
                            RemindersScreen(
                                userSettings = uiState.userSettings,
                                onUpdateSettings = { newSettings ->
                                    viewModel.updateSettings(newSettings)
                                },
                                onTriggerTestNotification = {
                                    viewModel.triggerTestNotification()
                                },
                                onClearAllData = {
                                    viewModel.clearAllLogs()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(appStrings.allDataResetSnackbar)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
