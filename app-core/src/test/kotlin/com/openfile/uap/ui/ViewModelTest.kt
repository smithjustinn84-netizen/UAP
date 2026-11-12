package com.openfile.uap.ui

import androidx.lifecycle.SavedStateHandle
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.data.FileThread
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.data.ThreadAppearance
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.ui.casefile.CaseFileUiState
import com.openfile.uap.ui.casefile.CaseFileViewModel
import com.openfile.uap.ui.files.FilesViewModel
import com.openfile.uap.ui.map.MapViewModel
import com.openfile.uap.ui.threads.ThreadsViewModel
import com.openfile.uap.ui.timeline.TimelineViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val archive = BundledArchiveRepository

  @Test
  fun filesStartWithEveryCaseAndNarrowAsTheQueryAndDecadeChange() {
    val viewModel = FilesViewModel(archive, SavedStateHandle())
    assertEquals(archive.cases, viewModel.uiState.value.results)

    viewModel.onQueryChange("Roswell")
    assertEquals("Roswell", viewModel.query)
    assertEquals(archive.search("Roswell", null), viewModel.uiState.value.results)

    val decade = archive.decades.first()
    viewModel.onDecadeSelect(decade)
    assertEquals(decade, viewModel.uiState.value.selectedDecade)
    assertEquals(archive.search("Roswell", decade), viewModel.uiState.value.results)
  }

  @Test
  fun filesRestoreTheQueryAndDecadeFromSavedState() {
    val saved = SavedStateHandle()
    FilesViewModel(archive, saved).apply {
      onQueryChange("Nimitz")
      onDecadeSelect(2000)
    }

    val restored = FilesViewModel(archive, saved)
    assertEquals("Nimitz", restored.query)
    assertEquals(2000, restored.uiState.value.selectedDecade)
    assertEquals(archive.search("Nimitz", 2000), restored.uiState.value.results)
  }

  @Test
  fun timelineFilterShowsOneKindAndKeepsTheFullCounts() {
    val viewModel = TimelineViewModel(archive, SavedStateHandle())
    val allCounts = viewModel.uiState.value.kindCounts

    viewModel.onFilterChange(TimelineKind.Sighting)
    assertTrue(viewModel.uiState.value.entries.all { it.kind == TimelineKind.Sighting })
    assertEquals(allCounts, viewModel.uiState.value.kindCounts)

    viewModel.onFilterChange(null)
    assertEquals(archive.timeline, viewModel.uiState.value.entries)
  }

  @Test
  fun timelineDecadeJumpsPointAtTheFirstEntryOfEachDecadeUnderTheFilter() {
    val viewModel = TimelineViewModel(archive, SavedStateHandle())
    viewModel.onFilterChange(TimelineKind.Culture)
    val state = viewModel.uiState.value

    assertEquals(state.entries.map { it.year / 10 * 10 }.distinct(), state.decadeStarts.map { it.decade })
    state.decadeStarts.forEach { start ->
      val first = state.entries.indexOfFirst { it.year / 10 * 10 == start.decade }
      assertEquals(first, start.firstIndex)
    }
  }

  @Test
  fun mapLoadsEachOutlineAndTogglesTheSelectedPin() {
    val ring = listOf(listOf(0f to 0f, 1f to 0f, 1f to 1f))
    val outlines =
      object : MapOutlineRepository {
        override suspend fun outline(sheet: MapSheet): SheetOutline = ring
      }
    val viewModel = MapViewModel(archive, outlines, SavedStateHandle())
    assertTrue(viewModel.uiState.value.sheets.all { it.outline == ring })

    val id = archive.cases.first().id
    viewModel.onPinSelect(id)
    assertEquals(id, viewModel.uiState.value.selectedId)
    viewModel.onPinSelect(id)
    assertNull(viewModel.uiState.value.selectedId)
  }

  @Test
  fun threadsLeaveOutAppearancesOfCasesNotInTheArchive() {
    val known = archive.cases.first()
    val withMissingCase =
      object : ArchiveRepository by archive {
        override val threads =
          listOf(
            FileThread(
              "T-1",
              "Title",
              "Summary",
              listOf(ThreadAppearance(known.id, "on file"), ThreadAppearance("OF-0000-00", "gone")),
              "Note",
            ),
          )
      }

    val appearances = ThreadsViewModel(withMissingCase).uiState.threads.single().appearances
    assertEquals(listOf(known), appearances.map { it.record })
  }

  @Test
  fun caseFileReportsAnIdThatIsNotInTheArchive() {
    assertEquals(CaseFileUiState.NotFound("OF-0000-00"), CaseFileViewModel(archive, "OF-0000-00").uiState)
    val known = archive.cases.first()
    assertEquals(CaseFileUiState.Found(known), CaseFileViewModel(archive, known.id).uiState)
  }
}
