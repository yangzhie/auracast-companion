package com.innovatex.auracast.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel

import com.innovatex.auracast.components.SetupCheckViewModel
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.OnViolet
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.ReceivingGreen
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.SignalAmber
import com.innovatex.auracast.ui.theme.Surface
import com.innovatex.auracast.ui.theme.Violet

/**
 * Prereqs before starting a journey:
 * Bluetooth, location, board, hearing device.
 */
@Composable
fun SetupCheckScreen(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: SetupCheckViewModel = viewModel()
    val status = viewModel.status

    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refresh(context)
    }

    // System activity
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refresh(context)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh(context)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Before you start",
                style = MaterialTheme.typography.headlineLarge,
                color = Paper
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Two things need to be ready for announcements to reach you.",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted
            )

            Spacer(Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                CheckRow(
                    isSet = status.bluetoothReady,
                    title = "Bluetooth",
                    detail = when {
                        status.bluetoothReady -> "On"
                        !status.bluetoothReady ->
                            "Allow this app to use Bluetooth"
                        else -> "Bluetooth is off — turn it on to find nearby stops"
                    }
                )

                RowDivider()

                CheckRow(
                    isSet = status.locationGranted,
                    title = "Location",
                    detail = if (status.locationGranted) {
                        "Allowed while using the app"
                    } else {
                        "Needed to know which stop you're at"
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            ExplainerCard()

            Spacer(Modifier.height(28.dp))
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = {
                    when {
                        status.allReady -> onContinue()

                        !status.permissionsGranted -> permLauncher.launch(
                            arrayOf(
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )

                        else -> enableBluetoothLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    // Min, not fixed — the label grows with the font setting.
                    .defaultMinSize(minHeight = 56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Violet,
                    contentColor = OnViolet
                )
            ) {
                Text(
                    when {
                        status.allReady -> "Continue"
                        !status.permissionsGranted -> "Grant permissions"
                        else -> "Turn on Bluetooth"
                    }
                )
            }

            // Force open settings after initial decline
            if (!status.permissionsGranted) {
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open app settings", color = Muted)
                }
            }
        }
    }
}

@Composable
private fun CheckRow(
    isSet: Boolean,
    title: String,
    detail: String
) {
    val accent = if (isSet) ReceivingGreen else SignalAmber

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$title. $detail"
            },
        verticalAlignment = Alignment.Top
    ) {
        StatusGlyph(accent = accent, isSet = isSet)

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Paper
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
        }
    }
}

// Tick/warning
@Composable
private fun StatusGlyph(accent: Color, isSet: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.14f))
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(2.dp, accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSet) Icons.Default.Check else Icons.Default.Warning,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ExplainerCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(20.dp)
            .semantics(mergeDescendants = true) { }
    ) {
        Text(
            text = "Why a board?",
            style = MaterialTheme.typography.titleSmall,
            color = Violet
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Android doesn't allow an app to join an Auracast broadcast, " +
                    "so a small board does it for you. Your hearing aids pair with " +
                    "the board, not with this phone.",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted
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