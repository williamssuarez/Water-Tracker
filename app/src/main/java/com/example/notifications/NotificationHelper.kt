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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.ui.i18n.EnglishStrings
import com.example.ui.i18n.SpanishStrings

object NotificationHelper {
    const val CHANNEL_ID = "hydration_reminders_channel"
    const val NOTIFICATION_ID = 1001
    const val ACTION_LOG_250 = "com.example.watertracker.ACTION_LOG_250"
    const val ACTION_LOG_500 = "com.example.watertracker.ACTION_LOG_500"
    const val ACTION_SNOOZE_30 = "com.example.watertracker.ACTION_SNOOZE_30"

    fun createNotificationChannel(context: Context, language: String = "en") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val strings = if (language.equals("es", ignoreCase = true)) SpanishStrings else EnglishStrings
            val name = strings.notificationChannelName
            val descriptionText = strings.notificationChannelDesc
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showHydrationReminder(
        context: Context,
        todayIntakeMl: Int,
        dailyGoalMl: Int,
        isTest: Boolean = false,
        language: String = "en"
    ) {
        createNotificationChannel(context, language)
        val strings = if (language.equals("es", ignoreCase = true)) SpanishStrings else EnglishStrings

        // Main Tap Intent -> Opens App
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action 1: Drink 250ml
        val log250Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_LOG_250
        }
        val log250PendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            log250Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action 2: Drink 500ml
        val log500Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_LOG_500
        }
        val log500PendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            log500Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val percentage = if (dailyGoalMl > 0) ((todayIntakeMl.toFloat() / dailyGoalMl) * 100).toInt() else 0
        val remainingMl = maxOf(0, dailyGoalMl - todayIntakeMl)

        val title = when {
            isTest -> strings.notifTestTitle
            percentage >= 100 -> strings.notifGoalReachedTitle
            percentage >= 75 -> strings.notifAlmostThereTitle
            percentage >= 50 -> strings.notifHalfwayTitle
            else -> strings.notifTimeForBreakTitle
        }

        val message = strings.notifMessage(todayIntakeMl, dailyGoalMl, percentage, remainingMl, isTest)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n${strings.notifTapToLogTip}")
            )
            .setColor(0xFF0077B6.toInt())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_input_add,
                strings.notifAction250,
                log250PendingIntent
            )
            .addAction(
                android.R.drawable.ic_input_add,
                strings.notifAction500,
                log500PendingIntent
            )
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permission might be denied on Android 13+
            e.printStackTrace()
        }
    }

    fun showLoggedConfirmation(
        context: Context,
        amountAdded: Int,
        newTotalMl: Int,
        dailyGoalMl: Int,
        language: String = "en"
    ) {
        val strings = if (language.equals("es", ignoreCase = true)) SpanishStrings else EnglishStrings
        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val percentage = if (dailyGoalMl > 0) ((newTotalMl.toFloat() / dailyGoalMl) * 100).toInt() else 0

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(strings.notifConfirmTitle(amountAdded))
            .setContentText(strings.notifConfirmMessage(newTotalMl, dailyGoalMl, percentage))
            .setColor(0xFF2EC4B6.toInt())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setTimeoutAfter(6000)
            .setContentIntent(contentPendingIntent)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
