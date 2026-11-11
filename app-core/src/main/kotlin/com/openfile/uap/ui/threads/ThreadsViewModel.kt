package com.openfile.uap.ui.threads

import androidx.lifecycle.ViewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.FileThread

/** A thread's appearance with its case already looked up. */
data class ThreadAppearanceUi(val record: CaseRecord, val note: String)

data class ThreadUi(val thread: FileThread, val appearances: List<ThreadAppearanceUi>)

data class ThreadsUiState(val threads: List<ThreadUi>)

/** The threads never change while the screen is open, so the state is a plain value rather than a stream. */
class ThreadsViewModel(archive: ArchiveRepository) : ViewModel() {
  val uiState: ThreadsUiState =
    ThreadsUiState(
      archive.threads.map { thread ->
        // An appearance naming a case that is not in the archive is left out rather than shown broken.
        ThreadUi(thread, thread.appearances.mapNotNull { a -> archive.case(a.caseId)?.let { ThreadAppearanceUi(it, a.note) } })
      },
    )
}
