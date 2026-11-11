package com.openfile.uap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapSheetsTest {
  @Test
  fun everyCaseHasACoordinateInRange() {
    Archive.cases.forEach { c ->
      assertTrue("${c.id} latitude", c.point.lat in -90.0..90.0)
      assertTrue("${c.id} longitude", c.point.lon in -180.0..180.0)
      assertTrue("${c.id} note", c.point.note.isNotBlank())
    }
  }

  @Test
  fun everyCaseLandsInsideASheet() {
    Archive.cases.forEach { c ->
      val sheet = MapSheets.sheetFor(c.point)
      assertNotNull("${c.id} has no sheet", sheet)
      val at = sheet!!.project(c.point.lat, c.point.lon)
      assertTrue("${c.id} x=${at.x}", at.x in 0f..1f)
      assertTrue("${c.id} y=${at.y}", at.y in 0f..1f)
    }
  }

  @Test
  fun projectionPutsNorthWestAtTopLeft() {
    val sheet = MapSheets.northAmerica
    assertEquals(SheetPosition(0f, 0f), sheet.project(sheet.north, sheet.west))
    assertEquals(SheetPosition(1f, 1f), sheet.project(sheet.south, sheet.east))
  }

  @Test
  fun parsesRingsAndSkipsCommentsBlanksAndDegenerateRings() {
    val text = "# header\n\n-1.5,50.25 0,51 1,50\n2,3 4,5\n"
    val rings = MapSheets.parseRings(text)
    assertEquals(1, rings.size)
    assertEquals(listOf(-1.5f to 50.25f, 0f to 51f, 1f to 50f), rings[0])
  }
}
