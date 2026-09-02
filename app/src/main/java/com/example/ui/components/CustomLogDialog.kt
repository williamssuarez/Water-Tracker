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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeverageType
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SkyBlue

@Composable
fun CustomLogDialog(
    initialBeverage: BeverageType,
    onLogConfirmed: (amountMl: Int, beverage: BeverageType, note: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val strings = LocalAppStrings.current
    var amountMl by remember { mutableFloatStateOf(300f) }
    var selectedBeverage by remember { mutableStateOf(initialBeverage) }
    var noteText by remember { mutableStateOf("") }
    var showBeveragePicker by remember { mutableStateOf(false) }

    if (showBeveragePicker) {
        BeverageSelectorDialog(
            selectedBeverage = selectedBeverage,
            onBeverageSelected = { selectedBeverage = it },
            onDismissRequest = { showBeveragePicker = false }
        )
    }

    val localizedBeverageName = selectedBeverage.getLocalizedName(strings)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = strings.logCustomDrinkTitle,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Beverage selector button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showBeveragePicker = true }
                        .testTag("dialog_select_beverage"),
                    shape = RoundedCornerShape(16.dp),
                    color = selectedBeverage.getColor().copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, selectedBeverage.getColor().copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(selectedBeverage.getColor().copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedBeverage.getIcon(),
                                contentDescription = localizedBeverageName,
                                tint = selectedBeverage.getColor(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localizedBeverageName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = String.format(strings.effectiveHydrationLabel, (selectedBeverage.hydrationFactor * 100).toInt()),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Text(
                            text = strings.changeBeverageButton,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = selectedBeverage.getColor(),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stepper + Big Volume Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { amountMl = maxOf(50f, amountMl - 50f) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("stepper_decrease_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease 50ml"
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${amountMl.toInt()} ml",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = OceanBlue
                            )
                        )
                        val effectiveMl = (amountMl * selectedBeverage.hydrationFactor).toInt()
                        if (effectiveMl != amountMl.toInt()) {
                            Text(
                                text = strings.approxEffectiveLabel(effectiveMl),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { amountMl = minOf(1500f, amountMl + 50f) },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("stepper_increase_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase 50ml"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slider
                Slider(
                    value = amountMl,
                    onValueChange = { amountMl = (it / 25).toInt() * 25f },
                    valueRange = 50f..1500f,
                    steps = 57,
                    colors = SliderDefaults.colors(
                        thumbColor = OceanBlue,
                        activeTrackColor = OceanBlue,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_amount_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(strings.noteOptionalLabel) },
                    placeholder = { Text(strings.notePlaceholder) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_log_note_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLogConfirmed(amountMl.toInt(), selectedBeverage, noteText.trim())
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_custom_log_button")
            ) {
                Text(strings.logAmountButton(amountMl.toInt()))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("cancel_custom_log_button")
            ) {
                Text(strings.cancelButton)
            }
        }
    )
}
