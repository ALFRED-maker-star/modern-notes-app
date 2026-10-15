package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QuickNoteViewModel
import com.example.ui.components.ChecklistEditor
import com.example.ui.components.ColorPickerRow
import com.example.ui.components.FormattingToolbar
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextCharcoalLight
import com.example.ui.theme.TextCharcoalMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long,
    initialChecklistMode: Boolean = false,
    viewModel: QuickNoteViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editorState by viewModel.editorState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val foldersWithCount by viewModel.foldersWithCount.collectAsState()

    var showColorPicker by remember { mutableStateOf(false) }
    var showFolderTagDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNoteForEditing(noteId)
        if (noteId == 0L && initialChecklistMode) {
            viewModel.toggleChecklistMode()
        }
    }

    // Intercept back navigation to flush auto-save safely
    BackHandler {
        viewModel.flushSave()
        onBack()
    }

    val backgroundColor by animateColorAsState(
        targetValue = parseHexColor(editorState.colorHex, Color(0xFFFFFDF7)),
        label = "editor_bg_color"
    )

    val fontScale = when (settings.fontSize) {
        "Small" -> 0.9f
        "Large" -> 1.18f
        else -> 1.0f
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("note_editor_screen"),
        containerColor = backgroundColor,
        topBar = {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.flushSave()
                        onBack()
                    },
                    modifier = Modifier.testTag("editor_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Save and return",
                        tint = TextCharcoal
                    )
                }

                // Auto-save status indicator
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = editorState.saveStatus,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextCharcoalMuted,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (editorState.folderName.isNotBlank()) {
                        Text(
                            text = "📁 ${editorState.folderName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberDark,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Pin toggle button
                IconButton(
                    onClick = { viewModel.togglePinInEditor() },
                    modifier = Modifier.testTag("editor_pin_btn")
                ) {
                    Icon(
                        imageVector = if (editorState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (editorState.isPinned) "Unpin" else "Pin",
                        tint = if (editorState.isPinned) AmberDark else TextCharcoalMuted
                    )
                }

                // Archive toggle button
                IconButton(
                    onClick = { viewModel.toggleArchiveInEditor() },
                    modifier = Modifier.testTag("editor_archive_btn")
                ) {
                    Icon(
                        imageVector = if (editorState.isArchived) Icons.Filled.Archive else Icons.Outlined.Archive,
                        contentDescription = if (editorState.isArchived) "Unarchive" else "Archive",
                        tint = if (editorState.isArchived) AmberDark else TextCharcoalMuted
                    )
                }

                // Delete / Trash button
                IconButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.testTag("editor_trash_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Move to trash",
                        tint = TextCharcoalMuted
                    )
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(visible = showColorPicker) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        ColorPickerRow(
                            selectedHex = editorState.colorHex,
                            onColorSelected = { colorHex ->
                                viewModel.updateColor(colorHex)
                            }
                        )
                    }
                }

                FormattingToolbar(
                    isChecklist = editorState.isChecklist,
                    showColorPicker = showColorPicker,
                    onBoldClick = {
                        val current = editorState.content
                        viewModel.updateContent("$current **bold**")
                    },
                    onItalicClick = {
                        val current = editorState.content
                        viewModel.updateContent("$current *italic*")
                    },
                    onHeadingClick = {
                        val current = editorState.content
                        viewModel.updateContent("$current\n# Heading\n")
                    },
                    onBulletClick = {
                        val current = editorState.content
                        viewModel.updateContent("$current\n• ")
                    },
                    onChecklistClick = {
                        viewModel.toggleChecklistMode()
                    },
                    onToggleColorPicker = {
                        showColorPicker = !showColorPicker
                    },
                    onFolderTagClick = {
                        showFolderTagDialog = true
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title Input
            BasicTextField(
                value = editorState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp)
                    .testTag("editor_title_input"),
                textStyle = TextStyle(
                    fontSize = (24 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCharcoal,
                    lineHeight = (30 * fontScale).sp
                ),
                cursorBrush = SolidColor(AmberDark),
                decorationBox = { innerTextField ->
                    if (editorState.title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = TextStyle(
                                fontSize = (24 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoalLight
                            )
                        )
                    }
                    innerTextField()
                }
            )

            // Tags display if present
            if (editorState.tags.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    editorState.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x1F000000)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = TextCharcoal
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Body: Checklist or Full-text
            if (editorState.isChecklist) {
                ChecklistEditor(
                    items = editorState.checklistItems,
                    fontSizeMultiplier = fontScale,
                    onItemChange = { id, text, isChecked ->
                        viewModel.updateChecklistItem(id, text, isChecked)
                    },
                    onItemDelete = { id ->
                        viewModel.removeChecklistItem(id)
                    },
                    onAddItem = {
                        viewModel.addChecklistItem()
                    }
                )
            } else {
                BasicTextField(
                    value = editorState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .testTag("editor_content_input"),
                    textStyle = TextStyle(
                        fontSize = (16 * fontScale).sp,
                        fontWeight = FontWeight.Normal,
                        color = TextCharcoal,
                        lineHeight = (24 * fontScale).sp
                    ),
                    cursorBrush = SolidColor(AmberDark),
                    decorationBox = { innerTextField ->
                        if (editorState.content.isEmpty()) {
                            Text(
                                text = "Start typing your note here...",
                                style = TextStyle(
                                    fontSize = (16 * fontScale).sp,
                                    color = TextCharcoalLight
                                )
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Move to Trash Confirmation
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Move to Trash?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("You can restore this note anytime from the Trash folder.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        if (editorState.noteId != 0L) {
                            viewModel.moveToTrash(editorState.noteId)
                        }
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_trash_btn")
                ) {
                    Text("Move to Trash")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Folder and Tag Assign Dialog
    if (showFolderTagDialog) {
        var tempFolderId by remember { mutableStateOf(editorState.folderId) }
        var tempFolderName by remember { mutableStateOf(editorState.folderName) }
        var tempTags by remember { mutableStateOf(editorState.tags) }

        AlertDialog(
            onDismissRequest = { showFolderTagDialog = false },
            title = { Text("Organize Note", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Assign Folder:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Folder choices
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempFolderId = null
                                    tempFolderName = ""
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "None (Unfiled)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (tempFolderId == null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (tempFolderId == null) AmberDark else TextCharcoal
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (tempFolderId == null) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AmberDark, modifier = Modifier.size(18.dp))
                            }
                        }

                        foldersWithCount.forEach { folder ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempFolderId = folder.id
                                        tempFolderName = folder.name
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📁 ${folder.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (tempFolderId == folder.id) FontWeight.Bold else FontWeight.Normal,
                                        color = if (tempFolderId == folder.id) AmberDark else TextCharcoal
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (tempFolderId == folder.id) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AmberDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tags (comma separated):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tempTags,
                        onValueChange = { tempTags = it },
                        placeholder = { Text("e.g. Work, Ideas, Recipe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setFolderInEditor(tempFolderId, tempFolderName)
                        viewModel.setTagsInEditor(tempTags)
                        showFolderTagDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = TextCharcoal)
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFolderTagDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = if (clean.length == 6) {
            "FF$clean".toLong(16)
        } else {
            clean.toLong(16)
        }
        Color(colorInt)
    } catch (e: Exception) {
        fallback
    }
}
