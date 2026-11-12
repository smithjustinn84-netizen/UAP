package com.openfile.uap.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.openfile.uap.Files
import com.openfile.uap.TopLevelSection
import com.openfile.uap.TopLevelSections
import com.openfile.uap.data.Citation
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
fun AppFrame(tabs: List<TopLevelSection>, current: NavKey, onTab: (NavKey) -> Unit, content: @Composable () -> Unit) {
  Column(
    Modifier.fillMaxSize()
      .background(Manila)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
      .imePadding(),
  ) {
    Box(Modifier.weight(1f).fillMaxWidth()) { content() }
    Box(Modifier.fillMaxWidth().height(2.dp).background(Ink))
    // The strip's paper extends behind the navigation bar; the tabs themselves sit above it.
    Column(Modifier.fillMaxWidth().background(Paper).windowInsetsPadding(WindowInsets.navigationBars)) {
      Row(Modifier.fillMaxWidth().height(56.dp)) {
        tabs.forEachIndexed { index, tab ->
          if (index > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(Ink))
          TabCell(label = tab.label.uppercase(), selected = tab.key == current, onClick = {
            onTab(tab.key)
          }, modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

/** One cell of a tab strip: inverted ink when selected. Shared by the bottom tabs and the in-file tabs. */
@Composable
fun TabCell(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val tick = rememberTick()
  Box(
    modifier
      .fillMaxHeight()
      .background(selectionColor(selected, Ink, Ink.copy(alpha = 0f)))
      .selectable(selected = selected, role = Role.Tab, onClick = {
        if (!selected) tick()
        onClick()
      }),
    contentAlignment = Alignment.Center,
  ) {
    Text(label, style = MaterialTheme.typography.labelMedium, color = selectionColor(selected, Paper, Ink), textAlign = TextAlign.Center)
  }
}

/** Small uppercase monospace label, as on a typed form. */
@Composable
fun MonoLabel(text: String, modifier: Modifier = Modifier, color: Color = InkSoft) {
  Text(text.uppercase(), modifier = modifier, style = MaterialTheme.typography.labelSmall, color = color)
}

/** Typewriter heading. */
@Composable
fun TypeTitle(text: String, size: TextUnit, modifier: Modifier = Modifier, color: Color = Ink) {
  Text(
    text,
    modifier = modifier.semantics { heading() },
    fontFamily = TypewriterFamily,
    fontSize = size,
    lineHeight = size * 1.25f,
    color = color,
  )
}

/**
 * A rubber stamp. Decorative framing only: every status uses the same ink, so color never implies a verdict.
 * With [animateIn] it is pressed onto the sheet once when it first appears.
 */
@Composable
fun Stamp(text: String, modifier: Modifier = Modifier, animateIn: Boolean = false) {
  val press = remember { Animatable(if (animateIn) 0f else 1f) }
  LaunchedEffect(Unit) {
    if (press.value < 1f) press.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))
  }
  Text(
    text.uppercase(),
    modifier =
      modifier
        .graphicsLayer {
          val scale = 1f + (1f - press.value) * STAMP_LIFT
          scaleX = scale
          scaleY = scale
          alpha = press.value.coerceIn(0f, 1f)
        }.rotate(-2.5f)
        .border(2.dp, StampRed).padding(horizontal = 10.dp, vertical = 5.dp),
    fontFamily = TypewriterFamily,
    fontSize = 12.sp,
    letterSpacing = 1.4.sp,
    color = StampRed,
  )
}

/** A sheet of paper with a solid ink outline. */
private const val STAMP_LIFT = 0.6f

@Composable
fun PaperSheet(modifier: Modifier = Modifier, dashed: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
  val outline = if (dashed) Modifier.dashedBorder(Ink) else Modifier.border(1.dp, Ink)
  Column(modifier.fillMaxWidth().background(Paper).then(outline), content = content)
}

fun Modifier.dashedBorder(color: Color, width: Dp = 1.dp): Modifier =
  drawWithCache {
    val stroke = width.toPx()
    val style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())))
    onDrawBehind {
      drawRect(
        color = color,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = size.copy(width = size.width - stroke, height = size.height - stroke),
        style = style,
      )
    }
  }

@Composable
fun SolidRule(modifier: Modifier = Modifier, color: Color = Rule) {
  Box(modifier.fillMaxWidth().height(1.dp).background(color))
}

@Composable
fun DashedRule(modifier: Modifier = Modifier) {
  Box(
    modifier.fillMaxWidth().height(1.dp).drawWithCache {
      val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
      onDrawBehind {
        drawLine(
          color = Dash,
          start = Offset(0f, size.height / 2),
          end = Offset(size.width, size.height / 2),
          strokeWidth = size.height,
          pathEffect = dash,
        )
      }
    },
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

/** Filled square bullets; [hollow] ones set points against an argument apart from the points for it without color. */
@Composable
fun BulletList(items: List<String>, modifier: Modifier = Modifier, hollow: Boolean = false) {
  Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items.forEach { item ->
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val mark = if (hollow) Modifier.border(1.dp, Ink) else Modifier.background(Ink)
        Box(Modifier.padding(top = 8.dp).size(5.dp).then(mark))
        Text(item, style = MaterialTheme.typography.bodyMedium, color = InkBody)
      }
    }
  }
}

/**
 * A full-width tappable row at least 48dp tall. A standalone button surface sets [pressIn] so its background and border
 * press in with it; rows ruled into a sheet leave it off, since shrinking would pull them away from the rules.
 */
@Composable
fun TapRow(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onClickLabel: String? = null,
  pressIn: Boolean = false,
  content: @Composable () -> Unit,
) {
  val tap =
    if (pressIn) {
      Modifier.pressable(onClickLabel = onClickLabel, onClick = onClick).then(modifier)
    } else {
      modifier.clickable(onClickLabel = onClickLabel, role = Role.Button, onClick = onClick)
    }
  Box(
    tap.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
    contentAlignment = Alignment.CenterStart,
  ) {
    content()
  }
}

/** A tappable citation at the foot of a sheet. Opens the source outside the app. */
@Composable
fun SourceRow(source: Citation, modifier: Modifier = Modifier) {
  val uriHandler = LocalUriHandler.current
  TapRow(onClick = { runCatching { uriHandler.openUri(source.url) } }, onClickLabel = "Open source", modifier = modifier) {
    Row(
      Modifier.fillMaxWidth().padding(vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text("Source: ${source.label}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = Ink)
      Chevron()
    }
  }
}

/**
 * A small drawn chevron: pointing right for "go to", down for "open" and [up] for "close" on something open. It turns,
 * rather than jumps, when it changes direction.
 */
@Composable
fun Chevron(modifier: Modifier = Modifier, down: Boolean = false, up: Boolean = false, color: Color = Ink) {
  val angle by animateFloatAsState(
    when {
      up -> -90f
      down -> 90f
      else -> 0f
    },
    tween(MOTION_MEDIUM),
    label = "chevron",
  )
  Spacer(
    modifier.padding(top = 4.dp).size(14.dp).rotate(angle).drawBehind {
      val w = size.width
      val stroke = 2.dp.toPx()
      val tip = Offset(w * 0.7f, w * 0.5f)
      val from = Offset(w * 0.35f, w * 0.15f)
      val to = Offset(w * 0.35f, w * 0.85f)
      drawLine(color, from, tip, strokeWidth = stroke, cap = StrokeCap.Round)
      drawLine(color, tip, to, strokeWidth = stroke, cap = StrokeCap.Round)
    },
  )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 320)
@Composable
private fun AppFramePreview() {
  DeskPreview {
    AppFrame(tabs = TopLevelSections, current = Files, onTab = {}) {
      Column(Modifier.padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MonoLabel("Archive of reported sightings")
        TypeTitle("Open File", size = 32.sp)
        PaperSheet {
          Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("OF-1947-01 · Sample sheet", color = Ink)
            Text("Manila desk with bottom navigation strip.", style = MaterialTheme.typography.bodyMedium, color = InkBody)
          }
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun SharedComponentsPreview() {
  DeskPreview(padding = 16.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        MonoLabel("Mono label")
        Stamp("No verdicts")
      }
      PaperSheet {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
          RecordField("Date", "24 June 1947")
          RecordField("Place", "Near Mount Rainier, Washington, USA", showRule = false)
        }
        SolidRule(color = Ink)
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          BulletList(listOf("Point supported on record", "Second corroborating detail"))
          DashedRule()
          BulletList(listOf("Point that complicates the account"), hollow = true)
        }
        SolidRule()
        SourceRow(Citation("Official archive record", "https://example.org"), Modifier.padding(horizontal = 14.dp))
      }
      PaperSheet(dashed = true) {
        Text(
          "Dashed sheet for disputes or missing records.",
          modifier = Modifier.padding(14.dp),
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }
  }
}
