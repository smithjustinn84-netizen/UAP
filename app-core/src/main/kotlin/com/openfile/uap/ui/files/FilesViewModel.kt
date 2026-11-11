package com.openfile.uap.ui.files

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FilesUiState(
  val decades: List<Int>,
  /** The decade being shown, or null for all of them. */
  val selectedDecade: Int?,
  val results: List<CaseRecord>,
  val threadCount: Int,
)

class FilesViewModel(private val archive: ArchiveRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {
  /**
   * The search text, kept as Compose state rather than in [uiState] so the text field reads back what it wrote in the
   * same frame. Routing keystrokes through a flow puts the field a frame behind the keyboard and makes the cursor jump.
   */
  var query: String by mutableStateOf(savedStateHandle[QUERY_KEY] ?: "")
    private set

  private val _uiState = MutableStateFlow(stateFor(query, savedStateHandle[DECADE_KEY]))
  val uiState: StateFlow<FilesUiState> = _uiState.asStateFlow()

  fun onQueryChange(newQuery: String) {
    query = newQuery
    savedStateHandle[QUERY_KEY] = newQuery
    _uiState.value = stateFor(newQuery, _uiState.value.selectedDecade)
  }

  fun onDecadeSelect(decade: Int?) {
    savedStateHandle[DECADE_KEY] = decade
    _uiState.value = stateFor(query, decade)
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
