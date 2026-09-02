package com.example.data.repository

import com.example.data.local.SettingsDao
import com.example.data.local.WaterDao
import com.example.data.model.BeverageType
import com.example.data.model.UserSettings
import com.example.data.model.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

data class DailyHydrationStat(
    val dateLabel: String,
    val dayOfWeek: String,
    val timestamp: Long,
    val totalHydrationMl: Int,
    val targetGoalMl: Int,
    val isGoalReached: Boolean
)

data class BeverageBreakdown(
    val beverageType: BeverageType,
    val totalAmountMl: Int,
    val percentage: Float
)

class WaterRepository(
    private val waterDao: WaterDao,
    private val settingsDao: SettingsDao
) {
    val allLogsFlow: Flow<List<WaterLog>> = waterDao.getAllLogs()

    val settingsFlow: Flow<UserSettings> = settingsDao.getSettingsFlow().map { it ?: UserSettings() }

    fun getTodayLogsFlow(): Flow<List<WaterLog>> {
        val (startOfDay, endOfDay) = getTodayTimeBounds()
        return waterDao.getLogsBetween(startOfDay, endOfDay)
    }

    fun getTodayHydrationSumFlow(): Flow<Int> {
        val (startOfDay, endOfDay) = getTodayTimeBounds()
        return waterDao.getHydrationSumBetween(startOfDay, endOfDay)
    }

    suspend fun getTodayHydrationSumSync(): Int {
        val (startOfDay, endOfDay) = getTodayTimeBounds()
        return waterDao.getHydrationSumBetweenSync(startOfDay, endOfDay)
    }

    suspend fun getSettingsSync(): UserSettings {
        return settingsDao.getSettings() ?: UserSettings().also {
            settingsDao.insertOrUpdate(it)
        }
    }

    suspend fun logWater(
        amountMl: Int,
        beverageType: BeverageType = BeverageType.WATER,
        note: String = ""
    ): Long {
        val log = WaterLog(
            timestamp = System.currentTimeMillis(),
            amountMl = amountMl,
            beverageType = beverageType,
            effectiveHydrationMl = (amountMl * beverageType.hydrationFactor).toInt(),
            note = note
        )
        return waterDao.insertLog(log)
    }

    suspend fun deleteLogById(id: Long) {
        waterDao.deleteLogById(id)
    }

    suspend fun deleteLog(log: WaterLog) {
        waterDao.deleteLog(log)
    }

    suspend fun updateSettings(settings: UserSettings) {
        settingsDao.insertOrUpdate(settings)
    }

    suspend fun clearAllData() {
        waterDao.clearAll()
    }

    companion object {
        fun getTodayTimeBounds(): Pair<Long, Long> {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            return Pair(startOfDay, endOfDay)
        }

        fun getDayTimeBounds(daysAgo: Int): Pair<Long, Long> {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            return Pair(startOfDay, endOfDay)
        }
    }
}
