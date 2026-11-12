package com.openfile.uap.ui.casefile

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.openfile.uap.data.Archive
import com.openfile.uap.data.CaseHero
import com.openfile.uap.data.CaseRecord
import com.openfile.uap.data.MapPoint
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.MapSheets
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.map.drawGraticule
import com.openfile.uap.ui.map.drawLand
import com.openfile.uap.ui.map.toLandPaths
import kotlin.math.abs
import kotlin.math.roundToInt

private val MaxHeroHeight = 240.dp

/** How far the picture drifts per pixel scrolled, so it sinks behind the sheet a little slower than the page. */
private const val PARALLAX = 0.4f

/** How much closer than the full sheet the locator map is drawn, so the place is easier to find. */
private const val LOCATOR_ZOOM = 1.3f

/**
 * Pictures come from many sources and decades; a partial sepia wash puts them on the same paper as the rest of the
 * file. The full-size viewer shows the original.
 */
private val SepiaWash: ColorFilter =
  run {
    val sepia =
      floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
      )
    val identity = ColorMatrix().values
    ColorFilter.colorMatrix(ColorMatrix(FloatArray(20) { i -> identity[i] * 0.4f + sepia[i] * 0.6f }))
  }

/**
 * The head of a file: its [CaseHero] picture with caption and credit, or, for a file without one, a locator map
 * drawn from [outline]. [scrollOffset] is read while drawing, so scrolling moves the picture without recomposing.
 */
@Composable
fun CaseHeroBanner(record: CaseRecord, outline: SheetOutline, scrollOffset: () -> Int, modifier: Modifier = Modifier) {
  // Where the banner sits in the scrolling sheet: the picture only drifts once the banner's top has left the screen,
  // so no gap opens above it while it is still in view.
  var top by remember { mutableIntStateOf(0) }
  BoxWithConstraints(modifier.fillMaxWidth().onPlaced { top = it.positionInParent().y.roundToInt() }) {
    val height = min(maxWidth * 9f / 16f, MaxHeroHeight)
    val hero = record.detail?.hero
    val sheet = MapSheets.sheetFor(record.point)
    when {
      hero != null -> PhotoHero(hero, height, drift = { (scrollOffset() - top).coerceAtLeast(0) })
      sheet != null -> LocatorHero(record, sheet, outline, height)
    }
  }
}

@Composable
private fun PhotoHero(hero: CaseHero, height: Dp, drift: () -> Int) {
  var viewing by rememberSaveable { mutableStateOf(false) }
  Column(Modifier.fillMaxWidth()) {
    SolidRule(color = Ink)
    Box(
      Modifier.fillMaxWidth()
        .height(height)
        .background(Ink)
        .clipToBounds()
        .clickable(onClickLabel = "View full image") { viewing = true },
    ) {
      BundledImage(
        resId = hero.image.toDrawableRes(),
        contentDescription = hero.caption,
        contentScale = ContentScale.Crop,
        alignment = BiasAlignment(0f, hero.focus * 2 - 1),
        colorFilter = SepiaWash,
        modifier = Modifier.fillMaxSize().graphicsLayer { translationY = drift() * PARALLAX },
      )
    }
    SolidRule(color = Ink)
    Column(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = ScreenPadding, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      MonoLabel("PLATE · ${hero.caption}", color = Ink)
      Text(hero.credit, style = MaterialTheme.typography.labelSmall, color = InkSoft)
    }
    SolidRule(color = Ink)
  }
  if (viewing) ZoomableImageDialog(hero.image.toDrawableRes(), hero.caption, onDismiss = { viewing = false })
}

/** A crop of the case's map sheet centred on its pin, which pulses slowly. The pin is ink like every other pin. */
@Composable
private fun LocatorHero(record: CaseRecord, sheet: MapSheet, outline: SheetOutline, height: Dp) {
  val landAlpha by animateFloatAsState(if (outline.isEmpty()) 0f else 1f, animationSpec = tween(600), label = "land")
  val pulse by
    rememberInfiniteTransition(label = "pin")
      .animateFloat(0f, 1f, infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart), label = "pulse")
  val description = "Locator map of ${sheet.title} marking ${record.placeShort}"

  Column(Modifier.fillMaxWidth()) {
    SolidRule(color = Ink)
    Box(
      Modifier.fillMaxWidth()
        .height(height)
        .background(Paper)
        .clipToBounds()
        .clearAndSetSemantics { contentDescription = description }
        .locatorMap(record, sheet, outline, landAlpha = { landAlpha }, pulse = { pulse }),
    ) {
      MonoLabel(
        "LOCATOR · ${sheet.title}",
        color = Ink,
        modifier = Modifier.align(Alignment.TopStart).padding(10.dp).labelPlate(),
      )
      Column(Modifier.align(Alignment.BottomStart).padding(10.dp).labelPlate()) {
        MonoLabel(formatCoordinates(record.point), color = Ink)
        MonoLabel("${record.placeShort} · ${record.dateShort}")
      }
    }
    SolidRule(color = Ink)
  }
}

/** The sheets whose land the locator maps for [cases] need: one for each sheet holding a file without a picture. */
fun locatorSheets(cases: List<CaseRecord>): List<MapSheet> =
  cases.filter { it.detail?.hero == null }.mapNotNull { MapSheets.sheetFor(it.point) }.distinct()

/**
 * Pictured files show their [CaseHero] picture, cropped and washed like the banner; the rest show a still locator map.
 * Purely decorative: the card around it already names the file, place and date.
 */
@Composable
fun CaseThumbnail(record: CaseRecord, outline: SheetOutline, modifier: Modifier = Modifier) {
  val hero = record.detail?.hero
  val sheet = MapSheets.sheetFor(record.point)
  val frame = modifier.fillMaxWidth().clipToBounds().clearAndSetSemantics {}
  when {
    hero != null ->
      BundledImage(
        resId = hero.image.toDrawableRes(),
        contentDescription = hero.caption,
        contentScale = ContentScale.Crop,
        alignment = BiasAlignment(0f, hero.focus * 2 - 1),
        colorFilter = SepiaWash,
        modifier = frame.background(Ink),
      )

    sheet != null -> {
      val landAlpha by animateFloatAsState(if (outline.isEmpty()) 0f else 1f, animationSpec = tween(600), label = "land")
      Box(frame.background(Paper).locatorMap(record, sheet, outline, landAlpha = { landAlpha }, pulse = null))
    }
  }
}

/**
 * Draws [sheet] cropped to centre on the case's pin, with crosshairs and the pin. [landAlpha] and [pulse] are read
 * while drawing, so animating them redraws without recomposing; without [pulse] the pin stands still.
 */
private fun Modifier.locatorMap(
  record: CaseRecord,
  sheet: MapSheet,
  outline: SheetOutline,
  landAlpha: () -> Float,
  pulse: (() -> Float)?,
): Modifier =
  drawWithCache {
    val sheetSize = coverSize(sheet.aspectRatio, size) * LOCATOR_ZOOM
    val at = sheet.project(record.point.lat, record.point.lon)
    val pin = Offset(at.x * sheetSize.width, at.y * sheetSize.height)
    // Centre the pin, but never past the sheet's edge, where there is no land data.
    val origin =
      Offset(
        (size.width / 2 - pin.x).coerceIn(size.width - sheetSize.width, 0f),
        (size.height / 2 - pin.y).coerceIn(size.height - sheetSize.height, 0f),
      )
    val land = outline.toLandPaths(sheet, sheetSize)
    val centre = origin + pin
    onDrawBehind {
      translate(origin.x, origin.y) {
        drawGraticule(sheet, sheetSize)
        drawLand(land, alpha = landAlpha())
      }
      val hair = 1.dp.toPx()
      drawLine(Ink.copy(alpha = 0.35f), Offset(0f, centre.y), Offset(size.width, centre.y), strokeWidth = hair)
      drawLine(Ink.copy(alpha = 0.35f), Offset(centre.x, 0f), Offset(centre.x, size.height), strokeWidth = hair)
      pulse?.invoke()?.let { p ->
        drawCircle(Ink.copy(alpha = 1f - p), radius = (14 + 26 * p).dp.toPx(), center = centre, style = Stroke(hair * 1.5f))
      }
      drawCircle(Paper, radius = 12.dp.toPx(), center = centre)
      drawCircle(Ink, radius = 12.dp.toPx(), center = centre, style = Stroke(hair * 2))
      drawCircle(Ink, radius = 4.dp.toPx(), center = centre)
    }
  }

/** A paper slip under a map label, so it stays legible over coastline and grid. */
private fun Modifier.labelPlate(): Modifier =
  drawBehind {
    drawRect(Paper)
    drawRect(Ink, style = Stroke(1.dp.toPx()))
  }.padding(horizontal = 6.dp, vertical = 3.dp)

/** The smallest size with [aspectRatio] that covers [bounds]. */
private fun coverSize(aspectRatio: Float, bounds: Size): Size =
  if (bounds.width / bounds.height > aspectRatio) {
    Size(bounds.width, bounds.width / aspectRatio)
  } else {
    Size(bounds.height * aspectRatio, bounds.height)
  }

/** Degrees and minutes, marked approximate because every pin is: see [MapPoint.note]. */
internal fun formatCoordinates(point: MapPoint): String = "≈ ${degrees(point.lat, 'N', 'S')} ${degrees(point.lon, 'E', 'W')}"

private fun degrees(value: Double, positive: Char, negative: Char): String {
  val totalMinutes = (abs(value) * 60).roundToInt()
  val minutes = (totalMinutes % 60).toString().padStart(2, '0')
  return "${totalMinutes / 60}°$minutes′${if (value < 0) negative else positive}"
}

@Preview(widthDp = 400)
@Composable
private fun PhotoHeroPreview() {
  DeskPreview {
    CaseHeroBanner(Archive.cases.first(), outline = emptyList(), scrollOffset = { 0 })
  }
}

@Preview(widthDp = 400)
@Composable
private fun LocatorHeroPreview() {
  DeskPreview {
    CaseHeroBanner(Archive.cases.first { it.detail?.hero == null }, outline = emptyList(), scrollOffset = { 0 })
  }
}
