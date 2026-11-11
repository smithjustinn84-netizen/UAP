package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
  // Ordinal of the one kind being shown; -1 shows every kind.
  var only by rememberSaveable { mutableIntStateOf(-1) }
  val entries = remember(only) { Archive.timeline.filter { only == -1 || it.kind.ordinal == only } }
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      MonoLabel("Open File")
      TypeTitle("Timeline", size = 32.sp)
      Text(
        "Reported sightings alongside the inquiries that followed them and the books, films and broadcasts that retold them.",
        style = MaterialTheme.typography.bodyLarge,
        color = InkBody,
      )
    }

    // The key doubles as the filter: tap a kind to show only that kind, tap it again to show everything.
    Column(Modifier.padding(horizontal = ScreenPadding).fillMaxWidth().background(Paper).border(1.dp, Ink)) {
      TimelineKind.entries.forEachIndexed { index, kind ->
        if (index > 0) SolidRule()
        val selected = only == kind.ordinal
        Row(
          Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .background(if (selected) Ink else Paper)
            .toggleable(value = selected, role = Role.Checkbox) { only = if (it) kind.ordinal else -1 }
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
            if (selected) "Only these · tap for all" else Archive.timeline.count { it.kind == kind }.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Paper else InkSoft,
          )
        }
      }
    }

    Column(Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp, bottom = 28.dp)) {
      entries.forEachIndexed { index, entry ->
        val showYear = index == 0 || entries[index - 1].year != entry.year
        TimelineRow(entry = entry, showYear = showYear, onOpenCase = onOpenCase)
      }
    }
  }
}

/** Filled for a sighting, hollow for an inquiry, hollow with a dot for print or screen: told apart by fill, not by color. */
@Composable
private fun KindMarker(kind: TimelineKind, inverted: Boolean = false) {
  val ink = if (inverted) Paper else Ink
  val paper = if (inverted) Ink else Paper
  Box(
    Modifier.size(10.dp).background(
      if (kind ==
        TimelineKind.Sighting
      ) {
        ink
      } else {
        paper
      },
    ).border(2.dp, ink),
    contentAlignment = Alignment.Center,
  ) {
    if (kind == TimelineKind.Culture) Box(Modifier.size(2.dp).background(ink))
  }
}

@Composable
private fun TimelineRow(entry: TimelineEntry, showYear: Boolean, onOpenCase: (String) -> Unit) {
  val caseId = entry.caseId
  val rowModifier =
    if (caseId != null) Modifier.clickable(onClickLabel = "Open file", role = Role.Button) { onOpenCase(caseId) } else Modifier
  Row(rowModifier.fillMaxWidth().height(IntrinsicSize.Min)) {
    Text(
      if (showYear) entry.year.toString() else "",
      modifier = Modifier.width(56.dp).padding(top = 10.dp),
      style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
      color = Ink,
    )
    Box(Modifier.width(2.dp).fillMaxHeight().background(Ink))
    Column(Modifier.weight(1f).padding(start = 16.dp, top = 10.dp, bottom = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
