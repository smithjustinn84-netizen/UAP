package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
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

private const val SEE_EXPLANATIONS = "See how each explanation reads this evidence"

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
  var section by rememberSaveable { mutableStateOf(Section.Summary) }

  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    Row(
      Modifier.fillMaxWidth().padding(start = 8.dp, end = ScreenPadding, top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      BackLink(backLabel, onBack)
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
      SectionTabs(current = section, onSelect = { section = it })
      Column(
        Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
      ) {
        when (section) {
          Section.Summary -> SummarySection(record, detail, onOpen = { section = it })

          Section.Evidence ->
            EvidenceSection(
              detail = detail,
              onSeeMedia = { section = Section.Media },
              onSeeExplanations = { section = Section.Explanations },
            )

          Section.Media ->
            MediaSection(
              detail = detail,
              onSeeEvidence = { section = Section.Evidence },
              onSeeExplanations = { section = Section.Explanations },
            )

          Section.Explanations -> ExplanationsSection(detail, onSeeEvidence = { section = Section.Evidence })

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

  if (record.id == NIMITZ_CASE_ID) {
    TitledBlock("Incident briefing graphic") {
      NimitzIncidentInfographic(
        onOpenEvidence = { onOpen(Section.Evidence) },
        onOpenMedia = { onOpen(Section.Media) },
      )
    }
  }

  TitledBlock("In this file") {
    PaperSheet {
      FileLink("Evidence", "${countOf(detail.evidence.size, "item")}, labelled by source") { onOpen(Section.Evidence) }
      if (detail.media.isNotEmpty()) {
        SolidRule()
        val kindsSummary = detail.media.map { it.kind.label.lowercase() }.distinct().joinToString(", ")
        FileLink("Media", "${countOf(detail.media.size, "exhibit")} · $kindsSummary") { onOpen(Section.Media) }
      }
      SolidRule()
      FileLink("Explanations", "${countOf(detail.explanations.size, "angle")}, listed A–Z") { onOpen(Section.Explanations) }
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
      ActionRow("Browse all ${countOf(detail.media.size, "media exhibit")} in this file", onClick = onSeeMedia, solid = false)
    }
    ActionRow(SEE_EXPLANATIONS, onClick = onSeeExplanations)
  }
}

@Composable
private fun EvidenceCard(item: Evidence, attachedMedia: List<ReportMedia>) {
  var showMedia by rememberSaveable(item.id) { mutableStateOf(false) }
  PaperSheet {
    CardHeader("${item.id} · ${item.kind}", item.sourceLabel.label)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(item.title, size = 19.sp)
      Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      LeadText("Keep in mind", item.caveat)
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
    CardHeader(entry.date, entry.kind.label)
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
    ActionRow(SEE_EXPLANATIONS, onClick = onSeeExplanations)
  }
}

@Composable
private fun ExplanationsSection(detail: CaseDetail, onSeeEvidence: () -> Unit) {
  // One angle open at a time, the first to begin with.
  var open by rememberSaveable { mutableStateOf<Int?>(0) }
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
        onToggle = { open = if (open == index) null else index },
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
