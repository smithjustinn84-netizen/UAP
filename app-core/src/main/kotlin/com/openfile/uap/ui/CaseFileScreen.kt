package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.Archive
import com.openfile.uap.data.CaseDetail
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.Evidence
import com.openfile.uap.data.Explanation
import com.openfile.uap.data.RecordGap
import com.openfile.uap.data.ReportMedia
import com.openfile.uap.data.StoryEntry
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper

private enum class Section(val label: String) {
  Summary("Summary"),
  Evidence("Evidence"),
  Media("Media"),
  Explanations("Explanations"),
  Lore("Lore"),
}

@Composable
fun CaseFileScreen(caseId: String, backLabel: String, onBack: () -> Unit) {
  val record = Archive.case(caseId)
  var sectionIndex by rememberSaveable { mutableIntStateOf(0) }
  val section = Section.entries[sectionIndex.coerceIn(0, Section.entries.lastIndex)]

  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    Row(
      Modifier.fillMaxWidth().padding(start = 8.dp, end = ScreenPadding, top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .clickable(onClickLabel = "Back to ${backLabel.lowercase()}", role = Role.Button, onClick = onBack)
          .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text("< $backLabel", style = MaterialTheme.typography.labelLarge, color = Ink)
      }
      Text(caseId, style = MaterialTheme.typography.labelMedium, color = InkSoft)
    }

    if (record == null) {
      Text(
        "This file could not be found.",
        modifier = Modifier.padding(ScreenPadding),
        style = MaterialTheme.typography.bodyLarge,
        color = InkBody,
      )
      return@Column
    }

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 12.dp, bottom = 20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      TypeTitle(record.title, size = if (section == Section.Summary) 30.sp else 22.sp)
      if (section == Section.Summary && record.detail != null) Stamp(record.detail.stamp)
    }

    val detail = record.detail
    if (detail == null) {
      UnwrittenFile(record)
    } else {
      SectionTabs(current = section, onSelect = { sectionIndex = it.ordinal })
      Column(
        Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
      ) {
        when (section) {
          Section.Summary -> SummarySection(record, detail, onOpen = { sectionIndex = it.ordinal })

          Section.Evidence ->
            EvidenceSection(
              detail = detail,
              onSeeMedia = { sectionIndex = Section.Media.ordinal },
              onSeeExplanations = { sectionIndex = Section.Explanations.ordinal },
            )

          Section.Media ->
            MediaSection(
              detail = detail,
              onSeeEvidence = { sectionIndex = Section.Evidence.ordinal },
              onSeeExplanations = { sectionIndex = Section.Explanations.ordinal },
            )

          Section.Explanations -> ExplanationsSection(detail, onSeeEvidence = { sectionIndex = Section.Evidence.ordinal })

          Section.Lore -> LoreSection(detail)
        }
      }
    }
  }
}

@Composable
private fun SectionTabs(current: Section, onSelect: (Section) -> Unit) {
  Row(Modifier.padding(horizontal = ScreenPadding).fillMaxWidth().height(48.dp).background(Paper).border(1.dp, Ink)) {
    Section.entries.forEachIndexed { index, s ->
      if (index > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
      // Width follows the length of the label, so five tabs fit a narrow phone without wrapping the longest.
      TabCell(label = s.label, selected = s == current, onClick = { onSelect(s) }, modifier = Modifier.weight(s.label.length + 2f))
    }
  }
}

/** A case that is in the index but has no evidence or explanations written up yet. */
@Composable
private fun UnwrittenFile(record: CaseRecord) {
  Column(
    Modifier.padding(start = ScreenPadding, end = ScreenPadding, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    PaperSheet {
      Column(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
        RecordField("Date", record.date)
        RecordField("Place", record.place)
        RecordField("On file", record.onFile.replaceFirstChar { it.uppercase() })
        RecordField("Status", record.status, showRule = false)
      }
    }
    PaperSheet(dashed = true) {
      Text(
        "The evidence and explanations for this file have not been written up yet.",
        modifier = Modifier.padding(14.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = InkBody,
      )
    }
  }
}

@Composable
private fun SummarySection(record: CaseRecord, detail: CaseDetail, onOpen: (Section) -> Unit) {
  PaperSheet {
    Column(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
      RecordField("Date", record.date)
      RecordField("Place", record.place)
      RecordField("Reported by", detail.reportedBy)
      RecordField("Official position", detail.officialPosition, showRule = false)
    }
  }

  TitledBlock("What was reported") {
    detail.reported.forEach { Text(it, style = MaterialTheme.typography.bodyLarge, color = Ink) }
  }

  if (record.id == "OF-2004-01") {
    TitledBlock("Incident briefing graphic") {
      NimitzIncidentInfographic(
        onOpenEvidence = { onOpen(Section.Evidence) },
        onOpenMedia = { onOpen(Section.Media) },
      )
    }
  }

  TitledBlock("In this file") {
    PaperSheet {
      FileLink("Evidence", "${detail.evidence.size} items, labelled by source") { onOpen(Section.Evidence) }
      if (detail.media.isNotEmpty()) {
        SolidRule()
        val kindsSummary = detail.media.map { it.kind.label.lowercase() }.distinct().joinToString(", ")
        FileLink("Media", "${detail.media.size} exhibits · $kindsSummary") { onOpen(Section.Media) }
      }
      SolidRule()
      FileLink("Explanations", "${detail.explanations.size} angles, listed A–Z") { onOpen(Section.Explanations) }
      if (detail.story.isNotEmpty()) {
        SolidRule()
        FileLink("Lore", "How the story has been told, ${detail.story.first().year}–${detail.story.last().year}") { onOpen(Section.Lore) }
      }
    }
  }

  TitledBlock("Not in dispute") { PaperSheet { BulletList(detail.notInDispute, Modifier.padding(14.dp)) } }

  TitledBlock("In dispute") { PaperSheet(dashed = true) { BulletList(detail.inDispute, Modifier.padding(14.dp)) } }

  Text(
    "This file records claims and their sources. It does not rule on them.",
    style = MaterialTheme.typography.labelMedium,
    color = InkSoft,
  )
}

@Composable
private fun TitledBlock(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    TypeTitle(title, size = 20.sp)
    content()
  }
}

@Composable
private fun FileLink(title: String, subtitle: String, onClick: () -> Unit) {
  TapRow(onClick = onClick, modifier = Modifier.defaultMinSize(minHeight = 60.dp)) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold), color = Ink)
        Text(subtitle, style = MaterialTheme.typography.labelMedium, color = InkSoft)
      }
      Chevron()
    }
  }
}

@Composable
private fun EvidenceSection(detail: CaseDetail, onSeeMedia: () -> Unit, onSeeExplanations: () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(
      "Each item is labelled by the kind of source it is. A label describes where something comes from, not whether it is true.",
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.evidence.forEach { item ->
      EvidenceCard(
        item = item,
        attachedMedia = detail.mediaForEvidence(item.id),
      )
    }
    if (detail.gaps.isNotEmpty()) MissingFromFile(detail.gaps)
    if (detail.media.isNotEmpty()) {
      TapRow(onClick = onSeeMedia, modifier = Modifier.defaultMinSize(minHeight = 52.dp).background(Paper).border(1.dp, Ink)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
          Text(
            "Browse all ${detail.media.size} media exhibits in this file",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = Ink,
          )
          Chevron(color = Ink)
        }
      }
    }
    TapRow(onClick = onSeeExplanations, modifier = Modifier.defaultMinSize(minHeight = 52.dp).background(Ink)) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
          "See how each explanation reads this evidence",
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.labelLarge,
          color = Paper,
        )
        Chevron(color = Paper)
      }
    }
  }
}

@Composable
private fun EvidenceCard(item: Evidence, attachedMedia: List<ReportMedia>) {
  var showMedia by rememberSaveable(item.id) { mutableStateOf(false) }
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("${item.id} · ${item.kind}", color = Ink)
      MonoLabel(item.sourceLabel, color = Ink)
    }
    SolidRule(color = Ink)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(item.title, size = 19.sp)
      Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      Text(
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Keep in mind: ") }
          append(item.caveat)
        },
        style = MaterialTheme.typography.bodySmall,
        color = InkBody,
      )
    }
    if (attachedMedia.isNotEmpty()) {
      SolidRule(color = Ink)
      TapRow(
        onClick = { showMedia = !showMedia },
        onClickLabel = if (showMedia) "Hide attached media" else "Show attached media",
        modifier =
          Modifier.background(Manila.copy(alpha = 0.4f)).semantics {
            stateDescription = if (showMedia) "Open" else "Closed"
          },
      ) {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val label =
            if (attachedMedia.size == 1) {
              "Attached exhibit: ${attachedMedia.first().id} · ${attachedMedia.first().kind.label}"
            } else {
              "Attached exhibits (${attachedMedia.size})"
            }
          MonoLabel(label, color = Ink)
          Chevron(down = true)
        }
      }
      if (showMedia) {
        SolidRule(color = Ink)
        Column(
          Modifier.background(Manila.copy(alpha = 0.25f)).padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          attachedMedia.forEach { media ->
            ReportMediaCard(media = media)
          }
        }
      }
    }
  }
}

/** Dashed like the "In dispute" sheet: these are the holes in the file, not items in it. */
@Composable
private fun MissingFromFile(gaps: List<RecordGap>) {
  PaperSheet(dashed = true) {
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      TypeTitle("Missing from the file", size = 19.sp)
      Text(
        "Records that would bear on this case but are lost, destroyed, withheld or were never made. " +
          "A gap shows that a record is missing, not what it said.",
        style = MaterialTheme.typography.bodySmall,
        color = InkBody,
      )
      gaps.forEach { gap ->
        DashedRule()
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          MonoLabel(gap.title, color = Ink)
          Text(gap.detail, style = MaterialTheme.typography.bodyMedium, color = Ink)
          gap.source?.let { SourceRow(it) }
        }
      }
    }
  }
}

@Composable
private fun LoreSection(detail: CaseDetail) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(
      "How this case has been told since it was first reported, in date order: who added what, and when. " +
        "A label says how a detail reached the public, not whether it is true.",
      modifier = Modifier.padding(bottom = 4.dp),
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.story.forEach { entry -> StoryCard(entry) }
    Text(
      "Late does not mean false, and early does not mean true. The dates show what can be checked against records made at the time.",
      modifier = Modifier.padding(top = 4.dp),
      style = MaterialTheme.typography.labelMedium,
      color = InkSoft,
    )
  }
}

@Composable
private fun StoryCard(entry: StoryEntry) {
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel(entry.date, color = Ink)
      MonoLabel(entry.kind.label, color = Ink)
    }
    SolidRule(color = Ink)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(entry.title, size = 18.sp)
      Text(entry.detail, style = MaterialTheme.typography.bodyMedium, color = Ink)
    }
    entry.source?.let {
      SolidRule()
      SourceRow(it, Modifier.padding(horizontal = 14.dp))
    }
  }
}

@Composable
private fun MediaSection(detail: CaseDetail, onSeeEvidence: () -> Unit, onSeeExplanations: () -> Unit) {
  val evidenceTitles = detail.evidence.associate { it.id to it.title }
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(
      "Archival diagrams, photographic plates, field recordings and verbatim document excerpts on file for this report. " +
        "Each exhibit is cross-referenced to its source in the evidence list.",
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.media.forEach { media ->
      ReportMediaCard(
        media = media,
        evidenceTitle = evidenceTitles[media.evidenceId],
        onOpenEvidence = onSeeEvidence,
      )
    }
    TapRow(onClick = onSeeExplanations, modifier = Modifier.defaultMinSize(minHeight = 52.dp).background(Ink)) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
          "See how each explanation reads this evidence",
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.labelLarge,
          color = Paper,
        )
        Chevron(color = Paper)
      }
    }
  }
}

@Composable
private fun ExplanationsSection(detail: CaseDetail, onSeeEvidence: () -> Unit) {
  // One angle open at a time; -1 means all closed.
  var open by rememberSaveable { mutableIntStateOf(0) }
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(
      "${detail.explanations.size} angles, listed A–Z and not by merit. Each shows who holds it, what supports it and what complicates it.",
      modifier = Modifier.padding(bottom = 4.dp),
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.explanations.forEachIndexed { index, explanation ->
      ExplanationCard(
        explanation = explanation,
        expanded = open == index,
        onToggle = { open = if (open == index) -1 else index },
        onSeeEvidence = onSeeEvidence,
      )
    }
    PaperSheet(dashed = true, modifier = Modifier.padding(top = 12.dp)) {
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MonoLabel("Official position", color = Ink)
        Text(detail.officialNote, style = MaterialTheme.typography.bodyMedium, color = Ink)
      }
    }
  }
}

@Composable
private fun ExplanationCard(explanation: Explanation, expanded: Boolean, onToggle: () -> Unit, onSeeEvidence: () -> Unit) {
  PaperSheet {
    TapRow(
      onClick = onToggle,
      onClickLabel = if (expanded) "Close" else "Open",
      modifier = Modifier.defaultMinSize(minHeight = 64.dp).semantics { stateDescription = if (expanded) "Open" else "Closed" },
    ) {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          TypeTitle(explanation.title, size = 18.sp)
          Text("Held by ${explanation.heldBy}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
        }
        Chevron(down = true)
      }
    }
    if (expanded) {
      SolidRule(color = Ink)
      Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          MonoLabel("Points in support", color = Ink)
          BulletList(explanation.supports)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          MonoLabel("Points that complicate it", color = Ink)
          BulletList(explanation.complicates)
        }
        DashedRule()
      }
      TapRow(onClick = onSeeEvidence, onClickLabel = "See the evidence") {
        Text(
          "Draws on ${explanation.drawsOn.joinToString(", ")}",
          modifier = Modifier.padding(horizontal = 14.dp),
          style = MaterialTheme.typography.labelMedium,
          color = Ink,
        )
      }
    }
  }
}
