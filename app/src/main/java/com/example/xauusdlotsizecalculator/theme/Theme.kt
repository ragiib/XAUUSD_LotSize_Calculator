package com.example.xauusdlotsizecalculator.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TvPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = TvPlumContainer,
    onPrimaryContainer = TvOnPlum,
    secondary = TvPurpleGlow,
    onSecondary = Color.Black,
    tertiary = TvLightGrey,
    onTertiary = TvOnLightGrey,
    background = TvBlackBackground,
    onBackground = TvTextPrimary,
    surface = TvDarkSurface,
    onSurface = TvTextPrimary,
    surfaceVariant = TvDarkSurfaceElevated,
    onSurfaceVariant = TvTextSecondary,
    outline = TvDarkSurfaceBorder,
    error = TvLightGrey,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = TvPurpleDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF230826),
    secondary = TvPurplePrimary,
    onSecondary = Color.White,
    tertiary = TvLightGrey,
    onTertiary = TvOnLightGrey,
    background = TvLightBackground,
    onBackground = TvLightTextPrimary,
    surface = TvLightSurface,
    onSurface = TvLightTextPrimary,
    surfaceVariant = TvLightSurfaceElevated,
    onSurfaceVariant = TvLightTextSecondary,
    outline = TvLightSurfaceBorder,
    error = TvLightGrey,
    onError = Color.Black
)

@Composable
fun XAUUSDLotSizeCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
