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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getLogsBetween(startTime: Long, endTime: Long): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getLogsBetweenSync(startTime: Long, endTime: Long): List<WaterLog>

    @Query("SELECT COALESCE(SUM(effectiveHydrationMl), 0) FROM water_logs WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getHydrationSumBetween(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(effectiveHydrationMl), 0) FROM water_logs WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getHydrationSumBetweenSync(startTime: Long, endTime: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WaterLog): Long

    @Update
    suspend fun updateLog(log: WaterLog)

    @Delete
    suspend fun deleteLog(log: WaterLog)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLog(): WaterLog?

    @Query("DELETE FROM water_logs")
    suspend fun clearAll()
}
