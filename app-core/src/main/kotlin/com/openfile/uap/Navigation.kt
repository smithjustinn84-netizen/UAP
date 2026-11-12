package com.openfile.uap

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.openfile.uap.ui.AppFrame
import com.openfile.uap.ui.casefile.CaseFileRoute
import com.openfile.uap.ui.files.FilesRoute
import com.openfile.uap.ui.map.MapRoute
import com.openfile.uap.ui.method.MethodRoute
import com.openfile.uap.ui.threads.ThreadsRoute
import com.openfile.uap.ui.timeline.TimelineRoute

@Composable
fun MainNavigation(container: AppContainer) {
  val backStack = rememberNavBackStack(Files)
  val switchTab: (NavKey) -> Unit = { backStack.switchTo(it) }
  val openCase: (String) -> Unit = { backStack.add(CaseFile(it)) }
  val openThreads: () -> Unit = { backStack.add(Threads) }
  // Opened on top of the current screen, not as a tab switch, so Back returns to the file or list it came from.
  val openMethod: () -> Unit = { if (backStack.lastOrNull() != Method) backStack.add(Method) }
  val archive = container.archiveRepository

  // The frame sits outside NavDisplay so the desk and tab strip are composed once, not once per destination.
  // The lit tab is the topmost section on the stack: a file or the threads belong to whichever section they were
  // opened from, while Method opened from a tile or a label sheet lights Method.
  val currentSection = backStack.lastOrNull { key -> TopLevelSections.any { it.key == key } } ?: Files
  AppFrame(tabs = TopLevelSections, current = currentSection, onTab = switchTab) {
    NavDisplay(
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      // Each entry gets its own ViewModelStore, so two open files never share a ViewModel and a popped entry's
      // ViewModel is cleared. The saveable state decorator must come first: the ViewModel store builds on it.
      entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()),
      transitionSpec = { forwardTransition() },
      popTransitionSpec = { backTransition() },
      // Drives the transition from the system back gesture, so the screen below shows through as the user swipes.
      predictivePopTransitionSpec = { backTransition() },
      entryProvider =
        entryProvider {
          entry<Files> { FilesRoute(archive = archive, onOpenCase = openCase, onOpenThreads = openThreads, onOpenMethod = openMethod) }
          entry<EventMap> { MapRoute(archive = archive, outlines = container.mapOutlineRepository, onOpenCase = openCase) }
          entry<Timeline> { TimelineRoute(archive = archive, onOpenCase = openCase) }
          entry<Method> { MethodRoute(archive = archive) }
          entry<Threads> { ThreadsRoute(archive = archive, onOpenCase = openCase, onBack = { backStack.removeLastOrNull() }) }
          entry<CaseFile> { key ->
            // Back names where the file was opened from.
            val backLabel = if (Threads in backStack) "Threads" else sectionLabel(backStack.firstOrNull() ?: Files)
            CaseFileRoute(
              archive = archive,
              caseId = key.id,
              backLabel = backLabel,
              onBack = { backStack.removeLastOrNull() },
              onOpenMethod = openMethod,
            )
          }
        },
    )
  }
}

private fun forwardTransition(): ContentTransform =
  (slideInHorizontally(initialOffsetX = { it }) togetherWith (slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()))

private fun backTransition(): ContentTransform =
  ((slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn()) togetherWith slideOutHorizontally(targetOffsetX = { it }))

private fun sectionLabel(key: NavKey): String = (TopLevelSections.firstOrNull { it.key == key } ?: TopLevelSections.first()).label

/** Replaces the whole stack with one top-level destination, without ever leaving it empty. */
private fun NavBackStack<NavKey>.switchTo(key: NavKey) {
  if (size == 1 && last() == key) return
  add(key)
  while (size > 1) removeAt(0)
}
