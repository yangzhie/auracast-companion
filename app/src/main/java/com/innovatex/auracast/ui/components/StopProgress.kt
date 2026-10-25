package com.innovatex.auracast.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.innovatex.auracast.data.Stop
import com.innovatex.auracast.ui.theme.IdleGrey
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.OnViolet
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.ReceivingGreen
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.SignalAmber
import com.innovatex.auracast.ui.theme.VioletDim

/**
 * How one stop is doing. Independent of the sink's own connection state,
 * which the band above reports separately.
 */
enum class StopProgressState {
    IDLE,       // not reached yet
    CONNECTING, // joining this stop's broadcast
    CONNECTED,  // audio flowing
    PASSED,     // done with, behind the rider
    UNFITTED    // no transmitter at this stop
}

private data class StopLook(
    val node: Color,
    val filled: Boolean,
    val label: Color,
    val status: String,
    val pulses: Boolean = false
)

private fun lookFor(state: StopProgressState) = when (state) {
    StopProgressState.IDLE ->
        StopLook(IdleGrey, filled = false, label = Muted, status = "Waiting")

    StopProgressState.CONNECTING ->
        StopLook(SignalAmber, filled = true, label = Paper, status = "Connecting…", pulses = true)

    StopProgressState.CONNECTED ->
        StopLook(ReceivingGreen, filled = true, label = Paper, status = "Playing")

    StopProgressState.PASSED ->
        StopLook(VioletDim, filled = true, label = Muted, status = "Passed")

    StopProgressState.UNFITTED ->
        StopLook(IdleGrey, filled = false, label = Muted, status = "No announcements here")
}

@Composable
fun StopProgress(
    stops: List<Stop>,
    modifier: Modifier = Modifier,
    stateFor: (Stop) -> StopProgressState = { StopProgressState.IDLE }
) {
    Column(modifier = modifier.fillMaxWidth()) {
        stops.forEachIndexed { index, stop ->
            val state = stateFor(stop)
            StopRow(
                stop = stop,
                state = state,
                isLast = index == stops.lastIndex,
                railDone = state == StopProgressState.PASSED
            )
        }
    }
}

@Composable
private fun StopRow(
    stop: Stop,
    state: StopProgressState,
    isLast: Boolean,
    railDone: Boolean
) {
    val look = lookFor(state)
    val isActive = state == StopProgressState.CONNECTING ||
            state == StopProgressState.CONNECTED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            /* One announcement per stop. Without this a screen reader
             * reads the node, the name and the status as three items.
             */
            .clearAndSetSemantics {
                contentDescription = "${stop.name}. ${look.status}"
            }
    ) {
        Column(
            modifier = Modifier.width(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StopNode(state = state, look = look)

            if (!isLast) {
                Spacer(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(3.dp)
                        .height(26.dp)
                        .clip(CircleShape)
                        .background(if (railDone) VioletDim else RouteLine)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 10.dp)) {
            Text(
                text = stop.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = look.label
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = look.status.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                color = if (isActive) look.node else Muted
            )
        }
    }
}

@Composable
private fun StopNode(state: StopProgressState, look: StopLook) {
    Box(
        modifier = Modifier.size(28.dp),
        contentAlignment = Alignment.Center
    ) {
        // Slow halo behind the node currently being joined
        if (look.pulses) {
            val transition = rememberInfiniteTransition(label = "pulse")
            val fade by transition.animateFloat(
                initialValue = 0.45f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400),
                    repeatMode = RepeatMode.Restart
                ),
                label = "pulseFade"
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .alpha(fade)
                    .clip(CircleShape)
                    .background(look.node)
            )
        }

        when (state) {
            StopProgressState.CONNECTED -> FilledNode(look.node, Icons.Default.Check)
            StopProgressState.PASSED -> FilledNode(look.node, Icons.Default.Check, size = 16.dp)
            StopProgressState.CONNECTING -> FilledNode(look.node, null)

            StopProgressState.UNFITTED -> Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(2.dp, look.node, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = look.node,
                    modifier = Modifier.size(12.dp)
                )
            }

            StopProgressState.IDLE -> Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(2.dp, look.node, CircleShape)
            )
        }
    }
}

@Composable
private fun FilledNode(
    color: Color,
    icon: ImageVector?,
    size: Dp = 22.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            // The row carries the description; this is decoration.
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OnViolet,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}