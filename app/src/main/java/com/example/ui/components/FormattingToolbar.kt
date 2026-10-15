package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.TextCharcoal

@Composable
fun FormattingToolbar(
    isChecklist: Boolean,
    showColorPicker: Boolean,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onHeadingClick: () -> Unit,
    onBulletClick: () -> Unit,
    onChecklistClick: () -> Unit,
    onToggleColorPicker: () -> Unit,
    onFolderTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("formatting_toolbar"),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bold
            IconButton(
                onClick = onBoldClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("format_bold_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FormatBold,
                    contentDescription = "Format bold",
                    tint = TextCharcoal
                )
            }

            // Italic
            IconButton(
                onClick = onItalicClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("format_italic_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FormatItalic,
                    contentDescription = "Format italic",
                    tint = TextCharcoal
                )
            }

            // Heading H1
            IconButton(
                onClick = onHeadingClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("format_heading_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = "Heading",
                    tint = TextCharcoal
                )
            }

            // Bullet List
            IconButton(
                onClick = onBulletClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("format_bullet_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListBulleted,
                    contentDescription = "Bullet list",
                    tint = TextCharcoal
                )
            }

            // Checklist
            IconButton(
                onClick = onChecklistClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isChecklist) AmberPrimary.copy(alpha = 0.35f) else Color.Transparent)
                    .testTag("format_checklist_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Checklist,
                    contentDescription = "Checklist mode",
                    tint = if (isChecklist) MaterialTheme.colorScheme.primary else TextCharcoal
                )
            }

            // Color Palette
            IconButton(
                onClick = onToggleColorPicker,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (showColorPicker) AmberPrimary.copy(alpha = 0.35f) else Color.Transparent)
                    .testTag("format_color_picker_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = "Change background color",
                    tint = if (showColorPicker) MaterialTheme.colorScheme.primary else TextCharcoal
                )
            }

            // Tags & Folder
            IconButton(
                onClick = onFolderTagClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("format_folder_tag_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Assign folder or tags",
                    tint = TextCharcoal
                )
            }
        }
    }
}
