package com.anant.sonqiva.ui.settings

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.anant.sonqiva.ui.components.AtmosphericBackground
import com.anant.sonqiva.ui.components.GlassCard
import com.anant.sonqiva.ui.theme.GlassBackground
import com.anant.sonqiva.ui.theme.OnSurface
import com.anant.sonqiva.ui.theme.OnSurfaceVariant
import com.anant.sonqiva.ui.theme.PrimaryAccent

@Composable
fun SettingsScreen(
    onRescanLibraryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lowMemoryMode by remember { mutableStateOf(false) }
    var resumeLastTrack by remember { mutableStateOf(true) }

    AtmosphericBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.displayMedium,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Section: Audio & Sound
            Text(
                text = "Audio & Equalizer",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryAccent,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { launchSystemEqualizer(context) },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = GlassBackground
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "System Equalizer",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnSurface
                        )
                        Text(
                            text = "Open system audio effects and sound tuning panel",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Playback & Performance
            Text(
                text = "Playback & Performance",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryAccent,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = GlassBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        icon = Icons.Default.Memory,
                        title = "Low-Memory Mode",
                        subtitle = "Optimizes caching and reduces RAM footprint for 2-4GB devices",
                        checked = lowMemoryMode,
                        onCheckedChange = { lowMemoryMode = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingToggleRow(
                        icon = Icons.Default.GraphicEq,
                        title = "Auto Resume Session",
                        subtitle = "Restore last played track and position on launch",
                        checked = resumeLastTrack,
                        onCheckedChange = { resumeLastTrack = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Library
            Text(
                text = "Media Library",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryAccent,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRescanLibraryClick),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = GlassBackground
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rescan Media Storage",
                            style = MaterialTheme.typography.titleSmall,
                            color = OnSurface
                        )
                        Text(
                            text = "Discover recently added audio files and folders",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: About
            Text(
                text = "About Sonqiva",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryAccent,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = GlassBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Sonqiva Music Player",
                                style = MaterialTheme.typography.titleSmall,
                                color = OnSurface
                            )
                            Text(
                                text = "Version 2.0 Stable • Offline-First Native Android",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "100% private and offline. No trackers, no streaming servers, no login required. Your music, your device, your way.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

private fun launchSystemEqualizer(context: Context) {
    try {
        val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No system equalizer app found on this device", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = OnSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryAccent,
                checkedTrackColor = PrimaryAccent.copy(alpha = 0.3f)
            )
        )
    }
}
