package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.UserSettings
import com.example.ui.components.GoalCalculatorDialog
import com.example.ui.theme.AquaCyan
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.i18n.LocalAppStrings

val INTERVAL_MINUTES = listOf(30, 45, 60, 90, 120, 180, 240)

val START_HOURS = listOf(
    6 to "6:00 AM",
    7 to "7:00 AM",
    8 to "8:00 AM",
    9 to "9:00 AM"
)

val END_HOURS = listOf(
    20 to "8:00 PM",
    21 to "9:00 PM",
    22 to "10:00 PM",
    23 to "11:00 PM"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersScreen(
    userSettings: UserSettings,
    onUpdateSettings: (UserSettings) -> Unit,
    onTriggerTestNotification: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted && !userSettings.notificationsEnabled) {
            onUpdateSettings(userSettings.copy(notificationsEnabled = true))
        }
    }

    var showCalculatorDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var testSentFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(testSentFeedback) {
        if (testSentFeedback) {
            kotlinx.coroutines.delay(3000)
            testSentFeedback = false
        }
    }

    if (showCalculatorDialog) {
        GoalCalculatorDialog(
            currentGoalMl = userSettings.dailyGoalMl,
            onGoalSelected = { newGoal ->
                onUpdateSettings(userSettings.copy(dailyGoalMl = newGoal))
            },
            onDismissRequest = { showCalculatorDialog = false }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(strings.resetConfirmTitle) },
            text = { Text(strings.resetConfirmBody) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.clearEverythingButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = strings.settingsTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = strings.settingsSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Language Selector Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_selector_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(OceanBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = strings.languageSectionTitle,
                                tint = OceanBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = strings.languageSectionTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = strings.languageSectionSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // English Option
                        val isEn = userSettings.language.equals("en", ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (!isEn) onUpdateSettings(userSettings.copy(language = "en"))
                                }
                                .testTag("language_option_en"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isEn) OceanBlue.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isEn) androidx.compose.foundation.BorderStroke(2.dp, OceanBlue) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🇺🇸", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.languageOptionEn,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isEn) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isEn) OceanBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                                if (isEn) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = OceanBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Spanish Option
                        val isEs = userSettings.language.equals("es", ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (!isEs) onUpdateSettings(userSettings.copy(language = "es"))
                                }
                                .testTag("language_option_es"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isEs) OceanBlue.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isEs) androidx.compose.foundation.BorderStroke(2.dp, OceanBlue) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🇪🇸", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.languageOptionEs,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isEs) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isEs) OceanBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                                if (isEs) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = OceanBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Android 13+ Permission Warning if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = "Permission Alert",
                                tint = WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.notificationPermissionNeeded,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.notificationPermissionBody,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("request_permission_button")
                        ) {
                            Text(strings.grantPermissionButton, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // Push Notifications Master Toggle Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notifications_master_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (userSettings.notificationsEnabled) OceanBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (userSettings.notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = "Notifications Status",
                                    tint = if (userSettings.notificationsEnabled) OceanBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = strings.pushRemindersTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (userSettings.notificationsEnabled) strings.pushRemindersActive else strings.pushRemindersDisabled,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (userSettings.notificationsEnabled) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        Switch(
                            checked = userSettings.notificationsEnabled,
                            onCheckedChange = { isEnabled ->
                                if (isEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onUpdateSettings(userSettings.copy(notificationsEnabled = isEnabled))
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFFFFFF),
                                checkedTrackColor = OceanBlue
                            ),
                            modifier = Modifier.testTag("notifications_master_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Instant Test Push Notification Action
                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onTriggerTestNotification()
                                testSentFeedback = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_test_notification_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (testSentFeedback) Icons.Default.CheckCircle else Icons.Default.Send,
                            contentDescription = "Send Test Notification",
                            tint = if (testSentFeedback) SuccessGreen else OceanBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (testSentFeedback) strings.testNotificationSent else strings.sendTestNotification,
                            color = if (testSentFeedback) SuccessGreen else OceanBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Reminder Interval & Active Window Configuration
        if (userSettings.notificationsEnabled) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = strings.reminderIntervalTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.reminderIntervalSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            INTERVAL_MINUTES.forEach { mins ->
                                val isSelected = userSettings.reminderIntervalMinutes == mins
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onUpdateSettings(userSettings.copy(reminderIntervalMinutes = mins))
                                    },
                                    label = { Text(strings.intervalLabel(mins)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = OceanBlue.copy(alpha = 0.2f),
                                        selectedLabelColor = OceanBlue
                                    ),
                                    modifier = Modifier.testTag("interval_chip_$mins")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Active Hours (Wake up to Bedtime)
                        Text(
                            text = strings.activeHoursTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.activeHoursSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(strings.wakeUpStartTime, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            START_HOURS.forEach { (hr, label) ->
                                FilterChip(
                                    selected = userSettings.startHour == hr,
                                    onClick = { onUpdateSettings(userSettings.copy(startHour = hr)) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(strings.bedtimeEndTime, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            END_HOURS.forEach { (hr, label) ->
                                FilterChip(
                                    selected = userSettings.endHour == hr,
                                    onClick = { onUpdateSettings(userSettings.copy(endHour = hr)) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Smart Reminders Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.smartRemindersTitle,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = strings.smartRemindersSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Switch(
                                checked = userSettings.smartRemindersEnabled,
                                onCheckedChange = { isSmart ->
                                    onUpdateSettings(userSettings.copy(smartRemindersEnabled = isSmart))
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = OceanBlue)
                            )
                        }
                    }
                }
            }
        }

        // Daily Goal Setting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_goal_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.dailyTargetTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${userSettings.dailyGoalMl} ml",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = OceanBlue
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = userSettings.dailyGoalMl.toFloat(),
                        onValueChange = {
                            val rounded = (it / 100).toInt() * 100
                            onUpdateSettings(userSettings.copy(dailyGoalMl = rounded))
                        },
                        valueRange = 1000f..5000f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = OceanBlue,
                            activeTrackColor = OceanBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_goal_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showCalculatorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_calculator_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculator",
                            tint = OceanBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.openCalculatorButton,
                            color = OceanBlue
                        )
                    }
                }
            }
        }

        // Reset / Clear Data Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.resetHistoryTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = strings.resetHistorySubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    TextButton(
                        onClick = { showClearDataDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
