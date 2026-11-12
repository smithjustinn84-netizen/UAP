package com.openfile.uap.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.openfile.uap.data.MapSheet
import com.openfile.uap.data.SheetOutline
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Rule

private val Land = Color(0xFFE4D7B4)

/** The coastline rings as closed paths, for the sheet drawn at [sheetSize]. Build once per size, not per frame. */
internal fun SheetOutline.toLandPaths(sheet: MapSheet, sheetSize: Size): List<Path> =
  map { ring ->
    Path().apply {
      ring.forEachIndexed { i, (lon, lat) ->
        val at = sheet.project(lat.toDouble(), lon.toDouble())
        if (i == 0) moveTo(at.x * sheetSize.width, at.y * sheetSize.height) else lineTo(at.x * sheetSize.width, at.y * sheetSize.height)
      }
      close()
    }
  }

internal fun DrawScope.drawLand(paths: List<Path>, alpha: Float = 1f) {
  val coast = Stroke(width = 0.75.dp.toPx())
  paths.forEach { path ->
    drawPath(path, Land, alpha = alpha)
    drawPath(path, Dash, alpha = alpha, style = coast)
  }
}

/** Faint dashed lines every 10 degrees (5 on a small sheet), like the grid on a printed chart. */
internal fun DrawScope.drawGraticule(sheet: MapSheet, sheetSize: Size = size) {
  val step = if (sheet.north - sheet.south > 20) 10 else 5
  val dash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))
  val stroke = 0.75.dp.toPx()
  var lon = Math.ceil(sheet.west / step) * step
  while (lon <= sheet.east) {
    val x = sheet.project(sheet.north, lon).x * sheetSize.width
    drawLine(Rule, Offset(x, 0f), Offset(x, sheetSize.height), strokeWidth = stroke, pathEffect = dash)
    lon += step
  }
  var lat = Math.ceil(sheet.south / step) * step
  while (lat <= sheet.north) {
    val y = sheet.project(lat, sheet.west).y * sheetSize.height
    drawLine(Rule, Offset(0f, y), Offset(sheetSize.width, y), strokeWidth = stroke, pathEffect = dash)
    lat += step
  }
}
