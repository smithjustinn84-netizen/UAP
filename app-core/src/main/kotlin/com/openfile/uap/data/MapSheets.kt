package com.openfile.uap.data

import kotlin.math.PI
import kotlin.math.cos

/** A point on a sheet, as fractions of its width and height from the top-left corner. */
data class SheetPosition(val x: Float, val y: Float)

/**
 * One rectangular sheet of the drawn map, in an equirectangular projection whose longitudes are squeezed by the cosine
 * of the middle latitude so shapes stay roughly true at that latitude.
 */
data class MapSheet(val id: String, val title: String, val west: Double, val south: Double, val east: Double, val north: Double) {
  private val lonScale: Double = cos((south + north) / 2 * PI / 180)

  /** Width over height of the sheet as projected. */
  val aspectRatio: Float
    get() = ((east - west) * lonScale / (north - south)).toFloat()

  fun contains(lat: Double, lon: Double): Boolean = lat in south..north && lon in west..east

  fun project(lat: Double, lon: Double): SheetPosition =
    SheetPosition(x = ((lon - west) / (east - west)).toFloat(), y = ((north - lat) / (north - south)).toFloat())
}

object MapSheets {
  // Bounds match the clipping used to produce the bundled outlines in res/raw.
  val northAmerica = MapSheet("north_america", "North America", west = -130.0, south = 20.0, east = -62.0, north = 54.0)
  val britishIsles = MapSheet("british_isles", "Britain and Ireland", west = -11.0, south = 49.5, east = 4.0, north = 57.0)
  val southEastAustralia =
    MapSheet(
      id = "south_east_australia",
      title = "South-East Australia",
      west = 138.0,
      south = -44.0,
      east = 154.0,
      north = -32.0,
    )
  val middleEast =
    MapSheet(
      id = "middle_east",
      title = "Middle East",
      west = 44.0,
      south = 25.0,
      east = 64.0,
      north = 40.0,
    )

  val centralEurope =
    MapSheet(
      id = "central_europe",
      title = "Central Europe",
      west = 4.0,
      south = 44.0,
      east = 16.0,
      north = 54.0,
    )

  val all: List<MapSheet> = listOf(northAmerica, britishIsles, southEastAustralia, middleEast, centralEurope)

  fun sheetFor(point: MapPoint): MapSheet? = all.firstOrNull { it.contains(point.lat, point.lon) }

  /**
   * Parses the bundled outline format: one closed ring per line, each a space-separated list of `lon,lat` pairs.
   * Lines starting with `#` and blank lines are skipped. Each ring is returned as a list of (lon, lat) pairs.
   */
  fun parseRings(text: String): List<List<Pair<Float, Float>>> =
    text
      .lineSequence()
      .map { it.trim() }
      .filter { it.isNotEmpty() && !it.startsWith("#") }
      .map { line ->
        line.split(' ').filter { it.isNotEmpty() }.map { pair ->
          val (lon, lat) = pair.split(',')
          lon.toFloat() to lat.toFloat()
        }
      }
      .filter { it.size >= 3 }
      .toList()
}
