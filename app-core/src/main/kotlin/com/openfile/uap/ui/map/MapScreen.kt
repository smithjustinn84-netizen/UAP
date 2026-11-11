package com.openfile.uap.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.MonoFamily
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.ui.ActionRow
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.countOf

private val Land = Color(0xFFE4D7B4)
private val PinLabelStyle = TextStyle(fontFamily = MonoFamily, fontSize = 11.sp, color = Paper)

@Composable
fun MapRoute(
  archive: ArchiveRepository,
  outlines: MapOutlineRepository,
  onOpenCase: (String) -> Unit,
  viewModel: MapViewModel = viewModel { MapViewModel(archive, outlines, createSavedStateHandle()) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  // The selection is passed as a provider, read while drawing, so selecting a pin redraws the pins and rebuilds nothing.
  MapScreen(uiState = uiState, selectedId = { uiState.selectedId }, onPinSelect = viewModel::onPinSelect, onOpenCase = onOpenCase)
}

/**
 * The sheets in [uiState] keep their instances from one state to the next unless their own outline changes, so each
 * sheet skips recomposition and keeps its cached drawing.
 */
@Composable
fun MapScreen(uiState: MapUiState, selectedId: () -> String?, onPinSelect: (String) -> Unit, onOpenCase: (String) -> Unit) {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    ScreenHeader(
      label = "Open File",
      title = "Map",
      intro = "Where each case was reported. Every pin is approximate: some events covered a wide area or open sea.",
    )

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      uiState.sheets.forEach { sheet ->
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          MonoLabel("Sheet · ${sheet.sheet.title}", color = Ink)
          SheetMap(sheet = sheet.sheet, pins = sheet.pins, outline = sheet.outline, selectedId = selectedId, onSelect = onPinSelect)
          sheet.pins.firstOrNull { it.record.id == uiState.selectedId }?.let { SelectedCard(it, onOpen = { onOpenCase(it.record.id) }) }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TypeTitle("Plotted cases", size = 20.sp)
        PaperSheet {
          uiState.plotted.forEachIndexed { index, p ->
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

/** [selectedId] is a provider so that it is read while drawing: selecting a pin redraws the pins and rebuilds nothing. */
@Composable
private fun SheetMap(
  sheet: MapSheet,
  pins: List<PlottedCase>,
  outline: SheetOutline,
  selectedId: () -> String?,
  onSelect: (String) -> Unit,
) {
  val measurer = rememberTextMeasurer()
  val description =
    "Map of ${sheet.title} with ${countOf(pins.size, "plotted case")}. " +
      "The cases are listed below the map."

  Spacer(
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
      // The coastline paths and pin labels are built once per size, not on every draw.
      .drawWithCache {
        val land =
          outline.map { ring ->
            Path().apply {
              ring.forEachIndexed { i, (lon, lat) ->
                val at = sheet.project(lat.toDouble(), lon.toDouble())
                if (i == 0) moveTo(at.x * size.width, at.y * size.height) else lineTo(at.x * size.width, at.y * size.height)
              }
              close()
            }
          }
        val coast = Stroke(width = 0.75.dp.toPx())
        val pinCentres = pins.map { pinOffset(sheet, it, size.width, size.height) }
        val pinLabels = pins.map { measurer.measure(it.number.toString(), PinLabelStyle) }
        onDrawBehind {
          drawGraticule(sheet)
          land.forEach { path ->
            drawPath(path, Land)
            drawPath(path, Dash, style = coast)
          }

          // Selected pin last so its ring sits on top. All pins share one ink: the map never codes a verdict.
          val selected = pins.indexOfFirst { it.record.id == selectedId() }
          pins.indices.forEach { i -> if (i != selected) drawPin(pinCentres[i], pinLabels[i], selected = false) }
          if (selected >= 0) drawPin(pinCentres[selected], pinLabels[selected], selected = true)
        }
      },
  )
}

private fun DrawScope.drawPin(at: Offset, label: TextLayoutResult, selected: Boolean) {
  val half = 11.dp.toPx()
  if (selected) {
    val ring = 17.dp.toPx()
    drawRect(Ink, topLeft = at - Offset(ring, ring), size = Size(ring * 2, ring * 2), style = Stroke(width = 2.dp.toPx()))
  }
  drawRect(Paper, topLeft = at - Offset(half + 1.5f, half + 1.5f), size = Size(half * 2 + 3f, half * 2 + 3f))
  drawRect(Ink, topLeft = at - Offset(half, half), size = Size(half * 2, half * 2))
  drawText(label, topLeft = at - Offset(label.size.width / 2f, label.size.height / 2f))
}

private fun pinOffset(sheet: MapSheet, p: PlottedCase, width: Float, height: Float): Offset {
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
private fun SelectedCard(p: PlottedCase, onOpen: () -> Unit) {
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
    ActionRow("Open file", onClick = onOpen)
  }
}
