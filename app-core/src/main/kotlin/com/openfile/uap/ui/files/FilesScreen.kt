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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Archive
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.PaperTint
import com.openfile.uap.ui.ActionRow
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.DoubleRule
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.Stamp
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.casefile.CaseThumbnail
import com.openfile.uap.ui.countOf
import com.openfile.uap.ui.dashedBorder
import com.openfile.uap.ui.deskShadow
import com.openfile.uap.ui.pressable
import com.openfile.uap.ui.rememberTick
import com.openfile.uap.ui.selectionColor

@Composable
fun FilesRoute(
  archive: ArchiveRepository,
  onOpenCase: (String) -> Unit,
  onOpenThreads: () -> Unit,
  onOpenMethod: () -> Unit,
  outlines: MapOutlineRepository,
  viewModel: FilesViewModel = viewModel { FilesViewModel(archive, outlines, createSavedStateHandle()) },
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
        PaperSheet(dashed = true, modifier = Modifier.animateItem().padding(horizontal = ScreenPadding)) {
          Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MonoLabel("No matching files", color = Ink)
            Text(
              "No files match. Try a place, a year, a name or a witness, or clear the decade filter.",
              style = MaterialTheme.typography.bodyMedium,
              color = InkBody,
            )
            if (query.isNotEmpty() || uiState.selectedDecade != null) {
              ActionRow(
                text = "Clear search and filters",
                onClick = {
                  onQueryChange("")
                  onDecadeSelect(null)
                },
                solid = false,
                modifier = Modifier.padding(top = 2.dp),
              )
            }
          }
        }
      }
    }
    items(results, key = { it.id }, contentType = { "folder" }) { record ->
      FolderCard(
        record = record,
        outline = MapSheets.sheetFor(record.point)?.let { uiState.outlines[it.id] }.orEmpty(),
        onClick = { onOpenCase(record.id) },
        modifier = Modifier.animateItem().padding(horizontal = ScreenPadding),
      )
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
    DoubleRule(Modifier.padding(top = 4.dp))
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
  val surface = if (inverted) Modifier.deskShadow(2.dp).background(Ink) else Modifier.deskShadow(2.dp).background(Paper).dashedBorder(Ink)
  val chevronColor = if (inverted) Manila else Ink
  Column(
    modifier
      .pressable(onClickLabel = "Open", onClick = onClick)
      .fillMaxHeight()
      .defaultMinSize(minHeight = 96.dp)
      .then(surface)
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      MonoLabel(label, color = if (inverted) Manila else InkSoft)
      Chevron(color = chevronColor)
    }
    TypeTitle(title, size = 17.sp, color = if (inverted) Paper else Ink)
    Text(detail, style = MaterialTheme.typography.labelMedium, color = if (inverted) Paper else InkSoft)
  }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
  var focused by remember { mutableStateOf(false) }
  Column(Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    MonoLabel("Search the files")
    BasicTextField(
      value = query,
      onValueChange = onQueryChange,
      singleLine = true,
      textStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, color = Ink),
      cursorBrush = SolidColor(Ink),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      modifier =
        Modifier
          .fillMaxWidth()
          .onFocusChanged { focused = it.isFocused }
          .semantics { contentDescription = "Search the files" },
      decorationBox = { field ->
        Row(
          Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .deskShadow(2.dp)
            .background(Paper)
            .border(if (focused) 2.dp else 1.dp, Ink)
            .padding(start = 12.dp, end = if (query.isEmpty()) 12.dp else 0.dp),
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
          if (query.isNotEmpty()) {
            Box(
              Modifier
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable(role = Role.Button, onClickLabel = "Clear search") { onQueryChange("") }
                .semantics { contentDescription = "Clear search" },
              contentAlignment = Alignment.Center,
            ) {
              ClearIcon()
            }
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

/** A drawn cross to clear the search field, in the same line weight as the magnifier. */
@Composable
private fun ClearIcon() {
  Spacer(
    Modifier.size(14.dp).drawBehind {
      val stroke = 2.dp.toPx()
      val pad = 2.dp.toPx()
      drawLine(Ink, Offset(pad, pad), Offset(size.width - pad, size.height - pad), strokeWidth = stroke, cap = StrokeCap.Round)
      drawLine(Ink, Offset(size.width - pad, pad), Offset(pad, size.height - pad), strokeWidth = stroke, cap = StrokeCap.Round)
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
  val tick = rememberTick()
  Box(
    Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
      .background(selectionColor(selected, Ink, Paper))
      .border(1.dp, Ink)
      .selectable(selected = selected, role = Role.RadioButton, onClick = {
        if (!selected) tick()
        onClick()
      })
      .padding(horizontal = 14.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = selectionColor(selected, Paper, Ink))
  }
}

private val ThumbnailHeight = 112.dp

/** A case shown as a manila folder: integrated file-number tab, its picture or locator map, then the summary. */
@Composable
private fun FolderCard(record: CaseRecord, outline: SheetOutline, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier.fillMaxWidth().pressable(onClickLabel = "Open file", onClick = onClick)) {
    FolderTab(record.id)
    PaperSheet {
      CaseThumbnail(record, outline, Modifier.height(ThumbnailHeight))
      SolidRule(color = Ink)
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
          TypeTitle(record.title, size = 19.sp, modifier = Modifier.weight(1f))
          Chevron()
        }
        Text("${record.dateShort} · ${record.placeShort}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
        Text("On file: ${record.onFile}", style = MaterialTheme.typography.bodySmall, color = InkBody)
      }
      SolidRule()
      Column(
        Modifier.fillMaxWidth().background(PaperTint).padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
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

/** An angled archival folder tab that fuses seamlessly into the top edge of the [PaperSheet] below it. */
@Composable
private fun FolderTab(id: String) {
  Text(
    id,
    modifier =
      Modifier
        .offset(y = 1.dp)
        .zIndex(1f)
        .drawBehind {
          val slope = 10.dp.toPx()
          val stroke = 1.dp.toPx()
          val half = stroke / 2f
          val fill =
            Path().apply {
              moveTo(0f, size.height)
              lineTo(0f, 0f)
              lineTo(size.width - slope, 0f)
              lineTo(size.width, size.height)
              close()
            }
          drawPath(fill, Paper)
          val outline =
            Path().apply {
              moveTo(half, size.height)
              lineTo(half, half)
              lineTo(size.width - slope, half)
              lineTo(size.width - half, size.height)
            }
          drawPath(outline, Ink, style = Stroke(stroke))
        }
        .padding(start = 12.dp, end = 22.dp, top = 5.dp, bottom = 4.dp),
    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
    color = Ink,
  )
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
