package com.tgplayer.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = TgAccent,
    onPrimary = TgTextPrimary,
    primaryContainer = TgAccentVariant,
    onPrimaryContainer = TgTextPrimary,
    secondary = TgAccent,
    onSecondary = TgTextPrimary,
    background = TgBackground,
    onBackground = TgTextPrimary,
    surface = TgSurface,
    onSurface = TgTextPrimary,
    surfaceVariant = TgSurfaceElevated,
    onSurfaceVariant = TgTextSecondary,
    outline = TgDivider,
    error = TgError,
    onError = TgTextPrimary
)

@Composable
fun TGPlayerTheme(
    darkTheme: Boolean = true, // Always dark per spec
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TgBackground.toArgb()
            window.navigationBarColor = TgBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TGPlayerTypography,
        shapes = TGPlayerShapes,
        content = content
    )
}
