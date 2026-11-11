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

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Files> { AppFrame(current = Files, onTab = switchTab) { FilesScreen(onOpenCase = openCase, onOpenThreads = openThreads) } }
        entry<EventMap> { AppFrame(current = EventMap, onTab = switchTab) { MapScreen(onOpenCase = openCase) } }
        entry<Timeline> { AppFrame(current = Timeline, onTab = switchTab) { TimelineScreen(onOpenCase = openCase) } }
        entry<Method> { AppFrame(current = Method, onTab = switchTab) { MethodScreen() } }
        entry<Threads> {
          AppFrame(current = backStack.firstOrNull() ?: Files, onTab = switchTab) {
            ThreadsScreen(onOpenCase = openCase, onBack = { backStack.removeLastOrNull() })
          }
        }
        entry<CaseFile> { key ->
          // A file belongs to whichever section it was opened from, so that tab stays lit and Back names it.
          val origin = backStack.firstOrNull() ?: Files
          val backLabel = if (Threads in backStack) "Threads" else sectionLabel(origin)
          AppFrame(current = origin, onTab = switchTab) {
            CaseFileScreen(caseId = key.id, backLabel = backLabel, onBack = { backStack.removeLastOrNull() })
          }
        }
      },
  )
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
