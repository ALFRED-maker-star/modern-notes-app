package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.FolderWithCount
import com.example.data.local.NoteEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.model.ChecklistItem
import com.example.data.repository.NoteRepository
import com.example.data.repository.QuickNoteSettings
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class FilterType {
    ALL, PINNED, FOLDER, TAG
}

data class FilterSelection(
    val type: FilterType = FilterType.ALL,
    val value: String = ""
)

data class EditorState(
    val noteId: Long = 0,
    val title: String = "",
    val content: String = "",
    val colorHex: String = "#FFFFFF",
    val folderId: Long? = null,
    val folderName: String = "",
    val tags: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val saveStatus: String = "Saved", // "Saving...", "Saved", "Saved just now"
    val lastModified: Long = System.currentTimeMillis()
)

class QuickNoteViewModel(
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<QuickNoteSettings> = settingsRepository.settings

    val activeNotes: StateFlow<List<NoteEntity>> = noteRepository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashNotes: StateFlow<List<NoteEntity>> = noteRepository.trashNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foldersWithCount: StateFlow<List<FolderWithCount>> = noteRepository.foldersWithCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<RecentSearchEntity>> = noteRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter by Chip
    private val _selectedFilter = MutableStateFlow(FilterSelection(FilterType.ALL, ""))
    val selectedFilter: StateFlow<FilterSelection> = _selectedFilter.asStateFlow()

    // Available tags extracted dynamically from active notes
    val availableTags: StateFlow<List<String>> = activeNotes.combine(_selectedFilter) { notes, _ ->
        notes.flatMap { note ->
            note.tags.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Notes
    val filteredNotes: StateFlow<List<NoteEntity>> = combine(activeNotes, _selectedFilter) { notes, filter ->
        when (filter.type) {
            FilterType.ALL -> notes
            FilterType.PINNED -> notes.filter { it.isPinned }
            FilterType.FOLDER -> notes.filter { it.folderName.equals(filter.value, ignoreCase = true) }
            FilterType.TAG -> notes.filter { note ->
                note.tags.split(",").map { it.trim().lowercase() }.contains(filter.value.lowercase())
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query & Live Search results
    val searchQuery = MutableStateFlow("")
    val searchResults: StateFlow<List<NoteEntity>> = searchQuery
        .debounce(200)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(emptyList())
            } else {
                noteRepository.searchNotes(query.trim())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Editor State
    private val _editorState = MutableStateFlow(EditorState())
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()

    private var autoSaveJob: Job? = null

    fun setFilter(filter: FilterSelection) {
        _selectedFilter.value = filter
    }

    // Load note into editor
    fun loadNoteForEditing(noteId: Long) {
        viewModelScope.launch {
            if (noteId == 0L) {
                // Brand new note
                val defaultColor = settings.value.defaultColorHex
                _editorState.value = EditorState(
                    noteId = 0,
                    colorHex = defaultColor,
                    saveStatus = "New Note",
                    lastModified = System.currentTimeMillis()
                )
            } else {
                val note = noteRepository.getNoteByIdSync(noteId)
                if (note != null) {
                    val checklist = if (note.isChecklist) {
                        noteRepository.parseChecklist(note.checklistJson)
                    } else {
                        emptyList()
                    }
                    _editorState.value = EditorState(
                        noteId = note.id,
                        title = note.title,
                        content = note.content,
                        colorHex = note.colorHex,
                        folderId = note.folderId,
                        folderName = note.folderName,
                        tags = note.tags,
                        isPinned = note.isPinned,
                        isArchived = note.isArchived,
                        isChecklist = note.isChecklist,
                        checklistItems = checklist,
                        saveStatus = "Saved",
                        lastModified = note.updatedAt
                    )
                }
            }
        }
    }

    // Editor field updates
    fun updateTitle(newTitle: String) {
        _editorState.value = _editorState.value.copy(title = newTitle)
        triggerAutoSave()
    }

    fun updateContent(newContent: String) {
        _editorState.value = _editorState.value.copy(content = newContent)
        triggerAutoSave()
    }

    fun updateColor(newColorHex: String) {
        _editorState.value = _editorState.value.copy(colorHex = newColorHex)
        triggerAutoSave()
    }

    fun togglePinInEditor() {
        val newPinned = !_editorState.value.isPinned
        _editorState.value = _editorState.value.copy(isPinned = newPinned)
        triggerAutoSave()
    }

    fun toggleArchiveInEditor() {
        val newArchived = !_editorState.value.isArchived
        _editorState.value = _editorState.value.copy(isArchived = newArchived)
        triggerAutoSave()
    }

    fun setFolderInEditor(folderId: Long?, folderName: String) {
        _editorState.value = _editorState.value.copy(folderId = folderId, folderName = folderName)
        triggerAutoSave()
    }

    fun setTagsInEditor(tags: String) {
        _editorState.value = _editorState.value.copy(tags = tags)
        triggerAutoSave()
    }

    fun toggleChecklistMode() {
        val currentState = _editorState.value
        val newMode = !currentState.isChecklist
        val checklistItems = if (newMode && currentState.checklistItems.isEmpty()) {
            // Convert lines of text to checklist
            currentState.content.lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    val cleanText = line.removePrefix("•").removePrefix("-").trim()
                    ChecklistItem(text = cleanText, isChecked = false)
                }.ifEmpty {
                    listOf(ChecklistItem(text = "", isChecked = false))
                }
        } else if (!newMode && currentState.checklistItems.isNotEmpty()) {
            // Convert checklist back to text
            emptyList()
        } else {
            currentState.checklistItems
        }

        val content = if (!newMode && currentState.checklistItems.isNotEmpty()) {
            currentState.checklistItems.joinToString("\n") { (if (it.isChecked) "[✓] " else "• ") + it.text }
        } else {
            currentState.content
        }

        _editorState.value = currentState.copy(
            isChecklist = newMode,
            checklistItems = checklistItems,
            content = content
        )
        triggerAutoSave()
    }

    fun addChecklistItem(text: String = "") {
        val items = _editorState.value.checklistItems.toMutableList()
        items.add(ChecklistItem(id = UUID.randomUUID().toString(), text = text, isChecked = false))
        _editorState.value = _editorState.value.copy(checklistItems = items)
        triggerAutoSave()
    }

    fun updateChecklistItem(id: String, text: String, isChecked: Boolean) {
        val items = _editorState.value.checklistItems.map {
            if (it.id == id) it.copy(text = text, isChecked = isChecked) else it
        }
        _editorState.value = _editorState.value.copy(checklistItems = items)
        triggerAutoSave()
    }

    fun removeChecklistItem(id: String) {
        val items = _editorState.value.checklistItems.filterNot { it.id == id }
        _editorState.value = _editorState.value.copy(checklistItems = items)
        triggerAutoSave()
    }

    // Quick toggle check from card
    fun toggleChecklistFromCard(note: NoteEntity, itemId: String) {
        viewModelScope.launch {
            val list = noteRepository.parseChecklist(note.checklistJson)
            val updated = list.map {
                if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it
            }
            val json = noteRepository.serializeChecklist(updated)
            noteRepository.updateNote(note.copy(checklistJson = json, updatedAt = System.currentTimeMillis()))
        }
    }

    private fun triggerAutoSave() {
        _editorState.value = _editorState.value.copy(saveStatus = "Saving...")
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(500)
            persistCurrentEditorState()
        }
    }

    fun flushSave() {
        autoSaveJob?.cancel()
        viewModelScope.launch {
            persistCurrentEditorState()
        }
    }

    private suspend fun persistCurrentEditorState() {
        val state = _editorState.value
        // Only save if there is content or title
        if (state.title.isBlank() && state.content.isBlank() && state.checklistItems.all { it.text.isBlank() }) {
            _editorState.value = state.copy(saveStatus = "Saved")
            return
        }

        val checklistJson = if (state.isChecklist) {
            noteRepository.serializeChecklist(state.checklistItems)
        } else ""

        val now = System.currentTimeMillis()
        val entity = NoteEntity(
            id = state.noteId,
            title = state.title.trim(),
            content = state.content.trim(),
            colorHex = state.colorHex,
            folderId = state.folderId,
            folderName = state.folderName,
            tags = state.tags,
            isPinned = state.isPinned,
            isArchived = state.isArchived,
            isDeleted = false,
            isChecklist = state.isChecklist,
            checklistJson = checklistJson,
            createdAt = if (state.noteId == 0L) now else state.lastModified,
            updatedAt = now
        )

        val savedId = if (state.noteId == 0L) {
            noteRepository.saveNote(entity)
        } else {
            noteRepository.updateNote(entity)
            state.noteId
        }

        _editorState.value = state.copy(
            noteId = savedId,
            saveStatus = "Saved just now",
            lastModified = now
        )
    }

    // Note actions from cards or lists
    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.togglePin(note.id, note.isPinned)
        }
    }

    fun toggleArchive(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.toggleArchive(note.id, note.isArchived)
        }
    }

    fun moveToTrash(noteId: Long) {
        viewModelScope.launch {
            noteRepository.moveToTrash(noteId)
        }
    }

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            noteRepository.restoreFromTrash(noteId)
        }
    }

    fun deletePermanently(noteId: Long) {
        viewModelScope.launch {
            noteRepository.deletePermanently(noteId)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            noteRepository.emptyTrash()
        }
    }

    // Folder actions
    fun createFolder(name: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                noteRepository.createFolder(name.trim(), colorHex, iconName)
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            noteRepository.deleteFolder(folderId)
        }
    }

    // Search actions
    fun performSearch(query: String) {
        searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                noteRepository.recordSearch(query)
            }
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            noteRepository.removeRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            noteRepository.clearRecentSearches()
        }
    }

    // Settings actions
    fun setDefaultNoteColor(colorHex: String) {
        settingsRepository.setDefaultColor(colorHex)
    }

    fun setFontSize(fontSize: String) {
        settingsRepository.setFontSize(fontSize)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        settingsRepository.setAutoSyncEnabled(enabled)
    }

    fun triggerBackup() {
        settingsRepository.updateBackupTimestamp()
    }
}

class QuickNoteViewModelFactory(
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuickNoteViewModel(noteRepository, settingsRepository) as T
    }
}
