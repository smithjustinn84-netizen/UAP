package com.openfile.uap

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openfile.uap.data.BundledArchiveRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Drives the real [MainActivity], so these also cover the app wiring: container, theme and the back stack. */
@RunWith(AndroidJUnit4::class)
class NavigationTest {
  @get:Rule val compose = createAndroidComposeRule<MainActivity>()

  private val roswell = BundledArchiveRepository.case("OF-1947-02")!!

  private fun tab(label: String) = compose.onNode(isSelectable() and hasText(label.uppercase()))

  private fun openRoswell() {
    compose.onNodeWithContentDescription("Search the files").performTextInput("Roswell")
    compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(roswell.title))
    compose.onNodeWithText(roswell.title).performClick()
    compose.onNodeWithText(roswell.id).assertIsDisplayed()
  }

  private fun pressBack() {
    compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
    compose.waitForIdle()
  }

  @Test
  fun startsOnFiles() {
    compose.onNodeWithText("Open File").assertIsDisplayed()
    tab("Files").assertIsSelected()
  }

  @Test
  fun backFromAFileReturnsToTheList() {
    openRoswell()
    compose.onNodeWithText("< Files").assertIsDisplayed()

    pressBack()
    compose.onNodeWithText("Open File").assertIsDisplayed()
    tab("Files").assertIsSelected()
  }

  @Test
  fun methodOpenedFromATileLightsItsTabAndBackReturnsToFiles() {
    compose.onNodeWithText("Labels, status, gaps").performClick()
    tab("Method").assertIsSelected()

    pressBack()
    tab("Files").assertIsSelected()
    compose.onNodeWithText("Open File").assertIsDisplayed()
  }

  @Test
  fun switchingTabsReplacesTheStackSoAnOpenFileIsDropped() {
    openRoswell()
    tab("Timeline").performClick()
    tab("Timeline").assertIsSelected()

    tab("Files").performClick()
    compose.onNodeWithText("Open File").assertIsDisplayed()
    compose.onNodeWithText("< Files").assertDoesNotExist()
  }
}
