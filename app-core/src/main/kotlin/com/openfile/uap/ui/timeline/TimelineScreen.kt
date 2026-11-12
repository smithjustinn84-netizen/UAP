package com.openfile.uap.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Archive
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.data.TimelineEntry
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.casefile.CaseThumbnail
import com.openfile.uap.ui.countOf
import com.openfile.uap.ui.pressable
import kotlinx.coroutines.launch

@Composable
fun TimelineRoute(
  archive: ArchiveRepository,
  outlines: MapOutlineRepository,
  onOpenCase: (String) -> Unit,
  viewModel: TimelineViewModel = viewModel { TimelineViewModel(archive, outlines, createSavedStateHandle()) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  TimelineScreen(uiState = uiState, onFilterChange = viewModel::onFilterChange, onOpenCase = onOpenCase)
}

/** Items in the list before the first row: the header, the kind key and the decade row. */
private const val LEADING_ITEMS = 3

/** A run of at least this many years with no entry is drawn as a break in the spine, so the jump is visible. */
private const val QUIET_YEARS = 25

private const val DECADE = 10

/** One row of the drawn timeline. */
private sealed interface TimelineItem {
  val key: String

  /** Heads each decade and stays pinned while its entries scroll past. */
  data class Decade(val decade: Int, val count: Int) : TimelineItem {
    override val key: String
      get() = "decade-$decade"
  }

  /** A stretch of [years] with nothing on the timeline, after [from]. */
  data class Quiet(val from: Int, val years: Int) : TimelineItem {
    override val key: String
      get() = "quiet-$from"
  }

  /** [showPicture] is true for the first entry linked to a file, so each file's picture appears once. */
  data class Entry(val entry: TimelineEntry, val showYear: Boolean, val showPicture: Boolean) : TimelineItem {
    override val key: String
      get() = "${entry.year}|${entry.title}"
  }
}

private fun timelineItems(entries: List<TimelineEntry>): List<TimelineItem> {
  val pictured = mutableSetOf<String>()
  return buildList {
    entries.forEachIndexed { index, entry ->
      val previous = entries.getOrNull(index - 1)
      val decade = entry.year / DECADE * DECADE
      if (previous != null && entry.year - previous.year >= QUIET_YEARS) {
        add(TimelineItem.Quiet(from = previous.year, years = entry.year - previous.year))
      }
      if (previous == null || previous.year / DECADE * DECADE != decade) {
        add(TimelineItem.Decade(decade, entries.count { it.year / DECADE * DECADE == decade }))
      }
      val caseId = entry.caseId
      add(TimelineItem.Entry(entry, showYear = previous?.year != entry.year, showPicture = caseId != null && pictured.add(caseId)))
    }
  }
}

@Composable
fun TimelineScreen(uiState: TimelineUiState, onFilterChange: (TimelineKind?) -> Unit, onOpenCase: (String) -> Unit) {
  val items = remember(uiState.entries) { timelineItems(uiState.entries) }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  // The status bar sits outside the list, so pinned decade headings stop beneath it.
  Column(Modifier.fillMaxSize()) {
    StatusBarSpacer()
    LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(bottom = 28.dp)) {
      item(key = "header", contentType = "header") {
        ScreenHeader(
          label = "Open File",
          title = "Timeline",
          intro = "Reported sightings alongside the inquiries that followed them and the books, films and broadcasts that retold them.",
        )
      }
      item(key = "kinds", contentType = "kinds") {
        KindFilter(only = uiState.only, kindCounts = uiState.kindCounts, onChange = onFilterChange)
      }
      item(key = "decades", contentType = "decades") {
        DecadeJump(
          starts = uiState.decadeStarts,
          onJump = { start ->
            val heading = items.indexOfFirst { it is TimelineItem.Decade && it.decade == start.decade }
            if (heading >= 0) scope.launch { listState.animateScrollToItem(LEADING_ITEMS + heading) }
          },
        )
      }
      items.forEach { item ->
        when (item) {
          is TimelineItem.Decade ->
            stickyHeader(key = item.key, contentType = "decade") { DecadeHeading(item, Modifier.animateItem()) }

          is TimelineItem.Quiet ->
            item(key = item.key, contentType = "quiet") { QuietStretch(item, uiState.only, Modifier.animateItem()) }

          is TimelineItem.Entry ->
            item(key = item.key, contentType = "entry") {
              val caseId = item.entry.caseId
              val file = caseId?.let { uiState.files[it] }
              TimelineRow(
                entry = item.entry,
                showYear = item.showYear,
                picture = if (item.showPicture) file else null,
                outline = file?.let { MapSheets.sheetFor(it.point) }?.let { uiState.outlines[it.id] }.orEmpty(),
                onOpenCase = onOpenCase,
                modifier = Modifier.animateItem(),
              )
            }
        }
      }
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
          .pressable(onClickLabel = "Jump to the ${start.decade}s", onClick = { onJump(start) })
          .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .background(Paper)
          .border(1.dp, Ink)
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
private val NodeSize = 12.dp

/** Where the spine's centre runs, measured from the row's start. */
private val SpineCentre = ScreenPadding + YearColumn + SpineWidth / 2

/** The content column starts this far in, clear of the year and the spine. */
private val ContentStart = YearColumn + SpineWidth + 16.dp

// The spine is drawn, not laid out: a rule as a child would need an intrinsic measure pass to match the text height.
private fun Modifier.spine(): Modifier =
  drawBehind {
    val x = SpineCentre.toPx()
    drawLine(Ink, Offset(x, 0f), Offset(x, size.height), strokeWidth = SpineWidth.toPx())
  }

/** The decade in large type, pinned under the status bar while its entries scroll past. */
@Composable
private fun DecadeHeading(heading: TimelineItem.Decade, modifier: Modifier = Modifier) {
  Column(modifier.fillMaxWidth().background(Manila)) {
    Row(
      Modifier
        .fillMaxWidth()
        .spine()
        .drawBehind {
          // A crossbar on the spine marks where the decade begins.
          val x = SpineCentre.toPx()
          val half = 9.dp.toPx()
          drawLine(Ink, Offset(x - half, size.height / 2), Offset(x + half, size.height / 2), strokeWidth = 4.dp.toPx())
        }
        .padding(start = ScreenPadding + ContentStart, end = ScreenPadding, top = 10.dp, bottom = 10.dp)
        .semantics(mergeDescendants = true) { heading() },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TypeTitle("${heading.decade}s", size = 24.sp, modifier = Modifier.weight(1f))
      MonoLabel(countOf(heading.count, "entry", "entries"))
    }
  }
}

/**
 * A long stretch with no entry: the spine turns dashed and is broken, and the gap is counted in years. Under a filter
 * it names the kind, since other kinds may fall in the gap.
 */
@Composable
private fun QuietStretch(quiet: TimelineItem.Quiet, only: TimelineKind?, modifier: Modifier = Modifier) {
  Box(
    modifier
      .fillMaxWidth()
      .height(76.dp)
      .drawBehind {
        val x = SpineCentre.toPx()
        val dash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 5.dp.toPx()))
        drawLine(Ink, Offset(x, 0f), Offset(x, size.height), strokeWidth = SpineWidth.toPx(), pathEffect = dash)
        // Two slanted ticks across the spine, the usual mark for a broken scale.
        val tick = 7.dp.toPx()
        val gap = 4.dp.toPx()
        val mid = size.height / 2
        listOf(mid - gap, mid + gap).forEach { y ->
          drawRect(Manila, topLeft = Offset(x - tick, y - 1.5.dp.toPx()), size = Size(tick * 2, 3.dp.toPx()))
          drawLine(Ink, Offset(x - tick, y + 3.dp.toPx()), Offset(x + tick, y - 3.dp.toPx()), strokeWidth = 1.5.dp.toPx())
        }
      }
      .padding(start = ScreenPadding + ContentStart, end = ScreenPadding),
    contentAlignment = Alignment.CenterStart,
  ) {
    MonoLabel("${quiet.years} years with no ${only?.label?.lowercase()?.plus(" ").orEmpty()}entry")
  }
}

/** Filled for a sighting, hollow for an inquiry, hollow with a dot for print or screen, as in the key. */
private fun DrawScope.drawNode(kind: TimelineKind, centre: Offset) {
  val half = NodeSize.toPx() / 2
  val topLeft = centre - Offset(half, half)
  val box = Size(half * 2, half * 2)
  val stroke = 2.dp.toPx()
  drawRect(if (kind == TimelineKind.Sighting) Ink else Paper, topLeft = topLeft, size = box)
  // The stroke is inset by half its width so the outline stays inside the node.
  val inset = Offset(stroke / 2, stroke / 2)
  drawRect(Ink, topLeft = topLeft + inset, size = Size(box.width - stroke, box.height - stroke), style = Stroke(stroke))
  if (kind == TimelineKind.Culture) {
    val dot = 3.dp.toPx()
    drawRect(Ink, topLeft = centre - Offset(dot / 2, dot / 2), size = Size(dot, dot))
  }
}

@Composable
private fun TimelineRow(
  entry: TimelineEntry,
  showYear: Boolean,
  picture: CaseRecord?,
  outline: SheetOutline,
  onOpenCase: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val caseId = entry.caseId
  Row(
    modifier
      .fillMaxWidth()
      .spine()
      // The node sits on the spine level with the kind label.
      .drawBehind { drawNode(entry.kind, Offset(SpineCentre.toPx(), 19.dp.toPx())) }
      .padding(horizontal = ScreenPadding),
  ) {
    Text(
      if (showYear) entry.year.toString() else "",
      modifier = Modifier.width(YearColumn).padding(top = 10.dp),
      style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
      color = Ink,
    )
    Column(
      Modifier.weight(1f).padding(start = SpineWidth + 16.dp, top = 12.dp, bottom = 18.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      MonoLabel(entry.kind.label)
      TypeTitle(entry.title, size = 17.sp)
      Text(entry.detail, style = MaterialTheme.typography.bodySmall, color = InkBody)
      if (caseId != null) FileLink(caseId, picture, outline, onOpen = { onOpenCase(caseId) })
    }
  }
}

/** Opens the linked file. The first entry for each file also shows its picture or locator map. */
@Composable
private fun FileLink(caseId: String, picture: CaseRecord?, outline: SheetOutline, onOpen: () -> Unit) {
  Column(
    Modifier
      .padding(top = 6.dp)
      .fillMaxWidth()
      .pressable(onClickLabel = "Open file", onClick = onOpen)
      .background(Paper)
      .border(1.dp, Ink),
  ) {
    if (picture != null) {
      CaseThumbnail(picture, outline, Modifier.height(96.dp))
      SolidRule(color = Ink)
    }
    Row(
      Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text("Open file $caseId", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = Ink)
      Chevron(modifier = Modifier.padding(bottom = 4.dp))
    }
  }
}

private fun previewTimelineState(only: TimelineKind?): TimelineUiState {
  val all = Archive.timeline
  val filtered = all.filter { only == null || it.kind == only }
  return TimelineUiState(
    only = only,
    entries = filtered,
    kindCounts = all.groupingBy { it.kind }.eachCount(),
    decadeStarts = decadeStarts(filtered),
    files = Archive.cases.associateBy { it.id },
  )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun TimelineScreenPreview() {
  DeskPreview {
    TimelineScreen(uiState = previewTimelineState(only = null), onFilterChange = {}, onOpenCase = {})
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun TimelineScreenFilteredPreview() {
  DeskPreview {
    TimelineScreen(uiState = previewTimelineState(only = TimelineKind.Inquiry), onFilterChange = {}, onOpenCase = {})
  }
}
