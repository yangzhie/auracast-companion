package com.innovatex.auracast.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AuracastColorScheme = darkColorScheme(
    primary = Violet,
    onPrimary = OnViolet,
    primaryContainer = VioletDim,
    onPrimaryContainer = Paper,

    secondary = Muted,
    onSecondary = OnViolet,

    background = Ink,
    onBackground = Paper,

    surface = Surface,
    onSurface = Paper,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = Muted,

    error = AlertRed,
    onError = OnViolet,

    outline = RouteLine,
    outlineVariant = RouteLine
)

@Composable
fun AuracastTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuracastColorScheme,
        typography = Typography,
        content = content
    )
}