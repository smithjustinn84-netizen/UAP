package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.Archive
import com.openfile.uap.data.TimelineEntry
import com.openfile.uap.data.TimelineKind
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Paper

@Composable
fun TimelineScreen(onOpenCase: (String) -> Unit) {
  // The one kind being shown, or null for every kind.
  var only by rememberSaveable { mutableStateOf<TimelineKind?>(null) }
  val entries = remember(only) { Archive.timeline.filter { only == null || it.kind == only } }
  val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = statusBarTop, bottom = 28.dp)) {
    item(key = "header", contentType = "header") {
      ScreenHeader(
        label = "Open File",
        title = "Timeline",
        intro = "Reported sightings alongside the inquiries that followed them and the books, films and broadcasts that retold them.",
      )
    }
    item(key = "kinds", contentType = "kinds") {
      KindFilter(only = only, onChange = { only = it }, modifier = Modifier.padding(bottom = 20.dp))
    }
    itemsIndexed(entries, key = { _, entry -> "${entry.year}|${entry.title}" }, contentType = { _, _ -> "entry" }) { index, entry ->
      val showYear = index == 0 || entries[index - 1].year != entry.year
      TimelineRow(entry = entry, showYear = showYear, onOpenCase = onOpenCase, modifier = Modifier.padding(horizontal = ScreenPadding))
    }
  }
}

private val kindCounts: Map<TimelineKind, Int> = Archive.timeline.groupingBy { it.kind }.eachCount()

/** The key doubles as the filter: tap a kind to show only that kind, tap it again to show everything. */
@Composable
private fun KindFilter(only: TimelineKind?, onChange: (TimelineKind?) -> Unit, modifier: Modifier = Modifier) {
  Column(modifier.padding(horizontal = ScreenPadding).fillMaxWidth().background(Paper).border(1.dp, Ink)) {
    TimelineKind.entries.forEachIndexed { index, kind ->
      if (index > 0) SolidRule()
      val selected = only == kind
      Row(
        Modifier
          .fillMaxWidth()
          .defaultMinSize(minHeight = 48.dp)
          .background(if (selected) Ink else Paper)
          .toggleable(value = selected, role = Role.Checkbox) { onChange(if (it) kind else null) }
          .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        KindMarker(kind, inverted = selected)
        Text(
          kind.label,
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.labelMedium,
          color = if (selected) Paper else Ink,
        )
        Text(
          if (selected) "Only these · tap for all" else (kindCounts[kind] ?: 0).toString(),
          style = MaterialTheme.typography.labelMedium,
          color = if (selected) Paper else InkSoft,
        )
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
  val rowModifier =
    if (caseId != null) modifier.clickable(onClickLabel = "Open file", role = Role.Button) { onOpenCase(caseId) } else modifier
  // The spine is drawn, not laid out: a rule as a child would need an intrinsic measure pass to match the text height.
  val spine = Modifier.drawBehind { drawRect(Ink, topLeft = Offset(YearColumn.toPx(), 0f), size = Size(SpineWidth.toPx(), size.height)) }
  Row(rowModifier.fillMaxWidth().then(spine)) {
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
        Text(
          "Open file $caseId",
          style = MaterialTheme.typography.labelMedium.copy(textDecoration = TextDecoration.Underline),
          color = Ink,
        )
      }
    }
  }
}
