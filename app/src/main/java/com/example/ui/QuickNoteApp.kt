package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.QuickNoteBottomBar
import com.example.ui.components.QuickNoteDestination
import com.example.ui.screens.FoldersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NoteEditorScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TrashScreen
import com.example.ui.theme.WarmBackground

sealed class ScreenState {
    object Main : ScreenState()
    data class Editor(val noteId: Long, val isChecklist: Boolean = false) : ScreenState()
    object Search : ScreenState()
}

@Composable
fun QuickNoteApp(
    viewModel: QuickNoteViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(QuickNoteDestination.NOTES) }
    var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Main) }

    when (val state = screenState) {
        is ScreenState.Editor -> {
            NoteEditorScreen(
                noteId = state.noteId,
                initialChecklistMode = state.isChecklist,
                viewModel = viewModel,
                onBack = { screenState = ScreenState.Main }
            )
        }

        is ScreenState.Search -> {
            BackHandler {
                screenState = ScreenState.Main
            }
            SearchScreen(
                viewModel = viewModel,
                onNoteClick = { noteId ->
                    screenState = ScreenState.Editor(noteId = noteId)
                },
                onBack = { screenState = ScreenState.Main },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        }

        is ScreenState.Main -> {
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .background(WarmBackground)
                    .testTag("quicknote_main_scaffold"),
                bottomBar = {
                    QuickNoteBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            currentDestination = destination
                        }
                    )
                },
                contentWindowInsets = WindowInsets.statusBars
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentDestination) {
                        QuickNoteDestination.NOTES -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNoteClick = { noteId ->
                                    screenState = ScreenState.Editor(noteId = noteId)
                                },
                                onNewNoteClick = { isChecklist ->
                                    screenState = ScreenState.Editor(noteId = 0L, isChecklist = isChecklist)
                                },
                                onSearchClick = {
                                    screenState = ScreenState.Search
                                }
                            )
                        }

                        QuickNoteDestination.SEARCH -> {
                            SearchScreen(
                                viewModel = viewModel,
                                onNoteClick = { noteId ->
                                    screenState = ScreenState.Editor(noteId = noteId)
                                },
                                onBack = {
                                    currentDestination = QuickNoteDestination.NOTES
                                }
                            )
                        }

                        QuickNoteDestination.FOLDERS -> {
                            FoldersScreen(
                                viewModel = viewModel,
                                onSelectFolder = { folderName ->
                                    if (folderName.isNotBlank()) {
                                        viewModel.setFilter(FilterSelection(FilterType.FOLDER, folderName))
                                    }
                                    currentDestination = QuickNoteDestination.NOTES
                                }
                            )
                        }

                        QuickNoteDestination.TRASH -> {
                            TrashScreen(
                                viewModel = viewModel
                            )
                        }

                        QuickNoteDestination.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
