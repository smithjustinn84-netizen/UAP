package com.openfile.uap.data

import android.content.res.Resources
import androidx.annotation.RawRes
import com.openfile.uap.R
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Coastline rings for a sheet, each a list of (lon, lat) pairs. */
typealias SheetOutline = List<List<Pair<Float, Float>>>

/** Supplies the land outline drawn under each map sheet. */
interface MapOutlineRepository {
  /** Main-safe: reading and parsing happen off the main thread. */
  suspend fun outline(sheet: MapSheet): SheetOutline
}

/** Reads the outlines bundled in res/raw. The files never change, so each is parsed once and kept. */
class RawResourceMapOutlineRepository(
  private val resources: Resources,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MapOutlineRepository {
  private val cache = ConcurrentHashMap<String, SheetOutline>()

  override suspend fun outline(sheet: MapSheet): SheetOutline =
    cache[sheet.id]
      ?: withContext(ioDispatcher) {
        cache.getOrPut(sheet.id) {
          resources.openRawResource(rawResourceFor(sheet)).bufferedReader().use { MapSheets.parseRings(it.readText()) }
        }
      }

  @RawRes
  private fun rawResourceFor(sheet: MapSheet): Int =
    when (sheet) {
      MapSheets.northAmerica -> R.raw.map_north_america
      MapSheets.britishIsles -> R.raw.map_british_isles
      MapSheets.southEastAustralia -> R.raw.map_south_east_australia
      MapSheets.middleEast -> R.raw.map_middle_east
      else -> error("No outline is bundled for map sheet ${sheet.id}")
    }
}
