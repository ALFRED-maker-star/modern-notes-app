package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QuickNoteViewModel
import com.example.ui.components.ColorPickerRow
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextCharcoalLight
import com.example.ui.theme.TextCharcoalMuted
import com.example.ui.theme.WarmBackground
import com.example.ui.theme.WarmSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: QuickNoteViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val activeNotes by viewModel.activeNotes.collectAsState()
    val trashNotes by viewModel.trashNotes.collectAsState()
    val foldersWithCount by viewModel.foldersWithCount.collectAsState()

    var backupFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextCharcoal
            )
        )
        Text(
            text = "Customize QuickNote appearance & sync",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextCharcoalMuted)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Default Note Color
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F000000))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = AmberDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Default Note Color",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Color applied to newly created notes",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextCharcoalMuted)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                ColorPickerRow(
                    selectedHex = settings.defaultColorHex,
                    onColorSelected = { viewModel.setDefaultNoteColor(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Font Size
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F000000))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = AmberDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Font Size",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Editor text reading size",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextCharcoalMuted)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Small", "Medium", "Large").forEach { size ->
                        val isSelected = settings.fontSize == size
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFontSize(size) },
                            label = { Text(size) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberPrimary,
                                selectedLabelColor = TextCharcoal
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("font_size_$size")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Backup & Sync
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F000000))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = AmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Auto Backup & Sync",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Keep notes backed up and synchronized",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextCharcoalMuted)
                            )
                        }
                    }

                    Switch(
                        checked = settings.isAutoSyncEnabled,
                        onCheckedChange = { viewModel.setAutoSyncEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberDark,
                            checkedTrackColor = AmberContainer
                        ),
                        modifier = Modifier.testTag("backup_sync_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val lastBackupText = if (settings.lastBackupTimestamp > 0L) {
                    "Last backed up: " + SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
                        .format(Date(settings.lastBackupTimestamp))
                } else {
                    "No backup recorded yet"
                }

                Text(
                    text = lastBackupText,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextCharcoalLight)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.triggerBackup()
                        backupFeedback = "Backup completed successfully!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = TextCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("backup_now_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Backup Now", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                backupFeedback?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ $msg",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF0D9488),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 4: QuickNote Overview & Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F000000))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "QUICKNOTE STATS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoalMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatPill("Notes", "${activeNotes.size}")
                    StatPill("Pinned", "${activeNotes.count { it.isPinned }}")
                    StatPill("Folders", "${foldersWithCount.size}")
                    StatPill("Trash", "${trashNotes.size}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = TextCharcoalLight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "QuickNote v1.0.0 • Warm Modern Notes",
                style = MaterialTheme.typography.labelSmall.copy(color = TextCharcoalLight)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = AmberDark
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextCharcoalMuted)
        )
    }
}
