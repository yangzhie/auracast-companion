package com.innovatex.auracast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.innovatex.auracast.ui.theme.Muted

/**
 * Persistent controls, top right of every screen.
 *
 * The glyphs are small but each sits in a 48dp target, which is the
 * minimum WCAG asks for. Shrinking the target to match the glyph would
 * undercut the point of an accessibility-first app.
 */
@Composable
fun ActionBar(
    onRoutes: () -> Unit,
    onAccessibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BarButton(Icons.Default.Place, "Choose a route", onRoutes)
        BarButton(Icons.Default.Settings, "Accessibility settings", onAccessibility)
    }
}

@Composable
private fun BarButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Muted,
            modifier = Modifier.size(20.dp)
        )
    }
}