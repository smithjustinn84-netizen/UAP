package com.openfile.uap.ui.casefile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Archive
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.CaseDetail
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.Evidence
import com.openfile.uap.data.Explanation
import com.openfile.uap.data.Infographic
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.RecordGap
import com.openfile.uap.data.ReportMedia
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.data.SourceLabel
import com.openfile.uap.data.StoryEntry
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.PaperTint
import com.openfile.uap.ui.ActionRow
import com.openfile.uap.ui.BackLink
import com.openfile.uap.ui.BulletList
import com.openfile.uap.ui.CardHeader
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.FoldOut
import com.openfile.uap.ui.LeadText
import com.openfile.uap.ui.MOTION_MEDIUM
import com.openfile.uap.ui.MOTION_SHORT
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.RecordField
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.SourceRow
import com.openfile.uap.ui.Stamp
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TabCell
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TitledBlock
import com.openfile.uap.ui.TypeTitle
import com.openfile.uap.ui.UnfoldIn
import com.openfile.uap.ui.countOf
import com.openfile.uap.ui.deskShadow
import com.openfile.uap.ui.pressable
import com.openfile.uap.ui.rememberTick
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private const val SEE_EXPLANATIONS = "See how each explanation reads this evidence"

private enum class Section(val label: String) {
  Summary("Summary"),
  Evidence("Evidence"),
  Media("Media"),
  Explanations("Explanations"),
  Lore("Lore"),
}

@Composable
fun CaseFileRoute(
  archive: ArchiveRepository,
  caseId: String,
  backLabel: String,
  onBack: () -> Unit,
  onOpenMethod: () -> Unit,
  outlines: MapOutlineRepository,
  viewModel: CaseFileViewModel = viewModel { CaseFileViewModel(archive, outlines, caseId) },
) {
  val heroOutline by viewModel.heroOutline.collectAsStateWithLifecycle()
  CaseFileScreen(
    uiState = viewModel.uiState,
    heroOutline = heroOutline,
    backLabel = backLabel,
    onBack = onBack,
    onOpenMethod = onOpenMethod,
  )
}

/** Where things sit in the scrolling sheet, so the tabs can pin and a cited evidence item can be scrolled to. */
private class SheetAnchors {
  var content: LayoutCoordinates? = null

  /** Top of the inline section tabs within the content, or -1 before they are placed. */
  var tabsTop by mutableIntStateOf(-1)
}

@Composable
fun CaseFileScreen(
  uiState: CaseFileUiState,
  backLabel: String,
  onBack: () -> Unit,
  onOpenMethod: () -> Unit,
  heroOutline: SheetOutline = emptyList(),
) {
  val record = (uiState as? CaseFileUiState.Found)?.record
  var section by rememberSaveable { mutableStateOf(Section.Summary) }
  // The evidence item to scroll to once the Evidence section is laid out; cleared when it has been reached.
  var targetEvidence by remember { mutableStateOf<String?>(null) }
  val scroll = rememberScrollState()
  val scope = rememberCoroutineScope()
  val anchors = remember { SheetAnchors() }
  val density = LocalDensity.current
  val statusBar = WindowInsets.statusBars.getTop(density)
  val pinned by remember(statusBar) { derivedStateOf { anchors.tabsTop >= 0 && scroll.value + statusBar > anchors.tabsTop } }

  val open: (Section) -> Unit = { next ->
    section = next
    // Start the new section at its top, just under the pinned tabs, unless the reader is still above them.
    if (scroll.value + statusBar > anchors.tabsTop) scope.launch { scroll.scrollTo(anchors.tabsTop - statusBar) }
  }
  val openEvidence: (String) -> Unit = { id ->
    section = Section.Evidence
    targetEvidence = id
  }
  val onTargetPlaced: (LayoutCoordinates) -> Unit = { card ->
    targetEvidence = null
    anchors.content?.let { content ->
      val cardTop = content.localPositionOf(card, Offset.Zero).y
      val clearance = statusBar + with(density) { (TabsHeight + 12.dp).roundToPx() }
      scope.launch { scroll.animateScrollTo((cardTop - clearance).roundToInt()) }
    }
  }

  Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().verticalScroll(scroll).onPlaced { anchors.content = it }) {
      StatusBarSpacer()
      FileTopBar(caseId = uiState.caseId, backLabel = backLabel, onBack = onBack)
      if (record == null) {
        Text(
          "This file could not be found.",
          modifier = Modifier.padding(ScreenPadding),
          style = MaterialTheme.typography.bodyLarge,
          color = InkBody,
        )
        return@Column
      }
      CaseHeroBanner(record, outline = heroOutline, scrollOffset = { scroll.value }, modifier = Modifier.padding(top = 8.dp))
      Column(
        Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        val titleSize by animateFloatAsState(if (section == Section.Summary) 30f else 22f, tween(MOTION_MEDIUM), label = "title")
        TypeTitle(record.title, size = titleSize.sp)
        if (section == Section.Summary && record.detail != null) Stamp(record.detail.stamp, animateIn = true)
      }
      val detail = record.detail
      if (detail == null) {
        UnwrittenFile(record)
        return@Column
      }
      SectionTabs(
        current = section,
        onSelect = open,
        // While the pinned copy shows, this one is hidden from screen readers so the tabs are announced once.
        modifier =
          Modifier.onPlaced { anchors.tabsTop = it.positionInParent().y.roundToInt() }
            .then(if (pinned) Modifier.clearAndSetSemantics {} else Modifier),
      )
      FileSection(
        section = section,
        record = record,
        detail = detail,
        targetEvidence = targetEvidence,
        actions = FileActions(open = open, openEvidence = openEvidence, onTargetPlaced = onTargetPlaced, onOpenMethod = onOpenMethod),
      )
    }
    // Pinned copy of the tabs, shown once the inline ones have scrolled under the status bar.
    AnimatedVisibility(
      visible = pinned && record?.detail != null,
      enter = fadeIn(tween(MOTION_SHORT)) + slideInVertically(tween(MOTION_MEDIUM)) { -it / 2 },
      exit = fadeOut(tween(MOTION_SHORT)),
    ) {
      Column(Modifier.fillMaxWidth().background(Manila)) {
        StatusBarSpacer()
        SectionTabs(current = section, onSelect = open, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
        SolidRule(color = Ink)
      }
    }
  }
}

private val TabsHeight = 48.dp

/** What a section can do to the sheet around it. */
private class FileActions(
  val open: (Section) -> Unit,
  val openEvidence: (String) -> Unit,
  val onTargetPlaced: (LayoutCoordinates) -> Unit,
  val onOpenMethod: () -> Unit,
)

@Composable
private fun FileTopBar(caseId: String, backLabel: String, onBack: () -> Unit) {
  Row(
    Modifier.fillMaxWidth().padding(start = 8.dp, end = ScreenPadding, top = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    BackLink(backLabel, onBack)
    Text(caseId, style = MaterialTheme.typography.labelMedium, color = InkSoft)
  }
}

@Composable
private fun FileSection(section: Section, record: CaseRecord, detail: CaseDetail, targetEvidence: String?, actions: FileActions) {
  var shownLabel by rememberSaveable { mutableStateOf<SourceLabel?>(null) }
  // A fade only: the section must be laid out at its final place at once, so jumps to an evidence item land on it.
  Crossfade(targetState = section, animationSpec = tween(MOTION_MEDIUM), label = "section") { shown ->
    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      when (shown) {
        Section.Summary -> SummarySection(record, detail, onOpen = actions.open)

        Section.Evidence ->
          EvidenceSection(
            detail = detail,
            targetEvidence = targetEvidence,
            onTargetPlaced = actions.onTargetPlaced,
            onLabelClick = { shownLabel = it },
            onSeeMedia = { actions.open(Section.Media) },
            onSeeExplanations = { actions.open(Section.Explanations) },
          )

        Section.Media ->
          MediaSection(
            detail = detail,
            onLabelClick = { shownLabel = it },
            onSeeEvidence = { actions.open(Section.Evidence) },
            onSeeExplanations = { actions.open(Section.Explanations) },
          )

        Section.Explanations -> ExplanationsSection(detail, onOpenEvidence = actions.openEvidence)

        Section.Lore -> LoreSection(detail)
      }
    }
  }
  shownLabel?.let { label ->
    SourceLabelSheet(
      label = label,
      inThisFile = detail.evidence.filter { it.sourceLabel == label }.map { "${it.id} ${it.title}" },
      onSeeAllLabels = actions.onOpenMethod,
      onDismiss = { shownLabel = null },
    )
  }
}

@Composable
private fun SectionTabs(current: Section, onSelect: (Section) -> Unit, modifier: Modifier = Modifier) {
  Row(modifier.padding(horizontal = ScreenPadding).fillMaxWidth().height(TabsHeight).deskShadow(2.dp).background(Paper).border(1.dp, Ink)) {
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

  detail.infographic?.let { infographic ->
    TitledBlock("Incident briefing graphic") {
      when (infographic) {
        Infographic.NimitzBriefing ->
          NimitzIncidentInfographic(
            caseId = record.id,
            onOpenEvidence = { onOpen(Section.Evidence) },
            onOpenMedia = { onOpen(Section.Media) },
          )

        Infographic.TehranBriefing ->
          TehranIncidentInfographic(
            caseId = record.id,
            onOpenEvidence = { onOpen(Section.Evidence) },
            onOpenMedia = { onOpen(Section.Media) },
          )
      }
    }
  }

  TitledBlock("Not in dispute") { PaperSheet { BulletList(detail.notInDispute, Modifier.padding(14.dp)) } }

  TitledBlock("In dispute") { PaperSheet(dashed = true) { BulletList(detail.inDispute, Modifier.padding(14.dp)) } }

  TitledBlock("Read on") { ReadOn(detail, onOpen) }

  Text(
    "This file records claims and their sources. It does not rule on them.",
    style = MaterialTheme.typography.labelMedium,
    color = InkSoft,
  )
}

/** The way on from the summary: evidence first, as the next thing to read, then the other sections. */
@Composable
private fun ReadOn(detail: CaseDetail, onOpen: (Section) -> Unit) {
  TapRow(
    onClick = { onOpen(Section.Evidence) },
    modifier = Modifier.defaultMinSize(minHeight = 64.dp).deskShadow(2.dp).background(Ink),
    pressIn = true,
  ) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        MonoLabel("Next · ${Section.Evidence.ordinal + 1} of ${Section.entries.size}", color = Manila)
        Text(
          "Evidence: ${countOf(detail.evidence.size, "item")}, labelled by source",
          style = MaterialTheme.typography.labelLarge,
          color = Paper,
        )
      }
      Chevron(color = Paper)
    }
  }
  PaperSheet {
    if (detail.media.isNotEmpty()) {
      FileLink("Media", "${countOf(detail.media.size, "exhibit")}, each tied to its evidence item") { onOpen(Section.Media) }
      SolidRule()
    }
    FileLink("Explanations", "${countOf(detail.explanations.size, "angle")}, listed A–Z") { onOpen(Section.Explanations) }
    if (detail.story.isNotEmpty()) {
      SolidRule()
      FileLink("Lore", "How the story has been told since") { onOpen(Section.Lore) }
    }
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
private fun EvidenceSection(
  detail: CaseDetail,
  targetEvidence: String?,
  onTargetPlaced: (LayoutCoordinates) -> Unit,
  onLabelClick: (SourceLabel) -> Unit,
  onSeeMedia: () -> Unit,
  onSeeExplanations: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(
      "${countOf(detail.evidence.size, "item")}. Each carries a source label: tap one to see what it means.",
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.evidence.forEach { item ->
      EvidenceCard(
        item = item,
        attachedMedia = detail.mediaForEvidence(item.id),
        onLabelClick = onLabelClick,
        modifier = if (item.id == targetEvidence) Modifier.onPlaced(onTargetPlaced) else Modifier,
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
private fun EvidenceCard(
  item: Evidence,
  attachedMedia: List<ReportMedia>,
  onLabelClick: (SourceLabel) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMedia by rememberSaveable(item.id) { mutableStateOf(false) }
  val tick = rememberTick()
  PaperSheet(modifier) {
    CardHeader("${item.id} · ${item.kind}", item.sourceLabel.label, onEndClick = { onLabelClick(item.sourceLabel) })
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(item.title, size = 19.sp)
      Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      LeadText("Keep in mind", item.caveat)
    }
    if (attachedMedia.isNotEmpty()) {
      SolidRule(color = Ink)
      TapRow(
        onClick = {
          tick()
          showMedia = !showMedia
        },
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
          Chevron(down = !showMedia, up = showMedia)
        }
      }
      AnimatedVisibility(visible = showMedia, enter = UnfoldIn, exit = FoldOut) {
        Column {
          SolidRule(color = Ink)
          Column(
            Modifier.background(Manila.copy(alpha = 0.25f)).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            attachedMedia.forEach { media ->
              ReportMediaCard(media = media, onLabelClick = onLabelClick)
            }
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
private fun MediaSection(
  detail: CaseDetail,
  onLabelClick: (SourceLabel) -> Unit,
  onSeeEvidence: () -> Unit,
  onSeeExplanations: () -> Unit,
) {
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
        onLabelClick = onLabelClick,
        onOpenEvidence = onSeeEvidence,
      )
    }
    ActionRow(SEE_EXPLANATIONS, onClick = onSeeExplanations)
  }
}

@Composable
private fun ExplanationsSection(detail: CaseDetail, onOpenEvidence: (String) -> Unit) {
  // One angle open at a time, the first to begin with.
  var open by rememberSaveable { mutableStateOf<Int?>(0) }
  val evidenceTitles = detail.evidence.associate { it.id to it.title }
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      MonoLabel(countOf(detail.explanations.size, "angle"), color = Ink)
      MonoLabel("Listed A–Z · not by merit", color = Ink)
    }
    Text(
      "Each shows who holds it, what supports it, what complicates it and the evidence it draws on.",
      modifier = Modifier.padding(bottom = 4.dp),
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
    detail.explanations.forEachIndexed { index, explanation ->
      ExplanationCard(
        explanation = explanation,
        evidenceTitles = evidenceTitles,
        expanded = open == index,
        onToggle = { open = if (open == index) null else index },
        onOpenEvidence = onOpenEvidence,
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
private fun ExplanationCard(
  explanation: Explanation,
  evidenceTitles: Map<String, String>,
  expanded: Boolean,
  onToggle: () -> Unit,
  onOpenEvidence: (String) -> Unit,
) {
  val tick = rememberTick()
  PaperSheet {
    TapRow(
      onClick = {
        tick()
        onToggle()
      },
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
        Chevron(down = !expanded, up = expanded)
      }
    }
    AnimatedVisibility(visible = expanded, enter = UnfoldIn, exit = FoldOut) {
      Column {
        SolidRule(color = Ink)
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("Points in support", color = Ink)
            BulletList(explanation.supports)
          }
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("Points that complicate it", color = Ink)
            BulletList(explanation.complicates, hollow = true)
          }
          DashedRule()
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("Draws on · tap to open the item")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              explanation.drawsOn.forEach { id -> EvidenceChip(id, evidenceTitles[id], onClick = { onOpenEvidence(id) }) }
            }
          }
        }
      }
    }
  }
}

/** A cited evidence item; opens the Evidence section at that item. */
@Composable
private fun EvidenceChip(id: String, title: String?, onClick: () -> Unit) {
  Row(
    Modifier
      .pressable(onClickLabel = "Open evidence item", onClick = onClick)
      .defaultMinSize(minHeight = 48.dp)
      .background(PaperTint)
      .border(1.dp, Ink)
      .padding(horizontal = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(id, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = Ink)
    if (title != null) Text(title, style = MaterialTheme.typography.labelMedium, color = InkSoft)
  }
}

@Composable
private fun CaseFileSectionPreview(caseId: String, section: Section) {
  val record = requireNotNull(Archive.case(caseId))
  val detail = requireNotNull(record.detail)
  DeskPreview {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
      FileTopBar(caseId = record.id, backLabel = "Files", onBack = {})
      Column(
        Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 12.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        TypeTitle(record.title, size = if (section == Section.Summary) 30.sp else 22.sp)
        if (section == Section.Summary) Stamp(detail.stamp)
      }
      SectionTabs(current = section, onSelect = {})
      FileSection(
        section = section,
        record = record,
        detail = detail,
        targetEvidence = null,
        actions = FileActions(open = {}, openEvidence = {}, onTargetPlaced = {}, onOpenMethod = {}),
      )
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CaseFileSummaryPreview() {
  val record = requireNotNull(Archive.case("OF-2004-01"))
  DeskPreview {
    CaseFileScreen(uiState = CaseFileUiState.Found(record), backLabel = "Files", onBack = {}, onOpenMethod = {})
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CaseFileEvidencePreview() {
  CaseFileSectionPreview(caseId = "OF-1980-01", section = Section.Evidence)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CaseFileMediaPreview() {
  CaseFileSectionPreview(caseId = "OF-1980-01", section = Section.Media)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CaseFileExplanationsPreview() {
  CaseFileSectionPreview(caseId = "OF-2004-01", section = Section.Explanations)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun CaseFileLorePreview() {
  CaseFileSectionPreview(caseId = "OF-1947-02", section = Section.Lore)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 640)
@Composable
private fun CaseFileUnwrittenPreview() {
  val record = Archive.cases.first().copy(detail = null)
  DeskPreview {
    CaseFileScreen(uiState = CaseFileUiState.Found(record), backLabel = "Files", onBack = {}, onOpenMethod = {})
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 360)
@Composable
private fun CaseFileNotFoundPreview() {
  DeskPreview {
    CaseFileScreen(uiState = CaseFileUiState.NotFound("OF-1999-99"), backLabel = "Files", onBack = {}, onOpenMethod = {})
  }
}
