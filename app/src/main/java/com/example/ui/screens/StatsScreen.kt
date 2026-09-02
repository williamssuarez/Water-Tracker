/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.data.repository.DailyHydrationStat
import com.example.ui.WaterTrackerUiState
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AquaCyan
import com.example.ui.theme.AquaLight
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.OceanBlueDark
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.StreakFire
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun StatsScreen(
    uiState: WaterTrackerUiState,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val weeklyAverage = if (uiState.weeklyStats.isNotEmpty()) {
        uiState.weeklyStats.map { it.totalHydrationMl }.average().toInt()
    } else 0

    val completedDaysCount = uiState.weeklyStats.count { it.isGoalReached }
    val completionRate = if (uiState.weeklyStats.isNotEmpty()) {
        ((completedDaysCount.toFloat() / uiState.weeklyStats.size.toFloat()) * 100).toInt()
    } else 0

    val totalWeeklyMl = uiState.weeklyStats.sumOf { it.totalHydrationMl }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column {
                Text(
                    text = strings.statsTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = strings.statsSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // 7-Day Bar Chart Card
        item {
            WeeklyBarChartCard(
                weeklyStats = uiState.weeklyStats,
                dailyGoalMl = uiState.userSettings.dailyGoalMl
            )
        }

        // Key Summary Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = strings.metricDailyAverage,
                        value = "$weeklyAverage ml",
                        subtitle = String.format(strings.targetPrefix, uiState.userSettings.dailyGoalMl),
                        icon = Icons.Default.WaterDrop,
                        iconTint = OceanBlue,
                        modifier = Modifier.weight(1f)
                    )

                    MetricStatCard(
                        title = strings.metricGoalRate,
                        value = "$completionRate%",
                        subtitle = String.format(strings.daysMetFormat, completedDaysCount),
                        icon = Icons.Default.CheckCircle,
                        iconTint = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = strings.metricStreak,
                        value = strings.streakDaysFormat(uiState.streakDays),
                        subtitle = if (uiState.streakDays > 0) strings.streakKeepGoing else strings.streakStartToday,
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = StreakFire,
                        modifier = Modifier.weight(1f)
                    )

                    MetricStatCard(
                        title = strings.metricWeeklyTotal,
                        value = String.format("%.1f L", totalWeeklyMl / 1000f),
                        subtitle = strings.totalWeeklyFormat(totalWeeklyMl),
                        icon = Icons.Default.ShowChart,
                        iconTint = SkyBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Beverage Distribution Breakdown
        item {
            BeverageDistributionCard(breakdowns = uiState.beverageBreakdowns)
        }

        // Hydration Tips & Health Wisdom
        item {
            HydrationTipsCard()
        }
    }
}

@Composable
private fun WeeklyBarChartCard(
    weeklyStats: List<DailyHydrationStat>,
    dailyGoalMl: Int
) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weekly_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.weeklyOverviewTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = String.format(strings.targetPrefix, dailyGoalMl),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = OceanBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bars Row
            val maxIntake = maxOf(dailyGoalMl * 1.2f, (weeklyStats.maxOfOrNull { it.totalHydrationMl } ?: 1).toFloat())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyStats.forEach { stat ->
                    val barHeightFraction = (stat.totalHydrationMl.toFloat() / maxIntake).coerceIn(0.06f, 1.0f)
                    val isToday = stat.dayOfWeek == "Today"

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Amount Label
                        if (stat.totalHydrationMl > 0) {
                            Text(
                                text = "${stat.totalHydrationMl}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = if (stat.isGoalReached) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Bar Capsule
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(barHeightFraction)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                .background(
                                    if (stat.isGoalReached) {
                                        Brush.verticalGradient(listOf(SuccessGreen, OceanBlue))
                                    } else if (isToday) {
                                        Brush.verticalGradient(listOf(AquaCyan, OceanBlue))
                                    } else {
                                        Brush.verticalGradient(listOf(AquaLight.copy(alpha = 0.6f), OceanBlue.copy(alpha = 0.7f)))
                                    }
                                ),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (stat.isGoalReached) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 3.dp)
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Day of Week Label
                        Text(
                            text = strings.dayLabel(stat.dayOfWeek),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isToday) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun BeverageDistributionCard(breakdowns: List<com.example.data.repository.BeverageBreakdown>) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("beverage_distribution_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = strings.beverageDistributionTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = strings.beverageDistributionSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (breakdowns.isEmpty()) {
                Text(
                    text = strings.noBeveragesLogged,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    breakdowns.forEach { item ->
                        val localizedBeverageName = item.beverageType.getLocalizedName(strings)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = item.beverageType.getIcon(),
                                        contentDescription = localizedBeverageName,
                                        tint = item.beverageType.getColor(),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = localizedBeverageName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                                Text(
                                    text = "${item.totalAmountMl} ml (${item.percentage.toInt()}%)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { item.percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = item.beverageType.getColor(),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HydrationTipsCard() {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OceanBlue.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = strings.tipTitle,
                    tint = OceanBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = strings.tipTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = strings.tipBody,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
