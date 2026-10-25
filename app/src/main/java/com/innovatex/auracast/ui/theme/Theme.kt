package com.innovatex.auracast.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.innovatex.auracast.data.AppSettings

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
    val density = LocalDensity.current

    val scaled = Density(
        density = density.density,
        fontScale = density.fontScale * AppSettings.textSize.scale
    )

    CompositionLocalProvider(LocalDensity provides scaled) {
        MaterialTheme(
            colorScheme = AuracastColorScheme,
            typography = Typography,
            content = content
        )
    }
}