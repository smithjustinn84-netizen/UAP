package com.openfile.uap.ui.timeline

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.TimelineEntry
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.TypeTitle
import kotlinx.coroutines.launch

@Composable
fun TimelineRoute(
  archive: ArchiveRepository,
  onOpenCase: (String) -> Unit,
  viewModel: TimelineViewModel = viewModel { TimelineViewModel(archive, createSavedStateHandle()) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  TimelineScreen(uiState = uiState, onFilterChange = viewModel::onFilterChange, onOpenCase = onOpenCase)
}

/** Items in the list before the first entry: the header, the kind key and the decade row. */
private const val LEADING_ITEMS = 3

@Composable
fun TimelineScreen(uiState: TimelineUiState, onFilterChange: (TimelineKind?) -> Unit, onOpenCase: (String) -> Unit) {
  val entries = uiState.entries
  val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(top = statusBarTop, bottom = 28.dp)) {
    item(key = "header", contentType = "header") {
      ScreenHeader(
        label = "Open File",
        title = "Timeline",
        intro = "Reported sightings alongside the inquiries that followed them and the books, films and broadcasts that retold them.",
      )
    }
    item(key = "kinds", contentType = "kinds") {
      KindFilter(
        only = uiState.only,
        kindCounts = uiState.kindCounts,
        onChange = onFilterChange,
      )
    }
    item(key = "decades", contentType = "decades") {
      DecadeJump(
        starts = uiState.decadeStarts,
        onJump = { start -> scope.launch { listState.animateScrollToItem(LEADING_ITEMS + start.firstIndex) } },
      )
    }
    itemsIndexed(entries, key = { _, entry -> "${entry.year}|${entry.title}" }, contentType = { _, _ -> "entry" }) { index, entry ->
      val showYear = index == 0 || entries[index - 1].year != entry.year
      TimelineRow(entry = entry, showYear = showYear, onOpenCase = onOpenCase, modifier = Modifier.padding(horizontal = ScreenPadding))
    }
  }
}

/** The key doubles as the filter: tap a kind to show only that kind, tap it again to show everything. */
@Composable
private fun KindFilter(only: TimelineKind?, kindCounts: Map<TimelineKind, Int>, onChange: (TimelineKind?) -> Unit) {
  Column(Modifier.padding(horizontal = ScreenPadding)) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).background(Paper).border(1.dp, Ink)) {
      TimelineKind.entries.forEachIndexed { index, kind ->
        if (index > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
        val selected = only == kind
        Column(
          Modifier
            .weight(1f)
            .fillMaxHeight()
            .defaultMinSize(minHeight = 64.dp)
            .background(if (selected) Ink else Paper)
            .toggleable(value = selected, role = Role.Checkbox) { onChange(if (it) kind else null) }
            .semantics { stateDescription = if (selected) "Showing only these" else "Showing all kinds" }
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            KindMarker(kind, inverted = selected)
            Text(
              (kindCounts[kind] ?: 0).toString(),
              style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
              color = if (selected) Paper else Ink,
            )
          }
          Text(kind.label, style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp), color = if (selected) Paper else Ink)
        }
      }
    }
    Text(
      if (only == null) "Tap a kind to show only that kind." else "Tap it again to show every kind.",
      modifier = Modifier.padding(top = 8.dp),
      style = MaterialTheme.typography.labelMedium,
      color = InkSoft,
    )
  }
}

@Composable
private fun DecadeJump(starts: List<DecadeStart>, onJump: (DecadeStart) -> Unit) {
  Row(
    Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(start = ScreenPadding, end = ScreenPadding, top = 14.dp, bottom = 20.dp)
      .semantics { contentDescription = "Jump to decade" },
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    starts.forEach { start ->
      Box(
        Modifier
          .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .background(Paper)
          .border(1.dp, Ink)
          .clickable(onClickLabel = "Jump to the ${start.decade}s", role = Role.Button) { onJump(start) }
          .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text("${start.decade}s", style = MaterialTheme.typography.labelLarge, color = Ink)
      }
    }
  }
}

/** Filled for a sighting, hollow for an inquiry, hollow with a dot for print or screen: told apart by fill, not by color. */
@Composable
private fun KindMarker(kind: TimelineKind, inverted: Boolean = false) {
  val ink = if (inverted) Paper else Ink
  val paper = if (inverted) Ink else Paper
  val fill = if (kind == TimelineKind.Sighting) ink else paper
  Box(
    Modifier.size(10.dp).background(fill).border(2.dp, ink),
    contentAlignment = Alignment.Center,
  ) {
    if (kind == TimelineKind.Culture) Box(Modifier.size(2.dp).background(ink))
  }
}

private val YearColumn = 56.dp
private val SpineWidth = 2.dp

@Composable
private fun TimelineRow(entry: TimelineEntry, showYear: Boolean, onOpenCase: (String) -> Unit, modifier: Modifier = Modifier) {
  val caseId = entry.caseId
  // The spine is drawn, not laid out: a rule as a child would need an intrinsic measure pass to match the text height.
  val spine = Modifier.drawBehind { drawRect(Ink, topLeft = Offset(YearColumn.toPx(), 0f), size = Size(SpineWidth.toPx(), size.height)) }
  Row(modifier.fillMaxWidth().then(spine)) {
    Text(
      if (showYear) entry.year.toString() else "",
      modifier = Modifier.width(YearColumn).padding(top = 10.dp),
      style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
      color = Ink,
    )
    Column(
      Modifier.weight(1f).padding(start = SpineWidth + 16.dp, top = 10.dp, bottom = 14.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        KindMarker(entry.kind)
        MonoLabel(entry.kind.label)
      }
      TypeTitle(entry.title, size = 17.sp)
      Text(entry.detail, style = MaterialTheme.typography.bodySmall, color = InkBody)
      if (caseId != null) {
        Row(
          Modifier
            .padding(top = 4.dp)
            .defaultMinSize(minHeight = 48.dp)
            .background(Paper)
            .border(1.dp, Ink)
            .clickable(onClickLabel = "Open file", role = Role.Button) { onOpenCase(caseId) }
            .padding(horizontal = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("Open file $caseId", style = MaterialTheme.typography.labelMedium, color = Ink)
          Chevron(modifier = Modifier.padding(bottom = 4.dp))
        }
      }
    }
  }
}
