package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElegantCyan,
    onPrimary = Color(0xFF003258),
    primaryContainer = ElegantCyanContainer,
    onPrimaryContainer = ElegantCyanOnContainer,
    secondary = ElegantPeriwinkle,
    onSecondary = Color(0xFF003062),
    secondaryContainer = ElegantPeriwinkleContainer,
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = ElegantCyan,
    onTertiary = Color(0xFF003258),
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantPeriwinkle,
    outline = ElegantDarkBorder
)

private val LightColorScheme = darkColorScheme(
    primary = ElegantCyan,
    onPrimary = Color(0xFF003258),
    primaryContainer = ElegantCyanContainer,
    onPrimaryContainer = ElegantCyanOnContainer,
    secondary = ElegantPeriwinkle,
    onSecondary = Color(0xFF003062),
    secondaryContainer = ElegantPeriwinkleContainer,
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = ElegantCyan,
    onTertiary = Color(0xFF003258),
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantPeriwinkle,
    outline = ElegantDarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep our brand water theme distinctive by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
