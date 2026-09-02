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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.repository.WaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == ReminderScheduler.ACTION_REMINDER
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val settings = db.settingsDao().getSettings()

                    if (settings != null && settings.notificationsEnabled) {
                        val (startOfDay, endOfDay) = WaterRepository.getTodayTimeBounds()
                        val currentIntake = db.waterDao().getHydrationSumBetweenSync(startOfDay, endOfDay)
                        val goal = settings.dailyGoalMl

                        // If it's a scheduled reminder trigger
                        if (action == ReminderScheduler.ACTION_REMINDER) {
                            val shouldNotify = if (settings.smartRemindersEnabled) {
                                // Smart mode: notify if intake hasn't reached goal
                                currentIntake < goal
                            } else {
                                true
                            }

                            if (shouldNotify) {
                                NotificationHelper.showHydrationReminder(
                                    context = context,
                                    todayIntakeMl = currentIntake,
                                    dailyGoalMl = goal,
                                    isTest = false,
                                    language = settings.language
                                )
                            }
                        }

                        // Schedule the next reminder interval
                        ReminderScheduler.scheduleNextReminder(context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
