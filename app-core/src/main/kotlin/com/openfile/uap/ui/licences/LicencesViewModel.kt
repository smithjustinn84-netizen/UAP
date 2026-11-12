package com.openfile.uap.ui.licences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfile.uap.data.Licence
import com.openfile.uap.data.Notice
import com.openfile.uap.data.NoticesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** The works under one licence. [fullText] is null while it loads and for licences that are only linked. */
data class LicenceGroup(val licence: Licence, val notices: List<Notice>, val fullText: String? = null)

data class LicencesUiState(val groups: List<LicenceGroup>)

class LicencesViewModel(private val notices: NoticesRepository) : ViewModel() {
  // Groups follow the Licence enum order, so the list reads the same however the notices are arranged.
  private val _uiState =
    MutableStateFlow(
      LicencesUiState(
        notices.notices.groupBy { it.licence }.toSortedMap().map { (licence, items) -> LicenceGroup(licence, items) },
      ),
    )
  val uiState: StateFlow<LicencesUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      _uiState.value.groups.forEach { group ->
        val text = notices.fullText(group.licence) ?: return@forEach
        _uiState.update { state ->
          state.copy(groups = state.groups.map { if (it.licence == group.licence) it.copy(fullText = text) else it })
        }
      }
    }
  }
}
