package com.innovatex.auracast.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.innovatex.auracast.ui.theme.Paper

/**
 * Non-auditory confirmation that a stop's audio has started.
 *
 * The rider this app is for may not hear the announcement begin, so the
 * moment of connection is also reported through touch and sight. Both are
 * opt-in: a vibration in a pocket is useful, a white flash on a dark
 * screen at night is not, for everyone.
 */

/** A short double pulse — distinct from a notification's single buzz. */
fun vibrateOnConnect(context: Context) {
    val vibrator = context.getSystemService(VibratorManager::class.java)
        ?.defaultVibrator
        ?: return

    if (!vibrator.hasVibrator()) return

    vibrator.vibrate(
        VibrationEffect.createWaveform(
            longArrayOf(0, 60, 90, 60),
            intArrayOf(0, 180, 0, 180),
            -1
        )
    )
}

/**
 * Full-screen flash, driven by a counter rather than a boolean so two
 * connections in a row each produce their own flash.
 */
@Composable
fun ConnectFlash(trigger: Int, modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect

        /* Peaks well below white. A full-brightness flash on a dark screen
         * is painful, and can be a seizure risk at higher frequencies.
         */
        alpha.snapTo(0.45f)
        alpha.animateTo(0f, animationSpec = tween(durationMillis = 420))
    }

    if (alpha.value > 0f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .alpha(alpha.value)
                .background(Paper)
                .clearAndSetSemantics { }
        )
    }
}

/**
 * Holds the screen awake while composed, and releases it on the way out.
 * Left to a LaunchedEffect the flag would survive leaving the journey and
 * keep the screen lit for the rest of the session.
 */
@Composable
fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current

    DisposableEffect(enabled) {
        view.keepScreenOn = enabled
        onDispose { view.keepScreenOn = false }
    }
}