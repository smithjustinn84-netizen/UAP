package com.openfile.uap.ui.timeline

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.TimelineEntry
import com.openfile.uap.data.TimelineKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TimelineUiState(
  /** The one kind being shown, or null for every kind. */
  val only: TimelineKind?,
  val entries: List<TimelineEntry>,
  /** How many entries of each kind there are in the whole timeline, whatever the filter. */
  val kindCounts: Map<TimelineKind, Int>,
)

class TimelineViewModel(private val archive: ArchiveRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
  private val kindCounts = archive.timeline.groupingBy { it.kind }.eachCount()

  private val _uiState = MutableStateFlow(stateFor(restoredFilter()))
  val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

  fun onFilterChange(only: TimelineKind?) {
    savedStateHandle[ONLY_KEY] = only?.name
    _uiState.value = stateFor(only)
  }

  /** The filter is saved by name, so a saved state from an older build with a renamed kind falls back to all. */
  private fun restoredFilter(): TimelineKind? =
    savedStateHandle.get<String>(ONLY_KEY)?.let { name -> TimelineKind.entries.firstOrNull { it.name == name } }

  private fun stateFor(only: TimelineKind?) =
    TimelineUiState(only = only, entries = archive.timeline.filter { only == null || it.kind == only }, kindCounts = kindCounts)

  private companion object {
    const val ONLY_KEY = "only"
  }
}
