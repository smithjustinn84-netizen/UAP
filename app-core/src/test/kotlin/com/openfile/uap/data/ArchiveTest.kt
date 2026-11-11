package com.openfile.uap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchiveTest {
  @Test
  fun emptyQueryAndNoDecadeReturnsEveryFile() {
    assertEquals(Archive.cases, Archive.search("", null))
  }

  @Test
  fun decadeFilterKeepsOnlyThatDecade() {
    assertEquals(listOf("OF-1947-01", "OF-1947-02"), Archive.search("", 1940).map { it.id })
    assertEquals(listOf("OF-1965-01", "OF-1967-01"), Archive.search("", 1960).map { it.id })
    assertEquals(listOf("OF-1974-01"), Archive.search("", 1970).map { it.id })
    assertEquals(listOf("OF-2015-01"), Archive.search("", 2010).map { it.id })
  }

  @Test
  fun queryMatchesPlaceYearAndNameIgnoringCase() {
    assertEquals(listOf("OF-1980-01"), Archive.search("suffolk", null).map { it.id })
    assertEquals(listOf("OF-1952-01"), Archive.search("1952", null).map { it.id })
    assertEquals(listOf("OF-2004-01"), Archive.search("  NIMITZ ", null).map { it.id })
    assertEquals(listOf("OF-1967-01"), Archive.search("shag harbour", null).map { it.id })
    assertEquals(listOf("OF-1965-01"), Archive.search("kecksburg", null).map { it.id })
  }

  @Test
  fun queryAndDecadeCombine() {
    assertTrue(Archive.search("roswell", 1980).isEmpty())
    assertEquals(listOf("OF-1967-01"), Archive.search("shag", 1960).map { it.id })
  }

  @Test
  fun explanationsAreListedAlphabeticallyAndCiteEvidenceOnFile() {
    val casesWithDetail = Archive.cases.filter { it.detail != null }
    assertTrue(casesWithDetail.size >= 3)
    casesWithDetail.forEach { c ->
      val detail = c.detail!!
      val titles = detail.explanations.map { it.title }
      assertEquals("${c.id} explanations sorted alphabetically", titles.sortedBy { it.lowercase() }, titles)
      val ids = detail.evidence.map { it.id }.toSet()
      assertTrue("${c.id} citations valid", detail.explanations.flatMap { it.drawsOn }.all { it in ids })
    }
  }

  @Test
  fun everyTimelineCaseIdResolvesToAnArchiveCase() {
    Archive.timeline.mapNotNull { it.caseId }.forEach { id ->
      assertNotNull("Timeline case $id exists in archive", Archive.case(id))
    }
  }

  @Test
  fun everyDetailedCaseHasMediaLinkedToValidEvidence() {
    val casesWithDetail = Archive.cases.filter { it.detail != null }
    assertEquals(Archive.cases.size, casesWithDetail.size)
    val allKinds = mutableSetOf<MediaKind>()

    casesWithDetail.forEach { c ->
      val detail = c.detail!!
      assertTrue("${c.id} has media exhibits", detail.media.isNotEmpty())
      val evidenceIds = detail.evidence.map { it.id }.toSet()
      val mediaIds = detail.media.map { it.id }
      assertEquals("${c.id} has unique media IDs", mediaIds.distinct(), mediaIds)

      detail.media.forEach { item ->
        allKinds += item.kind
        assertTrue("${c.id} ${item.id} links to valid evidence ${item.evidenceId}", item.evidenceId in evidenceIds)
        assertTrue(detail.mediaForEvidence(item.evidenceId).contains(item))
        when (item.kind) {
          MediaKind.Diagram, MediaKind.Photo -> assertNotNull("${c.id} ${item.id} has figure", item.figure)
          MediaKind.Audio -> {
            assertNotNull("${c.id} ${item.id} has duration", item.durationSeconds)
            assertTrue("${c.id} ${item.id} has transcript", item.transcript.isNotEmpty())
          }
          MediaKind.Video -> {
            assertNotNull("${c.id} ${item.id} has figure", item.figure)
            assertNotNull("${c.id} ${item.id} has duration", item.durationSeconds)
            assertTrue("${c.id} ${item.id} has transcript", item.transcript.isNotEmpty())
          }
          MediaKind.Document -> {
            assertTrue("${c.id} ${item.id} has excerpt", item.excerpt.isNotEmpty())
          }
        }
      }
    }

    assertEquals(MediaKind.entries.toSet(), allKinds)
  }
}
