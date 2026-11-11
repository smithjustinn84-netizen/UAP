package com.openfile.uap.data

/**
 * The single way the UI layer reads the case files. The archive ships inside the app and never changes at run time,
 * so reads are synchronous: there is nothing to wait for and no loading state to show.
 */
interface ArchiveRepository {
  /** Every case, oldest first. */
  val cases: List<CaseRecord>

  val decades: List<Int>

  val threads: List<FileThread>

  val timeline: List<TimelineEntry>

  fun case(id: String): CaseRecord?

  /** Cases in [decade] (null means all) matching the free-text [query]. */
  fun search(query: String, decade: Int?): List<CaseRecord>
}

/** Reads the archive compiled into the app. */
object BundledArchiveRepository : ArchiveRepository {
  override val cases: List<CaseRecord> = Archive.cases

  override val decades: List<Int> = Archive.decades

  override val threads: List<FileThread> = Archive.threads

  override val timeline: List<TimelineEntry> = Archive.timeline

  override fun case(id: String): CaseRecord? = Archive.case(id)

  override fun search(query: String, decade: Int?): List<CaseRecord> = Archive.search(query, decade)
}
