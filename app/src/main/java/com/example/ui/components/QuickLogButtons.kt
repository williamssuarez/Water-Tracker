package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeverageType
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AquaCyan
import com.example.ui.theme.AquaLight
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.OceanBlueDark
import com.example.ui.theme.SkyBlue

data class PresetVolume(
    val amountMl: Int,
    val label: String,
    val iconDesc: String
)

val PRESET_VOLUMES = listOf(
    PresetVolume(150, "150 ml", "Cup"),
    PresetVolume(250, "250 ml", "Glass"),
    PresetVolume(350, "350 ml", "Mug"),
    PresetVolume(500, "500 ml", "Bottle"),
    PresetVolume(750, "750 ml", "Flask")
)

@Composable
fun QuickLogSection(
    currentBeverage: BeverageType,
    onOpenBeverageSelector: () -> Unit,
    onQuickLog: (Int) -> Unit,
    onOpenCustomLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val localizedBeverageName = currentBeverage.getLocalizedName(strings)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Beverage Type Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.quickHydrate,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onOpenBeverageSelector)
                    .testTag("current_beverage_badge"),
                shape = RoundedCornerShape(20.dp),
                color = currentBeverage.getColor().copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, currentBeverage.getColor().copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = currentBeverage.getIcon(),
                        contentDescription = localizedBeverageName,
                        tint = currentBeverage.getColor(),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = localizedBeverageName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = currentBeverage.getColor()
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = strings.changeBeverage,
                        tint = currentBeverage.getColor(),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Horizontal Quick Preset Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PRESET_VOLUMES.take(3).forEach { preset ->
                PresetCard(
                    preset = preset,
                    beverage = currentBeverage,
                    onClick = { onQuickLog(preset.amountMl) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PRESET_VOLUMES.drop(3).forEach { preset ->
                PresetCard(
                    preset = preset,
                    beverage = currentBeverage,
                    onClick = { onQuickLog(preset.amountMl) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Custom Amount Button
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenCustomLog)
                    .testTag("custom_log_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(OceanBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.customLabel,
                            tint = OceanBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.customLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OceanBlue
                        )
                    )
                    Text(
                        text = strings.specifyMlLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    preset: PresetVolume,
    beverage: BeverageType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val localizedDesc = remember(preset, strings) {
        strings.presetLabel(preset.amountMl, preset.iconDesc).removePrefix("${preset.amountMl} ml ")
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("preset_button_${preset.amountMl}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(beverage.getColor().copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = preset.label,
                    tint = beverage.getColor(),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "+${preset.label}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = localizedDesc,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
