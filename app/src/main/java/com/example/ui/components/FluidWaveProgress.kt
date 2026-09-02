package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AquaCyan
import com.example.ui.theme.AquaLight
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DropletFoam
import com.example.ui.theme.IceBlue
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.OceanBlueDark
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.StreakFire
import com.example.ui.theme.SuccessGreen
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FluidWaveProgress(
    currentIntakeMl: Int,
    dailyGoalMl: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val progress = if (dailyGoalMl > 0) {
        (currentIntakeMl.toFloat() / dailyGoalMl.toFloat()).coerceIn(0f, 1.5f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1.0f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "fluidProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "waveShift")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    val secondaryWaveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondaryWaveOffset"
    )

    val percentage = (progress * 100).toInt()
    val remainingMl = maxOf(0, dailyGoalMl - currentIntakeMl)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hydration_wave_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Streak and Status Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Hydration Status",
                            tint = OceanBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val statusText = if (strings.languageCode == "es") {
                            if (percentage >= 100) "¡Meta Cumplida! 🎉" else if (percentage >= 50) "Bien Hidratado 👍" else "Necesitas Agua 💧"
                        } else {
                            if (percentage >= 100) "Goal Met! 🎉" else if (percentage >= 50) "Hydrated 👍" else "Need Water 💧"
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (percentage >= 100) SuccessGreen else OceanBlue
                            )
                        )
                    }
                }

                if (streakDays > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = StreakFire.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = StreakFire,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(strings.streakBadge, streakDays),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StreakFire
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fluid Wave Sphere Container
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                DropletFoam.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .testTag("water_wave_circle"),
                contentAlignment = Alignment.Center
            ) {
                // Wave Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val waterLevelY = height * (1f - animatedProgress)

                    // Secondary Background Wave (Lighter Aqua)
                    val backWavePath = Path()
                    backWavePath.moveTo(0f, height)
                    backWavePath.lineTo(0f, waterLevelY)

                    val backWaveAmplitude = if (animatedProgress in 0.02f..0.98f) 14.dp.toPx() else 4.dp.toPx()
                    val backWaveLength = width

                    var x = 0f
                    while (x <= width) {
                        val y = waterLevelY + backWaveAmplitude * sin((x / backWaveLength * 2 * PI + secondaryWaveOffset).toDouble()).toFloat()
                        backWavePath.lineTo(x, y)
                        x += 4f
                    }
                    backWavePath.lineTo(width, height)
                    backWavePath.close()

                    drawPath(
                        path = backWavePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AquaLight.copy(alpha = 0.6f),
                                SkyBlue.copy(alpha = 0.7f)
                            )
                        )
                    )

                    // Front Wave (Deep Ocean Blue Gradient)
                    val frontWavePath = Path()
                    frontWavePath.moveTo(0f, height)
                    frontWavePath.lineTo(0f, waterLevelY)

                    val frontWaveAmplitude = if (animatedProgress in 0.02f..0.98f) 18.dp.toPx() else 6.dp.toPx()
                    val frontWaveLength = width

                    x = 0f
                    while (x <= width) {
                        val y = waterLevelY + frontWaveAmplitude * sin((x / frontWaveLength * 2 * PI + waveOffset).toDouble()).toFloat()
                        frontWavePath.lineTo(x, y)
                        x += 4f
                    }
                    frontWavePath.lineTo(width, height)
                    frontWavePath.close()

                    drawPath(
                        path = frontWavePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AquaCyan.copy(alpha = 0.9f),
                                OceanBlue,
                                OceanBlueDark
                            ),
                            startY = waterLevelY - 20f,
                            endY = height
                        )
                    )

                    // Top Wave Foam Highlight
                    if (animatedProgress in 0.02f..0.98f) {
                        val foamPath = Path()
                        foamPath.moveTo(0f, waterLevelY)
                        x = 0f
                        while (x <= width) {
                            val y = waterLevelY + frontWaveAmplitude * sin((x / frontWaveLength * 2 * PI + waveOffset).toDouble()).toFloat()
                            foamPath.lineTo(x, y)
                            x += 4f
                        }
                        drawPath(
                            path = foamPath,
                            color = Color.White.copy(alpha = 0.6f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Outer Subtle Ring Border
                    drawCircle(
                        color = AquaCyan.copy(alpha = 0.4f),
                        style = Stroke(width = 3.dp.toPx()),
                        center = center,
                        radius = size.minDimension / 2 - 2f
                    )
                }

                // Central Metrics overlay inside the liquid bubble
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (animatedProgress > 0.45f) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$currentIntakeMl ml",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (animatedProgress > 0.45f) DropletFoam else OceanBlue
                        )
                    )
                    Text(
                        text = String.format(strings.ofGoal, dailyGoalMl),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (animatedProgress > 0.45f) IceBlue.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtext Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (remainingMl > 0) strings.remainingTarget else strings.goalCompleted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            text = if (remainingMl > 0) String.format(strings.mlToGo, remainingMl) else String.format(strings.extraSurplus, currentIntakeMl - dailyGoalMl),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (remainingMl > 0) OceanBlue else SuccessGreen
                            )
                        )
                    }

                    if (percentage >= 100) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = strings.goalCompleted,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        val glassesLeft = (remainingMl + 249) / 250
                        Text(
                            text = String.format(strings.glassesLeft, glassesLeft),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}
