package com.openfile.uap.ui.files

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.theme.OpenFileTheme
import com.openfile.uap.ui.countOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilesScreenTest {
  @get:Rule val compose = createComposeRule()

  private val archive = BundledArchiveRepository
  private var openedCase: String? = null

  private fun showFiles() {
    val viewModel = FilesViewModel(archive, SavedStateHandle())
    compose.setContent {
      OpenFileTheme {
        FilesRoute(archive = archive, onOpenCase = { openedCase = it }, onOpenThreads = {}, onOpenMethod = {}, viewModel = viewModel)
      }
    }
  }

  @Test
  fun searchingNarrowsTheListAndTappingAFolderOpensIt() {
    showFiles()
    val roswell = archive.case("OF-1947-02")!!

    compose.onNodeWithContentDescription("Search the files").performTextInput("Roswell")
    compose.onNodeWithText(countOf(archive.search("Roswell", null).size, "file")).assertIsDisplayed()

    compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(roswell.title))
    compose.onNodeWithText(roswell.title).performClick()
    assertEquals(roswell.id, openedCase)
  }

  @Test
  fun aQueryWithNoMatchesSaysSo() {
    showFiles()
    compose.onNodeWithContentDescription("Search the files").performTextInput("zzzz no such place")
    compose.onNodeWithText("0 files").assertIsDisplayed()
    compose.onNodeWithText("No files match", substring = true).assertIsDisplayed()
  }

  @Test
  fun choosingADecadeSelectsItsChipAndShowsOnlyThatDecade() {
    showFiles()
    val decade = archive.decades[1]

    compose.onNodeWithText("${decade}s").performScrollTo().performClick()
    compose.onNodeWithText("${decade}s").assertIsSelected()
    compose.onNodeWithText(countOf(archive.search("", decade).size, "file")).assertIsDisplayed()
  }
}
