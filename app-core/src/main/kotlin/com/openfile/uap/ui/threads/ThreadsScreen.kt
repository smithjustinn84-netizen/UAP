package com.openfile.uap.ui.threads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Archive
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.ui.BackLink
import com.openfile.uap.ui.CardHeader
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.LeadText
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.countOf

@Composable
fun ThreadsRoute(
  archive: ArchiveRepository,
  onOpenCase: (String) -> Unit,
  onBack: () -> Unit,
  viewModel: ThreadsViewModel = viewModel { ThreadsViewModel(archive) },
) {
  ThreadsScreen(uiState = viewModel.uiState, onOpenCase = onOpenCase, onBack = onBack)
}

@Composable
fun ThreadsScreen(uiState: ThreadsUiState, onOpenCase: (String) -> Unit, onBack: () -> Unit) {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    BackLink("Files", onBack, Modifier.padding(start = 8.dp, top = 8.dp))
    ScreenHeader(
      label = "Cross-references",
      title = "Threads",
      intro = "Patterns that recur from one file to the next. Each lists where it appears and what to keep in mind about it.",
      top = 12.dp,
    )

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      uiState.threads.forEach { thread -> ThreadCard(thread, onOpenCase) }
      Text(
        "The threads describe the records. They do not rule on the events.",
        style = MaterialTheme.typography.labelMedium,
        color = InkSoft,
      )
    }
  }
}

@Composable
private fun ThreadCard(threadUi: ThreadUi, onOpenCase: (String) -> Unit) {
  val thread = threadUi.thread
  PaperSheet {
    CardHeader(thread.id, countOf(threadUi.appearances.size, "file"))
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(thread.title, size = 19.sp)
      Text(thread.summary, style = MaterialTheme.typography.bodyMedium, color = Ink)
    }
    threadUi.appearances.forEach { appearance ->
      val record = appearance.record
      SolidRule()
      TapRow(onClick = { onOpenCase(record.id) }, onClickLabel = "Open file") {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            MonoLabel("${record.id} · ${record.title}", color = Ink)
            Text(appearance.note, style = MaterialTheme.typography.bodyMedium, color = InkBody)
          }
          Chevron()
        }
      }
    }
    SolidRule(color = Ink)
    LeadText("Keep in mind", thread.keepInMind, Modifier.padding(14.dp))
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ThreadsScreenPreview() {
  val uiState =
    ThreadsUiState(
      Archive.threads.map { thread ->
        ThreadUi(thread, thread.appearances.mapNotNull { a -> Archive.case(a.caseId)?.let { ThreadAppearanceUi(it, a.note) } })
      },
    )
  DeskPreview {
    ThreadsScreen(uiState = uiState, onOpenCase = {}, onBack = {})
  }
}
