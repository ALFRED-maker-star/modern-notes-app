package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity
import com.example.data.model.ChecklistItem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextCharcoalMuted

@Composable
fun StaggeredNotesGrid(
    notes: List<NoteEntity>,
    onNoteClick: (NoteEntity) -> Unit,
    onPinClick: (NoteEntity) -> Unit,
    onChecklistToggle: (NoteEntity, String) -> Unit,
    getChecklistItems: (NoteEntity) -> List<ChecklistItem>,
    emptyMessage: String = "No notes found",
    emptySubMessage: String = "Tap the + button below to create your first note!",
    modifier: Modifier = Modifier
) {
    if (notes.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NoteAlt,
                    contentDescription = null,
                    tint = AmberDark.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextCharcoal
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = emptySubMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextCharcoalMuted
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val pinnedNotes = notes.filter { it.isPinned }
    val unpinnedNotes = notes.filter { !it.isPinned }
    val hasBothSections = pinnedNotes.isNotEmpty() && unpinnedNotes.isNotEmpty()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("notes_staggered_grid"),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        // Pinned Section Header
        if (pinnedNotes.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(
                    title = "PINNED",
                    icon = Icons.Default.PushPin
                )
            }

            items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                NoteCard(
                    note = note,
                    checklistItems = if (note.isChecklist) getChecklistItems(note) else emptyList(),
                    onClick = { onNoteClick(note) },
                    onPinClick = { onPinClick(note) },
                    onChecklistToggle = if (note.isChecklist) { itemId ->
                        onChecklistToggle(note, itemId)
                    } else null
                )
            }
        }

        // Others Section Header
        if (hasBothSections) {
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(title = "OTHERS")
            }
        }

        // Unpinned Notes
        items(unpinnedNotes, key = { "unpinned_${it.id}" }) { note ->
            NoteCard(
                note = note,
                checklistItems = if (note.isChecklist) getChecklistItems(note) else emptyList(),
                onClick = { onNoteClick(note) },
                onPinClick = { onPinClick(note) },
                onChecklistToggle = if (note.isChecklist) { itemId ->
                    onChecklistToggle(note, itemId)
                } else null
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberDark,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = TextCharcoalMuted
                )
            )
        }
    }
}
