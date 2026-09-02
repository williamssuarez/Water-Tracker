/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BeverageType
import com.example.data.model.UserSettings
import com.example.data.model.WaterLog
import com.example.data.repository.BeverageBreakdown
import com.example.data.repository.DailyHydrationStat
import com.example.data.repository.WaterRepository
import com.example.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class WaterTrackerUiState(
    val todayLogs: List<WaterLog> = emptyList(),
    val todayIntakeMl: Int = 0,
    val userSettings: UserSettings = UserSettings(),
    val streakDays: Int = 0,
    val weeklyStats: List<DailyHydrationStat> = emptyList(),
    val beverageBreakdowns: List<BeverageBreakdown> = emptyList(),
    val selectedBeverage: BeverageType = BeverageType.WATER,
    val showUndoSnackbar: Boolean = false,
    val lastDeletedLog: WaterLog? = null,
    val isLoading: Boolean = false
)

class WaterTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = WaterRepository(database.waterDao(), database.settingsDao())

    private val _selectedBeverage = MutableStateFlow(BeverageType.WATER)
    val selectedBeverage: StateFlow<BeverageType> = _selectedBeverage.asStateFlow()

    private val _lastDeletedLog = MutableStateFlow<WaterLog?>(null)
    val lastDeletedLog: StateFlow<WaterLog?> = _lastDeletedLog.asStateFlow()

    val uiState: StateFlow<WaterTrackerUiState> = combine(
        repository.getTodayLogsFlow(),
        repository.getTodayHydrationSumFlow(),
        repository.settingsFlow,
        repository.allLogsFlow,
        _selectedBeverage
    ) { todayLogs, todaySum, settings, allLogs, beverage ->
        val weeklyStats = calculateWeeklyStats(allLogs, settings.dailyGoalMl)
        val streak = calculateStreak(allLogs, settings.dailyGoalMl, todaySum)
        val breakdown = calculateBeverageBreakdown(allLogs)

        WaterTrackerUiState(
            todayLogs = todayLogs,
            todayIntakeMl = todaySum,
            userSettings = settings,
            streakDays = streak,
            weeklyStats = weeklyStats,
            beverageBreakdowns = breakdown,
            selectedBeverage = beverage,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WaterTrackerUiState(isLoading = true)
    )

    init {
        // Ensure reminder is scheduled on app open if enabled
        ReminderScheduler.scheduleNextReminder(getApplication())
    }

    fun setSelectedBeverage(beverage: BeverageType) {
        _selectedBeverage.value = beverage
    }

    fun logWater(amountMl: Int, beverageType: BeverageType = _selectedBeverage.value, note: String = "") {
        viewModelScope.launch {
            repository.logWater(amountMl, beverageType, note)
            // Refresh reminder schedule
            ReminderScheduler.scheduleNextReminder(getApplication())
        }
    }

    fun deleteLog(log: WaterLog) {
        viewModelScope.launch {
            _lastDeletedLog.value = log
            repository.deleteLog(log)
            ReminderScheduler.scheduleNextReminder(getApplication())
        }
    }

    fun undoDelete() {
        val deleted = _lastDeletedLog.value ?: return
        viewModelScope.launch {
            repository.logWater(deleted.amountMl, deleted.beverageType, deleted.note)
            _lastDeletedLog.value = null
        }
    }

    fun updateDailyGoal(newGoalMl: Int) {
        viewModelScope.launch {
            val current = repository.getSettingsSync()
            repository.updateSettings(current.copy(dailyGoalMl = newGoalMl))
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            if (settings.notificationsEnabled) {
                ReminderScheduler.scheduleNextReminder(getApplication())
            } else {
                ReminderScheduler.cancelReminders(getApplication())
            }
        }
    }

    fun triggerTestNotification() {
        ReminderScheduler.triggerTestNotification(getApplication())
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    private fun calculateWeeklyStats(allLogs: List<WaterLog>, goalMl: Int): List<DailyHydrationStat> {
        val stats = mutableListOf<DailyHydrationStat>()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

        for (i in 6 downTo 0) {
            val (startOfDay, endOfDay) = WaterRepository.getDayTimeBounds(i)
            val dayLogs = allLogs.filter { it.timestamp in startOfDay..endOfDay }
            val totalIntake = dayLogs.sumOf { it.effectiveHydrationMl }

            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dayOfWeek = if (i == 0) "Today" else dayFormat.format(cal.time)
            val dateLabel = dateFormat.format(cal.time)

            stats.add(
                DailyHydrationStat(
                    dateLabel = dateLabel,
                    dayOfWeek = dayOfWeek,
                    timestamp = cal.timeInMillis,
                    totalHydrationMl = totalIntake,
                    targetGoalMl = goalMl,
                    isGoalReached = totalIntake >= goalMl
                )
            )
        }
        return stats
    }

    private fun calculateStreak(allLogs: List<WaterLog>, goalMl: Int, todaySum: Int): Int {
        var streak = if (todaySum >= goalMl) 1 else 0
        var dayOffset = 1

        while (dayOffset < 30) {
            val (startOfDay, endOfDay) = WaterRepository.getDayTimeBounds(dayOffset)
            val dayLogs = allLogs.filter { it.timestamp in startOfDay..endOfDay }
            val sum = dayLogs.sumOf { it.effectiveHydrationMl }
            if (sum >= goalMl) {
                streak++
                dayOffset++
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateBeverageBreakdown(allLogs: List<WaterLog>): List<BeverageBreakdown> {
        if (allLogs.isEmpty()) return emptyList()
        val totalVolume = allLogs.sumOf { it.amountMl }.coerceAtLeast(1)

        return allLogs.groupBy { it.beverageType }
            .map { (type, logs) ->
                val typeSum = logs.sumOf { it.amountMl }
                BeverageBreakdown(
                    beverageType = type,
                    totalAmountMl = typeSum,
                    percentage = (typeSum.toFloat() / totalVolume.toFloat()) * 100f
                )
            }
            .sortedByDescending { it.totalAmountMl }
    }
}
