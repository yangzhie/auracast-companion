package com.innovatex.auracast.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.innovatex.auracast.data.JourneySummary
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.OnViolet
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.ReceivingGreen
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.Surface
import com.innovatex.auracast.ui.theme.Violet

/**
 * End of journey screen. Is displayed after the user
 * manually ends the journey, or the system determines
 * the last stop has been reached and its audio has played.
 */
@Composable
fun ArrivedScreen(
    modifier: Modifier = Modifier,
    summary: JourneySummary? = null,
    onPlanAnother: () -> Unit = {},
) {
    // Journey completed
    val completed = summary?.completed ?: false
    // All stops have been covered
    val allHeard = summary?.allCoveredStopsConnected == true

    // Distinction between user-ended journey and naturally-reached journey
    val headline = if (completed) "You've arrived" else "Journey ended"
    val place = summary?.destinationName
    val spoken = buildString {
        append(headline)
        if (place != null) append(if (completed) " at $place." else " at $place.")
        append("Your journey has ended. Please be sure to disconnect your nRF52840 board and hearing device.")
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Tick mark sign for complete journey
            ArrivalMark(accent = ReceivingGreen)

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = spoken
                }
            ) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Paper
                )

                if (place != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = place,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Violet
                    )
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Your journey has ended. Please be sure to disconnect your nRF52840 board and hearing device.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Muted
                )
            }

            if (summary != null) {
                Spacer(Modifier.height(32.dp))
                SummaryCard(summary = summary)
            }

            Spacer(Modifier.height(32.dp))
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = onPlanAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Violet,
                    contentColor = OnViolet
                )
            ) {
                Text("Plan another journey")
            }
        }
    }
}

// Confirmation glyph; the headline carries the meaning
@Composable
private fun ArrivalMark(accent: Color) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f))
        )
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .border(2.dp, accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun SummaryCard(summary: JourneySummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        SummaryRow(
            label = if (summary.completed) {
                "Stops with announcements"
            } else {
                "Stops heard so far"
            },
            value = "${summary.coveredStopsConnected} of ${summary.coveredStopsTotal}",
            valueColor = if (summary.allCoveredStopsConnected) ReceivingGreen else Paper
        )

        if (summary.uncoveredStopsPassed > 0) {
            RowDivider()
            SummaryRow(
                label = "Stops not yet fitted",
                value = summary.uncoveredStopsPassed.toString()
            )
        }

        RowDivider()
        SummaryRow(
            label = "Journey time",
            value = if (summary.journeyMinutes < 1) {
                "Under a minute"
            } else {
                "${summary.journeyMinutes} min"
            }
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Paper
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            // Label and value as one announcement, not two.
            .clearAndSetSemantics { contentDescription = "$label: $value" },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Muted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(RouteLine)
    )
}