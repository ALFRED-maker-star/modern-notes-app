package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [NoteEntity::class, FolderEntity::class, RecentSearchEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuickNoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        @Volatile
        private var INSTANCE: QuickNoteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuickNoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickNoteDatabase::class.java,
                    "quicknote_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.folderDao(), database.noteDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(folderDao: FolderDao, noteDao: NoteDao) {
            val personalFolderId = folderDao.insertFolder(
                FolderEntity(name = "Personal", colorHex = "#D1FAE5", iconName = "person")
            )
            val workFolderId = folderDao.insertFolder(
                FolderEntity(name = "Work", colorHex = "#BAE6FD", iconName = "work")
            )
            val ideasFolderId = folderDao.insertFolder(
                FolderEntity(name = "Ideas", colorHex = "#FEF9C3", iconName = "lightbulb")
            )
            val travelFolderId = folderDao.insertFolder(
                FolderEntity(name = "Travel", colorHex = "#FFEDD5", iconName = "flight")
            )

            val now = System.currentTimeMillis()

            // 1. Welcome Note (Pinned, Warm Cream)
            noteDao.insertNote(
                NoteEntity(
                    title = "Welcome to QuickNote! ✨",
                    content = "QuickNote is your modern, warm notebook inspired by Apple Notes and Google Keep.\n\n• Staggered masonry card layout\n• Pastel color customization\n• Rich formatting & interactive checklists\n• Pin your most important thoughts\n• Organize with folders & tags\n• Trash with quick restore or clean-up",
                    colorHex = "#FEF9C3",
                    folderId = ideasFolderId,
                    folderName = "Ideas",
                    tags = "Guide, Welcome",
                    isPinned = true,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = false,
                    createdAt = now - 1000 * 60 * 30,
                    updatedAt = now - 1000 * 60 * 30
                )
            )

            // 2. Checklist Note (Mint)
            val tripChecklistJson = """
                [
                    {"id": "1", "text": "Pack passport and boarding passes", "isChecked": true},
                    {"id": "2", "text": "Noise-cancelling headphones & charger", "isChecked": true},
                    {"id": "3", "text": "Light rain jacket & walking sneakers", "isChecked": false},
                    {"id": "4", "text": "Confirm hotel check-in time", "isChecked": false},
                    {"id": "5", "text": "Download offline city maps", "isChecked": false}
                ]
            """.trimIndent()

            noteDao.insertNote(
                NoteEntity(
                    title = "Weekend Trip Packing List ✈️",
                    content = "Things to double check before departure on Friday morning.",
                    colorHex = "#D1FAE5",
                    folderId = travelFolderId,
                    folderName = "Travel",
                    tags = "Travel, Checklist",
                    isPinned = true,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = true,
                    checklistJson = tripChecklistJson,
                    createdAt = now - 1000 * 60 * 60 * 2,
                    updatedAt = now - 1000 * 60 * 60 * 2
                )
            )

            // 3. Project Brainstorm (Sky Blue)
            noteDao.insertNote(
                NoteEntity(
                    title = "Product Redesign Sprint 🚀",
                    content = "Key priorities for Q4:\n1. Mobile-first ergonomics with fluid animations\n2. Instant search with keyword indexing\n3. One-tap color switching for visual categories\n4. Zero latency local-first responsiveness",
                    colorHex = "#E0F2FE",
                    folderId = workFolderId,
                    folderName = "Work",
                    tags = "Sprint, Strategy",
                    isPinned = false,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = false,
                    createdAt = now - 1000 * 60 * 60 * 5,
                    updatedAt = now - 1000 * 60 * 60 * 5
                )
            )

            // 4. Recipe Ideas (Peach)
            noteDao.insertNote(
                NoteEntity(
                    title = "Creamy Garlic Tuscan Pasta 🍝",
                    content = "Ingredients:\n• 8 oz fettuccine\n• 2 tbsp extra virgin olive oil\n• 4 cloves minced garlic\n• 1 cup heavy cream\n• 1/2 cup grated parmesan\n• Baby spinach & sun-dried tomatoes",
                    colorHex = "#FFEDD5",
                    folderId = personalFolderId,
                    folderName = "Personal",
                    tags = "Recipes, Dinner",
                    isPinned = false,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = false,
                    createdAt = now - 1000 * 60 * 60 * 24,
                    updatedAt = now - 1000 * 60 * 60 * 24
                )
            )

            // 5. Books to read (Lavender)
            val readingChecklistJson = """
                [
                    {"id": "b1", "text": "Atomic Habits - James Clear", "isChecked": true},
                    {"id": "b2", "text": "Design of Everyday Things - Don Norman", "isChecked": true},
                    {"id": "b3", "text": "Klara and the Sun - Kazuo Ishiguro", "isChecked": false},
                    {"id": "b4", "text": "Deep Work - Cal Newport", "isChecked": false}
                ]
            """.trimIndent()

            noteDao.insertNote(
                NoteEntity(
                    title = "Reading List 2026 📚",
                    content = "Goal: 20 books this year across design, fiction, and philosophy.",
                    colorHex = "#EDE9FE",
                    folderId = personalFolderId,
                    folderName = "Personal",
                    tags = "Books, Growth",
                    isPinned = false,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = true,
                    checklistJson = readingChecklistJson,
                    createdAt = now - 1000 * 60 * 60 * 36,
                    updatedAt = now - 1000 * 60 * 60 * 36
                )
            )

            // 6. Quick Reminder (Rose)
            noteDao.insertNote(
                NoteEntity(
                    title = "Plant watering schedule 🌿",
                    content = "Mon & Thu: Monstera and Fern\nEvery 2 weeks: Succulents and Snake Plant",
                    colorHex = "#FFE4E6",
                    folderId = personalFolderId,
                    folderName = "Personal",
                    tags = "Home",
                    isPinned = false,
                    isArchived = false,
                    isDeleted = false,
                    isChecklist = false,
                    createdAt = now - 1000 * 60 * 60 * 48,
                    updatedAt = now - 1000 * 60 * 60 * 48
                )
            )
        }
    }
}
