package com.openfile.uap.ui.casefile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.SheetOutline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CaseFileUiState {
  val caseId: String

  data class Found(val record: CaseRecord) : CaseFileUiState {
    override val caseId: String
      get() = record.id
  }

  /** The id came from a link or a restored back stack and is no longer in the archive. */
  data class NotFound(override val caseId: String) : CaseFileUiState
}

/**
 * Holds one file, looked up once. The section being read stays in the screen: it is UI element state, kept with
 * rememberSaveable, and not something the file itself knows about.
 */
class CaseFileViewModel(archive: ArchiveRepository, outlines: MapOutlineRepository, caseId: String) : ViewModel() {
  val uiState: CaseFileUiState = archive.case(caseId)?.let { CaseFileUiState.Found(it) } ?: CaseFileUiState.NotFound(caseId)

  private val _heroOutline = MutableStateFlow<SheetOutline>(emptyList())

  /** Land for the locator map that heads a file with no picture; empty until read, and for files with a picture. */
  val heroOutline: StateFlow<SheetOutline> = _heroOutline.asStateFlow()

  init {
    val record = (uiState as? CaseFileUiState.Found)?.record
    val sheet = record?.takeIf { it.detail?.hero == null }?.let { MapSheets.sheetFor(it.point) }
    if (sheet != null) viewModelScope.launch { _heroOutline.value = outlines.outline(sheet) }
  }
}
