package com.innovatex.auracast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.innovatex.auracast.ui.theme.AlertRed
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.ReceivingGreen
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.SignalAmber
import com.innovatex.auracast.ui.theme.Surface
import com.innovatex.auracast.ui.theme.Violet

/**
 * What is happening to the audio, right now.
 *
 * This is the one thing on the journey screen a rider checks mid-stride,
 * so it is the largest element and the only live region — it announces
 * itself when it changes rather than waiting to be found.
 */
enum class BandTone {
    SEARCHING,
    CONNECTING,
    RECEIVING,
    TRAVELLING,
    NO_COVERAGE,
    FAULT
}

private data class ToneLook(
    val accent: Color,
    val icon: ImageVector,
    val spin: Boolean = false
)

private fun lookFor(tone: BandTone) = when (tone) {
    BandTone.SEARCHING -> ToneLook(SignalAmber, Icons.Default.Search)
    BandTone.CONNECTING -> ToneLook(SignalAmber, Icons.Default.Refresh)
    BandTone.RECEIVING -> ToneLook(ReceivingGreen, Icons.Default.PlayArrow)
    BandTone.TRAVELLING -> ToneLook(Violet, Icons.Default.Place)
    BandTone.NO_COVERAGE -> ToneLook(Muted, Icons.Default.Info)
    BandTone.FAULT -> ToneLook(AlertRed, Icons.Default.Warning)
}

@Composable
fun StatusBand(
    kicker: String,
    headline: String,
    detail: String,
    tone: BandTone,
    modifier: Modifier = Modifier
) {
    val look = lookFor(tone)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            /* A tinted edge rather than a filled block. Filled, this used
             * to shout the same volume whether audio was flowing or the
             * rider was simply between stops.
             */
            .border(1.dp, look.accent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(20.dp)
            /* Announced as one sentence when it changes. Three Texts would
             * interrupt each other and the detail — the part that says what
             * to expect — would be cut off.
             */
            .semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "$kicker. $headline. $detail"
            },
        verticalAlignment = Alignment.Top
    ) {
        BandGlyph(accent = look.accent, icon = look.icon)

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = kicker.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = look.accent
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall,
                color = Paper
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

@Composable
private fun BandGlyph(accent: Color, icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(52.dp)
            // Decorative; the kicker says the same thing in words.
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f))
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/** Thin rule used between rows inside a card. */
@Composable
fun CardDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(RouteLine)
    )
}