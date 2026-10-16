package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004E5C),
    onPrimaryContainer = Color(0xFF97F0FF),

    secondary = NeonPurple,
    onSecondary = Color(0xFF280057),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFE9D5FF),

    tertiary = NeonAmber,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF784E00),
    onTertiaryContainer = Color(0xFFFFDF9E),

    background = BgDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,

    outline = SurfaceBorder,
    error = NeonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent cyber gaming styling
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
