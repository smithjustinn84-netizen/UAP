package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.Paper

/** "1 file", "3 files". */
fun countOf(count: Int, singular: String, plural: String = singular + "s"): String = "$count ${if (count == 1) singular else plural}"

/** The heading block at the top of a section screen: a small label, the typed title and one line of introduction. */
@Composable
fun ScreenHeader(label: String, title: String, intro: String, top: Dp = 28.dp) {
  Column(
    Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = top, bottom = 16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    MonoLabel(label)
    TypeTitle(title, size = 32.sp)
    Text(intro, style = MaterialTheme.typography.bodyLarge, color = InkBody)
  }
}

/** The typed "< Files" link back to where a screen was opened from. */
@Composable
fun BackLink(label: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
      .clickable(onClickLabel = "Back to ${label.lowercase()}", role = Role.Button, onClick = onBack)
      .padding(horizontal = 12.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text("< $label", style = MaterialTheme.typography.labelLarge, color = Ink)
  }
}

/** A typed title over its content, for the blocks that make up a long sheet. */
@Composable
fun TitledBlock(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    TypeTitle(title, size = 20.sp)
    content()
  }
}

/** The reference line across the top of a card (file number on the left, source on the right) and the rule under it. */
@Composable
fun CardHeader(start: String, end: String) {
  Row(
    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    MonoLabel(start, color = Ink, modifier = Modifier.weight(1f))
    MonoLabel(end, color = Ink)
  }
  SolidRule(color = Ink)
}

/** A full-width button row with a chevron: solid ink for the main way on, paper for a secondary one. */
@Composable
fun ActionRow(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, solid: Boolean = true) {
  val surface = if (solid) Modifier.background(Ink) else Modifier.background(Paper).border(1.dp, Ink)
  val ink = if (solid) Paper else Ink
  TapRow(onClick = onClick, modifier = modifier.defaultMinSize(minHeight = 52.dp).then(surface)) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
      Text(text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = ink)
      Chevron(color = ink)
    }
  }
}

/** Small body text led by a bold label, as in "Keep in mind: …". */
@Composable
fun LeadText(lead: String, text: String, modifier: Modifier = Modifier) {
  Text(
    buildAnnotatedString {
      withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("$lead: ") }
      append(text)
    },
    modifier = modifier,
    style = MaterialTheme.typography.bodySmall,
    color = InkBody,
  )
}
