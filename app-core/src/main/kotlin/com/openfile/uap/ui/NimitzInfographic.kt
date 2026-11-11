package com.openfile.uap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.MonoFamily
import com.openfile.uap.theme.OpenFileTheme
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.theme.StampRed

private data class MetricCellData(
  val figure: String,
  val unit: String,
  val label: String,
  val sourceLabel: String,
  val evidenceRef: String,
  val note: String,
)

private data class TacticalStation(
  val number: Int,
  val shortTab: String,
  val code: String,
  val title: String,
  val sourceLabel: String,
  val evidenceRef: String,
  val relativePos: Offset,
  val summary: String,
  val caveat: String,
)

private enum class RecordState(val label: String) {
  Preserved("On file"),
  AccountOnly("Account only"),
  Missing("Not preserved"),
}

private data class SequenceStep(
  val phase: String,
  val timeLabel: String,
  val title: String,
  val detail: String,
  val refs: String,
  val radar: RecordState,
  val visual: RecordState,
  val irPod: RecordState,
  val official: RecordState,
)

private val nimitzMetrics =
  listOf(
    MetricCellData(
      figure = "80,000 → 50",
      unit = "FT ALTITUDE",
      label = "AN/SPY-1B vertical drop",
      sourceLabel = "Reported",
      evidenceRef = "E-03",
      note = "Tracked by USS Princeton operators; raw radar tapes were not preserved.",
    ),
    MetricCellData(
      figure = "~40 FT",
      unit = "OBLONG HULL",
      label = "Visual craft estimate",
      sourceLabel = "First-hand",
      evidenceRef = "E-02 · E-04",
      note = "Two F/A-18F crews described a white wingless craft over 50–100 m churned water.",
    ),
    MetricCellData(
      figure = "~60 NM",
      unit = "RE-DETECTION",
      label = "Merge to CAP point",
      sourceLabel = "Reported",
      evidenceRef = "E-02 · E-03",
      note = "Princeton radar reported a track at the squadron's CAP point seconds after departure.",
    ),
    MetricCellData(
      figure = "76 SEC",
      unit = "ATFLIR CLIP",
      label = "Declassified 'FLIR1' video",
      sourceLabel = "Primary record",
      evidenceRef = "E-01 · E-05",
      note = "Recorded by a second F/A-18F crew at 20,000 ft; released by DoD in April 2020.",
    ),
  )

private val tacticalStations =
  listOf(
    TacticalStation(
      number = 1,
      shortTab = "1 · RADAR",
      code = "STA-01 · AN/SPY-1B PICKET",
      title = "USS Princeton (CG-59) & USS Nimitz (CVN-68)",
      sourceLabel = "Reported",
      evidenceRef = "E-03",
      relativePos = Offset(0.68f, 0.28f),
      summary =
        "Operating with Carrier Strike Group 11 roughly 100 NM southwest of San Diego, USS Princeton's AN/SPY-1B phased-array radar " +
          "logged multiple anomalous tracks over several days before vectoring two VFA-41 Super Hornets on 14 November 2004.",
      caveat = "accounts of the tracks come from operator testimony; digital radar recordings are not in the public record.",
    ),
    TacticalStation(
      number = 2,
      shortTab = "2 · MERGE",
      code = "STA-02 · VISUAL INTERCEPT",
      title = "Visual merge & ocean surface disturbance",
      sourceLabel = "First-hand",
      evidenceRef = "E-02 · E-04",
      relativePos = Offset(0.32f, 0.72f),
      summary =
        "Vectored ~40 NM southwest of the cruiser, Cdr. David Fravor (FastEagle 01) and Lt. Cdr. Alex Dietrich (FastEagle 02) " +
          "reported a 40-foot white oblong object hovering above a cross-shaped patch of churning water, then mirroring Fravor's " +
          "spiral descent before accelerating out of sight.",
      caveat = "visual estimates of size, range and speed were made by eye without a radar lock or gun-camera recording.",
    ),
    TacticalStation(
      number = 3,
      shortTab = "3 · CAP PT",
      code = "STA-03 · RENDEZVOUS POINT",
      title = "CAP point radar re-acquisition (~60 NM)",
      sourceLabel = "Reported",
      evidenceRef = "E-02 · E-03",
      relativePos = Offset(0.24f, 0.24f),
      summary =
        "Moments after the object vanished from the visual merge area, USS Princeton's radar operator radioed FastEagle 01 " +
          "to report that a contact had appeared at the flight's pre-briefed Combat Air Patrol (CAP) waypoint roughly 60 NM away.",
      caveat = "whether the contact at the CAP point was the same object seen at the merge rests on radar operator correlation.",
    ),
    TacticalStation(
      number = 4,
      shortTab = "4 · ATFLIR",
      code = "STA-04 · INFRARED TRACK",
      title = "Second flight ATFLIR capture ('FLIR1')",
      sourceLabel = "Primary record",
      evidenceRef = "E-01 · E-05",
      relativePos = Offset(0.62f, 0.68f),
      summary =
        "A follow-on F/A-18F crew (Lt. Chad Underwood) launched from USS Nimitz and acquired an infrared target at 20,000 ft " +
          "on the Raytheon ATFLIR pod, recording 76 seconds of narrow-FOV thermal video before the target exited the left frame.",
      caveat = "the second crew observed the target only on instruments, and the video lacks range telemetry.",
    ),
  )

private val sequenceSteps =
  listOf(
    SequenceStep(
      phase = "PHASE 01",
      timeLabel = "10–14 NOV 2004",
      title = "High-altitude tracks logged on AN/SPY-1B",
      detail = "USS Princeton radar operators track contacts near 80,000 ft descending rapidly over the training range.",
      refs = "E-03 · GAP",
      radar = RecordState.AccountOnly,
      visual = RecordState.Missing,
      irPod = RecordState.Missing,
      official = RecordState.Missing,
    ),
    SequenceStep(
      phase = "PHASE 02",
      timeLabel = "14 NOV · ~12:00 PST",
      title = "FastEagle 01 & 02 visual merge over churned sea",
      detail = "Two F/A-18F crews observe a white oblong object hovering over disturbed water; object mirrors spiral and departs.",
      refs = "E-02 · E-04",
      radar = RecordState.AccountOnly,
      visual = RecordState.AccountOnly,
      irPod = RecordState.Missing,
      official = RecordState.Missing,
    ),
    SequenceStep(
      phase = "PHASE 03",
      timeLabel = "14 NOV · +SECONDS",
      title = "Radar contact reported at CAP waypoint",
      detail = "Princeton controller informs returning flight crews that the track has reappeared ~60 NM away at their CAP coordinates.",
      refs = "E-02 · E-03",
      radar = RecordState.AccountOnly,
      visual = RecordState.Missing,
      irPod = RecordState.Missing,
      official = RecordState.Missing,
    ),
    SequenceStep(
      phase = "PHASE 04",
      timeLabel = "14 NOV · ~15:00 PST",
      title = "Second F/A-18F records 76-second 'FLIR1' video",
      detail =
        "Follow-on crew captures narrow-FOV infrared footage at 20,000 ft showing an oblong thermal signature " +
          "with no exhaust plume.",
      refs = "E-01",
      radar = RecordState.AccountOnly,
      visual = RecordState.Missing,
      irPod = RecordState.Preserved,
      official = RecordState.Preserved,
    ),
    SequenceStep(
      phase = "PHASE 05",
      timeLabel = "2007 · 2020 · 2023",
      title = "Forum leak, DoD release and Congressional hearing",
      detail =
        "Clip surfaces online in 2007; DoD formally releases it as 'unidentified' in April 2020; " +
          "Fravor testifies under oath in 2023.",
      refs = "E-01 · E-02 · E-05",
      radar = RecordState.Missing,
      visual = RecordState.AccountOnly,
      irPod = RecordState.Preserved,
      official = RecordState.Preserved,
    ),
  )

/**
 * Multi-panel archival dossier infographic for `OF-2004-01` (USS Nimitz encounter).
 * Combines key reported figures, an interactive operating-area tactical chart, an altitude descent profile,
 * and a four-channel evidence availability matrix.
 */
@Composable
fun NimitzIncidentInfographic(onOpenEvidence: () -> Unit, onOpenMedia: () -> Unit, modifier: Modifier = Modifier) {
  var selectedStationIndex by rememberSaveable { mutableIntStateOf(1) }
  val activeStation = tacticalStations[selectedStationIndex.coerceIn(0, tacticalStations.lastIndex)]

  Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    InfographicMetricsSheet()
    TacticalTheaterSheet(
      selectedStation = activeStation,
      onSelectStation = { index -> selectedStationIndex = index },
    )
    AltitudeProfileSheet()
    SequenceMatrixSheet(onOpenEvidence = onOpenEvidence, onOpenMedia = onOpenMedia)
  }
}

@Composable
private fun InfographicMetricsSheet() {
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 12.dp, vertical = 7.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("DOSSIER BRIEFING SHEET · OF-2004-01", color = Ink)
      MonoLabel("14 NOV 2004 · CSG-11", color = StampRed)
    }
    SolidRule(color = Ink)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle("Carrier Strike Group 11 · Encounter Synthesis", size = 20.sp)
      Text(
        "A side-by-side schematic of what was reported by radar operators and flight crews on 14 November 2004, " +
          "and what survives in the public record. Labels show the source of each figure, never a verdict.",
        style = MaterialTheme.typography.bodySmall,
        color = InkBody,
      )
    }
    SolidRule(color = Ink)
    // 2x2 metric grid with rules between cells
    Column(Modifier.fillMaxWidth()) {
      Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        MetricCell(nimitzMetrics[0], Modifier.weight(1f))
        Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
        MetricCell(nimitzMetrics[1], Modifier.weight(1f))
      }
      SolidRule(color = Ink)
      Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        MetricCell(nimitzMetrics[2], Modifier.weight(1f))
        Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
        MetricCell(nimitzMetrics[3], Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun MetricCell(data: MetricCellData, modifier: Modifier = Modifier) {
  Column(
    modifier.padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel(data.sourceLabel, color = StampRed)
      MonoLabel(data.evidenceRef, color = InkSoft)
    }
    TypeTitle(data.figure, size = 21.sp)
    MonoLabel("${data.unit} · ${data.label}", color = Ink)
    DashedRule(Modifier.padding(vertical = 2.dp))
    Text(data.note, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp), color = InkBody)
  }
}

@Composable
private fun TacticalTheaterSheet(selectedStation: TacticalStation, onSelectStation: (Int) -> Unit) {
  val measurer = rememberTextMeasurer()
  val pinLabelStyle = TextStyle(fontFamily = MonoFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Paper)
  val chartTagStyle = TextStyle(fontFamily = MonoFamily, fontSize = 10.sp, color = InkSoft)

  PaperSheet {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 12.dp, vertical = 7.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("PANEL I · SOCAL OPAREA GEOMETRY", color = Ink)
      MonoLabel("TAP STATION [1–4]", color = InkSoft)
    }
    SolidRule(color = Ink)
    Canvas(
      Modifier.fillMaxWidth()
        .height(216.dp)
        .background(Paper)
        .semantics {
          contentDescription =
            "Tactical schematic of the USS Nimitz operating area southwest of San Diego with four numbered stations. " +
            "Currently selected: ${selectedStation.title}."
        }
        .pointerInput(Unit) {
          detectTapGestures { tap ->
            val radius = 32.dp.toPx()
            tacticalStations
              .mapIndexed { idx, sta ->
                idx to Offset(sta.relativePos.x * size.width, sta.relativePos.y * size.height)
              }
              .minByOrNull { (_, pos) -> (pos - tap).getDistance() }
              ?.takeIf { (_, pos) -> (pos - tap).getDistance() <= radius }
              ?.let { (idx, _) -> onSelectStation(idx) }
          }
        },
    ) {
      drawTacticalGrid()
      drawTacticalTheater(
        stations = tacticalStations,
        selectedNumber = selectedStation.number,
        measurer = measurer,
        pinLabelStyle = pinLabelStyle,
        chartTagStyle = chartTagStyle,
      )
    }
    SolidRule(color = Ink)
    Row(Modifier.fillMaxWidth().height(44.dp).background(Paper)) {
      tacticalStations.forEachIndexed { index, sta ->
        if (index > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
        TabCell(
          label = sta.shortTab,
          selected = sta.number == selectedStation.number,
          onClick = { onSelectStation(index) },
          modifier = Modifier.weight(1f),
        )
      }
    }
    SolidRule(color = Ink)
    Column(
      Modifier.fillMaxWidth().background(Manila.copy(alpha = 0.35f)).padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        MonoLabel("${selectedStation.code} · ${selectedStation.evidenceRef}", color = Ink)
        MonoLabel(selectedStation.sourceLabel, color = StampRed)
      }
      TypeTitle(selectedStation.title, size = 18.sp)
      Text(selectedStation.summary, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      Text(
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Keep in mind: ") }
          append(selectedStation.caveat)
        },
        style = MaterialTheme.typography.bodySmall,
        color = InkBody,
      )
    }
  }
}

private fun DrawScope.drawTacticalGrid() {
  val step = 24.dp.toPx()
  var x = step
  while (x < size.width) {
    drawLine(Rule.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
    x += step
  }
  var y = step
  while (y < size.height) {
    drawLine(Rule.copy(alpha = 0.45f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
    y += step
  }
}

private fun DrawScope.drawTacticalTheater(
  stations: List<TacticalStation>,
  selectedNumber: Int,
  measurer: TextMeasurer,
  pinLabelStyle: TextStyle,
  chartTagStyle: TextStyle,
) {
  val w = size.width
  val h = size.height
  val p1 = Offset(stations[0].relativePos.x * w, stations[0].relativePos.y * h)
  val p2 = Offset(stations[1].relativePos.x * w, stations[1].relativePos.y * h)
  val p3 = Offset(stations[2].relativePos.x * w, stations[2].relativePos.y * h)
  val p4 = Offset(stations[3].relativePos.x * w, stations[3].relativePos.y * h)

  drawTacticalVectorsAndZones(p1, p2, p3, p4)
  drawTacticalAnnotations(p2, p3, measurer, chartTagStyle)
  drawTacticalStationPins(stations, selectedNumber, measurer, pinLabelStyle)
}

private fun DrawScope.drawTacticalVectorsAndZones(p1: Offset, p2: Offset, p3: Offset, p4: Offset) {
  val w = size.width
  val h = size.height
  val dash = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))
  val fineDash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))

  val coast = Path().apply {
    moveTo(w * 0.78f, 0f)
    cubicTo(w * 0.83f, h * 0.14f, w * 0.91f, h * 0.24f, w, h * 0.30f)
    lineTo(w, 0f)
    close()
  }
  drawPath(coast, color = Manila)
  drawPath(coast, color = Dash, style = Stroke(width = 1.dp.toPx(), pathEffect = fineDash))

  listOf(0.16f, 0.32f, 0.48f).forEach { rFrac ->
    drawCircle(
      color = Rule,
      radius = size.minDimension * rFrac,
      center = p1,
      style = Stroke(width = 1.dp.toPx(), pathEffect = fineDash),
    )
  }

  drawOval(
    color = Manila,
    topLeft = Offset(p2.x - 28.dp.toPx(), p2.y - 16.dp.toPx()),
    size = Size(56.dp.toPx(), 32.dp.toPx()),
  )
  drawOval(
    color = InkSoft,
    topLeft = Offset(p2.x - 28.dp.toPx(), p2.y - 16.dp.toPx()),
    size = Size(56.dp.toPx(), 32.dp.toPx()),
    style = Stroke(width = 1.2.dp.toPx(), pathEffect = fineDash),
  )

  drawLine(color = Ink, start = p1, end = p2, strokeWidth = 1.8.dp.toPx())
  drawLine(color = StampRed, start = p2, end = p3, strokeWidth = 2.2.dp.toPx(), pathEffect = dash)
  drawLine(color = InkSoft, start = p1, end = p4, strokeWidth = 1.6.dp.toPx(), pathEffect = dash)

  val fov = Path().apply {
    moveTo(p4.x, p4.y)
    lineTo(p4.x - 36.dp.toPx(), p4.y - 14.dp.toPx())
    lineTo(p4.x - 36.dp.toPx(), p4.y + 14.dp.toPx())
    close()
  }
  drawPath(fov, color = Manila.copy(alpha = 0.65f))
  drawPath(fov, color = Ink, style = Stroke(width = 1.dp.toPx(), pathEffect = fineDash))
}

private fun DrawScope.drawTacticalAnnotations(p2: Offset, p3: Offset, measurer: TextMeasurer, chartTagStyle: TextStyle) {
  drawText(
    measurer.measure("N ↑ · W-291 WARNING AREA", chartTagStyle),
    topLeft = Offset(10.dp.toPx(), 8.dp.toPx()),
  )
  drawText(
    measurer.measure("SAN DIEGO ~100 NM NE", chartTagStyle),
    topLeft = Offset(size.width - 138.dp.toPx(), 8.dp.toPx()),
  )
  drawText(
    measurer.measure("~60 NM TO CAP", chartTagStyle.copy(color = StampRed)),
    topLeft = Offset((p2.x + p3.x) * 0.5f - 28.dp.toPx(), (p2.y + p3.y) * 0.5f - 8.dp.toPx()),
  )
  drawText(
    measurer.measure("NOT TO SCALE", chartTagStyle),
    topLeft = Offset(10.dp.toPx(), size.height - 20.dp.toPx()),
  )
}

private fun DrawScope.drawTacticalStationPins(
  stations: List<TacticalStation>,
  selectedNumber: Int,
  measurer: TextMeasurer,
  pinLabelStyle: TextStyle,
) {
  stations.sortedBy { it.number == selectedNumber }.forEach { sta ->
    val at = Offset(sta.relativePos.x * size.width, sta.relativePos.y * size.height)
    val half = 11.dp.toPx()
    if (sta.number == selectedNumber) {
      val ring = 16.dp.toPx()
      drawRect(
        color = StampRed,
        topLeft = at - Offset(ring, ring),
        size = Size(ring * 2, ring * 2),
        style = Stroke(width = 2.dp.toPx()),
      )
    }
    drawRect(Paper, topLeft = at - Offset(half + 1.5f, half + 1.5f), size = Size(half * 2 + 3f, half * 2 + 3f))
    drawRect(
      color = if (sta.number == selectedNumber) StampRed else Ink,
      topLeft = at - Offset(half, half),
      size = Size(half * 2, half * 2),
    )
    val measured = measurer.measure(sta.number.toString(), pinLabelStyle)
    drawText(measured, topLeft = at - Offset(measured.size.width / 2f, measured.size.height / 2f))
  }
}

@Composable
private fun AltitudeProfileSheet() {
  val measurer = rememberTextMeasurer()
  val labelStyle = TextStyle(fontFamily = MonoFamily, fontSize = 10.sp, color = InkSoft)
  val calloutStyle = TextStyle(fontFamily = MonoFamily, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Ink)

  PaperSheet {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 12.dp, vertical = 7.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("PANEL II · VERTICAL ALTITUDE PROFILE", color = Ink)
      MonoLabel("80,000 FT → SEA LEVEL", color = StampRed)
    }
    SolidRule(color = Ink)
    Canvas(
      Modifier.fillMaxWidth()
        .height(212.dp)
        .background(Paper)
        .semantics {
          contentDescription =
            "Vertical altitude profile showing reported AN/SPY-1B radar drop from 80,000 feet to 20,000 feet and 50 feet, " +
            "F/A-18F spiral visual merge down from 20,000 feet over a 50 to 100 metre water disturbance, and ATFLIR lock at 20,000 feet."
        },
    ) {
      drawTacticalGrid()
      drawAltitudeCrossSection(measurer, labelStyle, calloutStyle)
    }
    SolidRule(color = Rule)
    Column(
      Modifier.fillMaxWidth().background(Paper).padding(horizontal = 12.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        MonoLabel("TRACK A (SOLID RED): AN/SPY-1B DROP (~0.78S REPORTED · E-03)", color = StampRed)
      }
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        MonoLabel("TRACK B (INK): F/A-18F SPIRAL MERGE & BOILING SEA (E-02, E-04)", color = Ink)
      }
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        MonoLabel("TRACK C (DASHED): ATFLIR POD LOCK AT 20,000 FT (FLIR1 · E-01)", color = InkSoft)
      }
    }
  }
}

private data class AltitudeBounds(
  val leftMargin: Float,
  val rightEdge: Float,
  val y80k: Float,
  val y28k: Float,
  val y20k: Float,
  val y50ft: Float,
  val ySea: Float,
)

private fun DrawScope.drawAltitudeCrossSection(measurer: TextMeasurer, labelStyle: TextStyle, calloutStyle: TextStyle) {
  val bounds =
    AltitudeBounds(
      leftMargin = 68.dp.toPx(),
      rightEdge = size.width - 12.dp.toPx(),
      y80k = size.height * 0.14f,
      y28k = size.height * 0.38f,
      y20k = size.height * 0.54f,
      y50ft = size.height * 0.78f,
      ySea = size.height * 0.86f,
    )

  drawAltitudeBands(bounds, measurer, labelStyle)
  drawSpy1bDropTrack(bounds, measurer, calloutStyle)
  drawSpiralMergeTrack(bounds, measurer, calloutStyle)
  drawAtflirLockTrack(bounds, measurer, calloutStyle)
}

private fun DrawScope.drawAltitudeBands(bounds: AltitudeBounds, measurer: TextMeasurer, labelStyle: TextStyle) {
  val fineDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
  drawRect(
    color = Manila.copy(alpha = 0.55f),
    topLeft = Offset(0f, bounds.ySea),
    size = Size(size.width, size.height - bounds.ySea),
  )
  drawLine(Ink, Offset(0f, bounds.ySea), Offset(size.width, bounds.ySea), strokeWidth = 2.dp.toPx())

  val bands =
    listOf(
      bounds.y80k to "80,000 FT",
      bounds.y28k to "28,000 FT",
      bounds.y20k to "20,000 FT",
      bounds.y50ft to "50 FT",
    )
  bands.forEach { (y, text) ->
    drawLine(Rule, Offset(bounds.leftMargin, y), Offset(bounds.rightEdge, y), strokeWidth = 1.dp.toPx(), pathEffect = fineDash)
    val measured = measurer.measure(text, labelStyle)
    drawText(measured, topLeft = Offset(8.dp.toPx(), y - measured.size.height / 2f))
  }
  drawText(measurer.measure("SEA LEVEL", labelStyle.copy(color = Ink)), topLeft = Offset(8.dp.toPx(), bounds.ySea + 3.dp.toPx()))
}

private fun DrawScope.drawSpy1bDropTrack(bounds: AltitudeBounds, measurer: TextMeasurer, calloutStyle: TextStyle) {
  val span = bounds.rightEdge - bounds.leftMargin
  val xDropStart = bounds.leftMargin + span * 0.06f
  val xDropVert = bounds.leftMargin + span * 0.24f
  val xHoldMid = bounds.leftMargin + span * 0.42f
  val xDropLow = bounds.leftMargin + span * 0.46f
  val trackA = Path().apply {
    moveTo(xDropStart, bounds.y80k)
    lineTo(xDropVert, bounds.y80k)
    lineTo(xDropVert + 6.dp.toPx(), bounds.y20k)
    lineTo(xHoldMid, bounds.y20k)
    lineTo(xDropLow, bounds.y50ft)
  }
  drawPath(trackA, color = StampRed, style = Stroke(width = 2.2.dp.toPx()))
  listOf(
    Offset(xDropVert, bounds.y80k),
    Offset(xDropVert + 6.dp.toPx(), bounds.y20k),
    Offset(xDropLow, bounds.y50ft),
  ).forEach { pt ->
    drawCircle(color = StampRed, radius = 3.5.dp.toPx(), center = pt)
  }
  drawText(
    measurer.measure("A · SPY-1B DROP", calloutStyle.copy(color = StampRed)),
    topLeft = Offset(xDropStart, bounds.y80k - 15.dp.toPx()),
  )
}

private fun DrawScope.drawSpiralMergeTrack(bounds: AltitudeBounds, measurer: TextMeasurer, calloutStyle: TextStyle) {
  val fineDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
  val xMergeCenter = bounds.leftMargin + (bounds.rightEdge - bounds.leftMargin) * 0.56f
  drawOval(
    color = Paper,
    topLeft = Offset(xMergeCenter - 24.dp.toPx(), bounds.ySea - 4.dp.toPx()),
    size = Size(48.dp.toPx(), 12.dp.toPx()),
  )
  drawOval(
    color = Ink,
    topLeft = Offset(xMergeCenter - 24.dp.toPx(), bounds.ySea - 4.dp.toPx()),
    size = Size(48.dp.toPx(), 12.dp.toPx()),
    style = Stroke(width = 1.3.dp.toPx(), pathEffect = fineDash),
  )
  val spiralDown = Path().apply {
    moveTo(xMergeCenter - 22.dp.toPx(), bounds.y28k)
    cubicTo(
      xMergeCenter + 18.dp.toPx(),
      bounds.y20k,
      xMergeCenter - 18.dp.toPx(),
      (bounds.y20k + bounds.y50ft) * 0.5f,
      xMergeCenter,
      bounds.y50ft - 8.dp.toPx(),
    )
  }
  drawPath(spiralDown, color = Ink, style = Stroke(width = 1.8.dp.toPx()))
  val objectClimb = Path().apply {
    moveTo(xMergeCenter, bounds.y50ft)
    cubicTo(
      xMergeCenter + 20.dp.toPx(),
      (bounds.y20k + bounds.y50ft) * 0.5f,
      xMergeCenter - 10.dp.toPx(),
      bounds.y20k,
      xMergeCenter + 26.dp.toPx(),
      bounds.y28k - 8.dp.toPx(),
    )
  }
  drawPath(objectClimb, color = StampRed, style = Stroke(width = 1.8.dp.toPx(), pathEffect = fineDash))
  drawCircle(color = Paper, radius = 4.5.dp.toPx(), center = Offset(xMergeCenter, bounds.y50ft))
  drawCircle(color = Ink, radius = 4.5.dp.toPx(), center = Offset(xMergeCenter, bounds.y50ft), style = Stroke(width = 1.8.dp.toPx()))
  drawText(
    measurer.measure("B · SPIRAL MERGE", calloutStyle),
    topLeft = Offset(xMergeCenter - 30.dp.toPx(), bounds.y28k - 15.dp.toPx()),
  )
}

private fun DrawScope.drawAtflirLockTrack(bounds: AltitudeBounds, measurer: TextMeasurer, calloutStyle: TextStyle) {
  val fineDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
  val span = bounds.rightEdge - bounds.leftMargin
  val xFlirJet = bounds.leftMargin + span * 0.78f
  val xFlirTarget = bounds.leftMargin + span * 0.95f
  val cone = Path().apply {
    moveTo(xFlirJet, bounds.y20k)
    lineTo(xFlirTarget, bounds.y20k - 14.dp.toPx())
    lineTo(xFlirTarget, bounds.y20k + 14.dp.toPx())
    close()
  }
  drawPath(cone, color = Manila.copy(alpha = 0.7f))
  drawPath(cone, color = InkSoft, style = Stroke(width = 1.3.dp.toPx(), pathEffect = fineDash))
  drawCircle(color = Ink, radius = 4.dp.toPx(), center = Offset(xFlirJet, bounds.y20k))
  drawCircle(color = StampRed, radius = 4.dp.toPx(), center = Offset(xFlirTarget - 4.dp.toPx(), bounds.y20k))
  drawText(
    measurer.measure("C · ATFLIR 20K", calloutStyle),
    topLeft = Offset(xFlirJet - 10.dp.toPx(), bounds.y20k - 18.dp.toPx()),
  )
}

@Composable
private fun SequenceMatrixSheet(onOpenEvidence: () -> Unit, onOpenMedia: () -> Unit) {
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 12.dp, vertical = 7.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("PANEL III · SEQUENCE & RECORD MATRIX", color = Ink)
      MonoLabel("2004 → 2023", color = InkSoft)
    }
    SolidRule(color = Ink)
    // Legend row: told apart by fill/outline, not color verdict
    Row(
      Modifier.fillMaxWidth().background(Paper).padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      RecordLegendItem(RecordState.Preserved)
      RecordLegendItem(RecordState.AccountOnly)
      RecordLegendItem(RecordState.Missing)
    }
    SolidRule(color = Ink)
    sequenceSteps.forEachIndexed { index, step ->
      if (index > 0) SolidRule()
      SequenceStepRow(step)
    }
    SolidRule(color = Ink)
    TapRow(
      onClick = onOpenEvidence,
      onClickLabel = "Inspect all 5 evidence items and 2 record gaps",
      modifier = Modifier.background(Manila.copy(alpha = 0.4f)),
    ) {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        MonoLabel("OPEN EVIDENCE & RECORD GAPS (E-01 TO E-05)", color = Ink)
        Chevron(color = Ink)
      }
    }
    SolidRule(color = Ink)
    TapRow(
      onClick = onOpenMedia,
      onClickLabel = "Inspect FLIR1 video and archival exhibits",
      modifier = Modifier.background(Ink),
    ) {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        MonoLabel("OPEN ATFLIR VIDEO & EXHIBITS (M-01 TO M-04)", color = Paper)
        Chevron(color = Paper)
      }
    }
  }
}

@Composable
private fun RecordLegendItem(state: RecordState) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RecordStateBadge(state)
    MonoLabel(state.label, color = Ink)
  }
}

/**
 * Like `TimelineScreen.KindMarker`, uses fill and border geometry (filled, hollow, or dashed)
 * rather than color to distinguish what survives on file from what rests on testimony or is missing.
 */
@Composable
private fun RecordStateBadge(state: RecordState) {
  when (state) {
    RecordState.Preserved -> Box(Modifier.size(10.dp).background(Ink).border(1.5.dp, Ink))
    RecordState.AccountOnly -> Box(Modifier.size(10.dp).background(Paper).border(1.5.dp, Ink))
    RecordState.Missing -> Box(Modifier.size(10.dp).background(Paper).dashedBorder(Dash, width = 1.dp))
  }
}

@Composable
private fun SequenceStepRow(step: SequenceStep) {
  Column(
    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("${step.phase} · ${step.timeLabel}", color = Ink)
      MonoLabel(step.refs, color = StampRed)
    }
    TypeTitle(step.title, size = 17.sp)
    Text(step.detail, style = MaterialTheme.typography.bodySmall, color = InkBody)
    Row(
      Modifier.fillMaxWidth()
        .padding(top = 4.dp)
        .background(Manila.copy(alpha = 0.35f))
        .border(1.dp, Rule)
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      ChannelChip("RADAR", step.radar)
      ChannelChip("VISUAL", step.visual)
      ChannelChip("IR POD", step.irPod)
      ChannelChip("ARCHIVE", step.official)
    }
  }
}

@Composable
private fun ChannelChip(channel: String, state: RecordState) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RecordStateBadge(state)
    MonoLabel(channel, color = if (state == RecordState.Missing) InkSoft else Ink)
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1520)
@Composable
private fun NimitzIncidentInfographicPreview() {
  OpenFileTheme {
    Column(Modifier.background(Manila).padding(16.dp)) {
      NimitzIncidentInfographic(onOpenEvidence = {}, onOpenMedia = {})
    }
  }
}
