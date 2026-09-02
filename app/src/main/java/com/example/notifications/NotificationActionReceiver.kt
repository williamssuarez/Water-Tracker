package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.model.BeverageType
import com.example.data.model.WaterLog
import com.example.data.repository.WaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val amountToAdd = when (action) {
                    NotificationHelper.ACTION_LOG_250 -> 250
                    NotificationHelper.ACTION_LOG_500 -> 500
                    else -> 0
                }

                if (amountToAdd > 0) {
                    val log = WaterLog(
                        timestamp = System.currentTimeMillis(),
                        amountMl = amountToAdd,
                        beverageType = BeverageType.WATER,
                        effectiveHydrationMl = amountToAdd,
                        note = "Quick notification log"
                    )
                    db.waterDao().insertLog(log)

                    val (startOfDay, endOfDay) = WaterRepository.getTodayTimeBounds()
                    val newTotal = db.waterDao().getHydrationSumBetweenSync(startOfDay, endOfDay)
                    val settings = db.settingsDao().getSettings()
                    val goal = settings?.dailyGoalMl ?: 2500

                    val language = settings?.language ?: "en"

                    NotificationHelper.showLoggedConfirmation(
                        context = context,
                        amountAdded = amountToAdd,
                        newTotalMl = newTotal,
                        dailyGoalMl = goal,
                        language = language
                    )

                    // Reschedule next reminder interval
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
