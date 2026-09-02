/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BeverageType
import com.example.data.model.UserSettings
import com.example.data.model.WaterLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromBeverageType(value: BeverageType): String {
        return value.name
    }

    @TypeConverter
    fun toBeverageType(value: String): BeverageType {
        return try {
            BeverageType.valueOf(value)
        } catch (e: Exception) {
            BeverageType.WATER
        }
    }
}

@Database(
    entities = [WaterLog::class, UserSettings::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "water_tracker_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initialize default user settings and sample starter data
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                database.settingsDao().insertOrUpdate(UserSettings())
                                
                                // Provide pleasant initial sample entries for today so user gets instant hydration feedback
                                val now = System.currentTimeMillis()
                                val oneHourAgo = now - 3600_000L * 2
                                val threeHoursAgo = now - 3600_000L * 4
                                
                                database.waterDao().insertLog(
                                    WaterLog(
                                        timestamp = threeHoursAgo,
                                        amountMl = 350,
                                        beverageType = BeverageType.WATER,
                                        note = "Morning glass"
                                    )
                                )
                                database.waterDao().insertLog(
                                    WaterLog(
                                        timestamp = oneHourAgo,
                                        amountMl = 250,
                                        beverageType = BeverageType.LEMON_WATER,
                                        note = "Midday refresh"
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
