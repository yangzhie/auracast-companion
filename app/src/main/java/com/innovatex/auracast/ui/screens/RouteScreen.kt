package com.innovatex.auracast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.innovatex.auracast.data.SampleData
import com.innovatex.auracast.data.TransitRoute
import com.innovatex.auracast.ui.components.RouteBadge
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.OnViolet
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.Surface
import com.innovatex.auracast.ui.theme.SurfaceHigh
import com.innovatex.auracast.ui.theme.Violet

/**
 * Pick a route before starting.
 *
 * A route with nothing fitted is still a legitimate choice — the rider
 * may simply want to follow it — so unfitted routes are dimmed rather
 * than disabled, and the note below the list says why.
 */
@Composable
fun RouteScreen(
    modifier: Modifier = Modifier,
    routes: List<TransitRoute> = SampleData.routes,
    onContinue: (TransitRoute) -> Unit = {}
) {
    var selectedId by remember { mutableStateOf(routes.firstOrNull()?.id) }
    val selectedRoute = routes.firstOrNull { it.id == selectedId }

    Column(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Choose your route",
                style = MaterialTheme.typography.headlineLarge,
                color = Paper
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Coverage is still being rolled out, so some routes are only partly fitted.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted
            )

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.selectableGroup()) {
                routes.forEach { route ->
                    RouteCard(
                        route = route,
                        selected = route.id == selectedId,
                        onSelect = { selectedId = route.id }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "You can still follow a route with no fitted stops — " +
                        "you just won't receive announcements on it.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(20.dp)
            )

            Spacer(Modifier.height(28.dp))
        }

        Button(
            onClick = { selectedRoute?.let(onContinue) },
            enabled = selectedRoute != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                // Min, not fixed — the label grows with the font setting.
                .defaultMinSize(minHeight = 56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Violet,
                contentColor = OnViolet
            )
        ) {
            Text("Start journey")
        }
    }
}

@Composable
private fun RouteCard(
    route: TransitRoute,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fitted = route.coveredStopCount > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) SurfaceHigh else Surface)
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            /* Violet, not green. Green means audio is flowing in this app;
             * borrowing it for "selected" would make the two states read
             * as the same thing on the journey screen.
             */
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Violet else RouteLine,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(18.dp)
            // One announcement; the tick is already implied by the role.
            .semantics(mergeDescendants = true) {
                contentDescription =
                    "Route ${route.routeNumber}, ${route.destination}. ${route.coverageSummary}"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RouteBadge(route.routeNumber)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = route.destination,
                style = MaterialTheme.typography.titleMedium,
                color = Paper
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = route.coverageSummary,
                style = MaterialTheme.typography.bodyMedium,
                // Dimmer again when there is nothing fitted to announce.
                color = if (fitted) Muted else RouteLine
            )
        }

        Spacer(Modifier.width(10.dp))

        SelectionDot(selected = selected)
    }
}

/** Filled tick when chosen, empty ring when not. */
@Composable
private fun SelectionDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Violet),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = OnViolet,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, RouteLine, CircleShape)
            )
        }
    }
}