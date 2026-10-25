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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.innovatex.auracast.R
import com.innovatex.auracast.data.AppLanguage
import com.innovatex.auracast.data.AppSettings
import com.innovatex.auracast.data.TextSizeOption
import com.innovatex.auracast.ui.theme.Ink
import com.innovatex.auracast.ui.theme.Muted
import com.innovatex.auracast.ui.theme.OnViolet
import com.innovatex.auracast.ui.theme.Paper
import com.innovatex.auracast.ui.theme.RouteLine
import com.innovatex.auracast.ui.theme.Surface
import com.innovatex.auracast.ui.theme.SurfaceHigh
import com.innovatex.auracast.ui.theme.Violet

/**
 * Settings that change how the app reaches the rider.
 *
 * Everything here takes effect immediately and persists — there is no
 * save step, because a settings screen that can be left in an unsaved
 * state is one more thing to get wrong.
 */
@Composable
fun AccessibilityScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentLanguage = AppSettings.currentLanguage(context)

    Column(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.a11y_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Paper
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.a11y_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = Muted
            )

            Spacer(Modifier.height(28.dp))

            SectionLabel(stringResource(R.string.a11y_language))

            Spacer(Modifier.height(12.dp))

            LanguageCard(
                current = currentLanguage,
                onSelect = { AppSettings.setLanguage(context, it) }
            )

            Spacer(Modifier.height(28.dp))

            SectionLabel(stringResource(R.string.a11y_text_size))

            Spacer(Modifier.height(12.dp))

            TextSizeCard(
                current = AppSettings.textSize,
                onSelect = { AppSettings.updateTextSize(it) }
            )

            Spacer(Modifier.height(28.dp))

            SectionLabel(stringResource(R.string.a11y_alerts))

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                ToggleRow(
                    title = stringResource(R.string.a11y_vibrate),
                    detail = stringResource(R.string.a11y_vibrate_detail),
                    checked = AppSettings.vibrateOnConnect,
                    onCheckedChange = { AppSettings.updateVibrateOnConnect(it) }
                )
                RowDivider()
                ToggleRow(
                    title = stringResource(R.string.a11y_flash),
                    detail = stringResource(R.string.a11y_flash_detail),
                    checked = AppSettings.flashOnConnect,
                    onCheckedChange = { AppSettings.updateFlashOnConnect(it) }
                )
            }

            Spacer(Modifier.height(28.dp))

            SectionLabel(stringResource(R.string.a11y_display))

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                ToggleRow(
                    title = stringResource(R.string.a11y_keep_awake),
                    detail = stringResource(R.string.a11y_keep_awake_detail),
                    checked = AppSettings.keepScreenOn,
                    onCheckedChange = { AppSettings.updateKeepScreenOn(it) }
                )
            }

            Spacer(Modifier.height(28.dp))
        }

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                // Min, not fixed — the label grows with the font setting.
                .defaultMinSize(minHeight = 56.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(stringResource(R.string.a11y_done), color = Paper)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Muted
    )
}

/**
 * Languages are listed one per row rather than in a dropdown. A rider who
 * has the app in a script they cannot read needs to see every option at
 * once, each written in its own language.
 */
@Composable
private fun LanguageCard(
    current: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .selectableGroup()
    ) {
        AppLanguage.entries.forEachIndexed { index, language ->
            if (index > 0) RowDivider()

            val selected = language == current

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected,
                        onClick = { if (!selected) onSelect(language) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 16.dp)
                    .semantics(mergeDescendants = true) { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.endonym,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected) Paper else Muted,
                    modifier = Modifier.weight(1f)
                )
                SelectionTick(selected)
            }
        }

        RowDivider()

        Text(
            text = stringResource(R.string.a11y_language_detail),
            style = MaterialTheme.typography.bodySmall,
            color = Muted,
            modifier = Modifier.padding(vertical = 14.dp)
        )
    }
}

@Composable
private fun TextSizeCard(
    current: TextSizeOption,
    onSelect: (TextSizeOption) -> Unit
) {
    val labels = mapOf(
        TextSizeOption.STANDARD to stringResource(R.string.a11y_size_standard),
        TextSizeOption.LARGE to stringResource(R.string.a11y_size_large),
        TextSizeOption.LARGEST to stringResource(R.string.a11y_size_largest)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextSizeOption.entries.forEach { option ->
                val selected = option == current

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Violet else SurfaceHigh)
                        .selectable(
                            selected = selected,
                            onClick = { onSelect(option) },
                            role = Role.RadioButton
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) Violet else RouteLine,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    /* Each chip is rendered at the size it selects, so the
                     * choice is previewed rather than described.
                     */
                    Text(
                        text = labels[option].orEmpty(),
                        style = when (option) {
                            TextSizeOption.STANDARD -> MaterialTheme.typography.bodyMedium
                            TextSizeOption.LARGE -> MaterialTheme.typography.bodyLarge
                            TextSizeOption.LARGEST -> MaterialTheme.typography.titleMedium
                        },
                        color = if (selected) OnViolet else Muted
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.a11y_text_size_detail),
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    detail: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            // One announcement; the switch supplies the on/off state.
            .semantics(mergeDescendants = true) {
                contentDescription = "$title. $detail"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
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

        Spacer(Modifier.width(16.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OnViolet,
                checkedTrackColor = Violet,
                uncheckedThumbColor = Muted,
                uncheckedTrackColor = Ink,
                uncheckedBorderColor = RouteLine
            )
        )
    }
}

@Composable
private fun SelectionTick(selected: Boolean) {
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

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(RouteLine)
    )
}