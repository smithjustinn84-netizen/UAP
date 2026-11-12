package com.openfile.uap.ui.timeline

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.data.TimelineEntry
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.ui.casefile.locatorSheets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimelineUiState(
  /** The one kind being shown, or null for every kind. */
  val only: TimelineKind?,
  val entries: List<TimelineEntry>,
  /** How many entries of each kind there are in the whole timeline, whatever the filter. */
  val kindCounts: Map<TimelineKind, Int>,
  /** Each decade that has an entry under the current filter, with the index of its first entry in [entries]. */
  val decadeStarts: List<DecadeStart>,
  /** The files the timeline links to, by id, so a linked entry can show the file's picture. */
  val files: Map<String, CaseRecord> = emptyMap(),
  /** Land for the locator thumbnails of linked files without a picture, by sheet id; a sheet is missing until read. */
  val outlines: Map<String, SheetOutline> = emptyMap(),
)

data class DecadeStart(val decade: Int, val firstIndex: Int)

class TimelineViewModel(
  private val archive: ArchiveRepository,
  outlines: MapOutlineRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val kindCounts = archive.timeline.groupingBy { it.kind }.eachCount()
  private val files = archive.timeline.mapNotNull { it.caseId }.distinct().mapNotNull { archive.case(it) }.associateBy { it.id }

  private val _uiState = MutableStateFlow(stateFor(restoredFilter()))
  val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

  init {
    locatorSheets(files.values.toList()).forEach { sheet ->
      viewModelScope.launch {
        val outline = outlines.outline(sheet)
        _uiState.update { it.copy(outlines = it.outlines + (sheet.id to outline)) }
      }
    }
  }

  fun onFilterChange(only: TimelineKind?) {
    savedStateHandle[ONLY_KEY] = only?.name
    _uiState.update { stateFor(only).copy(outlines = it.outlines) }
  }

  /** The filter is saved by name, so a saved state from an older build with a renamed kind falls back to all. */
  private fun restoredFilter(): TimelineKind? =
    savedStateHandle.get<String>(ONLY_KEY)?.let { name -> TimelineKind.entries.firstOrNull { it.name == name } }

  private fun stateFor(only: TimelineKind?): TimelineUiState {
    val entries = archive.timeline.filter { only == null || it.kind == only }
    return TimelineUiState(
      only = only,
      entries = entries,
      kindCounts = kindCounts,
      decadeStarts = decadeStarts(entries),
      files = files,
    )
  }

  private companion object {
    const val ONLY_KEY = "only"
  }
}

/** The timeline is in date order, so the first entry of each decade is where that decade starts. */
internal fun decadeStarts(entries: List<TimelineEntry>): List<DecadeStart> =
  entries
    .withIndex()
    .distinctBy { it.value.year / DECADE * DECADE }
    .map { DecadeStart(decade = it.value.year / DECADE * DECADE, firstIndex = it.index) }

private const val DECADE = 10
