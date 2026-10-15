package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class FolderWithCount(
    val id: Long,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val noteCount: Int
)

@Dao
interface FolderDao {

    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("""
        SELECT f.id, f.name, f.colorHex, f.iconName, 
               (SELECT COUNT(*) FROM notes n WHERE n.folderId = f.id AND n.isDeleted = 0) AS noteCount
        FROM folders f
        ORDER BY f.name ASC
    """)
    fun getFoldersWithCount(): Flow<List<FolderWithCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolder(id: Long)

    @Query("UPDATE notes SET folderId = NULL, folderName = '' WHERE folderId = :folderId")
    suspend fun unassignNotesFromFolder(folderId: Long)
}
