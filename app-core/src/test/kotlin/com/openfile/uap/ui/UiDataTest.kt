package com.openfile.uap.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UiDataTest {
  @Test
  fun countOfUsesTheSingularOnlyForOne() {
    assertEquals("0 files", countOf(0, "file"))
    assertEquals("1 file", countOf(1, "file"))
    assertEquals("2 files", countOf(2, "file"))
    assertEquals("3 media exhibits", countOf(3, "media exhibit"))
    assertEquals("2 analyses", countOf(2, "analysis", "analyses"))
  }
}
