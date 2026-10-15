package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QuickNoteSettings(
    val defaultColorHex: String = "#FFFFFF",
    val fontSize: String = "Medium", // "Small", "Medium", "Large"
    val isAutoSyncEnabled: Boolean = false,
    val lastBackupTimestamp: Long = 0L
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("quicknote_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        QuickNoteSettings(
            defaultColorHex = prefs.getString("default_color_hex", "#FFFFFF") ?: "#FFFFFF",
            fontSize = prefs.getString("font_size", "Medium") ?: "Medium",
            isAutoSyncEnabled = prefs.getBoolean("auto_sync", false),
            lastBackupTimestamp = prefs.getLong("last_backup", 0L)
        )
    )
    val settings: StateFlow<QuickNoteSettings> = _settings.asStateFlow()

    fun setDefaultColor(colorHex: String) {
        prefs.edit().putString("default_color_hex", colorHex).apply()
        _settings.value = _settings.value.copy(defaultColorHex = colorHex)
    }

    fun setFontSize(fontSize: String) {
        prefs.edit().putString("font_size", fontSize).apply()
        _settings.value = _settings.value.copy(fontSize = fontSize)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_sync", enabled).apply()
        _settings.value = _settings.value.copy(isAutoSyncEnabled = enabled)
    }

    fun updateBackupTimestamp() {
        val now = System.currentTimeMillis()
        prefs.edit().putLong("last_backup", now).apply()
        _settings.value = _settings.value.copy(lastBackupTimestamp = now)
    }
}
