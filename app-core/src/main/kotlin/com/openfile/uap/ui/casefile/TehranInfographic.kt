package com.openfile.uap.ui.casefile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.openfile.uap.data.SourceLabel
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
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.TabCell
import com.openfile.uap.ui.TypeTitle

// Figures and sequence follow the US Defense Attaché cable of 23 Sep 1976 relaying the Iranian account (E-01)
// and Jafari's 2007 statement (E-02). Times are local, 19 September 1976.

private data class InterceptStation(
  val number: Int,
  val shortTab: String,
  val code: String,
  val title: String,
  val sourceLabel: SourceLabel,
  val evidenceRef: String,
  val relativePos: Offset,
  val summary: String,
  val caveat: String,
)

/** One reported system on one aircraft; [reported] is false where the record says nothing about it. */
private data class SystemReading(val text: String, val reported: Boolean = true)

private data class SystemRow(val system: String, val firstF4: SystemReading, val secondF4: SystemReading)

private data class SurvivingRecord(val title: String, val date: String, val refs: String, val state: RecordState, val note: String)

private val tehranMetrics =
  listOf(
    MetricCellData(
      figure = "2 × F-4",
      unit = "SCRAMBLED",
      label = "01:30 and 01:40",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01",
      note = "Both from Shahrokhi Air Base, after calls to the Air Force command post from about 00:30.",
    ),
    MetricCellData(
      figure = "27 NM",
      unit = "RADAR LOCK",
      label = "Second F-4",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01 · E-02",
      note = "Return described as comparable in size to a 707 tanker. No scope film has been published.",
    ),
    MetricCellData(
      figure = "25 NM",
      unit = "RANGE",
      label = "Losses and stand-off",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01",
      note = "First F-4 lost instruments and radio closing to 25 NM; the object then held 25 NM ahead of the second.",
    ),
    MetricCellData(
      figure = "AIM-9",
      unit = "NOT FIRED",
      label = "Weapons panel went off",
      sourceLabel = SourceLabel.FirstHand,
      evidenceRef = "E-01 · E-02",
      note = "As the second pilot tried to fire at a smaller object heading toward him. He broke away in a dive.",
    ),
  )

private val interceptStations =
  listOf(
    InterceptStation(
      number = 1,
      shortTab = "1 · CALLS",
      code = "STA-01 · ~00:30",
      title = "Calls from Shemiran, north Tehran",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01",
      relativePos = Offset(0.50f, 0.46f),
      summary =
        "At about 00:30 the Imperial Iranian Air Force command post received four calls from residents of the Shemiran area " +
          "reporting strange objects in the sky. A general, after speaking to Mehrabad tower, looked for himself, saw an " +
          "object like a star but bigger and brighter, and ordered an F-4 scrambled.",
      caveat = "the callers are known only through the cable's summary; none was interviewed on record.",
    ),
    InterceptStation(
      number = 2,
      shortTab = "2 · F-4 #1",
      code = "STA-02 · 01:30",
      title = "First F-4 · systems lost at 25 NM",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01",
      relativePos = Offset(0.26f, 0.20f),
      summary =
        "Airborne from Shahrokhi Air Base at 01:30 and sent toward a point about 40 NM north of Tehran. Closing to about 25 NM, " +
          "the crew reported losing all instrumentation and communications (UHF and intercom). Both returned once the pilot " +
          "turned back toward base.",
      caveat = "the cable relays the Iranian debriefing; no US personnel saw the events it describes.",
    ),
    InterceptStation(
      number = 3,
      shortTab = "3 · F-4 #2",
      code = "STA-03 · 01:40",
      title = "Second F-4 · lock, pursuit, launch attempt",
      sourceLabel = SourceLabel.FirstHand,
      evidenceRef = "E-01 · E-02",
      relativePos = Offset(0.74f, 0.74f),
      summary =
        "Scrambled at 01:40. The backseater reported a radar lock at 27 NM; at 25 NM the object moved away and held that range. " +
          "The pursuit continued south of Tehran, where a smaller bright object was reported to separate and head toward the " +
          "F-4. As the pilot, Maj. Parviz Jafari, tried to fire an AIM-9, the weapons control panel and his UHF and interphone " +
          "went off. He turned away in a negative-G dive.",
      caveat = "no radar film or scope photographs have been published, and Jafari's public account came 31 years later.",
    ),
    InterceptStation(
      number = 4,
      shortTab = "4 · LAKE",
      code = "STA-04 · NIGHT · DAWN",
      title = "Dry lake bed",
      sourceLabel = SourceLabel.OfficialRecord,
      evidenceRef = "E-01",
      relativePos = Offset(0.38f, 0.88f),
      summary =
        "The crew reported another object leaving the first and descending, appearing to come down gently on a dry lake bed. " +
          "At daylight they were flown there by helicopter and saw nothing; the cable adds that residents of a nearby house " +
          "spoke of a loud noise and a very bright light.",
      caveat = "the cable says the area was being checked for radiation; no results have been released.",
    ),
  )

private val systemRows =
  listOf(
    SystemRow(
      "Instruments",
      SystemReading("Lost closing to 25 NM; back on turning away"),
      SystemReading("Not stated", reported = false),
    ),
    SystemRow(
      "UHF & intercom",
      SystemReading("Lost closing to 25 NM; back on turning away"),
      SystemReading("Lost at the AIM-9 attempt; regained before the object's descent"),
    ),
    SystemRow(
      "Radar",
      SystemReading("Not stated", reported = false),
      SystemReading("Locked at 27 NM"),
    ),
    SystemRow(
      "Weapons panel",
      SystemReading("Not stated", reported = false),
      SystemReading("Went off at the AIM-9 attempt; regained before the object's descent"),
    ),
  )

private val survivingRecords =
  listOf(
    SurvivingRecord(
      "Tehran Journal report",
      "20 SEP 1976",
      "LORE",
      RecordState.Preserved,
      "Newspaper account published the following day.",
    ),
    SurvivingRecord(
      "Defense Attaché cable and DIA evaluation",
      "SEP 1976",
      "E-01 · M-01",
      RecordState.Preserved,
      "Sent 23 Sep 1976, relaying the Iranian account; filed with a DIA evaluation form. Released under FOIA.",
    ),
    SurvivingRecord(
      "Second F-4 radar scope film",
      "19 SEP 1976",
      "GAP",
      RecordState.Missing,
      "The lock at 27 NM is described in the cable; no film or photograph of the scope has been published.",
    ),
    SurvivingRecord(
      "Mehrabad tower observations",
      "19 SEP 1976",
      "E-01 · E-03",
      RecordState.AccountOnly,
      "The cable says the tower saw a later object once the pilot pointed it out. No tower or radar logs have been published.",
    ),
    SurvivingRecord(
      "Daylight visit to the lake bed",
      "19 SEP 1976",
      "E-01 · GAP",
      RecordState.AccountOnly,
      "Summarised in the cable; the promised radiation check results have not been released.",
    ),
    SurvivingRecord(
      "Jafari's public statement",
      "12 NOV 2007",
      "E-02 · M-02",
      RecordState.AccountOnly,
      "First-hand account given at the National Press Club, 31 years after the intercept.",
    ),
  )

/**
 * Briefing graphic for the 1976 Tehran intercepts, shown on the file whose detail carries
 * [com.openfile.uap.data.Infographic.TehranBriefing]; [caseId] is that file's id.
 * Combines reported figures, a tappable intercept schematic, the systems each crew reported losing,
 * and a list of which records survive.
 */
@Composable
fun TehranIncidentInfographic(caseId: String, onOpenEvidence: () -> Unit, onOpenMedia: () -> Unit, modifier: Modifier = Modifier) {
  var selectedStationIndex by rememberSaveable { mutableIntStateOf(2) }
  val activeStation = interceptStations[selectedStationIndex.coerceIn(0, interceptStations.lastIndex)]

  Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    BriefingMetricsSheet(
      caseId = caseId,
      dateTag = "19 SEP 1976",
      title = "Two Intercepts over Tehran · Record Synthesis",
      lead =
        "What the two F-4 crews and the people of Tehran reported on the night of 18–19 September 1976, as relayed to US " +
          "officials, and what survives in the public record. Labels show the source of each figure, never a verdict.",
      metrics = tehranMetrics,
    )
    InterceptSchematicSheet(
      selectedStation = activeStation,
      onSelectStation = { index -> selectedStationIndex = index },
    )
    SystemsReportedSheet()
    SurvivingRecordsSheet(onOpenEvidence = onOpenEvidence, onOpenMedia = onOpenMedia)
  }
}

@Composable
private fun InterceptSchematicSheet(selectedStation: InterceptStation, onSelectStation: (Int) -> Unit) {
  val measurer = rememberTextMeasurer()
  val pinLabelStyle = TextStyle(fontFamily = MonoFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Paper)
  val chartTagStyle = TextStyle(fontFamily = MonoFamily, fontSize = 10.sp, color = InkSoft)

  PaperSheet {
    PanelHeader("PANEL I · INTERCEPT SCHEMATIC", "TAP 1–4")
    Canvas(
      Modifier.fillMaxWidth()
        .height(224.dp)
        .background(Paper)
        .semantics {
          contentDescription =
            "Schematic, not to scale, of the area around Tehran with four numbered stations: the residents' calls, the first " +
            "F-4 north of the city, the second F-4's pursuit south of the city, and the dry lake bed. " +
            "Currently selected: ${selectedStation.title}."
        }
        .pointerInput(Unit) {
          detectTapGestures { tap ->
            val radius = 32.dp.toPx()
            interceptStations
              .mapIndexed { idx, sta -> idx to Offset(sta.relativePos.x * size.width, sta.relativePos.y * size.height) }
              .minByOrNull { (_, pos) -> (pos - tap).getDistance() }
              ?.takeIf { (_, pos) -> (pos - tap).getDistance() <= radius }
              ?.let { (idx, _) -> onSelectStation(idx) }
          }
        },
    ) {
      drawChartGrid()
      drawInterceptPaths()
      drawInterceptAnnotations(measurer, chartTagStyle)
      drawInterceptPins(selectedStation.number, measurer, pinLabelStyle)
    }
    SolidRule(color = Ink)
    Row(Modifier.fillMaxWidth().height(44.dp).background(Paper)) {
      interceptStations.forEachIndexed { index, sta ->
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
    StationDetail(selectedStation)
  }
}

@Composable
private fun StationDetail(station: InterceptStation) {
  Column(
    Modifier.fillMaxWidth().background(Manila.copy(alpha = 0.35f)).padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
      MonoLabel("${station.code} · ${station.evidenceRef}", color = Ink)
      MonoLabel(station.sourceLabel.label, color = StampRed)
    }
    TypeTitle(station.title, size = 18.sp)
    Text(station.summary, style = MaterialTheme.typography.bodyMedium, color = Ink)
    DashedRule()
    Text(
      buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Keep in mind: ") }
        append(station.caveat)
      },
      style = MaterialTheme.typography.bodySmall,
      color = InkBody,
    )
  }
}

private fun DrawScope.stationAt(index: Int): Offset =
  interceptStations[index].relativePos.let { Offset(it.x * size.width, it.y * size.height) }

/** Where the first crew was sent, about 40 NM north of the city; drawn hollow because no track of it survives. */
private fun DrawScope.objectAt(): Offset = Offset(size.width * 0.54f, size.height * 0.10f)

/** Shahrokhi lies off the chart to the south-west; both scrambles enter from this edge. */
private fun DrawScope.baseAt(): Offset = Offset(0f, size.height * 0.62f)

private fun DrawScope.drawInterceptPaths() {
  val dash = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))
  val fineDash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))
  val city = stationAt(0)
  val firstF4 = stationAt(1)
  val secondF4 = stationAt(2)
  val lakeBed = stationAt(3)
  val target = objectAt()

  // City block and lake bed, drawn as areas rather than points.
  val block = Size(60.dp.toPx(), 36.dp.toPx())
  drawRect(Manila, topLeft = city - Offset(block.width / 2, block.height / 2), size = block)
  drawRect(
    Dash,
    topLeft = city - Offset(block.width / 2, block.height / 2),
    size = block,
    style = Stroke(1.dp.toPx(), pathEffect = fineDash),
  )
  val lake = Size(64.dp.toPx(), 20.dp.toPx())
  drawOval(Manila, topLeft = lakeBed - Offset(lake.width / 2, lake.height / 2), size = lake)
  drawOval(
    InkSoft,
    topLeft = lakeBed - Offset(lake.width / 2, lake.height / 2),
    size = lake,
    style = Stroke(1.2.dp.toPx(), pathEffect = fineDash),
  )

  // First F-4: out from Shahrokhi, systems lost closing to 25 NM.
  drawLine(Ink, baseAt(), firstF4, strokeWidth = 1.6.dp.toPx(), pathEffect = dash)
  drawLine(InkSoft, firstF4, target, strokeWidth = 1.2.dp.toPx(), pathEffect = fineDash)

  // Second F-4: lock on the object, then a pursuit that the cable says continued south of Tehran.
  val pursuit = Path().apply {
    moveTo(target.x, target.y)
    quadraticTo(size.width * 0.92f, size.height * 0.34f, secondF4.x, secondF4.y)
  }
  drawPath(pursuit, color = StampRed, style = Stroke(width = 2.dp.toPx()))
  drawLine(StampRed, secondF4, lakeBed, strokeWidth = 1.8.dp.toPx(), pathEffect = dash)

  drawCircle(Paper, radius = 9.dp.toPx(), center = target)
  drawCircle(StampRed, radius = 9.dp.toPx(), center = target, style = Stroke(1.8.dp.toPx(), pathEffect = fineDash))
}

private fun DrawScope.drawInterceptAnnotations(measurer: TextMeasurer, tagStyle: TextStyle) {
  val target = objectAt()
  val firstF4 = stationAt(1)
  val secondF4 = stationAt(2)
  val lakeBed = stationAt(3)
  val red = tagStyle.copy(color = StampRed)
  drawText(measurer.measure("N ↑", tagStyle), topLeft = Offset(10.dp.toPx(), 8.dp.toPx()))
  drawText(measurer.measure("OBJECT · ~40 NM N", red), topLeft = target + Offset(14.dp.toPx(), -6.dp.toPx()))
  drawText(
    measurer.measure("25 NM", tagStyle),
    topLeft = Offset((firstF4.x + target.x) / 2 - 10.dp.toPx(), (firstF4.y + target.y) / 2 + 2.dp.toPx()),
  )
  // Right-aligned so the label sits inside the pursuit curve without crossing it.
  listOf("27 NM LOCK,", "PURSUIT SOUTH").forEachIndexed { line, text ->
    val measured = measurer.measure(text, red)
    drawText(measured, topLeft = Offset(size.width * 0.64f - measured.size.width, size.height * 0.22f + line * 13.dp.toPx()))
  }
  drawText(
    measurer.measure("DESCENT", red),
    topLeft = Offset(
      (secondF4.x + lakeBed.x) / 2 - 8.dp.toPx(),
      (secondF4.y + lakeBed.y) / 2 + 4.dp.toPx(),
    ),
  )
  drawText(measurer.measure("TEHRAN", tagStyle.copy(color = Ink)), topLeft = stationAt(0) + Offset(-20.dp.toPx(), 22.dp.toPx()))
  drawText(measurer.measure("DRY LAKE BED", tagStyle), topLeft = lakeBed + Offset(-36.dp.toPx(), 11.dp.toPx()))
  drawText(measurer.measure("← SHAHROKHI AB (HAMADAN)", tagStyle), topLeft = baseAt() + Offset(6.dp.toPx(), 4.dp.toPx()))
  drawText(measurer.measure("NOT TO SCALE", tagStyle), topLeft = Offset(size.width - 82.dp.toPx(), size.height - 18.dp.toPx()))
}

private fun DrawScope.drawInterceptPins(selectedNumber: Int, measurer: TextMeasurer, pinLabelStyle: TextStyle) {
  interceptStations.sortedBy { it.number == selectedNumber }.forEach { sta ->
    val at = Offset(sta.relativePos.x * size.width, sta.relativePos.y * size.height)
    val half = 11.dp.toPx()
    val selected = sta.number == selectedNumber
    if (selected) {
      val ring = 16.dp.toPx()
      drawRect(StampRed, topLeft = at - Offset(ring, ring), size = Size(ring * 2, ring * 2), style = Stroke(width = 2.dp.toPx()))
    }
    drawRect(Paper, topLeft = at - Offset(half + 1.5f, half + 1.5f), size = Size(half * 2 + 3f, half * 2 + 3f))
    drawRect(if (selected) StampRed else Ink, topLeft = at - Offset(half, half), size = Size(half * 2, half * 2))
    val measured = measurer.measure(sta.number.toString(), pinLabelStyle)
    drawText(measured, topLeft = at - Offset(measured.size.width / 2f, measured.size.height / 2f))
  }
}

@Composable
private fun SystemsReportedSheet() {
  PaperSheet {
    PanelHeader("PANEL II · SYSTEMS REPORTED LOST", "DEBRIEFINGS", asideColor = StampRed)
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).background(Manila.copy(alpha = 0.35f))) {
      SystemCell(0.8f) { MonoLabel("SYSTEM", color = InkSoft) }
      ColumnDivider()
      SystemCell { MonoLabel("F-4 #1 · 01:30", color = Ink) }
      ColumnDivider()
      SystemCell { MonoLabel("F-4 #2 · 01:40", color = Ink) }
    }
    systemRows.forEach { row ->
      SolidRule()
      Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        SystemCell(0.8f) { MonoLabel(row.system.uppercase(), color = Ink) }
        ColumnDivider()
        SystemCell { ReadingText(row.firstF4) }
        ColumnDivider()
        SystemCell { ReadingText(row.secondF4) }
      }
    }
    SolidRule(color = Ink)
    Text(
      buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Keep in mind: ") }
        append(
          "each entry is what a crew said, as relayed in the cable (E-01). " +
            "None is an instrument reading, and \"not stated\" means the record is silent, not that the system worked.",
        )
      },
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      style = MaterialTheme.typography.bodySmall,
      color = InkBody,
    )
  }
}

@Composable
private fun RowScope.SystemCell(weight: Float = 1f, content: @Composable () -> Unit) {
  Box(Modifier.weight(weight).padding(horizontal = 10.dp, vertical = 8.dp)) { content() }
}

@Composable
private fun ColumnDivider() {
  Box(Modifier.width(1.dp).fillMaxHeight().background(Rule))
}

@Composable
private fun ReadingText(reading: SystemReading) {
  Text(
    reading.text,
    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
    color = if (reading.reported) Ink else InkSoft,
  )
}

@Composable
private fun SurvivingRecordsSheet(onOpenEvidence: () -> Unit, onOpenMedia: () -> Unit) {
  PaperSheet {
    PanelHeader("PANEL III · WHAT SURVIVES", "1976 → 2007")
    RecordLegendRow()
    survivingRecords.forEachIndexed { index, record ->
      if (index > 0) SolidRule()
      SurvivingRecordRow(record)
    }
    BriefingLinkRows(
      evidenceLabel = "OPEN EVIDENCE & RECORD GAPS (E-01 TO E-03)",
      evidenceClickLabel = "Inspect all 3 evidence items and 2 record gaps",
      onOpenEvidence = onOpenEvidence,
      mediaLabel = "OPEN CABLE & TESTIMONY (M-01 TO M-02)",
      mediaClickLabel = "Inspect the DIA cable excerpt and Jafari's statement",
      onOpenMedia = onOpenMedia,
    )
  }
}

@Composable
private fun SurvivingRecordRow(record: SurvivingRecord) {
  Row(
    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(Modifier.padding(top = 4.dp)) { RecordStateBadge(record.state) }
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        MonoLabel("${record.date} · ${record.state.label}", color = Ink)
        MonoLabel(record.refs, color = StampRed)
      }
      TypeTitle(record.title, size = 16.sp)
      Text(record.note, style = MaterialTheme.typography.bodySmall, color = InkBody)
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1600)
@Composable
private fun TehranIncidentInfographicPreview() {
  OpenFileTheme {
    Column(Modifier.background(Manila).padding(16.dp)) {
      TehranIncidentInfographic(caseId = "OF-1976-01", onOpenEvidence = {}, onOpenMedia = {})
    }
  }
}
