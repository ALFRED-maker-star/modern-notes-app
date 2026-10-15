package com.example.data.repository

import com.example.data.local.FolderDao
import com.example.data.local.FolderEntity
import com.example.data.local.FolderWithCount
import com.example.data.local.NoteDao
import com.example.data.local.NoteEntity
import com.example.data.local.RecentSearchDao
import com.example.data.local.RecentSearchEntity
import com.example.data.model.ChecklistItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val folderDao: FolderDao,
    private val recentSearchDao: RecentSearchDao
) {
    private val moshi = Moshi.Builder().build()
    private val checklistListType = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
    private val checklistAdapter = moshi.adapter<List<ChecklistItem>>(checklistListType)

    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val trashNotes: Flow<List<NoteEntity>> = noteDao.getTrashNotes()
    val archivedNotes: Flow<List<NoteEntity>> = noteDao.getArchivedNotes()
    val allFolders: Flow<List<FolderEntity>> = folderDao.getAllFolders()
    val foldersWithCount: Flow<List<FolderWithCount>> = folderDao.getFoldersWithCount()
    val recentSearches: Flow<List<RecentSearchEntity>> = recentSearchDao.getRecentSearches()
    val totalNotesCount: Flow<Int> = noteDao.getTotalNotesCount()
    val trashCount: Flow<Int> = noteDao.getTrashCount()

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdSync(id: Long): NoteEntity? = noteDao.getNoteByIdSync(id)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun saveNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun moveToTrash(id: Long) = noteDao.moveToTrash(id)

    suspend fun restoreFromTrash(id: Long) = noteDao.restoreFromTrash(id)

    suspend fun togglePin(id: Long, currentPinned: Boolean) = noteDao.setPinned(id, !currentPinned)

    suspend fun toggleArchive(id: Long, currentArchived: Boolean) = noteDao.setArchived(id, !currentArchived)

    suspend fun deletePermanently(id: Long) = noteDao.deletePermanently(id)

    suspend fun emptyTrash() = noteDao.emptyTrash()

    // Folder actions
    suspend fun createFolder(name: String, colorHex: String, iconName: String): Long {
        return folderDao.insertFolder(FolderEntity(name = name, colorHex = colorHex, iconName = iconName))
    }

    suspend fun deleteFolder(folderId: Long) {
        folderDao.unassignNotesFromFolder(folderId)
        folderDao.deleteFolder(folderId)
    }

    // Search actions
    suspend fun recordSearch(query: String) {
        if (query.isNotBlank()) {
            recentSearchDao.deleteSearchByQuery(query.trim())
            recentSearchDao.insertSearch(RecentSearchEntity(query = query.trim()))
        }
    }

    suspend fun clearRecentSearches() = recentSearchDao.clearRecentSearches()

    suspend fun removeRecentSearch(query: String) = recentSearchDao.deleteSearchByQuery(query)

    // Checklist JSON helpers
    fun parseChecklist(json: String): List<ChecklistItem> {
        return if (json.isBlank()) {
            emptyList()
        } else {
            try {
                checklistAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun serializeChecklist(items: List<ChecklistItem>): String {
        return try {
            checklistAdapter.toJson(items)
        } catch (e: Exception) {
            "[]"
        }
    }
}
