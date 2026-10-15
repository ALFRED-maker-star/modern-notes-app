package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette per user brief
val AmberPrimary = Color(0xFFFBBF24)
val AmberDark = Color(0xFFD97706)
val AmberDeep = Color(0xFFB45309)
val AmberContainer = Color(0xFFFEF3C7)
val OnAmberContainer = Color(0xFF78350F)

val WarmBackground = Color(0xFFFFFDF7)
val WarmSurface = Color(0xFFFFFFFF)
val WarmSurfaceVariant = Color(0xFFF8F5EE)

val TextCharcoal = Color(0xFF1E293B)
val TextCharcoalMuted = Color(0xFF64748B)
val TextCharcoalLight = Color(0xFF94A3B8)
val DividerBorder = Color(0xFFE2E8F0)

// Pastel Note Colors
val NoteMint = Color(0xFFD1FAE5)
val NoteLavender = Color(0xFFEDE9FE)
val NotePeach = Color(0xFFFFEDD5)
val NoteSkyBlue = Color(0xFFE0F2FE)
val NoteCream = Color(0xFFFEF9C3)
val NoteRose = Color(0xFFFFE4E6)
val NoteDefault = Color(0xFFFFFFFF)

data class PastelNoteColor(
    val id: String,
    val name: String,
    val color: Color,
    val hex: String,
    val borderColor: Color
)

val AvailableNoteColors = listOf(
    PastelNoteColor("default", "Classic", NoteDefault, "#FFFFFF", Color(0xFFE2E8F0)),
    PastelNoteColor("mint", "Mint", NoteMint, "#D1FAE5", Color(0xFFA7F3D0)),
    PastelNoteColor("lavender", "Lavender", NoteLavender, "#EDE9FE", Color(0xFFDDD6FE)),
    PastelNoteColor("peach", "Peach", NotePeach, "#FFEDD5", Color(0xFFFED7AA)),
    PastelNoteColor("sky_blue", "Sky Blue", NoteSkyBlue, "#E0F2FE", Color(0xFFBAE6FD)),
    PastelNoteColor("cream", "Cream", NoteCream, "#FEF9C3", Color(0xFFFDE047)),
    PastelNoteColor("rose", "Rose", NoteRose, "#FFE4E6", Color(0xFFFECDD3))
)
