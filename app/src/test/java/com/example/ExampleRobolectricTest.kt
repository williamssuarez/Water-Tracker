package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BeverageType
import com.example.data.model.WaterLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Water Tracker", appName)
  }

  @Test
  fun `water log calculates effective hydration correctly`() {
    val waterLog = WaterLog(amountMl = 500, beverageType = BeverageType.WATER)
    assertEquals(500, waterLog.effectiveHydrationMl)

    val coffeeLog = WaterLog(amountMl = 200, beverageType = BeverageType.COFFEE)
    assertEquals(170, coffeeLog.effectiveHydrationMl)

    val electrolyteLog = WaterLog(amountMl = 500, beverageType = BeverageType.ELECTROLYTE)
    assertEquals(550, electrolyteLog.effectiveHydrationMl)
  }
}
