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

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val amountMl: Int,
    val beverageType: BeverageType = BeverageType.WATER,
    val effectiveHydrationMl: Int = (amountMl * beverageType.hydrationFactor).toInt(),
    val note: String = ""
)
