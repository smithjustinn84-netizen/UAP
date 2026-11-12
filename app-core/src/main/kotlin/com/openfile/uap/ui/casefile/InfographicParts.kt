package com.openfile.uap.ui.casefile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.SourceLabel
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.theme.StampRed
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.dashedBorder

// Pieces shared by the per-file briefing graphics, so each file's graphic reads as part of one set.

/** One figure on a briefing sheet. [sourceLabel] says where the figure comes from, never whether it is right. */
internal data class MetricCellData(
  val figure: String,
  val unit: String,
  val label: String,
  val sourceLabel: SourceLabel,
  val evidenceRef: String,
  val note: String,
)

/** Whether something survives to examine, rests on an account of it, or is not in the public record. */
internal enum class RecordState(val label: String) {
  Preserved("On file"),
  AccountOnly("Account only"),
  Missing("Not preserved"),
}

/** The manila strip that heads every panel. */
@Composable
internal fun PanelHeader(title: String, aside: String, asideColor: Color = InkSoft) {
  Row(
    Modifier.fillMaxWidth().background(Manila).padding(horizontal = 12.dp, vertical = 7.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    MonoLabel(title, Modifier.weight(1f), color = Ink)
    MonoLabel(aside, color = asideColor)
  }
  SolidRule(color = Ink)
}

/** Title sheet with a 2×2 grid of reported figures; [metrics] must hold four. */
@Composable
internal fun BriefingMetricsSheet(caseId: String, dateTag: String, title: String, lead: String, metrics: List<MetricCellData>) {
  PaperSheet {
    PanelHeader("DOSSIER BRIEFING SHEET · $caseId", dateTag, asideColor = StampRed)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(title, size = 20.sp)
      Text(lead, style = MaterialTheme.typography.bodySmall, color = InkBody)
    }
    SolidRule(color = Ink)
    Column(Modifier.fillMaxWidth()) {
      metrics.chunked(2).forEachIndexed { index, pair ->
        if (index > 0) SolidRule(color = Ink)
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
          MetricCell(pair[0], Modifier.weight(1f))
          Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
          MetricCell(pair[1], Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun MetricCell(data: MetricCellData, modifier: Modifier = Modifier) {
  Column(modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    MonoLabel(data.sourceLabel.label, color = StampRed)
    TypeTitle(data.figure, size = 21.sp)
    MonoLabel("${data.unit} · ${data.label}", color = Ink)
    MonoLabel(data.evidenceRef, color = InkSoft)
    DashedRule(Modifier.padding(vertical = 2.dp))
    Text(data.note, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp), color = InkBody)
  }
}

@Composable
internal fun RecordLegendItem(state: RecordState) {
  Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
    RecordStateBadge(state)
    MonoLabel(state.label, color = Ink)
  }
}

@Composable
internal fun RecordLegendRow() {
  Row(
    Modifier.fillMaxWidth().background(Paper).padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RecordState.entries.forEach { RecordLegendItem(it) }
  }
  SolidRule(color = Ink)
}

/**
 * Like `TimelineScreen.KindMarker`, uses fill and border geometry (filled, hollow, or dashed)
 * rather than color to distinguish what survives on file from what rests on testimony or is missing.
 */
@Composable
internal fun RecordStateBadge(state: RecordState) {
  when (state) {
    RecordState.Preserved -> Box(Modifier.size(10.dp).background(Ink).border(1.5.dp, Ink))
    RecordState.AccountOnly -> Box(Modifier.size(10.dp).background(Paper).border(1.5.dp, Ink))
    RecordState.Missing -> Box(Modifier.size(10.dp).background(Paper).dashedBorder(Dash, width = 1.dp))
  }
}

/** The two rows closing a graphic that jump to the file's Evidence and Media sections. */
@Composable
internal fun BriefingLinkRows(
  evidenceLabel: String,
  evidenceClickLabel: String,
  onOpenEvidence: () -> Unit,
  mediaLabel: String,
  mediaClickLabel: String,
  onOpenMedia: () -> Unit,
) {
  SolidRule(color = Ink)
  TapRow(onClick = onOpenEvidence, onClickLabel = evidenceClickLabel, modifier = Modifier.background(Manila.copy(alpha = 0.4f))) {
    LinkRowContent(evidenceLabel, Ink)
  }
  SolidRule(color = Ink)
  TapRow(onClick = onOpenMedia, onClickLabel = mediaClickLabel, modifier = Modifier.background(Ink)) {
    LinkRowContent(mediaLabel, Paper)
  }
}

@Composable
private fun LinkRowContent(label: String, color: Color) {
  Row(
    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    MonoLabel(label, color = color)
    Chevron(color = color)
  }
}

internal fun DrawScope.drawChartGrid() {
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
