package com.openfile.uap

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.openfile.uap.ui.AppFrame
import com.openfile.uap.ui.CaseFileScreen
import com.openfile.uap.ui.FilesScreen
import com.openfile.uap.ui.MapScreen
import com.openfile.uap.ui.MethodScreen
import com.openfile.uap.ui.ThreadsScreen
import com.openfile.uap.ui.TimelineScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Files)
  val switchTab: (NavKey) -> Unit = { backStack.switchTo(it) }
  val openCase: (String) -> Unit = { backStack.add(CaseFile(it)) }

  val openThreads: () -> Unit = { backStack.add(Threads) }

  // The frame sits outside NavDisplay so the desk and tab strip are composed once, not once per destination.
  // A file or the threads belong to whichever section they were opened from, so that tab stays lit.
  AppFrame(current = backStack.firstOrNull() ?: Files, onTab = switchTab) {
    NavDisplay(
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      entryProvider =
        entryProvider {
          entry<Files> { FilesScreen(onOpenCase = openCase, onOpenThreads = openThreads) }
          entry<EventMap> { MapScreen(onOpenCase = openCase) }
          entry<Timeline> { TimelineScreen(onOpenCase = openCase) }
          entry<Method> { MethodScreen() }
          entry<Threads> { ThreadsScreen(onOpenCase = openCase, onBack = { backStack.removeLastOrNull() }) }
          entry<CaseFile> { key ->
            // Back names where the file was opened from.
            val backLabel = if (Threads in backStack) "Threads" else sectionLabel(backStack.firstOrNull() ?: Files)
            CaseFileScreen(caseId = key.id, backLabel = backLabel, onBack = { backStack.removeLastOrNull() })
          }
        },
    )
  }
}

private fun sectionLabel(key: NavKey): String =
  when (key) {
    EventMap -> "Map"
    Timeline -> "Timeline"
    Method -> "Method"
    else -> "Files"
  }

/** Replaces the whole stack with one top-level destination, without ever leaving it empty. */
private fun NavBackStack<NavKey>.switchTo(key: NavKey) {
  if (size == 1 && last() == key) return
  add(key)
  while (size > 1) removeAt(0)
}
