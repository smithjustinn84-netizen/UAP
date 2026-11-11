package com.openfile.uap.ui

import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.R
import com.openfile.uap.data.Archive
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.MapSheets
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.MonoFamily
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule

private val Land = Color(0xFFE4D7B4)

/** A case placed on the map, numbered in archive order so pins and list rows match. */
private data class Plotted(val number: Int, val record: CaseRecord)

@RawRes
private fun outlineFor(sheet: MapSheet): Int =
  when (sheet.id) {
    MapSheets.britishIsles.id -> R.raw.map_british_isles
    else -> R.raw.map_north_america
  }

@Composable
fun MapScreen(onOpenCase: (String) -> Unit) {
  val plotted = remember { Archive.cases.mapIndexed { i, c -> Plotted(i + 1, c) } }
  var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      MonoLabel("Open File")
      TypeTitle("Map", size = 32.sp)
      Text(
        "Where each case was reported. Every pin is approximate: some events covered a wide area or open sea.",
        style = MaterialTheme.typography.bodyLarge,
        color = InkBody,
      )
    }

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      MapSheets.all.forEach { sheet ->
        val onSheet = plotted.filter { MapSheets.sheetFor(it.record.point) == sheet }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          MonoLabel("Sheet · ${sheet.title}", color = Ink)
          SheetMap(
            sheet = sheet,
            pins = onSheet,
            selectedId = selectedId,
            onSelect = { selectedId = if (selectedId == it) null else it },
          )
          onSheet.firstOrNull { it.record.id == selectedId }?.let { SelectedCard(it, onOpen = { onOpenCase(it.record.id) }) }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TypeTitle("Plotted cases", size = 20.sp)
        PaperSheet {
          plotted.forEachIndexed { index, p ->
            if (index > 0) SolidRule()
            TapRow(onClick = { onOpenCase(p.record.id) }, onClickLabel = "Open file") {
              Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                PinBadge(p.number)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  TypeTitle(p.record.title, size = 16.sp)
                  Text("${p.record.dateShort} · ${p.record.placeShort}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
                }
                Chevron()
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SheetMap(sheet: MapSheet, pins: List<Plotted>, selectedId: String?, onSelect: (String) -> Unit) {
  val context = LocalContext.current
  val rings =
    remember(sheet.id) {
      context.resources.openRawResource(outlineFor(sheet)).bufferedReader().use { MapSheets.parseRings(it.readText()) }
    }
  val measurer = rememberTextMeasurer()
  val labelStyle = TextStyle(fontFamily = MonoFamily, fontSize = 11.sp, color = Paper)
  val description = "Map of ${sheet.title} with ${pins.size} plotted ${if (pins.size == 1) "case" else "cases"}. The cases are listed below the map."

  Canvas(
    Modifier.fillMaxWidth()
      .aspectRatio(sheet.aspectRatio)
      .background(Paper)
      .border(1.dp, Ink)
      .semantics { contentDescription = description }
      .pointerInput(pins) {
        detectTapGestures { tap ->
          val radius = 28.dp.toPx()
          pins
            .map { it to pinOffset(sheet, it, size.width.toFloat(), size.height.toFloat()) }
            .minByOrNull { (_, at) -> (at - tap).getDistance() }
            ?.takeIf { (_, at) -> (at - tap).getDistance() <= radius }
            ?.let { (p, _) -> onSelect(p.record.id) }
        }
      }
  ) {
    drawGraticule(sheet)
    rings.forEach { ring ->
      val path = Path()
      ring.forEachIndexed { i, (lon, lat) ->
        val at = sheet.project(lat.toDouble(), lon.toDouble())
        val o = Offset(at.x * size.width, at.y * size.height)
        if (i == 0) path.moveTo(o.x, o.y) else path.lineTo(o.x, o.y)
      }
      path.close()
      drawPath(path, Land)
      drawPath(path, Dash, style = Stroke(width = 0.75.dp.toPx()))
    }

    // Selected pin last so its ring sits on top. All pins share one ink: the map never codes a verdict.
    pins.sortedBy { it.record.id == selectedId }.forEach { p ->
      val at = pinOffset(sheet, p, size.width, size.height)
      val half = 11.dp.toPx()
      if (p.record.id == selectedId) {
        val ring = 17.dp.toPx()
        drawRect(Ink, topLeft = at - Offset(ring, ring), size = Size(ring * 2, ring * 2), style = Stroke(width = 2.dp.toPx()))
      }
      drawRect(Paper, topLeft = at - Offset(half + 1.5f, half + 1.5f), size = Size(half * 2 + 3f, half * 2 + 3f))
      drawRect(Ink, topLeft = at - Offset(half, half), size = Size(half * 2, half * 2))
      val label = measurer.measure(p.number.toString(), labelStyle)
      drawText(label, topLeft = at - Offset(label.size.width / 2f, label.size.height / 2f))
    }
  }
}

private fun pinOffset(sheet: MapSheet, p: Plotted, width: Float, height: Float): Offset {
  val at = sheet.project(p.record.point.lat, p.record.point.lon)
  return Offset(at.x * width, at.y * height)
}

/** Faint dashed lines every 10 degrees (5 on a small sheet), like the grid on a printed chart. */
private fun DrawScope.drawGraticule(sheet: MapSheet) {
  val step = if (sheet.north - sheet.south > 20) 10 else 5
  val dash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))
  val stroke = 0.75.dp.toPx()
  var lon = Math.ceil(sheet.west / step) * step
  while (lon <= sheet.east) {
    val x = sheet.project(sheet.north, lon).x * size.width
    drawLine(Rule, Offset(x, 0f), Offset(x, size.height), strokeWidth = stroke, pathEffect = dash)
    lon += step
  }
  var lat = Math.ceil(sheet.south / step) * step
  while (lat <= sheet.north) {
    val y = sheet.project(lat, sheet.west).y * size.height
    drawLine(Rule, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke, pathEffect = dash)
    lat += step
  }
}

@Composable
private fun PinBadge(number: Int) {
  Box(Modifier.size(24.dp).background(Ink), contentAlignment = Alignment.Center) {
    Text(number.toString(), style = MaterialTheme.typography.labelMedium, color = Paper)
  }
}

@Composable
private fun SelectedCard(p: Plotted, onOpen: () -> Unit) {
  PaperSheet {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        PinBadge(p.number)
        MonoLabel(p.record.id, color = Ink)
      }
      TypeTitle(p.record.title, size = 19.sp)
      Text("${p.record.dateShort} · ${p.record.placeShort}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
      Text(p.record.point.note, style = MaterialTheme.typography.bodySmall, color = InkBody)
      DashedRule()
      MonoLabel(p.record.status, color = Ink)
    }
    TapRow(onClick = onOpen, modifier = Modifier.defaultMinSize(minHeight = 52.dp).background(Ink)) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("Open file", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = Paper)
        Chevron(color = Paper)
      }
    }
  }
}
