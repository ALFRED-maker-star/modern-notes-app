package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AvailableNoteColors
import com.example.ui.theme.TextCharcoal

@Composable
fun ColorPickerRow(
    selectedHex: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.testTag("color_picker_row"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        items(AvailableNoteColors, key = { it.id }) { pastel ->
            val isSelected = pastel.hex.equals(selectedHex, ignoreCase = true)
            val animatedBorderColor by animateColorAsState(
                targetValue = if (isSelected) TextCharcoal else pastel.borderColor,
                label = "color_border"
            )

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(pastel.color)
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = animatedBorderColor,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(pastel.hex) }
                    .testTag("color_option_${pastel.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected color ${pastel.name}",
                        tint = TextCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
