package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity
import com.example.ui.FilterSelection
import com.example.ui.FilterType
import com.example.ui.QuickNoteViewModel
import com.example.ui.components.StaggeredNotesGrid
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextCharcoalLight
import com.example.ui.theme.TextCharcoalMuted
import com.example.ui.theme.WarmBackground
import com.example.ui.theme.WarmSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: QuickNoteViewModel,
    onNoteClick: (Long) -> Unit,
    onNewNoteClick: (isChecklist: Boolean) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredNotes by viewModel.filteredNotes.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val foldersWithCount by viewModel.foldersWithCount.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()

    var showFabMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar Header
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onSearchClick() }
                    .testTag("search_bar_trigger"),
                color = WarmSurfaceVariant,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F000000))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search notes",
                        tint = AmberDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search notes, tags, checklists...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextCharcoalMuted
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Q",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberDark
                            )
                        )
                    }
                }
            }

            // Folder & Tag Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("filter_chips_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // All Chip
                item {
                    val isSelected = selectedFilter.type == FilterType.ALL
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(FilterSelection(FilterType.ALL)) },
                        label = { Text("All Notes") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = TextCharcoal,
                            containerColor = Color.Transparent,
                            labelColor = TextCharcoalMuted
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("chip_all")
                    )
                }

                // Pinned Chip
                item {
                    val isSelected = selectedFilter.type == FilterType.PINNED
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(FilterSelection(FilterType.PINNED)) },
                        label = { Text("📌 Pinned") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = TextCharcoal,
                            containerColor = Color.Transparent,
                            labelColor = TextCharcoalMuted
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("chip_pinned")
                    )
                }

                // Folder Chips
                items(foldersWithCount, key = { "folder_${it.id}" }) { folder ->
                    val isSelected = selectedFilter.type == FilterType.FOLDER && selectedFilter.value == folder.name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) viewModel.setFilter(FilterSelection(FilterType.ALL))
                            else viewModel.setFilter(FilterSelection(FilterType.FOLDER, folder.name))
                        },
                        label = { Text("📁 ${folder.name} (${folder.noteCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = TextCharcoal,
                            containerColor = Color.Transparent,
                            labelColor = TextCharcoalMuted
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("chip_folder_${folder.id}")
                    )
                }

                // Tag Chips
                items(availableTags, key = { "tag_$it" }) { tag ->
                    val isSelected = selectedFilter.type == FilterType.TAG && selectedFilter.value == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) viewModel.setFilter(FilterSelection(FilterType.ALL))
                            else viewModel.setFilter(FilterSelection(FilterType.TAG, tag))
                        },
                        label = { Text("#$tag") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = TextCharcoal,
                            containerColor = Color.Transparent,
                            labelColor = TextCharcoalMuted
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("chip_tag_$tag")
                    )
                }
            }

            // Masonry Staggered Grid of Note Cards
            StaggeredNotesGrid(
                notes = filteredNotes,
                onNoteClick = { note -> onNoteClick(note.id) },
                onPinClick = { note -> viewModel.togglePin(note) },
                onChecklistToggle = { note, itemId -> viewModel.toggleChecklistFromCard(note, itemId) },
                getChecklistItems = { note ->
                    // Parse checklist items for preview
                    try {
                        viewModel.editorState.value // Trigger state read
                        val moshi = com.squareup.moshi.Moshi.Builder().build()
                        val type = com.squareup.moshi.Types.newParameterizedType(
                            List::class.java,
                            com.example.data.model.ChecklistItem::class.java
                        )
                        val adapter = moshi.adapter<List<com.example.data.model.ChecklistItem>>(type)
                        adapter.fromJson(note.checklistJson) ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                },
                emptyMessage = if (selectedFilter.type == FilterType.ALL) "No notes yet" else "No notes match filter",
                emptySubMessage = if (selectedFilter.type == FilterType.ALL) "Capture ideas, plans, and checklists with QuickNote!" else "Try choosing another filter above."
            )
        }

        // Floating Action Button with dropdown for Text Note or Checklist
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            FloatingActionButton(
                onClick = { showFabMenu = !showFabMenu },
                containerColor = AmberPrimary,
                contentColor = TextCharcoal,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .size(58.dp)
                    .testTag("create_note_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create note",
                    modifier = Modifier.size(28.dp)
                )
            }

            DropdownMenu(
                expanded = showFabMenu,
                onDismissRequest = { showFabMenu = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("create_menu")
            ) {
                DropdownMenuItem(
                    text = { Text("New Note", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        showFabMenu = false
                        onNewNoteClick(false)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = AmberDark
                        )
                    },
                    modifier = Modifier.testTag("create_text_note_item")
                )
                DropdownMenuItem(
                    text = { Text("New Checklist", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        showFabMenu = false
                        onNewNoteClick(true)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = AmberDark
                        )
                    },
                    modifier = Modifier.testTag("create_checklist_item")
                )
            }
        }
    }
}
