package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistItem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextCharcoalLight
import com.example.ui.theme.TextCharcoalMuted

@Composable
fun ChecklistEditor(
    items: List<ChecklistItem>,
    onItemChange: (id: String, text: String, isChecked: Boolean) -> Unit,
    onItemDelete: (id: String) -> Unit,
    onAddItem: () -> Unit,
    fontSizeMultiplier: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val uncheckedItems = items.filter { !it.isChecked }
        val checkedItems = items.filter { it.isChecked }

        // Unchecked items
        uncheckedItems.forEach { item ->
            ChecklistItemRow(
                item = item,
                fontSizeMultiplier = fontSizeMultiplier,
                onCheckedChange = { isChecked -> onItemChange(item.id, item.text, isChecked) },
                onTextChange = { text -> onItemChange(item.id, text, item.isChecked) },
                onDelete = { onItemDelete(item.id) },
                onImeDone = onAddItem
            )
        }

        // Add new item quick prompt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddItem() }
                .padding(vertical = 10.dp, horizontal = 4.dp)
                .testTag("add_checklist_item_row"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add list item",
                tint = AmberDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "List item",
                style = TextStyle(
                    fontSize = (16 * fontSizeMultiplier).sp,
                    color = TextCharcoalMuted
                )
            )
        }

        // Checked / Completed section
        if (checkedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "${checkedItems.size} Completed items",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextCharcoalMuted
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            checkedItems.forEach { item ->
                ChecklistItemRow(
                    item = item,
                    fontSizeMultiplier = fontSizeMultiplier,
                    onCheckedChange = { isChecked -> onItemChange(item.id, item.text, isChecked) },
                    onTextChange = { text -> onItemChange(item.id, text, item.isChecked) },
                    onDelete = { onItemDelete(item.id) },
                    onImeDone = onAddItem
                )
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    fontSizeMultiplier: Float,
    onCheckedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    onImeDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag("checklist_item_${item.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AmberDark,
                uncheckedColor = TextCharcoalMuted,
                checkmarkColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.testTag("checkbox_${item.id}")
        )

        BasicTextField(
            value = item.text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .testTag("checklist_text_${item.id}"),
            textStyle = TextStyle(
                fontSize = (16 * fontSizeMultiplier).sp,
                lineHeight = (22 * fontSizeMultiplier).sp,
                color = if (item.isChecked) TextCharcoalLight else TextCharcoal,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
            ),
            cursorBrush = SolidColor(AmberDark),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeDone() }
            ),
            decorationBox = { innerTextField ->
                if (item.text.isEmpty()) {
                    Text(
                        text = "Item",
                        style = TextStyle(
                            fontSize = (16 * fontSizeMultiplier).sp,
                            color = TextCharcoalLight
                        )
                    )
                }
                innerTextField()
            }
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(36.dp)
                .testTag("delete_item_${item.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove item",
                tint = TextCharcoalLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
