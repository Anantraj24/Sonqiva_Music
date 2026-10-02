package com.anant.sonqiva.ui.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anant.sonqiva.data.model.FolderItem
import com.anant.sonqiva.ui.components.AtmosphericBackground
import com.anant.sonqiva.ui.components.GlassCard
import com.anant.sonqiva.ui.theme.GlassBackground
import com.anant.sonqiva.ui.theme.OnSurface
import com.anant.sonqiva.ui.theme.OnSurfaceVariant
import com.anant.sonqiva.ui.theme.PrimaryAccent

@Composable
fun FolderFilterScreen(
    allFolders: List<FolderItem>,
    excludedPaths: Set<String>,
    onExcludedPathsChanged: (Set<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Local working copy so changes feel instant without waiting for DataStore round-trip
    var localExcluded by remember(excludedPaths) { mutableStateOf(excludedPaths) }

    fun commitChange(newExcluded: Set<String>) {
        localExcluded = newExcluded
        onExcludedPathsChanged(newExcluded)
    }

    val allVisible = localExcluded.isEmpty()
    val allHidden = allFolders.isNotEmpty() && allFolders.all { it.path in localExcluded }

    AtmosphericBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = OnSurface
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Folder Visibility",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface
                    )
                    Text(
                        text = "Hidden folders won't play or appear anywhere",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            // ── Select All / Deselect All ─────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!allVisible) {
                    TextButton(onClick = { commitChange(emptySet()) }) {
                        Text("Show all", color = PrimaryAccent)
                    }
                }
                if (!allHidden && allFolders.isNotEmpty()) {
                    TextButton(onClick = { commitChange(allFolders.map { it.path }.toSet()) }) {
                        Text("Hide all", color = OnSurfaceVariant)
                    }
                }
            }

            // ── Folder list ───────────────────────────────────────────
            if (allFolders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOff,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No folders found",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface
                        )
                        Text(
                            text = "Scan your library first to discover folders",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${allFolders.size} folders • ${localExcluded.size} hidden",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(allFolders, key = { it.path }) { folder ->
                        val isVisible = folder.path !in localExcluded
                        FolderVisibilityRow(
                            folder = folder,
                            isVisible = isVisible,
                            onToggle = {
                                val newSet = if (isVisible) {
                                    localExcluded + folder.path
                                } else {
                                    localExcluded - folder.path
                                }
                                commitChange(newSet)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }
        }
    }
}

@Composable
private fun FolderVisibilityRow(
    folder: FolderItem,
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isVisible) GlassBackground else GlassBackground.copy(alpha = 0.4f),
        animationSpec = tween(200),
        label = "row_bg"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isVisible) PrimaryAccent else OnSurfaceVariant,
        animationSpec = tween(200),
        label = "icon_tint"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder icon with coloured badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Folder else Icons.Default.FolderOff,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isVisible) OnSurface else OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = folder.path,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isVisible) "${folder.songCount} songs · visible"
                           else "${folder.songCount} songs · hidden",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isVisible) PrimaryAccent else OnSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Custom animated checkmark pill
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isVisible) PrimaryAccent.copy(alpha = 0.15f)
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isVisible) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Visible",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
