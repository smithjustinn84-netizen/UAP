package com.openfile.uap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchiveTest {
  @Test
  fun eachInfographicIsDrawnForExactlyOneFile() {
    Infographic.entries.forEach { infographic ->
      assertEquals(infographic.name, 1, Archive.cases.count { it.detail?.infographic == infographic })
    }
  }

  @Test
  fun emptyQueryAndNoDecadeReturnsEveryFile() {
    assertEquals(Archive.cases, Archive.search("", null))
  }

  @Test
  fun decadeFilterKeepsOnlyThatDecade() {
    assertEquals(listOf("OF-1947-01", "OF-1947-02"), Archive.search("", 1940).map { it.id })
    assertEquals(listOf("OF-1965-01", "OF-1966-01", "OF-1967-01"), Archive.search("", 1960).map { it.id })
    assertEquals(listOf("OF-1974-01", "OF-1976-01", "OF-1978-01"), Archive.search("", 1970).map { it.id })
    assertEquals(listOf("OF-1980-01"), Archive.search("", 1980).map { it.id })
    assertEquals(listOf("OF-1997-01"), Archive.search("", 1990).map { it.id })
    assertEquals(listOf("OF-2004-01"), Archive.search("", 2000).map { it.id })
    assertEquals(listOf("OF-2015-01"), Archive.search("", 2010).map { it.id })
    assertEquals(listOf("OF-2026-01"), Archive.search("", 2020).map { it.id })
  }

  @Test
  fun queryMatchesPlaceYearAndNameIgnoringCase() {
    assertEquals(listOf("OF-1980-01"), Archive.search("suffolk", null).map { it.id })
    assertEquals(listOf("OF-1952-01"), Archive.search("1952", null).map { it.id })
    assertEquals(listOf("OF-2004-01"), Archive.search("  NIMITZ ", null).map { it.id })
    assertEquals(listOf("OF-1967-01"), Archive.search("shag harbour", null).map { it.id })
    assertEquals(listOf("OF-1965-01"), Archive.search("kecksburg", null).map { it.id })
    assertEquals(listOf("OF-1966-01", "OF-1978-01"), Archive.search("melbourne", null).map { it.id })
    assertEquals(listOf("OF-1976-01", "OF-2026-01"), Archive.search("tehran", null).map { it.id })
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
          MediaKind.Diagram -> assertFalse("${c.id} ${item.id} has a drawn figure", item.figure?.plate ?: true)

          MediaKind.Photo -> {
            assertTrue("${c.id} ${item.id} has image or figure", item.image != null || item.figure != null)
            assertTrue("${c.id} ${item.id} figure is a plate", item.figure?.plate ?: true)
          }

          MediaKind.Audio, MediaKind.Video -> {
            // No more simulated players: every recording is either bundled or links to where it plays.
            assertTrue("${c.id} ${item.id} is bundled or linked", item.clip != null || item.sourceUrl != null)
            assertNotNull("${c.id} ${item.id} has duration", item.durationSeconds)
          }

          MediaKind.Document -> {
            assertTrue("${c.id} ${item.id} has excerpt", item.excerpt.isNotEmpty())
          }
        }
      }
    }

    assertEquals(MediaKind.entries.toSet(), allKinds)
  }

  @Test
  fun queryMatchesWitnessesAndEvidenceTitles() {
    assertEquals(listOf("OF-1980-01"), Archive.search("halt", null).map { it.id })
    assertEquals(listOf("OF-1947-02"), Archive.search("mogul", null).map { it.id })
  }

  @Test
  fun everyCaseHasItsLoreInDateOrderAndAtLeastOneGap() {
    Archive.cases.forEach { c ->
      val detail = c.detail!!
      assertTrue("${c.id} has a story", detail.story.size >= 3)
      val years = detail.story.map { it.year }
      assertEquals("${c.id} story is in date order", years.sorted(), years)
      assertTrue("${c.id} story starts no earlier than the event", years.first() >= c.year - 1)
      assertEquals("${c.id} story opens with what was said at the time", StoryKind.AtTheTime, detail.story.first().kind)
      assertTrue("${c.id} records what is missing", detail.gaps.isNotEmpty())
      (detail.story.map { it.title to it.source } + detail.gaps.map { it.title to it.source }).forEach { (title, source) ->
        assertNotNull("${c.id} “$title” cites a source", source)
        assertTrue("${c.id} “$title” has a citation label", source!!.label.isNotBlank())
        assertTrue("${c.id} “$title” links to its source", source.url.startsWith("http"))
      }
    }
  }

  @Test
  fun timelineIsInDateOrder() {
    val years = Archive.timeline.map { it.year }
    assertEquals(years.sorted(), years)
    assertEquals(TimelineKind.entries.toSet(), Archive.timeline.map { it.kind }.toSet())
  }

  @Test
  fun timelineEntriesAreDistinctByYearAndTitle() {
    // The timeline list keys its rows on year and title.
    val keys = Archive.timeline.map { it.year to it.title }
    assertEquals(keys.distinct(), keys)
  }

  @Test
  fun everyThreadRunsThroughSeveralFilesThatExist() {
    assertTrue(Archive.threads.isNotEmpty())
    assertEquals(Archive.threads.map { it.id }.distinct(), Archive.threads.map { it.id })
    Archive.threads.forEach { thread ->
      val ids = thread.appearances.map { it.caseId }
      assertTrue("${thread.id} appears in more than one file", ids.size >= 2)
      assertEquals("${thread.id} lists each file once, oldest first", ids.distinct().sorted(), ids)
      ids.forEach { assertNotNull("${thread.id} case $it exists", Archive.case(it)) }
      assertTrue("${thread.id} says what to keep in mind", thread.keepInMind.isNotBlank())
    }
  }
}
