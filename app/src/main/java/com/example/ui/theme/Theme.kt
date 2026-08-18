package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = BrandAccent,
    secondary = BrandSecondary,
    onSecondary = BrandText,
    secondaryContainer = BrandSurfaceVariant,
    onSecondaryContainer = BrandText,
    tertiary = BrandAccent,
    onTertiary = Color.Black,
    background = BrandBackground,
    onBackground = BrandText,
    surface = BrandSurface,
    onSurface = BrandText,
    surfaceVariant = BrandSurfaceVariant,
    onSurfaceVariant = BrandTextSecondary,
    error = BrandError,
    onError = Color.White,
    outline = BrandCardBorder
)

@Composable
fun YouBTechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
