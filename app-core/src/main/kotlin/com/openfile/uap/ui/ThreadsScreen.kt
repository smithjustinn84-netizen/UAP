package com.openfile.uap.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.Archive
import com.openfile.uap.data.FileThread
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft

@Composable
fun ThreadsScreen(onOpenCase: (String) -> Unit, onBack: () -> Unit) {
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
      Archive.threads.forEach { thread -> ThreadCard(thread, onOpenCase) }
      Text(
        "The threads describe the records. They do not rule on the events.",
        style = MaterialTheme.typography.labelMedium,
        color = InkSoft,
      )
    }
  }
}

@Composable
private fun ThreadCard(thread: FileThread, onOpenCase: (String) -> Unit) {
  PaperSheet {
    CardHeader(thread.id, countOf(thread.appearances.size, "file"))
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(thread.title, size = 19.sp)
      Text(thread.summary, style = MaterialTheme.typography.bodyMedium, color = Ink)
    }
    thread.appearances.forEach { appearance ->
      val record = Archive.case(appearance.caseId) ?: return@forEach
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
