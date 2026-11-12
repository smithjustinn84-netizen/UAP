package com.openfile.uap.ui.files

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Archive
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.Stamp
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.countOf
import com.openfile.uap.ui.dashedBorder

@Composable
fun FilesRoute(
  archive: ArchiveRepository,
  onOpenCase: (String) -> Unit,
  onOpenThreads: () -> Unit,
  onOpenMethod: () -> Unit,
  viewModel: FilesViewModel = viewModel { FilesViewModel(archive, createSavedStateHandle()) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  FilesScreen(
    uiState = uiState,
    query = viewModel.query,
    onQueryChange = viewModel::onQueryChange,
    onDecadeSelect = viewModel::onDecadeSelect,
    onOpenCase = onOpenCase,
    onOpenThreads = onOpenThreads,
    onOpenMethod = onOpenMethod,
  )
}

@Composable
fun FilesScreen(
  uiState: FilesUiState,
  query: String,
  onQueryChange: (String) -> Unit,
  onDecadeSelect: (Int?) -> Unit,
  onOpenCase: (String) -> Unit,
  onOpenThreads: () -> Unit,
  onOpenMethod: () -> Unit,
) {
  val results = uiState.results
  val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(top = statusBarTop, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item(key = "header", contentType = "header") {
      Column {
        FilesHeader()
        StartHere(threadCount = uiState.threadCount, onOpenMethod = onOpenMethod, onOpenThreads = onOpenThreads)
        SearchField(query = query, onQueryChange = onQueryChange)
        DecadeChips(decades = uiState.decades, selected = uiState.selectedDecade, onSelect = onDecadeSelect)
        Row(
          Modifier.fillMaxWidth().padding(start = ScreenPadding, end = ScreenPadding, top = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(countOf(results.size, "file"), style = MaterialTheme.typography.labelMedium, color = InkSoft)
          Text("Oldest first", style = MaterialTheme.typography.labelMedium, color = InkSoft)
        }
      }
    }
    if (results.isEmpty()) {
      item(key = "empty", contentType = "empty") {
        Text(
          "No files match. Try a place, a year, a name or a witness, or clear the decade filter.",
          modifier = Modifier.padding(horizontal = ScreenPadding),
          style = MaterialTheme.typography.bodyMedium,
          color = InkBody,
        )
      }
    }
    items(results, key = { it.id }, contentType = { "folder" }) { record ->
      FolderCard(record = record, onClick = { onOpenCase(record.id) }, modifier = Modifier.padding(horizontal = ScreenPadding))
    }
  }
}

@Composable
private fun FilesHeader() {
  Column(
    Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      MonoLabel("Archive of reported sightings")
      Stamp("No verdicts")
    }
    TypeTitle("Open File", size = 36.sp)
    Text(
      "Historical UFO and UAP reports, the records behind them, and every explanation on file.",
      style = MaterialTheme.typography.bodyLarge,
      color = InkBody,
    )
  }
}

/** Two ways in before the list: how to read a file, and the patterns that run across files. */
@Composable
private fun StartHere(threadCount: Int, onOpenMethod: () -> Unit, onOpenThreads: () -> Unit) {
  Row(
    Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(start = ScreenPadding, end = ScreenPadding, top = 18.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    StartTile(
      label = "New here?",
      title = "How to read a file",
      detail = "Labels, status, gaps",
      inverted = false,
      onClick = onOpenMethod,
      modifier = Modifier.weight(1f),
    )
    StartTile(
      label = "Across the files",
      title = "Threads",
      detail = countOf(threadCount, "recurring pattern"),
      inverted = true,
      onClick = onOpenThreads,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun StartTile(label: String, title: String, detail: String, inverted: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val surface = if (inverted) Modifier.background(Ink) else Modifier.background(Paper).dashedBorder(Ink)
  Column(
    modifier
      .fillMaxHeight()
      .defaultMinSize(minHeight = 92.dp)
      .then(surface)
      .clickable(onClickLabel = "Open", role = Role.Button, onClick = onClick)
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    MonoLabel(label, color = if (inverted) Manila else InkSoft)
    TypeTitle(title, size = 17.sp, color = if (inverted) Paper else Ink)
    Text(detail, style = MaterialTheme.typography.labelMedium, color = if (inverted) Paper else InkSoft)
  }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
  Column(Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    MonoLabel("Search the files")
    BasicTextField(
      value = query,
      onValueChange = onQueryChange,
      singleLine = true,
      textStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, color = Ink),
      cursorBrush = SolidColor(Ink),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Search the files" },
      decorationBox = { field ->
        Row(
          Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).background(Paper).border(1.dp, Ink).padding(horizontal = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Magnifier()
          Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
              Text("Place, year, name or witness", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp), color = InkSoft)
            }
            field()
          }
        }
      },
    )
  }
}

/** A drawn magnifying glass, in the same line weight as the chevrons. */
@Composable
private fun Magnifier() {
  Spacer(
    Modifier.size(18.dp).drawBehind {
      val stroke = 2.dp.toPx()
      val unit = size.width / 18f
      drawCircle(Ink, radius = 5 * unit, center = Offset(7.5f * unit, 7.5f * unit), style = Stroke(width = stroke))
      drawLine(Ink, Offset(11.5f * unit, 11.5f * unit), Offset(16 * unit, 16 * unit), strokeWidth = stroke, cap = StrokeCap.Round)
    },
  )
}

@Composable
private fun DecadeChips(decades: List<Int>, selected: Int?, onSelect: (Int?) -> Unit) {
  Row(
    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = ScreenPadding, end = ScreenPadding, top = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Chip(label = "All", selected = selected == null, onClick = { onSelect(null) })
    decades.forEach { d -> Chip(label = "${d}s", selected = selected == d, onClick = { onSelect(d) }) }
  }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
  Box(
    Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
      .background(if (selected) Ink else Paper)
      .border(1.dp, Ink)
      .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
      .padding(horizontal = 14.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) Paper else Ink)
  }
}

/** A case shown as a manila folder: file number on the tab, summary on the sheet. */
@Composable
private fun FolderCard(record: CaseRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier.fillMaxWidth().clickable(onClickLabel = "Open file", role = Role.Button, onClick = onClick)) {
    Text(
      record.id,
      modifier = Modifier.background(Paper).border(1.dp, Ink).padding(horizontal = 12.dp, vertical = 4.dp),
      style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
      color = Ink,
    )
    PaperSheet {
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          TypeTitle(record.title, size = 19.sp, modifier = Modifier.weight(1f))
          Chevron()
        }
        Text("${record.dateShort} · ${record.placeShort}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
        Text("On file: ${record.onFile}", style = MaterialTheme.typography.bodySmall, color = InkBody)
        DashedRule()
        MonoLabel(record.status, color = Ink)
        record.detail?.let { detail ->
          val contents = listOfNotNull(
            countOf(detail.evidence.size, "evidence item"),
            detail.media.size.takeIf { it > 0 }?.let { countOf(it, "exhibit") },
          )
          MonoLabel(contents.joinToString(" · "))
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun FilesScreenPreview() {
  DeskPreview {
    FilesScreen(
      uiState =
        FilesUiState(
          decades = Archive.decades,
          selectedDecade = null,
          results = Archive.cases,
          threadCount = Archive.threads.size,
        ),
      query = "",
      onQueryChange = {},
      onDecadeSelect = {},
      onOpenCase = {},
      onOpenThreads = {},
      onOpenMethod = {},
    )
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 640)
@Composable
private fun FilesScreenEmptyPreview() {
  DeskPreview {
    FilesScreen(
      uiState =
        FilesUiState(
          decades = Archive.decades,
          selectedDecade = 1970,
          results = emptyList(),
          threadCount = Archive.threads.size,
        ),
      query = "antarctica",
      onQueryChange = {},
      onDecadeSelect = {},
      onOpenCase = {},
      onOpenThreads = {},
      onOpenMethod = {},
    )
  }
}
