package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.BeverageType
import com.example.data.model.UserSettings
import com.example.data.model.WaterLog
import com.example.ui.WaterTrackerUiState
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleState = WaterTrackerUiState(
      todayLogs = listOf(
        WaterLog(id = 1, timestamp = System.currentTimeMillis() - 7200000, amountMl = 350, beverageType = BeverageType.WATER, note = "Morning drink"),
        WaterLog(id = 2, timestamp = System.currentTimeMillis() - 3600000, amountMl = 250, beverageType = BeverageType.LEMON_WATER, note = "Midday refresh")
      ),
      todayIntakeMl = 600,
      userSettings = UserSettings(dailyGoalMl = 2500, notificationsEnabled = true),
      streakDays = 3,
      selectedBeverage = BeverageType.WATER
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TodayScreen(
          uiState = sampleState,
          onQuickLog = {},
          onLogCustom = { _, _, _ -> },
          onDeleteLog = {},
          onSelectBeverage = {},
          onNavigateToReminders = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
