package com.openfile.uap.ui.files

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.ui.casefile.locatorSheets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilesUiState(
  val decades: List<Int>,
  /** The decade being shown, or null for all of them. */
  val selectedDecade: Int?,
  val results: List<CaseRecord>,
  val threadCount: Int,
  /** Land for the locator thumbnails of files without a picture, by sheet id; a sheet is missing until read. */
  val outlines: Map<String, SheetOutline> = emptyMap(),
)

class FilesViewModel(
  private val archive: ArchiveRepository,
  outlines: MapOutlineRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  /**
   * The search text, kept as Compose state rather than in [uiState] so the text field reads back what it wrote in the
   * same frame. Routing keystrokes through a flow puts the field a frame behind the keyboard and makes the cursor jump.
   */
  var query: String by mutableStateOf(savedStateHandle[QUERY_KEY] ?: "")
    private set

  private val _uiState = MutableStateFlow(stateFor(query, savedStateHandle[DECADE_KEY]))
  val uiState: StateFlow<FilesUiState> = _uiState.asStateFlow()

  init {
    locatorSheets(archive.cases).forEach { sheet ->
      viewModelScope.launch {
        val outline = outlines.outline(sheet)
        _uiState.update { it.copy(outlines = it.outlines + (sheet.id to outline)) }
      }
    }
  }

  fun onQueryChange(newQuery: String) {
    query = newQuery
    savedStateHandle[QUERY_KEY] = newQuery
    _uiState.update { stateFor(newQuery, it.selectedDecade).copy(outlines = it.outlines) }
  }

  fun onDecadeSelect(decade: Int?) {
    savedStateHandle[DECADE_KEY] = decade
    _uiState.update { stateFor(query, decade).copy(outlines = it.outlines) }
  }

  private fun stateFor(query: String, decade: Int?) =
    FilesUiState(
      decades = archive.decades,
      selectedDecade = decade,
      results = archive.search(query, decade),
      threadCount = archive.threads.size,
    )

  private companion object {
    const val QUERY_KEY = "query"
    const val DECADE_KEY = "decade"
  }
}
