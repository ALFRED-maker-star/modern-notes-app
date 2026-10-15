package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.QuickNoteDatabase
import com.example.data.repository.NoteRepository
import com.example.data.repository.SettingsRepository
import com.example.ui.QuickNoteApp
import com.example.ui.QuickNoteViewModel
import com.example.ui.QuickNoteViewModelFactory
import com.example.ui.theme.QuickNoteTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: QuickNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = QuickNoteDatabase.getDatabase(applicationContext, lifecycleScope)
        val noteRepository = NoteRepository(
            noteDao = database.noteDao(),
            folderDao = database.folderDao(),
            recentSearchDao = database.recentSearchDao()
        )
        val settingsRepository = SettingsRepository(applicationContext)

        val factory = QuickNoteViewModelFactory(noteRepository, settingsRepository)
        viewModel = ViewModelProvider(this, factory)[QuickNoteViewModel::class.java]

        setContent {
            QuickNoteTheme {
                QuickNoteApp(viewModel = viewModel)
            }
        }
    }
}
