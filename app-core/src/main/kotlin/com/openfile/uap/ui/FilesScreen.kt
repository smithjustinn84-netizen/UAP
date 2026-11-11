package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.Archive
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Paper

@Composable
fun FilesScreen(onOpenCase: (String) -> Unit, onOpenThreads: () -> Unit) {
  var query by rememberSaveable { mutableStateOf("") }
  var decade by rememberSaveable { mutableStateOf<Int?>(null) }
  val results = remember(query, decade) { Archive.search(query, decade) }
  val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(top = statusBarTop, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item(key = "header") {
      Column {
        FilesHeader()
        SearchField(query = query, onQueryChange = { query = it })
        DecadeChips(selected = decade, onSelect = { decade = it })
        Text(
          text = if (results.size == 1) "1 file · oldest first" else "${results.size} files · oldest first",
          modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 12.dp),
          style = MaterialTheme.typography.labelMedium,
          color = InkSoft,
        )
      }
    }
    if (results.isEmpty()) {
      item(key = "empty") {
        Text(
          "No files match. Try a place, a year, a name or a witness, or clear the decade filter.",
          modifier = Modifier.padding(horizontal = ScreenPadding),
          style = MaterialTheme.typography.bodyMedium,
          color = InkBody,
        )
      }
    }
    items(results, key = { it.id }) { record ->
      FolderCard(record = record, onClick = { onOpenCase(record.id) }, modifier = Modifier.padding(horizontal = ScreenPadding))
    }
    item(key = "threads") { ThreadsLink(onClick = onOpenThreads, modifier = Modifier.padding(horizontal = ScreenPadding)) }
  }
}

@Composable
private fun FilesHeader() {
  Column(
    Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp, bottom = 18.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      MonoLabel("Archive of reported sightings")
      Stamp("No verdicts")
    }
    TypeTitle("Open File", size = 40.sp)
    Text(
      "Historical UFO and UAP reports, the records behind them, and every explanation on file.",
      style = MaterialTheme.typography.bodyLarge,
      color = InkBody,
    )
  }
}

/** The way in to the cross-file threads. Sits after the folders, like the index sheet at the back of a drawer. */
@Composable
private fun ThreadsLink(onClick: () -> Unit, modifier: Modifier = Modifier) {
  TapRow(onClick = onClick, onClickLabel = "Open threads", modifier = modifier.defaultMinSize(minHeight = 64.dp).background(Ink)) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text("Threads across the files", style = MaterialTheme.typography.labelLarge, color = Paper)
        Text(
          "${Archive.threads.size} patterns that recur from one case to the next",
          style = MaterialTheme.typography.labelMedium,
          color = Paper,
        )
      }
      Chevron(color = Paper)
    }
  }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
  Column(Modifier.padding(horizontal = ScreenPadding), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
        Box(
          Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).background(Paper).border(1.dp, Ink).padding(horizontal = 12.dp),
          contentAlignment = Alignment.CenterStart,
        ) {
          if (query.isEmpty()) {
            Text("Place, year, name or witness", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp), color = InkSoft)
          }
          field()
        }
      },
    )
  }
}

@Composable
private fun DecadeChips(selected: Int?, onSelect: (Int?) -> Unit) {
  Row(
    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = ScreenPadding, end = ScreenPadding, top = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Chip(label = "All", selected = selected == null, onClick = { onSelect(null) })
    Archive.decades.forEach { d -> Chip(label = "${d}s", selected = selected == d, onClick = { onSelect(d) }) }
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
        Row(
          Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          MonoLabel(record.status, color = Ink, modifier = Modifier.weight(1f))
          val mediaCount = record.detail?.media?.size ?: 0
          if (mediaCount > 0) {
            MonoLabel("$mediaCount exhibits", color = InkSoft)
          }
        }
      }
    }
  }
}
