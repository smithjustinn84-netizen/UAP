package com.openfile.uap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.openfile.uap.EventMap
import com.openfile.uap.Files
import com.openfile.uap.Method
import com.openfile.uap.Timeline
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.theme.StampRed
import com.openfile.uap.theme.TypewriterFamily

val ScreenPadding = 20.dp

/**
 * Leading spacer for a screen's scrolling content, as tall as the status bar. Placing it inside the scroll area lets
 * content draw behind the transparent status bar and scroll under it, rather than being clipped beneath it.
 */
@Composable
fun StatusBarSpacer() {
  Spacer(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars))
}

/**
 * The manila desk every screen sits on, with the section tabs pinned to the bottom.
 *
 * The app draws edge to edge: the desk and the tab strip extend behind the system bars, side cutouts and the keyboard
 * are respected here, and each screen handles the status bar itself with [StatusBarSpacer].
 */
@Composable
fun AppFrame(current: NavKey, onTab: (NavKey) -> Unit, content: @Composable () -> Unit) {
  Column(
    Modifier.fillMaxSize()
      .background(Manila)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
      .imePadding()
  ) {
    Box(Modifier.weight(1f).fillMaxWidth()) { content() }
    Box(Modifier.fillMaxWidth().height(2.dp).background(Ink))
    // The strip's paper extends behind the navigation bar; the tabs themselves sit above it.
    Column(Modifier.fillMaxWidth().background(Paper).windowInsetsPadding(WindowInsets.navigationBars)) {
      Row(Modifier.fillMaxWidth().height(56.dp)) {
        listOf(Files to "Files", EventMap to "Map", Timeline to "Timeline", Method to "Method").forEachIndexed { index, (key, label) ->
          if (index > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
          TabCell(label = label.uppercase(), selected = key == current, onClick = { onTab(key) }, modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

/** One cell of a tab strip: inverted ink when selected. Shared by the bottom tabs and the in-file tabs. */
@Composable
fun TabCell(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier
      .fillMaxHeight()
      .background(if (selected) Ink else Color.Transparent)
      .selectable(selected = selected, role = Role.Tab, onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = if (selected) Paper else Ink, textAlign = TextAlign.Center)
  }
}

/** Small uppercase monospace label, as on a typed form. */
@Composable
fun MonoLabel(text: String, modifier: Modifier = Modifier, color: Color = InkSoft) {
  Text(text.uppercase(), modifier = modifier, style = MaterialTheme.typography.labelSmall, color = color)
}

/** Typewriter heading. */
@Composable
fun TypeTitle(text: String, size: TextUnit, modifier: Modifier = Modifier) {
  Text(
    text,
    modifier = modifier.semantics { heading() },
    fontFamily = TypewriterFamily,
    fontSize = size,
    lineHeight = size * 1.25f,
    color = Ink,
  )
}

/** A rubber stamp. Decorative framing only: every status uses the same ink, so color never implies a verdict. */
@Composable
fun Stamp(text: String, modifier: Modifier = Modifier) {
  Text(
    text.uppercase(),
    modifier = modifier.rotate(-2.5f).border(2.dp, StampRed).padding(horizontal = 10.dp, vertical = 5.dp),
    fontFamily = TypewriterFamily,
    fontSize = 12.sp,
    letterSpacing = 1.4.sp,
    color = StampRed,
  )
}

/** A sheet of paper with a solid ink outline. */
@Composable
fun PaperSheet(modifier: Modifier = Modifier, dashed: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
  val outline = if (dashed) Modifier.dashedBorder(Ink) else Modifier.border(1.dp, Ink)
  Column(modifier.fillMaxWidth().background(Paper).then(outline), content = content)
}

fun Modifier.dashedBorder(color: Color, width: Dp = 1.dp): Modifier = drawBehind {
  val stroke = width.toPx()
  drawRect(
    color = color,
    topLeft = Offset(stroke / 2, stroke / 2),
    size = size.copy(width = size.width - stroke, height = size.height - stroke),
    style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))),
  )
}

@Composable
fun SolidRule(modifier: Modifier = Modifier, color: Color = Rule) {
  Box(modifier.fillMaxWidth().height(1.dp).background(color))
}

@Composable
fun DashedRule(modifier: Modifier = Modifier) {
  Box(
    modifier.fillMaxWidth().height(1.dp).drawBehind {
      drawLine(
        color = Dash,
        start = Offset(0f, size.height / 2),
        end = Offset(size.width, size.height / 2),
        strokeWidth = size.height,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())),
      )
    }
  )
}

/** A labelled line on a typed record sheet. */
@Composable
fun RecordField(label: String, value: String, showRule: Boolean = true) {
  Column(Modifier.padding(vertical = 11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
    MonoLabel(label)
    Text(value, style = MaterialTheme.typography.bodyLarge, color = Ink)
  }
  if (showRule) SolidRule()
}

@Composable
fun BulletList(items: List<String>, modifier: Modifier = Modifier) {
  Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items.forEach { item ->
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.padding(top = 8.dp).size(5.dp).background(Ink))
        Text(item, style = MaterialTheme.typography.bodyMedium, color = InkBody)
      }
    }
  }
}

/** A full-width tappable row at least 48dp tall. */
@Composable
fun TapRow(onClick: () -> Unit, modifier: Modifier = Modifier, onClickLabel: String? = null, content: @Composable () -> Unit) {
  Box(
    modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).clickable(onClickLabel = onClickLabel, role = Role.Button, onClick = onClick),
    contentAlignment = Alignment.CenterStart,
  ) {
    content()
  }
}

/** A small drawn chevron: pointing right for "go to", down for "open or close". */
@Composable
fun Chevron(modifier: Modifier = Modifier, down: Boolean = false, color: Color = Ink) {
  Canvas(modifier.padding(top = 4.dp).size(14.dp)) {
    val w = size.width
    val stroke = 2.dp.toPx()
    val points =
      if (down) listOf(Offset(w * 0.15f, w * 0.35f), Offset(w * 0.5f, w * 0.7f), Offset(w * 0.85f, w * 0.35f))
      else listOf(Offset(w * 0.35f, w * 0.15f), Offset(w * 0.7f, w * 0.5f), Offset(w * 0.35f, w * 0.85f))
    drawLine(color, points[0], points[1], strokeWidth = stroke, cap = StrokeCap.Round)
    drawLine(color, points[1], points[2], strokeWidth = stroke, cap = StrokeCap.Round)
  }
}
