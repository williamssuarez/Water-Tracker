/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.OceanBlue

enum class ActivityLevel(val multiplier: Float) {
    SEDENTARY(1.0f),
    MODERATE(1.2f),
    INTENSE(1.4f)
}

enum class Climate(val bonusMl: Int) {
    MILD(0),
    WARM(250),
    HOT(500)
}

@Composable
fun GoalCalculatorDialog(
    currentGoalMl: Int,
    onGoalSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val strings = LocalAppStrings.current
    var weightKg by remember { mutableFloatStateOf(70f) }
    var activity by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var climate by remember { mutableStateOf(Climate.MILD) }

    // Standard formula: 35ml per kg of bodyweight * activity multiplier + climate bonus
    val calculatedGoal = ((weightKg * 35f * activity.multiplier).toInt() + climate.bonusMl)
        .let { (it / 50) * 50 } // round to nearest 50ml

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = strings.calculatorTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strings.calculatorSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weight Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.bodyWeightLabel, style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${weightKg.toInt()} kg (${(weightKg * 2.20462).toInt()} lbs)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OceanBlue)
                    )
                }
                Slider(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    valueRange = 40f..140f,
                    colors = SliderDefaults.colors(thumbColor = OceanBlue, activeTrackColor = OceanBlue),
                    modifier = Modifier.testTag("calculator_weight_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Activity Level
                Text(strings.activityLevelLabel, style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActivityLevel.values().forEach { level ->
                        val label = when (level) {
                            ActivityLevel.SEDENTARY -> strings.activityLow
                            ActivityLevel.MODERATE -> strings.activityModerate
                            ActivityLevel.INTENSE -> strings.activityHigh
                        }
                        FilterChip(
                            selected = activity == level,
                            onClick = { activity = level },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Climate
                Text(strings.climateLabel, style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Climate.values().forEach { c ->
                        val label = when (c) {
                            Climate.MILD -> strings.climateMild
                            Climate.WARM -> strings.climateWarm
                            Climate.HOT -> strings.climateHot
                        }
                        FilterChip(
                            selected = climate == c,
                            onClick = { climate = c },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OceanBlue.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.recommendedTargetLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$calculatedGoal ml / day",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = OceanBlue
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGoalSelected(calculatedGoal)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("apply_calculated_goal_button")
            ) {
                Text(strings.applyGoalButton(calculatedGoal))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(strings.cancelButton)
            }
        }
    )
}
