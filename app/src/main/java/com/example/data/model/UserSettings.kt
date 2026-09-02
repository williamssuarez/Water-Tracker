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
