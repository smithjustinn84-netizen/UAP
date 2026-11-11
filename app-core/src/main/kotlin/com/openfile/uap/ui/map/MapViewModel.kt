package com.openfile.uap.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.SheetOutline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A case placed on the map, numbered in archive order so pins and list rows match. */
data class PlottedCase(val number: Int, val record: CaseRecord)

/** One sheet and its pins. [outline] is empty until the land has been read, and the sheet draws without it until then. */
data class MapSheetUiState(val sheet: MapSheet, val pins: List<PlottedCase>, val outline: SheetOutline = emptyList())

data class MapUiState(val sheets: List<MapSheetUiState>, val plotted: List<PlottedCase>, val selectedId: String?)

class MapViewModel(
  archive: ArchiveRepository,
  private val outlines: MapOutlineRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val _uiState = MutableStateFlow(initialState(archive))
  val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

  init {
    // One load per sheet, so each sheet gets its land as soon as it is read instead of all waiting on the slowest.
    _uiState.value.sheets.forEach { loadOutline(it.sheet) }
  }

  /** Tapping the selected pin again clears the selection. */
  fun onPinSelect(caseId: String) {
    val next = if (_uiState.value.selectedId == caseId) null else caseId
    savedStateHandle[SELECTED_KEY] = next
    _uiState.update { it.copy(selectedId = next) }
  }

  private fun loadOutline(sheet: MapSheet) {
    viewModelScope.launch {
      val outline = outlines.outline(sheet)
      // Only the loaded sheet is copied: the others keep their instances, so their drawings are not rebuilt.
      _uiState.update { state -> state.copy(sheets = state.sheets.map { if (it.sheet == sheet) it.copy(outline = outline) else it }) }
    }
  }

  private fun initialState(archive: ArchiveRepository): MapUiState {
    val plotted = archive.cases.mapIndexed { i, c -> PlottedCase(i + 1, c) }
    val sheets = MapSheets.all.map { sheet -> MapSheetUiState(sheet, plotted.filter { MapSheets.sheetFor(it.record.point) == sheet }) }
    return MapUiState(sheets = sheets, plotted = plotted, selectedId = savedStateHandle[SELECTED_KEY])
  }

  private companion object {
    const val SELECTED_KEY = "selectedId"
  }
}
