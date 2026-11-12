package com.openfile.uap.ui

import androidx.lifecycle.SavedStateHandle
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.data.FileThread
import com.openfile.uap.data.Licence
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.Notice
import com.openfile.uap.data.NoticesRepository
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.data.ThreadAppearance
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.ui.casefile.CaseFileUiState
import com.openfile.uap.ui.casefile.CaseFileViewModel
import com.openfile.uap.ui.files.FilesViewModel
import com.openfile.uap.ui.licences.LicencesViewModel
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
    val viewModel = FilesViewModel(archive, RecordingOutlines(), SavedStateHandle())
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
    FilesViewModel(archive, RecordingOutlines(), saved).apply {
      onQueryChange("Nimitz")
      onDecadeSelect(2000)
    }

    val restored = FilesViewModel(archive, RecordingOutlines(), saved)
    assertEquals("Nimitz", restored.query)
    assertEquals(2000, restored.uiState.value.selectedDecade)
    assertEquals(archive.search("Nimitz", 2000), restored.uiState.value.results)
  }

  @Test
  fun timelineFilterShowsOneKindAndKeepsTheFullCounts() {
    val viewModel = TimelineViewModel(archive, RecordingOutlines(), SavedStateHandle())
    val allCounts = viewModel.uiState.value.kindCounts

    viewModel.onFilterChange(TimelineKind.Sighting)
    assertTrue(viewModel.uiState.value.entries.all { it.kind == TimelineKind.Sighting })
    assertEquals(allCounts, viewModel.uiState.value.kindCounts)

    viewModel.onFilterChange(null)
    assertEquals(archive.timeline, viewModel.uiState.value.entries)
  }

  @Test
  fun timelineKnowsEveryLinkedFileAndKeepsItsMapsAcrossFilters() {
    val outlines = RecordingOutlines()
    val viewModel = TimelineViewModel(archive, outlines, SavedStateHandle())
    val linked = archive.timeline.mapNotNull { it.caseId }.toSet()
    assertEquals(linked, viewModel.uiState.value.files.keys)
    assertEquals(outlines.requested.size, outlines.requested.distinct().size)

    val loaded = viewModel.uiState.value.outlines
    assertTrue(loaded.isNotEmpty())
    viewModel.onFilterChange(TimelineKind.Inquiry)
    assertEquals(loaded, viewModel.uiState.value.outlines)
  }

  @Test
  fun timelineDecadeJumpsPointAtTheFirstEntryOfEachDecadeUnderTheFilter() {
    val viewModel = TimelineViewModel(archive, RecordingOutlines(), SavedStateHandle())
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
  fun mapShowsOneSheetAndRestoresItFromSavedState() {
    val outlines =
      object : MapOutlineRepository {
        override suspend fun outline(sheet: MapSheet): SheetOutline = emptyList()
      }
    val saved = SavedStateHandle()
    val viewModel = MapViewModel(archive, outlines, saved)
    assertEquals(MapSheets.all.first().id, viewModel.uiState.value.sheetId)

    viewModel.onSheetSelect(MapSheets.britishIsles.id)
    assertEquals(MapSheets.britishIsles, viewModel.uiState.value.sheet.sheet)
    viewModel.onSheetSelect("no_such_sheet")
    assertEquals(MapSheets.britishIsles.id, viewModel.uiState.value.sheetId)

    assertEquals(MapSheets.britishIsles.id, MapViewModel(archive, outlines, saved).uiState.value.sheetId)
  }

  @Test
  fun mapOpensOnTheSheetHoldingARestoredPin() {
    val outlines =
      object : MapOutlineRepository {
        override suspend fun outline(sheet: MapSheet): SheetOutline = emptyList()
      }
    val rendlesham = "OF-1980-01"
    val viewModel = MapViewModel(archive, outlines, SavedStateHandle(mapOf("selectedId" to rendlesham)))
    assertEquals(MapSheets.britishIsles.id, viewModel.uiState.value.sheetId)
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
    val outlines = RecordingOutlines()
    assertEquals(CaseFileUiState.NotFound("OF-0000-00"), CaseFileViewModel(archive, outlines, "OF-0000-00").uiState)
    val known = archive.cases.first()
    assertEquals(CaseFileUiState.Found(known), CaseFileViewModel(archive, outlines, known.id).uiState)
  }

  @Test
  fun caseFileReadsTheLocatorMapOnlyForAFileWithoutAPicture() {
    val unpictured = archive.cases.first { it.detail?.hero == null }
    val outlines = RecordingOutlines()
    val viewModel = CaseFileViewModel(archive, outlines, unpictured.id)
    assertEquals(listOf(MapSheets.sheetFor(unpictured.point)), outlines.requested)
    assertEquals(outlines.ring, viewModel.heroOutline.value)

    val pictured = archive.cases.first { it.detail?.hero != null }
    val untouched = RecordingOutlines()
    assertTrue(CaseFileViewModel(archive, untouched, pictured.id).heroOutline.value.isEmpty())
    assertTrue(untouched.requested.isEmpty())
  }

  @Test
  fun filesReadOneOutlinePerSheetForFilesWithoutAPicture() {
    val outlines = RecordingOutlines()
    val viewModel = FilesViewModel(archive, outlines, SavedStateHandle())
    val needed = archive.cases.filter { it.detail?.hero == null }.mapNotNull { MapSheets.sheetFor(it.point) }.toSet()
    assertEquals(needed, outlines.requested.toSet())
    assertEquals(outlines.requested.size, outlines.requested.distinct().size)

    viewModel.onQueryChange("Westall")
    assertEquals(needed.map { it.id }.toSet(), viewModel.uiState.value.outlines.keys)
  }

  @Test
  fun licencesGroupNoticesInLicenceOrderAndFillInBundledTexts() {
    val notices =
      listOf(
        Notice("Photo", "Someone", Licence.CcBySa40),
        Notice("Font", "Foundry", Licence.Ofl11),
        Notice("Library", "Authors", Licence.Apache2),
        Notice("Other library", "Authors", Licence.Apache2),
      )
    val viewModel = LicencesViewModel(FakeNotices(notices, mapOf(Licence.Apache2 to "APACHE")))
    val groups = viewModel.uiState.value.groups
    assertEquals(listOf(Licence.Apache2, Licence.Ofl11, Licence.CcBySa40), groups.map { it.licence })
    assertEquals(2, groups.first().notices.size)
    assertEquals("APACHE", groups.first().fullText)
    assertNull(groups[1].fullText)
  }

  private class FakeNotices(override val notices: List<Notice>, private val texts: Map<Licence, String>) : NoticesRepository {
    override suspend fun fullText(licence: Licence): String? = texts[licence]
  }

  private class RecordingOutlines : MapOutlineRepository {
    val ring: SheetOutline = listOf(listOf(0f to 0f, 1f to 0f, 1f to 1f))
    val requested = mutableListOf<MapSheet>()

    override suspend fun outline(sheet: MapSheet): SheetOutline {
      requested += sheet
      return ring
    }
  }
}
