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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.innovatex.auracast.bluetooth.JourneyViewModel
import com.innovatex.auracast.core.JourneyPhase
import com.innovatex.auracast.core.JourneyState
import com.innovatex.auracast.data.AppSettings
import com.innovatex.auracast.data.Stop
import com.innovatex.auracast.data.TransitRoute
import com.innovatex.auracast.ui.components.BandTone
import com.innovatex.auracast.ui.components.ConnectFlash
import com.innovatex.auracast.ui.components.KeepScreenOn
import com.innovatex.auracast.ui.components.RouteBadge
import com.innovatex.auracast.ui.components.StatusBand
import com.innovatex.auracast.ui.components.StopProgress
import com.innovatex.auracast.ui.components.StopProgressState
import com.innovatex.auracast.ui.components.vibrateOnConnect
import com.innovatex.auracast.ui.theme.AlertRed
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.SignalAmber
import com.innovatex.auracast.ui.theme.Surface

/**
 * Live journey. The ViewModel is passed in rather than resolved here, so
 * the action bar — which lives above the NavHost — ends the same instance
 * this screen is driving.
 */
@Composable
fun JourneyRoute(
    route: TransitRoute,
    viewModel: JourneyViewModel,
    modifier: Modifier = Modifier,
    onEndJourney: () -> Unit = {},
    onOpenAccessibility: () -> Unit = {}
) {
    val context = LocalContext.current

    // Keyed on route id so a different route restarts the journey.
    LaunchedEffect(route.id) {
        viewModel.startJourney(context, route)
    }

    val state = viewModel.state

    /* Counter rather than a boolean, so two stops in a row each get their
     * own flash instead of the second being swallowed.
     */
    var flashTrigger by remember { mutableIntStateOf(0) }

    /* Keyed on the stop as well as the phase: the rider should be told
     * once per stop that audio has started, not again on every
     * recomposition while it plays.
     */
    LaunchedEffect(state?.phase, state?.joinedStopIndex) {
        if (state?.phase == JourneyPhase.RECEIVING) {
            if (AppSettings.vibrateOnConnect) vibrateOnConnect(context)
            if (AppSettings.flashOnConnect) flashTrigger++
        }
    }

    KeepScreenOn(AppSettings.keepScreenOn)

    /* Running out of stops ends the journey by the same path the button
     * uses, so the summary is built once and in one place.
     */
    LaunchedEffect(state?.isJourneyOver) {
        if (state?.isJourneyOver == true) {
            viewModel.endJourney()
            onEndJourney()
        }
    }

    if (state == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Starting journey…",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted
            )
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        JourneyScreen(
            state = state,
            boardConnected = viewModel.boardConnected,
            onEndJourney = {
                viewModel.endJourney()
                onEndJourney()
            }
        )

        // Drawn over the journey so it reads as the screen itself flashing.
        ConnectFlash(trigger = flashTrigger)
    }
}

/**
 * Stateless, so the same screen renders a real journey and a hand-built
 * one.
 *
 * Two independent readings live here. The band is the link to the hearing
 * device — whether audio is reaching the rider at all. The list below is
 * the route — which stop is which. A red band above green stops is a
 * coherent state, not a contradiction: the journey is progressing, the
 * sink has dropped.
 */
@Composable
fun JourneyScreen(
    state: JourneyState,
    modifier: Modifier = Modifier,
    boardConnected: Boolean = true,
    onEndJourney: () -> Unit = {}
) {
    val route = state.route

    Column(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RouteBadge(route.routeNumber)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = route.destination,
                    style = MaterialTheme.typography.titleMedium,
                    color = Muted,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            /* The board is the only thing that can actually join a
             * broadcast. Without it the stops below still advance and
             * nothing plays, which reads as a hardware fault unless it
             * is said plainly.
             */
            if (!boardConnected) {
                BoardWarning()
                Spacer(Modifier.height(14.dp))
            }

            LinkBand(state = state)

            Spacer(Modifier.height(30.dp))

            Text(
                text = "STOPS ON THIS ROUTE",
                style = MaterialTheme.typography.labelMedium,
                color = Muted
            )

            Spacer(Modifier.height(16.dp))

            /* Every stop, always. A rider needs to see what is coming,
             * not only what is next, and the route is short enough.
             */
            StopProgress(
                stops = route.stops,
                stateFor = { stop -> stopStateFor(stop, state) }
            )

            Spacer(Modifier.height(28.dp))
        }

        OutlinedButton(
            onClick = onEndJourney,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                // Min, not fixed — the label grows with the font setting.
                .defaultMinSize(minHeight = 56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
        ) {
            Text("End journey")
        }
    }
}

@Composable
private fun BoardWarning() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, SignalAmber.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(16.dp)
            .semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
                contentDescription =
                    "Board not connected. Switch it on and keep it with you — " +
                            "nothing can play without it."
            },
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = SignalAmber,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Board not connected",
                style = MaterialTheme.typography.titleSmall,
                color = Paper
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Switch it on and keep it with you — nothing can play without it.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

/** The hearing device link, top of the screen. */
@Composable
private fun LinkBand(state: JourneyState) {
    val currentStop = state.currentTargetStop
    val nextCovered = state.nextAuracastEnabledStop

    if (currentStop == null) {
        StatusBand(
            kicker = "Journey complete",
            headline = "You've arrived",
            detail = "There are no more stops on this route.",
            tone = BandTone.TRAVELLING
        )
        return
    }

    when (state.phase) {
        JourneyPhase.SEARCHING -> StatusBand(
            kicker = "Searching",
            headline = "Approaching ${currentStop.name}",
            detail = "Looking for this stop's announcements.",
            tone = BandTone.SEARCHING
        )

        JourneyPhase.CONNECTING -> StatusBand(
            kicker = "Connecting",
            headline = currentStop.name,
            detail = "Joining this stop's announcements.",
            tone = BandTone.CONNECTING
        )

        JourneyPhase.RECEIVING -> StatusBand(
            kicker = "Playing",
            headline = currentStop.name,
            detail = "Audio is going to your hearing device.",
            tone = BandTone.RECEIVING
        )

        JourneyPhase.TRAVELLING -> StatusBand(
            kicker = "Between stops",
            headline = "On the way to ${nextCovered?.name ?: "your destination"}",
            detail = "You'll connect automatically when you arrive.",
            tone = BandTone.TRAVELLING
        )

        JourneyPhase.AT_UNCOVERED -> StatusBand(
            kicker = "No announcements",
            headline = currentStop.name,
            detail = nextCovered?.let {
                "This stop isn't fitted yet. Nothing is wrong — you'll reconnect at ${it.name}."
            } ?: "This stop isn't fitted yet. Nothing is wrong.",
            tone = BandTone.NO_COVERAGE
        )

        JourneyPhase.DROP_OUT -> StatusBand(
            kicker = "Not receiving",
            headline = "Connection dropped",
            detail = "Trying to reconnect to ${currentStop.name}.",
            tone = BandTone.FAULT
        )
    }
}

/**
 * A stop's own progress. Deliberately ignores DROP_OUT: losing the sink
 * says nothing about where the rider is on the route.
 */
private fun stopStateFor(stop: Stop, state: JourneyState): StopProgressState {
    val index = state.route.stops.indexOf(stop)

    if (!stop.hasAuracast) return StopProgressState.UNFITTED
    if (index < state.currentStopIndex) return StopProgressState.PASSED
    if (index > state.currentStopIndex) return StopProgressState.IDLE

    return when (state.phase) {
        JourneyPhase.RECEIVING -> StopProgressState.CONNECTED
        JourneyPhase.SEARCHING,
        JourneyPhase.CONNECTING,
        JourneyPhase.DROP_OUT -> StopProgressState.CONNECTING
        JourneyPhase.AT_UNCOVERED -> StopProgressState.UNFITTED
        JourneyPhase.TRAVELLING -> StopProgressState.IDLE
    }
}