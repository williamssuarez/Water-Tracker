package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val amountMl: Int,
    val beverageType: BeverageType = BeverageType.WATER,
    val effectiveHydrationMl: Int = (amountMl * beverageType.hydrationFactor).toInt(),
    val note: String = ""
)
