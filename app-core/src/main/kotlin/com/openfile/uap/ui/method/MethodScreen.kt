package com.openfile.uap.ui.method

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.Evidence
import com.openfile.uap.data.SourceLabel
import com.openfile.uap.data.StoryKind
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.LeadText
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TitledBlock
import com.openfile.uap.ui.TypeTitle

private val sourceLabels = SourceLabel.entries.map { it.label to it.meaning }

private val statusTerms =
  listOf(
    "Official explanation on record" to "A public body has published an explanation. It does not mean the matter is settled.",
    "No official explanation" to "None has been published. It does not mean the event cannot be explained.",
    "Disputed" to "Witnesses or researchers have publicly rejected an explanation.",
  )

private val storyKinds = StoryKind.entries.map { it.label to it.meaning }

/** One collapsible topic under "More on method". */
private class MethodTopic(val title: String, val subtitle: String, val body: @Composable () -> Unit)

private val topics: List<MethodTopic> =
  listOf(
    MethodTopic("Status terms", "What each status does and does not mean") { Glossary(statusTerms) },
    MethodTopic("Explanations", "Listed A–Z, never ranked") {
      Body(
        "Listed A–Z, never ranked. Each one names who holds it, the points in its support and the points that " +
          "complicate it, and links to the evidence it draws on.",
      )
    },
    MethodTopic("Lore", "How each story has been told since") {
      Body(
        "Each file follows how its story has been told since, in date order. These cases are folklore as well as " +
          "record, and the retelling is part of what happened. Every step carries a label for how it reached the public.",
      )
      Glossary(storyKinds)
      Body(
        "Each step and each gap names a source you can open. Where the original could not be reached, the citation " +
          "says what was read in its place: a syndicated copy, a transcription or a summary.",
      )
      Body("Late does not mean false, and early does not mean true. The dates show what can be checked against records made at the time.")
    },
    MethodTopic("Missing from the file", "Records lost, destroyed or withheld") {
      Body(
        "Where a record is known to be lost, destroyed, withheld or never made, the file says so. " +
          "A gap shows that a record is missing. It cannot show what the record said.",
      )
    },
    MethodTopic("Threads", "Patterns across the files") {
      Body(
        "Some patterns recur from one file to the next. The threads list where each one appears and what should be " +
          "kept in mind about it. They describe the records and draw no conclusion about the events.",
      )
    },
    MethodTopic("Why keep these files", "Worth knowing accurately") {
      Body(
        "Whatever their cause, these reports have become part of modern lore. They are worth knowing accurately: " +
          "what was said, by whom, and what has been offered to account for it.",
      )
    },
  )

@Composable
fun MethodRoute(archive: ArchiveRepository) {
  // Any real item will do to show how an item reads; the first written-up one keeps it stable.
  val sample = remember(archive) { archive.cases.firstNotNullOfOrNull { it.detail?.evidence?.firstOrNull() } }
  MethodScreen(sample = sample)
}

@Composable
fun MethodScreen(sample: Evidence?) {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    ScreenHeader(
      label = "Method",
      title = "How to read a file",
      titleSize = 30.sp,
      intro =
        "We record what was reported, what is on record and every explanation that has been seriously put forward. " +
          "We do not rule on any of it.",
    )

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      if (sample != null) TitledBlock("One item, three things to read") { AnnotatedItem(sample) }
      TitledBlock("Source labels") { Glossary(sourceLabels) }
      TitledBlock("More on method") { Topics() }
    }
  }
}

/** A real evidence card with numbered marks on the parts the list under it explains. */
@Composable
private fun AnnotatedItem(item: Evidence) {
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Marker(1)
      MonoLabel("${item.id} · ${item.kind}", color = Ink, modifier = Modifier.weight(1f))
      MonoLabel(item.sourceLabel.label, color = Ink)
      Marker(2)
    }
    SolidRule(color = Ink)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      TypeTitle(item.title, size = 19.sp)
      Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Marker(3)
        LeadText("Keep in mind", item.caveat)
      }
    }
  }
  Column(Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Explained(1, "Reference", "The item's number in this file. Explanations cite it, so you can check what each one draws on.")
    Explained(2, "Source label", "It says where the item comes from, not whether it is true.")
    Explained(3, "Keep in mind", "The limit of this item as a record.")
  }
}

@Composable
private fun Marker(number: Int) {
  Box(Modifier.size(18.dp).background(Ink), contentAlignment = Alignment.Center) {
    Text(number.toString(), style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp), color = Paper)
  }
}

@Composable
private fun Explained(number: Int, term: String, meaning: String) {
  Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    Box(Modifier.padding(top = 2.dp)) { Marker(number) }
    Text(
      buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("$term. ") }
        append(meaning)
      },
      style = MaterialTheme.typography.bodyMedium,
      color = Ink,
    )
  }
}

/** The rest of the method, one topic open at a time so the screen stays short. */
@Composable
private fun Topics() {
  var open by rememberSaveable { mutableStateOf<Int?>(null) }
  PaperSheet {
    topics.forEachIndexed { index, topic ->
      if (index > 0) SolidRule()
      val expanded = open == index
      TapRow(
        onClick = { open = if (expanded) null else index },
        onClickLabel = if (expanded) "Close" else "Open",
        modifier = Modifier.defaultMinSize(minHeight = 56.dp).semantics { stateDescription = if (expanded) "Open" else "Closed" },
      ) {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              topic.title,
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
              color = Ink,
            )
            Text(topic.subtitle, style = MaterialTheme.typography.labelMedium, color = InkSoft)
          }
          Chevron(down = !expanded, up = expanded)
        }
      }
      if (expanded) {
        Column(Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          topic.body()
        }
      }
    }
  }
}

@Composable
private fun Body(text: String) {
  Text(text, style = MaterialTheme.typography.bodyMedium, color = InkBody)
}

@Composable
private fun Glossary(terms: List<Pair<String, String>>) {
  PaperSheet {
    Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
      terms.forEachIndexed { index, (term, meaning) ->
        Column(Modifier.padding(vertical = 11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
          MonoLabel(term, color = Ink)
          Text(meaning, style = MaterialTheme.typography.bodyMedium, color = Ink)
        }
        if (index < terms.lastIndex) SolidRule()
      }
    }
  }
}
