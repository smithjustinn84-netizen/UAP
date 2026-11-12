package com.openfile.uap.ui.casefile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.theme.OpenFileTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CaseFileScreenTest {
  @get:Rule val compose = createComposeRule()

  private val record = BundledArchiveRepository.cases.first { it.detail != null }

  private fun sectionTab(label: String) = compose.onNode(isSelectable() and hasText(label))

  @Test
  fun opensOnTheSummaryAndBackLinkNamesWhereItCameFrom() {
    var backed = false
    compose.setContent {
      OpenFileTheme {
        CaseFileScreen(CaseFileUiState.Found(record), backLabel = "Timeline", onBack = { backed = true }, onOpenMethod = {})
      }
    }

    compose.onNodeWithText(record.title).assertIsDisplayed()
    sectionTab("Summary").assertIsSelected()
    compose.onNodeWithText("< Timeline").performClick()
    assertTrue(backed)
  }

  @Test
  fun theChosenSectionSurvivesRecreation() {
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      OpenFileTheme { CaseFileScreen(CaseFileUiState.Found(record), backLabel = "Files", onBack = {}, onOpenMethod = {}) }
    }

    sectionTab("Evidence").performClick()
    sectionTab("Evidence").assertIsSelected()

    restoration.emulateSavedInstanceStateRestore()
    sectionTab("Evidence").assertIsSelected()
    sectionTab("Summary").assertIsNotSelected()
  }

  @Test
  fun aMissingFileSaysSoInsteadOfCrashing() {
    compose.setContent {
      OpenFileTheme { CaseFileScreen(CaseFileUiState.NotFound("OF-1999-99"), backLabel = "Files", onBack = {}, onOpenMethod = {}) }
    }
    compose.onNodeWithText("OF-1999-99").assertIsDisplayed()
    compose.onNodeWithText("This file could not be found.").assertIsDisplayed()
  }
}
