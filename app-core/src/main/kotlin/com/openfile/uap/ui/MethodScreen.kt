package com.openfile.uap.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody

private val sourceLabels =
  listOf(
    "Primary record" to "Made at or near the time by someone involved.",
    "First-hand" to "An account from a person who was present.",
    "Official record" to "Produced by a government or other public body.",
    "Reported" to "Described in another source. The original is not available to check.",
    "Not preserved" to "Seen at the time. Nothing survives to examine.",
  )

private val statusTerms =
  listOf(
    "Official explanation on record" to "A public body has published an explanation. It does not mean the matter is settled.",
    "No official explanation" to "None has been published. It does not mean the event cannot be explained.",
    "Disputed" to "Witnesses or researchers have publicly rejected an explanation.",
  )

private val storyKinds =
  listOf(
    "At the time" to "Said, written or done within days or weeks of the event.",
    "New account" to "A witness or researcher adds something that was not public before. The date is when it was first made public.",
    "Print or screen" to "A book, article, film or broadcast that carried the story to a wider audience.",
    "Inquiry or record" to "A public body or a study examines the case, or releases a record.",
    "Revised or withdrawn" to "Someone changes or takes back what they said earlier.",
  )

@Composable
fun MethodScreen() {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 28.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      MonoLabel("Open File")
      TypeTitle("How we file things", size = 32.sp)
      Text(
        "We record what was reported, what is on record and every explanation that has been seriously put forward. " +
          "We do not rule on any of it.",
        style = MaterialTheme.typography.bodyLarge,
        color = InkBody,
      )
    }

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      Block("Source labels") {
        Body("Every item of evidence carries one. It says where the item comes from, not whether it is true.")
        Glossary(sourceLabels)
      }
      Block("Status terms") { Glossary(statusTerms) }
      Block("Explanations") {
        Body(
          "Listed A–Z, never ranked. Each one names who holds it, the points in its support and the points that " +
            "complicate it, and links to the evidence it draws on.",
        )
      }
      Block("Lore") {
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
      }
      Block("Missing from the file") {
        Body(
          "Where a record is known to be lost, destroyed, withheld or never made, the file says so. " +
            "A gap shows that a record is missing. It cannot show what the record said.",
        )
      }
      Block("Threads") {
        Body(
          "Some patterns recur from one file to the next. The threads list where each one appears and what should be " +
            "kept in mind about it. They describe the records and draw no conclusion about the events.",
        )
      }
      Block("Why keep these files") {
        Body(
          "Whatever their cause, these reports have become part of modern lore. They are worth knowing accurately: " +
            "what was said, by whom, and what has been offered to account for it.",
        )
      }
    }
  }
}

@Composable
private fun Block(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    TypeTitle(title, size = 20.sp)
    content()
  }
}

@Composable
private fun Body(text: String) {
  Text(text, style = MaterialTheme.typography.bodyMedium, color = Ink)
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
