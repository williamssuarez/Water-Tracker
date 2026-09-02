/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.BeverageCoffeeColor
import com.example.ui.theme.BeverageElectrolyteColor
import com.example.ui.theme.BeverageJuiceColor
import com.example.ui.theme.BeverageLemonColor
import com.example.ui.theme.BeverageSparklingColor
import com.example.ui.theme.BeverageTeaColor
import com.example.ui.theme.BeverageWaterColor

enum class BeverageType(
    val displayName: String,
    val hydrationFactor: Float, // Hydration efficiency multiplier
    val description: String
) {
    WATER("Pure Water", 1.0f, "100% hydration"),
    LEMON_WATER("Lemon Water", 1.0f, "Refreshing citrus hydration"),
    HERBAL_TEA("Herbal Tea", 1.0f, "Caffeine-free soothing infusion"),
    ELECTROLYTE("Electrolytes", 1.1f, "Enhanced rehydration"),
    TEA("Green/Black Tea", 0.95f, "Antioxidant rich"),
    COFFEE("Coffee", 0.85f, "Mild diuretic effect"),
    JUICE("Fresh Juice", 0.85f, "Vitamins & natural sugars"),
    SPARKLING("Sparkling Water", 1.0f, "Crisp effervescence");

    fun getLocalizedName(strings: com.example.ui.i18n.AppStrings): String = strings.beverageName(this)
    fun getLocalizedDescription(strings: com.example.ui.i18n.AppStrings): String = strings.beverageDescription(this)

    fun getIcon(): ImageVector {
        return when (this) {
            WATER -> Icons.Default.WaterDrop
            LEMON_WATER -> Icons.Default.WbSunny
            HERBAL_TEA -> Icons.Default.Spa
            ELECTROLYTE -> Icons.Default.ElectricBolt
            TEA -> Icons.Default.Nightlight
            COFFEE -> Icons.Default.LocalCafe
            JUICE -> Icons.Default.LocalDrink
            SPARKLING -> Icons.Default.Opacity
        }
    }

    fun getColor(): Color {
        return when (this) {
            WATER -> BeverageWaterColor
            LEMON_WATER -> BeverageLemonColor
            HERBAL_TEA -> BeverageTeaColor
            ELECTROLYTE -> BeverageElectrolyteColor
            TEA -> BeverageTeaColor
            COFFEE -> BeverageCoffeeColor
            JUICE -> BeverageJuiceColor
            SPARKLING -> BeverageSparklingColor
        }
    }
}
