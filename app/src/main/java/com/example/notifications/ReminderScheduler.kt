/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"
    private const val REMINDER_REQUEST_CODE = 2001
    const val ACTION_REMINDER = "com.example.watertracker.ACTION_REMINDER"

    fun scheduleNextReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val settings = db.settingsDao().getSettings()

            if (settings == null || !settings.notificationsEnabled) {
                cancelReminders(context)
                return@launch
            }

            val triggerTime = computeNextTriggerTime(
                intervalMinutes = settings.reminderIntervalMinutes,
                startHour = settings.startHour,
                startMinute = settings.startMinute,
                endHour = settings.endHour,
                endMinute = settings.endMinute
            )

            val intent = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_REMINDER
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Next water reminder scheduled for: ${java.util.Date(triggerTime)}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule reminder alarm", e)
            }
        }
    }

    fun cancelReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun triggerTestNotification(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val settings = db.settingsDao().getSettings()
            val (startOfDay, endOfDay) = com.example.data.repository.WaterRepository.getTodayTimeBounds()
            val currentIntake = db.waterDao().getHydrationSumBetweenSync(startOfDay, endOfDay)
            val goal = settings?.dailyGoalMl ?: 2500

            NotificationHelper.showHydrationReminder(
                context = context,
                todayIntakeMl = currentIntake,
                dailyGoalMl = goal,
                isTest = true,
                language = settings?.language ?: "en"
            )
        }
    }

    private fun computeNextTriggerTime(
        intervalMinutes: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ): Long {
        val now = Calendar.getInstance()
        val currentMillis = now.timeInMillis

        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If currently before active window start today
        if (now.before(startCal)) {
            return startCal.timeInMillis
        }

        // If currently after active window end today -> schedule for tomorrow start
        if (now.after(endCal)) {
            startCal.add(Calendar.DAY_OF_YEAR, 1)
            return startCal.timeInMillis
        }

        // Currently in active window -> now + intervalMinutes
        val nextTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, intervalMinutes)
        }

        // If next time exceeds end window, wrap to next morning
        if (nextTime.after(endCal)) {
            startCal.add(Calendar.DAY_OF_YEAR, 1)
            return startCal.timeInMillis
        }

        return nextTime.timeInMillis
    }
}
