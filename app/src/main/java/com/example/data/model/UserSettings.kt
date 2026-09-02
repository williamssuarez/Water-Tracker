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

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val dailyGoalMl: Int = 2500,
    val notificationsEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 90, // Every 1.5 hours
    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 22,
    val endMinute: Int = 0,
    val smartRemindersEnabled: Boolean = true, // Only notify if intake is behind expected daily pace
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val language: String = "en"
)
