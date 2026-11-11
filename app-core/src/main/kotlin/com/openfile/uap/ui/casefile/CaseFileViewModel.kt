package com.openfile.uap.ui.casefile

import androidx.lifecycle.ViewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord

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
class CaseFileViewModel(archive: ArchiveRepository, caseId: String) : ViewModel() {
  val uiState: CaseFileUiState = archive.case(caseId)?.let { CaseFileUiState.Found(it) } ?: CaseFileUiState.NotFound(caseId)
}
