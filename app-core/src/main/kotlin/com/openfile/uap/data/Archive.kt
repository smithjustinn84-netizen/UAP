package com.openfile.uap.data

/** One case file in the archive. [detail] is null until the file has been written up. */
data class CaseRecord(
  val id: String,
  val title: String,
  val year: Int,
  val dateShort: String,
  val placeShort: String,
  val date: String,
  val place: String,
  val onFile: String,
  val status: String,
  val point: MapPoint,
  val detail: CaseDetail? = null,
) {
  val decade: Int
    get() = year / 10 * 10
}

/**
 * Where a case is plotted on the map. Every point is approximate: some events covered a wide area or open sea,
 * so [note] says what the pin stands for.
 */
data class MapPoint(val lat: Double, val lon: Double, val note: String)

data class CaseDetail(
  val stamp: String,
  val reportedBy: String,
  val officialPosition: String,
  val reported: List<String>,
  val notInDispute: List<String>,
  val inDispute: List<String>,
  val evidence: List<Evidence>,
  val explanations: List<Explanation>,
  val officialNote: String,
  val media: List<ReportMedia> = emptyList(),
  /** How the account has been told since, oldest first. Written up in `Lore.kt`. */
  val story: List<StoryEntry> = emptyList(),
  /** Records that would bear on the case but are lost, destroyed, withheld or were never made. */
  val gaps: List<RecordGap> = emptyList(),
  /** A briefing graphic drawn for this file alone, shown on its summary. */
  val infographic: Infographic? = null,
  /** The picture heading the file. Without one, the file is headed by a locator map of [CaseRecord.point]. */
  val hero: CaseHero? = null,
) {
  fun mediaForEvidence(evidenceId: String): List<ReportMedia> = media.filter { it.evidenceId == evidenceId }
}

/**
 * Where an evidence item or exhibit comes from, never whether it is true. These are the only labels the archive uses;
 * [meaning] is the definition the Method screen gives for [label].
 */
enum class SourceLabel(val label: String, val meaning: String) {
  PrimaryRecord("Primary record", "Made at or near the time by someone involved."),
  FirstHand("First-hand", "An account from a person who was present."),
  OfficialRecord("Official record", "Produced by a government or other public body."),
  Reported("Reported", "Described in another source. The original is not available to check."),
  NotPreserved("Not preserved", "Seen at the time. Nothing survives to examine."),
}

/** Interactive graphics drawn in code for one particular file; `ui/casefile` has a composable for each. */
enum class Infographic {
  NimitzBriefing,
  TehranBriefing,
}

/** [sourceLabel] says where an item comes from, never whether it is true. */
data class Evidence(
  val id: String,
  val kind: String,
  val sourceLabel: SourceLabel,
  val title: String,
  val description: String,
  val caveat: String,
)

enum class MediaKind(val label: String) {
  Diagram("Diagram"),
  Photo("Photographic plate"),
  Audio("Audio recording"),
  Video("Video recording"),
  Document("Document excerpt"),
}

/** A figure for an exhibit: drawn in code for a diagram, or a bundled vector drawable when [plate] is true. */
enum class MediaFigure(val plate: Boolean = false) {
  ArnoldCascadeTrack,
  ArnoldOriginalSketch(plate = true),
  MogulBalloonTrain,
  WashingtonRadarScope,
  KecksburgAcornProfile,
  ShagHarbourImpactChart,
  BerwynSeismograph,
  RendleshamLandingTriangle,
  PhoenixVFormation,
  PhoenixEstrellasPhoto(plate = true),
  NimitzAltitudeDrop,
}

/** Bundled recordings in `res/raw`. Video without a clip links out to [ReportMedia.sourceUrl] instead. */
enum class MediaClip {
  NimitzFlir1,
  RooseveltGimbal,
  RooseveltGoFast,
  WashingtonSamford,
}

/** Real photographs and document scans bundled as raster drawables; see THIRD-PARTY-NOTICES.md for each source. */
enum class MediaImage {
  HaltMemo,
  RoswellDailyRecord,
  RameyDuboseDebris,
  MogulReflectorTrain,
  KecksburgReplica,
  SamfordMinutes,
  NurembergBroadsheet,
  LosAngelesTimesPage,
  NightFighterBeaufighter,
  MountRainier,
  ShagHarbour,
  BerwynRidge,
  UssNimitz,
  UssTheodoreRoosevelt,
}

/**
 * The picture at the head of a file: a record made at the time or a view of the setting, never a later
 * reconstruction of what was reported. [caption] says only what the picture is; [credit] names its maker, source and
 * licence, and is always shown. [focus] is where to crop vertically, from 0 (top) to 1 (bottom).
 */
data class CaseHero(val image: MediaImage, val caption: String, val credit: String, val focus: Float = 0.5f)

data class TranscriptCue(val timestamp: String, val speaker: String, val text: String)

/**
 * An archival media exhibit attached to a case file and linked to an [Evidence] item via [evidenceId].
 * Supports ink diagrams, photographic/sensor plates, audio/video recordings with transcripts and external links,
 * and verbatim document/telex excerpts.
 */
data class ReportMedia(
  val id: String,
  val evidenceId: String,
  val kind: MediaKind,
  val sourceLabel: SourceLabel,
  val title: String,
  val caption: String,
  val provenance: String,
  val figure: MediaFigure? = null,
  val documentHeader: List<Pair<String, String>> = emptyList(),
  val excerpt: List<String> = emptyList(),
  val durationSeconds: Int? = null,
  val transcript: List<TranscriptCue> = emptyList(),
  val telemetryLabels: List<String> = emptyList(),
  val sourceUrl: String? = null,
  val clip: MediaClip? = null,
  /** A real photograph or scan. Photos show it in place of [figure]; documents show it above the excerpt. */
  val image: MediaImage? = null,
)

data class Explanation(
  val title: String,
  val heldBy: String,
  val supports: List<String>,
  val complicates: List<String>,
  val drawsOn: List<String>,
)

/**
 * What kind of step a [StoryEntry] is. Like a source label, it says how a detail arrived, never whether it is true.
 * [meaning] is the definition the Method screen gives for [label].
 */
enum class StoryKind(val label: String, val meaning: String) {
  AtTheTime("At the time", "Said, written or done within days or weeks of the event."),
  NewAccount(
    "New account",
    "A witness or researcher adds something that was not public before. The date is when it was first made public.",
  ),
  PrintOrScreen("Print or screen", "A book, article, film or broadcast that carried the story to a wider audience."),
  Inquiry("Inquiry or record", "A public body or a study examines the case, or releases a record."),
  Revised("Revised or withdrawn", "Someone changes or takes back what they said earlier."),
}

/** Where a statement in the archive can be checked: [label] is the citation as printed, [url] opens it. */
data class Citation(val label: String, val url: String)

/** One step in how a case has been told and retold. [year] orders the entries; [date] is what is shown. */
data class StoryEntry(
  val year: Int,
  val date: String,
  val kind: StoryKind,
  val title: String,
  val detail: String,
  val source: Citation? = null,
)

data class RecordGap(val title: String, val detail: String, val source: Citation? = null)

/** A pattern that recurs across files. It points at what the records share and draws no conclusion from it. */
data class FileThread(
  val id: String,
  val title: String,
  val summary: String,
  val appearances: List<ThreadAppearance>,
  val keepInMind: String,
)

data class ThreadAppearance(val caseId: String, val note: String)

enum class TimelineKind(val label: String) {
  Sighting("Sighting"),
  Inquiry("Inquiry or record"),
  Culture("Print or screen"),
}

data class TimelineEntry(
  val year: Int,
  val kind: TimelineKind,
  val title: String,
  val detail: String,
  val caseId: String? = null,
)

object Archive {
  /** Oldest first. */
  val cases: List<CaseRecord> =
    listOf(
      CaseRecord(
        id = "OF-1561-01",
        title = "Nuremberg celestial phenomenon",
        year = 1561,
        dateShort = "14 Apr 1561",
        placeShort = "Nuremberg, Germany",
        date = "14 April 1561",
        place = "Nuremberg, Holy Roman Empire (now Germany)",
        onFile = "printed broadsheet, later commentary",
        status = "Natural explanation proposed · disputed",
        point = MapPoint(49.45, 11.08, "Plotted at Nuremberg. The broadsheet says the sights were seen above and around the city."),
        detail = nuremberg1561,
      ),
      CaseRecord(
        id = "OF-1942-01",
        title = "Battle of Los Angeles",
        year = 1942,
        dateShort = "24–25 Feb 1942",
        placeShort = "Los Angeles, USA",
        date = "24–25 February 1942",
        place = "Los Angeles and the southern California coast, USA",
        onFile = "official memoranda, press reports, photograph, testimony",
        status = "Official statements conflict · disputed",
        point = MapPoint(34.05, -118.24, "Plotted at Los Angeles. Anti-aircraft fire was reported across the city and along the coast."),
        detail = battleOfLosAngeles,
      ),
      CaseRecord(
        id = "OF-1944-01",
        title = "Foo fighters",
        year = 1944,
        dateShort = "Late 1944",
        placeShort = "Rhine front, France and Germany",
        date = "Late 1944 to early 1945",
        place = "Night skies along the Rhine, France and Germany",
        onFile = "crew accounts, intelligence reports, press reports",
        status = "No agreed explanation · several proposed",
        point = MapPoint(48.58, 7.75, "Plotted at Strasbourg. Night-fighter crews reported the lights along the Rhine. Similar reports came from other theatres."),
        detail = fooFighters,
      ),
      CaseRecord(
        id = "OF-1947-01",
        title = "Kenneth Arnold sighting",
        year = 1947,
        dateShort = "24 Jun 1947",
        placeShort = "Mount Rainier, USA",
        date = "24 June 1947",
        place = "Near Mount Rainier, Washington, USA",
        onFile = "testimony, press reports",
        status = "Explanations proposed · none agreed",
        point = MapPoint(46.85, -121.76, "Plotted at Mount Rainier. The objects were reported in flight near the mountain."),
        detail = arnold,
      ),
      CaseRecord(
        id = "OF-1947-02",
        title = "Roswell incident",
        year = 1947,
        dateShort = "Jul 1947",
        placeShort = "Roswell, New Mexico, USA",
        date = "July 1947",
        place = "Roswell, New Mexico, USA",
        onFile = "testimony, press reports, official reports",
        status = "Official explanation on record · disputed",
        point = MapPoint(33.39, -104.52, "Plotted at the town of Roswell. The debris was reported on a ranch outside the town."),
        detail = roswell,
      ),
      CaseRecord(
        id = "OF-1952-01",
        title = "Washington, D.C. radar sightings",
        year = 1952,
        dateShort = "19–27 Jul 1952",
        placeShort = "Washington, D.C., USA",
        date = "19–27 July 1952",
        place = "Washington, D.C., USA",
        onFile = "radar reports, testimony, press reports",
        status = "Official explanation on record · disputed",
        point = MapPoint(38.90, -77.04, "Plotted at Washington, D.C. Radar returns were reported over the capital area."),
        detail = washingtonRadar,
      ),
      CaseRecord(
        id = "OF-1965-01",
        title = "Kecksburg incident",
        year = 1965,
        dateShort = "9 Dec 1965",
        placeShort = "Pennsylvania, USA",
        date = "9 December 1965",
        place = "Kecksburg, Pennsylvania, USA",
        onFile = "witness testimony, military records, FOIA litigation",
        status = "Official explanation on record · disputed",
        point = MapPoint(40.18, -79.53, "Plotted at Kecksburg, Pennsylvania, where an acorn-shaped metallic object was reported in woodland."),
        detail = kecksburg,
      ),
      CaseRecord(
        id = "OF-1966-01",
        title = "Westall encounter",
        year = 1966,
        dateShort = "6 Apr 1966",
        placeShort = "Melbourne, Australia",
        date = "6 April 1966",
        place = "Clayton South, Melbourne, Victoria, Australia",
        onFile = "witness testimony, press reports, official archives",
        status = "No official explanation · disputed",
        point = MapPoint(-37.94, 145.13, "Plotted at Westall Secondary College in Clayton South, Melbourne."),
        detail = westall,
      ),
      CaseRecord(
        id = "OF-1967-01",
        title = "Shag Harbour incident",
        year = 1967,
        dateShort = "4 Oct 1967",
        placeShort = "Nova Scotia, Canada",
        date = "4 October 1967",
        place = "Shag Harbour, Nova Scotia, Canada",
        onFile = "police reports, military telexes, diver logs, testimony",
        status = "Official inquiry on record · unsolved",
        point = MapPoint(43.49, -65.71, "Plotted at Shag Harbour, where multiple witnesses watched an illuminated object descend into the Gulf of Maine."),
        detail = shagHarbour,
      ),
      CaseRecord(
        id = "OF-1974-01",
        title = "Berwyn Mountains incident",
        year = 1974,
        dateShort = "23 Jan 1974",
        placeShort = "North Wales, UK",
        date = "23 January 1974",
        place = "Berwyn Mountains, Clwyd, Wales",
        onFile = "police logs, seismic records, MoD files",
        status = "Official explanation on record · disputed",
        point = MapPoint(52.92, -3.44, "Plotted in the Berwyn Mountains near Llandrillo, where a tremor and sky phenomena were reported simultaneously."),
        detail = berwyn,
      ),
      CaseRecord(
        id = "OF-1976-01",
        title = "Tehran UFO incident",
        year = 1976,
        dateShort = "19 Sep 1976",
        placeShort = "Tehran, Iran",
        date = "18–19 September 1976",
        place = "Tehran, Iran",
        onFile = "military intelligence report, pilot testimony, radar logs",
        status = "Official military inquiry · unidentified",
        point = MapPoint(35.69, 51.39, "Plotted at Tehran, Iran, where IIAF F-4 Phantoms attempted interception."),
        detail = tehran,
      ),
      CaseRecord(
        id = "OF-1978-01",
        title = "Valentich disappearance",
        year = 1978,
        dateShort = "21 Oct 1978",
        placeShort = "Bass Strait, Australia",
        date = "21 October 1978",
        place = "Bass Strait, between Victoria and King Island, Australia",
        onFile = "air traffic control audio, accident investigation report, weather data",
        status = "Official inquiry on record · unsolved",
        point = MapPoint(-39.50, 144.50, "Plotted in Bass Strait along the planned route between Cape Otway and King Island."),
        detail = valentich,
      ),
      CaseRecord(
        id = "OF-1980-01",
        title = "Rendlesham Forest incident",
        year = 1980,
        dateShort = "26–28 Dec 1980",
        placeShort = "Suffolk, England",
        date = "26–28 December 1980",
        place = "Rendlesham Forest, Suffolk, England, beside RAF Woodbridge and RAF Bentwaters",
        onFile = "documents, audio, testimony, physical traces",
        status = "No official explanation · 5 proposed",
        point = MapPoint(52.09, 1.43, "Plotted at Rendlesham Forest, beside RAF Woodbridge and RAF Bentwaters."),
        detail = rendlesham,
      ),
      CaseRecord(
        id = "OF-1997-01",
        title = "Phoenix Lights",
        year = 1997,
        dateShort = "13 Mar 1997",
        placeShort = "Arizona, USA",
        date = "13 March 1997",
        place = "Arizona, USA",
        onFile = "video, testimony, official statements",
        status = "Official explanation on record · disputed",
        point = MapPoint(33.45, -112.07, "Plotted at Phoenix. Lights were reported across a wide area of Arizona."),
        detail = phoenixLights,
      ),
      CaseRecord(
        id = "OF-2004-01",
        title = "USS Nimitz encounter",
        year = 2004,
        dateShort = "14 Nov 2004",
        placeShort = "Pacific, SW of San Diego",
        date = "14 November 2004",
        place = "Pacific Ocean, southwest of San Diego, California",
        onFile = "video, testimony, official reports",
        status = "Official military inquiry · unidentified",
        point = MapPoint(31.50, -118.80, "Plotted in open sea southwest of San Diego. The exact position is not on public record."),
        detail = nimitz,
      ),
      CaseRecord(
        id = "OF-2015-01",
        title = "USS Theodore Roosevelt encounters",
        year = 2015,
        dateShort = "Jan–Mar 2015",
        placeShort = "Atlantic, East Coast USA",
        date = "January–March 2015",
        place = "Atlantic Ocean off Florida and Virginia, USA",
        onFile = "ATFLIR video, cockpit audio, pilot testimony, Congressional records",
        status = "Official inquiry on record · unidentified",
        point = MapPoint(31.00, -79.50, "Plotted in the military warning area off the southeastern US coast where aviators recorded the Gimbal and GoFast videos."),
        detail = rooseveltEncounters,
      ),
      CaseRecord(
        id = "OF-2026-01",
        title = "Tehran aerial phenomenon",
        year = 2026,
        dateShort = "Sep 2026",
        placeShort = "Tehran, Iran",
        date = "September 2026",
        place = "Tehran, Iran",
        onFile = "video recordings, social media reports, aviation analyses",
        status = "Explanations proposed · none agreed",
        point = MapPoint(35.70, 51.42, "Plotted over Tehran, where residents captured video of an illuminated aerial object."),
        detail = tehran2026,
      ),
    )

  val decades: List<Int> = cases.map { it.decade }.distinct().sorted()

  fun case(id: String): CaseRecord? = cases.firstOrNull { it.id == id }

  /** Cross-file patterns, in no particular order of weight. */
  val threads: List<FileThread> = fileThreads

  /**
   * Filters by decade (null means all) and by a free-text query over title, place, date, file number, who reported it
   * and the titles of the evidence and explanations on file.
   */
  fun search(query: String, decade: Int?): List<CaseRecord> {
    val q = query.trim()
    return cases.filter { c ->
      (decade == null || c.decade == decade) && (q.isEmpty() || c.searchText().any { it.contains(q, ignoreCase = true) })
    }
  }

  private fun CaseRecord.searchText(): List<String> =
    listOf(title, place, date, id) +
      detail?.let { d -> listOf(d.reportedBy) + d.evidence.map { it.title } + d.explanations.map { it.title } }.orEmpty()

  val timeline: List<TimelineEntry> =
    listOf(
      TimelineEntry(
        1561,
        TimelineKind.Sighting,
        "Nuremberg celestial phenomenon",
        "14 April · Nuremberg, Holy Roman Empire. A broadsheet by Hans Glaser describes globes, crosses and cylindrical shapes reported in the morning sky",
        "OF-1561-01",
      ),
      TimelineEntry(
        1942,
        TimelineKind.Sighting,
        "Battle of Los Angeles",
        "24–25 February · Los Angeles, California, USA. Anti-aircraft artillery fires over 1,400 shells following radar tracks and visual reports of unidentified aerial objects during a blackout",
        "OF-1942-01",
      ),
      TimelineEntry(
        1944,
        TimelineKind.Sighting,
        "Foo fighters",
        "November 1944 · European and Pacific theaters. Allied aircrews report glowing nocturnal spheres pacing their aircraft, investigated by military intelligence without a confirmed explanation",
        "OF-1944-01",
      ),
      TimelineEntry(
        1947,
        TimelineKind.Sighting,
        "Kenneth Arnold sighting",
        "24 June · Mount Rainier, Washington, USA",
        "OF-1947-01",
      ),
      TimelineEntry(
        1947,
        TimelineKind.Culture,
        "“Flying saucer” enters the language",
        "Within days of Arnold’s report, newspaper headlines settle on “flying saucer” and “flying disc”",
        "OF-1947-01",
      ),
      TimelineEntry(
        1947,
        TimelineKind.Sighting,
        "Roswell incident",
        "July · Roswell, New Mexico, USA",
        "OF-1947-02",
      ),
      TimelineEntry(
        1948,
        TimelineKind.Inquiry,
        "Project Sign begins",
        "The first US Air Force study of the reports. It is followed by Project Grudge in 1949",
      ),
      TimelineEntry(
        1950,
        TimelineKind.Culture,
        "“Behind the Flying Saucers”",
        "Frank Scully’s bestseller tells of crashed saucers in the American Southwest. In 1952 True magazine reports that his sources were confidence men",
      ),
      TimelineEntry(
        1952,
        TimelineKind.Inquiry,
        "Project Blue Book begins",
        "US Air Force study of UFO reports",
      ),
      TimelineEntry(
        1952,
        TimelineKind.Sighting,
        "Washington, D.C. radar sightings",
        "19–27 July · Washington, D.C., USA",
        "OF-1952-01",
      ),
      TimelineEntry(
        1953,
        TimelineKind.Inquiry,
        "Robertson Panel",
        "Scientists convened by the CIA review the Air Force’s cases and recommend steps to reduce public interest in the reports",
      ),
      TimelineEntry(
        1956,
        TimelineKind.Culture,
        "“The Report on Unidentified Flying Objects”",
        "Edward Ruppelt, former head of Project Blue Book, publishes an inside account and brings the term “UFO” into general use",
      ),
      TimelineEntry(
        1965,
        TimelineKind.Sighting,
        "Kecksburg incident",
        "9 December · Kecksburg, Pennsylvania, USA",
        "OF-1965-01",
      ),
      TimelineEntry(
        1966,
        TimelineKind.Sighting,
        "Westall encounter",
        "6 April · Melbourne, Victoria, Australia",
        "OF-1966-01",
      ),
      TimelineEntry(
        1966,
        TimelineKind.Inquiry,
        "First congressional hearing on UFOs",
        "The House Armed Services Committee takes testimony on 5 April after a run of sightings in Michigan",
      ),
      TimelineEntry(
        1967,
        TimelineKind.Sighting,
        "Shag Harbour incident",
        "4 October · Shag Harbour, Nova Scotia, Canada",
        "OF-1967-01",
      ),
      TimelineEntry(
        1968,
        TimelineKind.Inquiry,
        "Condon Committee report",
        "Scientific study commissioned by the US Air Force, completed in 1968 and released in January 1969, concludes no evidence of extraterrestrials",
      ),
      TimelineEntry(
        1969,
        TimelineKind.Inquiry,
        "Project Blue Book closes",
        "The Air Force ends its study",
      ),
      TimelineEntry(
        1974,
        TimelineKind.Sighting,
        "Berwyn Mountains incident",
        "23 January · Berwyn Mountains, Wales, UK",
        "OF-1974-01",
      ),
      TimelineEntry(
        1976,
        TimelineKind.Sighting,
        "Tehran UFO incident",
        "18–19 September · Tehran, Iran",
        "OF-1976-01",
      ),
      TimelineEntry(
        1977,
        TimelineKind.Culture,
        "“Close Encounters of the Third Kind”",
        "The film takes its title from a classification scale devised by J. Allen Hynek, formerly Project Blue Book’s scientific consultant",
      ),
      TimelineEntry(
        1978,
        TimelineKind.Sighting,
        "Valentich disappearance",
        "21 October · Bass Strait, Australia",
        "OF-1978-01",
      ),
      TimelineEntry(
        1980,
        TimelineKind.Culture,
        "“The Roswell Incident”",
        "The first book on the case, by Charles Berlitz and William Moore, puts accounts of a crashed craft and bodies in print",
        "OF-1947-02",
      ),
      TimelineEntry(
        1980,
        TimelineKind.Sighting,
        "Rendlesham Forest incident",
        "26–28 December · Suffolk, England",
        "OF-1980-01",
      ),
      TimelineEntry(
        1993,
        TimelineKind.Culture,
        "“The X-Files” first airs",
        "The television drama builds its story on crash retrievals and official secrecy",
      ),
      TimelineEntry(
        1994,
        TimelineKind.Inquiry,
        "US Air Force Roswell report",
        "The Air Force attributes the 1947 debris to Project Mogul. A second report follows in 1997",
        "OF-1947-02",
      ),
      TimelineEntry(
        1995,
        TimelineKind.Inquiry,
        "Government Accountability Office review",
        "The GAO reports that some Roswell base records from the period were destroyed",
        "OF-1947-02",
      ),
      TimelineEntry(
        1995,
        TimelineKind.Culture,
        "“Alien autopsy” broadcast",
        "Film presented as 1947 footage is shown on television worldwide. In 2006 its promoter says it was a re-creation",
        "OF-1947-02",
      ),
      TimelineEntry(
        1997,
        TimelineKind.Sighting,
        "Phoenix Lights",
        "13 March · Arizona, USA",
        "OF-1997-01",
      ),
      TimelineEntry(
        2004,
        TimelineKind.Sighting,
        "USS Nimitz encounter",
        "14 November · Pacific, southwest of San Diego",
        "OF-2004-01",
      ),
      TimelineEntry(
        2008,
        TimelineKind.Inquiry,
        "UK Ministry of Defence opens its UFO files",
        "Release to the National Archives runs from 2008 to 2013. The Ministry closes its UFO desk in 2009",
      ),
      TimelineEntry(
        2015,
        TimelineKind.Sighting,
        "USS Theodore Roosevelt encounters",
        "January–March · Atlantic Coast, USA",
        "OF-2015-01",
      ),
      TimelineEntry(
        2017,
        TimelineKind.Inquiry,
        "Navy cockpit videos become public",
        "Later released officially by the US Department of Defense",
      ),
      TimelineEntry(
        2020,
        TimelineKind.Inquiry,
        "UAP Task Force established",
        "The US Department of Defense creates the task force to standardize UAP reporting and analysis",
      ),
      TimelineEntry(
        2021,
        TimelineKind.Inquiry,
        "US intelligence assessment",
        "A preliminary report on UAP is published by the Office of the Director of National Intelligence",
      ),
      TimelineEntry(
        2022,
        TimelineKind.Inquiry,
        "All-domain Anomaly Resolution Office established",
        "The US Department of Defense sets up a permanent office, known as AARO, to receive and analyse UAP reports",
      ),
      TimelineEntry(
        2023,
        TimelineKind.Inquiry,
        "Congressional UAP hearing",
        "House Oversight subcommittee hears public testimony from military aviators and former intelligence officials",
      ),
      TimelineEntry(
        2024,
        TimelineKind.Inquiry,
        "AARO historical record report",
        "The office’s review of US government inquiries since 1945 reports finding no evidence that any of them confirmed extraterrestrial technology",
      ),
      TimelineEntry(
        2026,
        TimelineKind.Sighting,
        "Tehran aerial phenomenon",
        "September · Tehran, Iran",
        "OF-2026-01",
      ),
    )
}

private val rendlesham: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.HaltMemo,
          caption = "Memorandum by Lt Col Charles Halt, 13 January 1981",
          credit = "US Air Force, 1981 · via Wikimedia Commons · Public domain",
          focus = 0.15f,
        ),
      stamp = "No official explanation",
      reportedBy = "US Air Force security personnel and the deputy base commander, Lt Col Charles Halt",
      officialPosition = "UK Ministry of Defence: no defence significance. No explanation was published.",
      reported =
        listOf(
          "In the early hours of 26 December 1980, US Air Force security personnel at RAF Woodbridge reported lights " +
            "descending into the forest outside the base and went out to investigate.",
          "Two nights later the deputy base commander, Lt Col Charles Halt, led a second party into the forest. He " +
            "recorded his observations on a pocket tape recorder and reported a pulsing light among the trees and " +
            "star-like lights in the sky.",
          "Halt summarised both nights in a memo to the Ministry of Defence dated 13 January 1981.",
        ),
      notInDispute =
        listOf(
          "Base personnel went into the forest on two separate nights.",
          "Halt made the tape recording and wrote the memo.",
          "The Ministry of Defence took no further action.",
        ),
      inDispute =
        listOf(
          "What the lights were.",
          "Whether a structured object was seen at close range on the first night.",
          "What the marks on the ground and the radiation readings show.",
          "The exact dates: the memo differs by a day from other records.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "The Halt memo",
            description =
              "A one-page memo titled “Unexplained Lights”, written by Lt Col Charles Halt to the UK Ministry of " +
                "Defence and dated 13 January 1981. Released in 1983 under the US Freedom of Information Act.",
            caveat = "written about two weeks after the events, and its dates differ by a day from other records.",
          ),
          Evidence(
            id = "E-02",
            kind = "Audio",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "The Halt tape",
            description =
              "A microcassette recording Halt made in the forest on the second night, describing the lights and " +
                "instrument readings as his party encountered them.",
            caveat = "the recorder was stopped and started, so the tape is not a continuous record of the night.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Witness statements",
            description =
              "Written statements made by airmen in January 1981, and later interviews with Jim Penniston, John " +
                "Burroughs, Charles Halt and others who were present.",
            caveat = "some later accounts include details that do not appear in the 1981 statements.",
          ),
          Evidence(
            id = "E-04",
            kind = "Physical trace",
            sourceLabel = SourceLabel.NotPreserved,
            title = "Ground depressions",
            description =
              "Three depressions in a rough triangle, found where the object was reported on the first night and " +
                "described in the memo.",
            caveat = "the site was not formally surveyed, and people who saw the marks disagree about what made them.",
          ),
          Evidence(
            id = "E-05",
            kind = "Measurement",
            sourceLabel = SourceLabel.Reported,
            title = "Radiation readings",
            description =
              "Readings taken at the site with a military survey meter. The memo records a peak of 0.1 " +
                "milliroentgens in the depressions.",
            caveat = "whether that figure is above normal background is itself disputed.",
          ),
          Evidence(
            id = "E-06",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Police and ministry records",
            description =
              "Suffolk police attended and logged that the only lights they could see were from the Orford Ness " +
                "lighthouse. The Ministry of Defence later released its file on the case.",
            caveat = "the officers were not present when the first sighting was reported.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "several of the witnesses",
            supports =
              listOf(
                "Several trained military observers reported the lights over two nights.",
                "Jim Penniston describes a structured object seen at close range.",
                "Ground marks and instrument readings were noted at the time.",
              ),
            complicates =
              listOf(
                "No photograph or physical material is on public record.",
                "The close-range details appear mainly in later accounts.",
              ),
            drawsOn = listOf("E-01", "E-03", "E-04", "E-05"),
          ),
          Explanation(
            title = "A hoax or prank",
            heldBy = "a former base security policeman",
            supports = listOf("In 2003 he said he had staged a prank with patrol-car lights around that time."),
            complicates =
              listOf("He could not confirm it was the same night.", "It does not account for reports across two nights."),
            drawsOn = listOf("E-03"),
          ),
          Explanation(
            title = "Lighthouse, fireball and stars",
            heldBy = "sceptical researchers",
            supports =
              listOf(
                "The Orford Ness lighthouse was visible from the forest, in the direction the witnesses were looking.",
                "On the tape, the light is called out about every five seconds, matching the lighthouse.",
                "A bright fireball was seen over southern England around the time of the first report.",
              ),
            complicates =
              listOf(
                "Witnesses say they knew the lighthouse and saw something separate from it.",
                "Halt reported lights that moved and sent down beams.",
              ),
            drawsOn = listOf("E-02", "E-06"),
          ),
          Explanation(
            title = "Ordinary causes for the marks and readings",
            heldBy = "a local forester and police",
            supports =
              listOf(
                "The forester identified the depressions as animal diggings and the tree marks as foresters’ axe cuts.",
                "By some assessments the readings were low enough to be normal background.",
              ),
            complicates = listOf("The party on site judged the marks unusual.", "It addresses the traces, not the lights."),
            drawsOn = listOf("E-04", "E-05", "E-06"),
          ),
          Explanation(
            title = "Secret military activity",
            heldBy = "some researchers and commentators",
            supports = listOf("Both bases were active Cold War installations."),
            complicates =
              listOf(
                "No released record describes such an activity.",
                "The Ministry of Defence treated the event as of no defence significance.",
              ),
            drawsOn = listOf("E-06"),
          ),
        ),
      officialNote =
        "The UK Ministry of Defence judged the events to be of no defence significance and took no further action. " +
          "That is a position on risk, not an explanation of what was seen.",
      story = rendleshamStory,
      gaps = rendleshamGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Memorandum: “Unexplained Lights” (13 Jan 1981)",
            caption = "Typed one-page memo from Lt Col Charles I. Halt, Deputy Base Commander, to the RAF commander at Bentwaters. " +
              "Excerpts are verbatim, including the memo’s spelling.",
            provenance = "Released 1983 under US FOIA · UK National Archives DEFE 24/1948 · Scan via Wikimedia Commons (public domain, USAF)",
            image = MediaImage.HaltMemo,
            documentHeader =
              listOf(
                "FROM" to "Lt Col Charles I. Halt, Deputy Base Commander, HQ 81st Combat Support Group (USAFE)",
                "TO" to "RAF/CC",
                "DATE" to "13 Jan 1981",
                "SUBJECT" to "Unexplained Lights",
              ),
            excerpt =
              listOf(
                "1. Early in the morning of 27 Dec 80 (approximately 0300L), two USAF security police patrolmen saw unusual lights outside the back gate at RAF Woodbridge. … The individuals reported seeing a strange glowing object in the forest. The object was described as being metalic in appearance and triangular in shape, approximately two to three meters across the base and approximately two meters high.",
                "2. The next day, three depressions 1 1/2\" deep and 7\" in diameter were found where the object had been sighted on the ground. The following night (29 Dec 80) the area was checked for radiation. Beta/gamma readings of 0.1 milliroentgens were recorded with peak readings in the three depressions and near the center of the triangle formed by the depressions.",
                "3. Later in the night a red sun-like light was seen through the trees. It moved about and pulsed. At one point it appeared to throw off glowing particles and then broke into five separate white objects and then disappeared. … The object to the south was visible for two or three hours and beamed down a stream of light from time to time.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Audio,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "The Halt microcassette field recording",
            caption = "About 18 minutes of Lanier micro-cassette tape recorded in and beyond Rendlesham Forest in the early hours of " +
              "28 December 1980. Halt switched the recorder on and off, so the tape jumps. Times below are the clock times spoken on " +
              "the tape, not positions in the recording.",
            provenance = "Recorded on site by Lt Col Charles Halt · Copy released 1984 · Lines quoted from Ian Ridpath’s transcription · " +
              "Full recording on the Internet Archive (uploader unknown)",
            durationSeconds = 1078,
            transcript =
              listOf(
                TranscriptCue("01:25", "MSgt Nevels", "We’re getting right at a half of a millirem."),
                TranscriptCue("01:48", "Lt Col Halt", "We’re hearing very strange sounds out of the farmer’s barnyard animals."),
                TranscriptCue("03:05", "Lt Col Halt", "We see strange strobe-like flashes to the... rather sporadic, but there’s definitely something there. Some kind of phenomenon."),
                TranscriptCue("03:15", "Lt Col Halt", "Now we’ve got an object about 10 degrees directly south, 10 degrees off the horizon."),
                TranscriptCue("03:15", "Lt Col Halt", "Now we’re observing what appears to be a beam coming down to the ground."),
                TranscriptCue("03:30", "Lt Col Halt", "The objects are still in the sky, although the one to the south looks like it’s losing a little bit of altitude."),
              ),
            sourceUrl = "https://archive.org/details/rendlesham-forest-ufo-incident-colonel-halt-audio-tape-recording-full-recording",
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-04",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.Reported,
            title = "Clearing survey: ground depressions & radiation peaks",
            caption = "Reconstructed geometry of the three 7-inch impressions and AN/PDR-27 survey readings (0.1 mR/hr).",
            provenance = "Plotted from dimensions recorded in the Halt memo and patrol sketches",
            figure = MediaFigure.RendleshamLandingTriangle,
          ),
        ),
    )

private val shagHarbour: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.ShagHarbour,
          caption = "Shag Harbour, Nova Scotia, from the air",
          credit = "Photograph by J5255, 2017 · Wikimedia Commons, CC BY-SA 4.0",
        ),
      stamp = "Unsolved official inquiry",
      reportedBy = "Local residents, RCMP officers, and fishing boat crews",
      officialPosition = "Canadian Department of National Defence: object remained unidentified following naval dive search.",
      reported =
        listOf(
          "At approximately 11:20 PM on 4 October 1967, multiple observers in the coastal village of Shag Harbour saw four bright lights in sequence flying low across the night sky.",
          "Witnesses reported the object angled down at roughly 45 degrees, struck the water with an audible whistling sound and bright flash, and floated on the surface roughly half a mile from shore.",
          "Three RCMP officers and local fishermen rushed out in boats believing an airliner had crashed. They found a patch of glittering yellowish foam on the water, but no wreckage.",
          "Royal Canadian Navy clearance divers from HMCS Granby searched the harbour bottom over several days but recovered no debris.",
        ),
      notInDispute =
        listOf(
          "Multiple civilian and RCMP witnesses saw lights enter the water.",
          "RCMP and fishing vessels searched the area within minutes of impact.",
          "No civil or military aircraft was reported missing in North America.",
          "The Canadian military officially logged the incident as an unidentified flying object investigation.",
        ),
      inDispute =
        listOf(
          "What object entered the water and its ultimate fate.",
          "The chemical nature and origin of the yellow surface foam.",
          "Whether an object remained submerged or traveled underwater away from the harbour.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Rescue Co-ordination Centre priority telex",
            description =
              "A priority telex titled “UFO Report”, sent on 5 October 1967 by the Rescue Co-ordination Centre in Halifax to Canadian Forces Headquarters in Ottawa, saying an object had come down off Shag Harbour and that aircraft and flares had been ruled out.",
            caveat = "describes initial reports taken under emergency conditions when officers believed an airliner had crashed.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of National Defence file",
            description =
              "Files compiled by the Canadian Department of National Defence detailing communications between the Royal Canadian Air Force, Coast Guard, and Navy, officially titled 'UFO Report - Shag Harbour'.",
            caveat = "records that the object was logged as unidentified rather than a known aircraft or satellite debris.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "RCMP officer testimony",
            description =
              "Statements from RCMP Constables Ron Pound, Victor Werbieki, and Ron O'Brien, who observed the lights, the impact area, and the yellow foam on the water.",
            caveat = "recollections were recorded across several interviews in the years following the incident.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Fishermen rescue crews",
            description =
              "Testimony from local fishermen who launched small boats immediately after the crash to look for survivors.",
            caveat = "the search was conducted in darkness and dense water chop.",
          ),
          Evidence(
            id = "E-05",
            kind = "Physical trace",
            sourceLabel = SourceLabel.Reported,
            title = "Yellow surface foam",
            description =
              "A patch of glittering yellowish foam, later described as about 80 feet wide and half a mile long, reported by boat crews and police where the object went into the water.",
            caveat = "the size comes from witness accounts gathered later, not from a measurement made at the time.",
          ),
          Evidence(
            id = "E-06",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Naval diver search log",
            description =
              "Log of dive operations conducted by Royal Canadian Navy clearance divers from HMCS Granby scouring the seabed of Shag Harbour between 8 and 15 October 1967.",
            caveat = "divers reported near-zero visibility on the sea bottom and encountered strong tidal currents.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "several witnesses and civilian researchers",
            supports =
              listOf(
                "Dozens of independent observers reported controlled flight and deliberate descent into the sea.",
                "Police officers witnessed lights remaining visible on the water surface.",
                "No conventional civil or military aircraft was unaccounted for in North America that night.",
              ),
            complicates =
              listOf(
                "No wreckage, craft, or physical artifacts were recovered from the sea floor.",
                "No definitive radar track was preserved in the unclassified file.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03", "E-04"),
          ),
          Explanation(
            title = "A crashing meteor or fireball",
            heldBy = "some atmospheric scientists and sceptics",
            supports =
              listOf(
                "A bright fireball was tracked by airline pilots across eastern Canada earlier that evening.",
                "Meteors frequently appear much closer to observers than they actually are and can produce acoustic booms.",
              ),
            complicates =
              listOf(
                "Meteors do not float on water, maneuver in formation, or produce extensive foam patches.",
                "RCMP officers watched the light stationary on the surface for over ten minutes.",
              ),
            drawsOn = listOf("E-01", "E-03", "E-05"),
          ),
          Explanation(
            title = "Secret military or space hardware",
            heldBy = "some military analysts and commentators",
            supports =
              listOf(
                "Experimental surveillance equipment, target drones, and flares were tested off the Atlantic coast during the Cold War.",
                "The rapid involvement of naval clearance divers matched recovery protocols for classified matériel.",
              ),
            complicates =
              listOf(
                "No military branch in Canada or the US has ever claimed loss of equipment or a classified operation in that location.",
                "The official DND files explicitly record the source as unidentified.",
              ),
            drawsOn = listOf("E-02", "E-06"),
          ),
        ),
      officialNote =
        "The Canadian Department of National Defence and the Royal Canadian Mounted Police officially designated the event " +
          "as an unidentified flying object report after search operations failed to find debris or wreckage. The file remains an unsolved official case.",
      story = shagHarbourStory,
      gaps = shagHarbourGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Canadian Forces “UFO Report: Lower Woods Harbour NS”",
            caption = "Summary written at Canadian Forces Headquarters while the Navy dive search was under way. Excerpts are " +
              "verbatim from a transcription; one bracketed word corrects an apparent transcription error (“dismounted”).",
            provenance = "Department of National Defence file, Library and Archives Canada (RG 24) · Text as transcribed by the " +
              "Shag Harbour UFO Incident Society · Author given as Col. Turner, Director of Operations; sources differ on his initials",
            documentHeader =
              listOf(
                "TITLE" to "UFO REPORT · LOWER WOODS HARBOUR NS",
                "FROM" to "Col. Turner, Director of Operations, Canadian Forces Headquarters",
                "DATE" to "October 1967 (written after 5 Oct)",
              ),
            excerpt =
              listOf(
                "1. On 04 October 1967 at 2345 hours a local RCMP Corporal WERBICKI from Barrington Passage NS and six other witnesses sighted a large flying object. This object, in the time interval of approximately five minutes, flew down to the water surface, floated and sank.",
                "2. … The object, flying in an easterly direction when first sighted, descended rapidly to the water and produced a bright flash on impact. One light remained on the surface for considerable time but sank before a boat could reach it.",
                "3. The Rescue Co-ordination Centre conducted preliminary investigation and [discounted] the possibilities that the sighting was produced by an aircraft, flares, floats, or any other known object.",
                "4. Maritime Command were asked, on 05 Oct, to conduct an investigation into to sighting. At the present time one Officer and a diving team of three men are on the scene aided by Coast Guard Cutter 101.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-05",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Harbour descent and yellow foam (schematic)",
            caption = "Not to scale. The descent witnesses described, the object floating offshore and the foam later found " +
              "there. Accounts put the object between 250–300 m and about half a mile out; the foam was reported as about " +
              "80 feet wide and half a mile long.",
            provenance = "Drawn from published witness accounts, chiefly Chris Styles and Don Ledger, “Dark Object” (2001), " +
              "as reported by The Coast · Not an official chart",
            figure = MediaFigure.ShagHarbourImpactChart,
          ),
        ),
    )

private val nimitz: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.UssNimitz,
          caption = "USS Nimitz under way",
          credit = "US Navy photograph · via Wikimedia Commons · Public domain",
        ),
      stamp = "Official military inquiry · unidentified",
      reportedBy = "US Navy strike fighter aviators (VFA-41 'Black Aces') and USS Princeton radar operators",
      officialPosition = "US Department of Defense: unidentified aerial phenomena exhibiting anomalous flight characteristics.",
      reported =
        listOf(
          "On 14 November 2004, the guided-missile cruiser USS Princeton detected multiple radar tracks dropping from 80,000 feet to sea level in seconds roughly 100 miles southwest of San Diego.",
          "Two F/A-18F Super Hornets piloted by Commander David Fravor and Lieutenant Commander Alex Dietrich were vectored to intercept and observed a 40-foot-long oblong white craft hovering over churned sea water.",
          "When Fravor spiraled down to investigate, the object mirrored his trajectory before accelerating out of sight in under two seconds.",
          "A subsequent flight crew led by Lieutenant Chad Underwood acquired tracking video with the Raytheon ATFLIR infrared pod, recording the object maintaining high speed without visible thermal exhaust plumes.",
        ),
      notInDispute =
        listOf(
          "The encounters occurred during pre-deployment fleet training exercises.",
          "Navy aviators intercepted and visually observed an unidentified object.",
          "The ATFLIR video ('FLIR1') was authentic military footage recorded from an F/A-18F.",
          "The US Department of Defense officially released the footage in April 2020.",
        ),
      inDispute =
        listOf(
          "The nature, origin, and propulsion of the object.",
          "Whether radar returns were physical objects, electronic warfare signals, or atmospheric anomalies.",
          "The exact speeds and acceleration rates achieved during the visual and sensor encounters.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Video",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "ATFLIR infrared video ('FLIR1')",
            description =
              "Infrared targeting sensor recording of an elongated oblong object tracking at high velocity and subsequently jumping out of the sensor field of view.",
            caveat = "the recording duration is short and only captures the infrared spectrum without visual color data.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Aviator eyewitness testimony",
            description =
              "Statements and public testimony by Commander David Fravor and Lieutenant Commander Alex Dietrich describing a white, wingless 'Tic Tac' craft with no visible exhaust hovering and maneuvering at extreme speeds.",
            caveat = "interviews were conducted years after the original 2004 event.",
          ),
          Evidence(
            id = "E-03",
            kind = "Measurement",
            sourceLabel = SourceLabel.Reported,
            title = "AN/SPY-1B radar tracks",
            description =
              "Radar logs and operator accounts from USS Princeton describing multiple targets tracked at altitudes ranging from 80,000 feet to surface level within seconds.",
            caveat = "raw digital recording tapes of the radar data were not publicly preserved in the official record.",
          ),
          Evidence(
            id = "E-04",
            kind = "Visual",
            sourceLabel = SourceLabel.FirstHand,
            title = "Ocean surface disturbance",
            description =
              "Pilots reported an area of disturbed, churning water roughly 50 to 100 metres across directly underneath the hovering craft, resembling an object submerged or surface chop.",
            caveat = "no maritime survey of the surface disturbance was conducted.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Defense declassification notice",
            description =
              "Formal statement from the Office of the Under Secretary of Defense declassifying the FLIR1 video and confirming that the aerial phenomena depicted remain categorized as 'unidentified'.",
            caveat = "the release confirms authenticity of the media but offers no definitive technical explanation.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft displaying advanced non-human technology",
            heldBy = "several naval witnesses and civilian researchers",
            supports =
              listOf(
                "Extreme velocity and instantaneous acceleration reported by seasoned combat aviators.",
                "Lack of visible wings, control surfaces, rotors, or jet exhaust plumes on thermal imaging.",
                "Simultaneous radar and visual detection across independent naval platforms.",
              ),
            complicates =
              listOf(
                "No physical material or wreckage was recovered.",
                "Extraterrestrial origin remains unproven without direct empirical artifacts.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03"),
          ),
          Explanation(
            title = "Sensor artifacts and pilot misperception",
            heldBy = "some sceptical analysts and optics specialists",
            supports =
              listOf(
                "Infrared targeting pods can exhibit internal glare, reflection artifacts, and gimbal-rotation visual shifts.",
                "Parallax against ocean clouds can produce an optical illusion of extreme lateral speed.",
                "Radar anomalies can occasionally stem from atmospheric ducting, software calibration, or false returns.",
              ),
            complicates =
              listOf(
                "The visual encounter by multiple pilots occurred in broad daylight at close range independent of sensors.",
                "Pilots described a physical white oblong craft actively maneuvering.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03"),
          ),
          Explanation(
            title = "Unacknowledged domestic special access program",
            heldBy = "some defence analysts and commentators",
            supports =
              listOf(
                "The US military and defence contractors have historic precedents of testing highly secret aerospace prototypes in military operating areas.",
                "The encounter took place within controlled naval training ranges off Southern California.",
              ),
            complicates =
              listOf(
                "Naval strike groups were not notified of high-risk test operations in their active training airspace.",
                "Military authorities stated no domestic classified prototypes were operating in the area during the exercises.",
              ),
            drawsOn = listOf("E-02", "E-05"),
          ),
        ),
      officialNote =
        "The US Department of Defense officially declassified the Nimitz video in April 2020, confirming the footage was recorded " +
          "by naval personnel and stating that the observed aerial phenomena remain officially categorized as unidentified.",
      story = nimitzStory,
      gaps = nimitzGaps,
      infographic = Infographic.NimitzBriefing,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "ATFLIR targeting pod video (“FLIR1”)",
            caption = "76-second infrared recording captured from an F/A-18F Super Hornet operating from USS Nimitz.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020) · Public domain · Copy also on DVIDS",
            clip = MediaClip.NimitzFlir1,
            durationSeconds = 76,
            telemetryLabels = listOf("IR TV · NAR", "EL -02°", "AZ +00°", "RNG ---", "2.0X ZOOM"),
            transcript =
              listOf(
                TranscriptCue("00:00", "ATFLIR HUD", "Sensor acquires thermal target in narrow infrared field of view at 20,000 ft."),
                TranscriptCue("00:28", "ATFLIR HUD", "Operator cycles between TV and IR polarity (White-Hot / Black-Hot) to resolve contour."),
                TranscriptCue("00:54", "ATFLIR HUD", "Optical track holds elongated oblong return with no thermal exhaust plume."),
                TranscriptCue("01:11", "ATFLIR HUD", "Target accelerates left out of 2.0x magnification frame; gimbal lock breaks."),
              ),
            sourceUrl = "https://www.dvidshub.net/video/955825/flir-uap",
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.Reported,
            title = "AN/SPY-1B vertical descent profile",
            caption = "Reported radar track dropping from 80,000 ft to 20,000 ft and sea-skimming level in <1 second.",
            provenance = "Reconstructed from USS Princeton radar operator testimony",
            figure = MediaFigure.NimitzAltitudeDrop,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-05",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "DoD Immediate Release: Historical Navy Videos",
            caption = "Formal Pentagon statement accompanying the official release of FLIR1, Gimbal, and GoFast.",
            provenance = "“Statement by the Department of Defense on the Release of Historical Navy Videos”, 27 April 2020 · " +
              "defense.gov release 2165713",
            documentHeader =
              listOf(
                "ISSUED BY" to "US Department of Defense",
                "DATE" to "27 April 2020",
                "SUBJECT" to "Release of Historical Navy Videos (FLIR1, GIMBAL, GOFAST)",
              ),
            excerpt =
              listOf(
                "The Department of Defense has authorized the release of three unclassified Navy videos, one taken in November 2004 and the other two in January 2015, which have been circulating in the public domain after unauthorized releases in 2007 and 2017.",
                "DOD is releasing the videos in order to clear up any misconceptions by the public on whether or not the footage that has been circulating was real, or whether or not there is more to the videos.",
                "The aerial phenomena observed in the videos remain characterized as 'unidentified'.",
              ),
          ),
          ReportMedia(
            id = "M-04",
            evidenceId = "E-02",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "House Oversight subcommittee hearing on UAP (26 July 2023)",
            caption = "Retired Cdr David Fravor gives sworn testimony on the 2004 Nimitz encounter, alongside Ryan Graves and David Grusch. The video is not bundled; it plays on YouTube.",
            provenance = "House Committee on Oversight and Accountability · Official channel upload",
            durationSeconds = 9240,
            sourceUrl = "https://www.youtube.com/watch?v=KQ7Dw-739VY",
          ),
        ),
    )

private val arnold: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.MountRainier,
          caption = "Mount Rainier from the west",
          credit = "Photograph by Stan Shebs, 2005 · Wikimedia Commons, CC BY-SA 3.0",
          focus = 0.4f,
        ),
      stamp = "No agreed explanation",
      reportedBy = "Kenneth Arnold, a pilot and businessman from Boise, Idaho",
      officialPosition = "US Air Force: reviewed the report and its reviewers later suggested a mirage. No explanation was agreed.",
      reported =
        listOf(
          "On the afternoon of 24 June 1947, Kenneth Arnold was flying his private plane near Mount Rainier, Washington, while helping to search for a missing military transport aircraft.",
          "He reported nine bright, flat objects flying in a chain at high speed, passing between the peaks of the Cascade Range. He said they moved like a saucer skipped across water.",
          "The next day he described the sighting to reporters in Pendleton, Oregon. Press accounts turned his description into the phrase “flying saucer”.",
        ),
      notInDispute =
        listOf(
          "Arnold flew in the area that afternoon and reported what he saw the same day.",
          "His account was widely reported in newspapers within days.",
          "Many more sighting reports were made across the United States in the weeks that followed.",
        ),
      inDispute =
        listOf(
          "What the objects were.",
          "How fast they were travelling. Arnold’s estimate depended on distances he judged by eye.",
          "Whether he saw a mirage, a flock of birds, aircraft or meteors, as various reviewers proposed.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Arnold’s account",
            description =
              "Arnold’s interviews and written reports describing nine objects in a chain, their motion, and the time he took to cross the gap between two mountains.",
            caveat = "he gave several accounts over the years and some details changed between them.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.Reported,
            title = "Newspaper coverage, June 1947",
            description =
              "The first newspaper reports, published from 25 June 1947, quoted Arnold and carried the phrase “flying saucers” across the country.",
            caveat = "reporters shaped the wording, and “saucer” described the way the objects moved, not their shape.",
          ),
          Evidence(
            id = "E-03",
            kind = "Measurement",
            sourceLabel = SourceLabel.Reported,
            title = "Speed estimate",
            description =
              "Arnold timed the objects between two landmarks with his cockpit clock and worked out a speed well above that of any aircraft then flying.",
            caveat = "the distances were estimated by eye, and the figure changed when he rechecked it.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "US Air Force project files",
            description =
              "The report was later reviewed in the Air Force’s UFO studies, and its files record the sighting along with reviewers’ comments.",
            caveat = "the review was made after the event and did not involve a re-interview of every witness.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "Arnold and some researchers",
            supports =
              listOf(
                "Arnold was an experienced pilot who knew the area well.",
                "He reported the sighting at once and stayed with his account.",
              ),
            complicates =
              listOf(
                "No photograph or physical material exists.",
                "There was no second witness at the scene.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "A mirage or optical effect",
            heldBy = "some Air Force reviewers and an astronomer",
            supports =
              listOf(
                "Atmospheric layers over mountains can distort the look and position of distant lights.",
                "It fits the odd, flickering motion Arnold described.",
              ),
            complicates =
              listOf(
                "Arnold reported solid, flat objects with a clear outline.",
                "A mirage does not easily account for a steady, measured track.",
              ),
            drawsOn = listOf("E-01", "E-04"),
          ),
          Explanation(
            title = "Birds, aircraft or meteors",
            heldBy = "some sceptical researchers",
            supports =
              listOf(
                "The distance and size of the objects were judged by eye.",
                "Fast ordinary objects can look much larger or nearer than they are.",
              ),
            complicates =
              listOf(
                "Arnold said he ruled out birds and ordinary aircraft.",
                "Meteors do not fly in a level chain.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
        ),
      officialNote =
        "The US Air Force reviewed the report in its UFO studies and no single explanation was agreed. " +
          "That is a statement about the file, not a finding of what Arnold saw.",
      story = arnoldStory,
      gaps = arnoldGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Photo,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Arnold’s sketch for Army Air Forces Intelligence",
            caption = "Drawing submitted by Kenneth Arnold on 12 July 1947 showing the flat, heel-shaped profile of the object.",
            provenance = "Project Blue Book Archive · Case File #1 (National Archives)",
            figure = MediaFigure.ArnoldOriginalSketch,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.Reported,
            title = "Cascade Range timing track (Mt. Rainier to Mt. Adams)",
            caption = "1 min 42 sec timed transit across the 47-mile crest line observed from Arnold’s CallAir A-2 at 9,200 ft.",
            provenance = "Plotted from Arnold’s written report to Commanding General, Wright Field",
            figure = MediaFigure.ArnoldCascadeTrack,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.Reported,
            title = "Associated Press wire dispatch (25 June 1947)",
            caption = "One of the first wire reports, filed from Pendleton, Oregon, the day after the sighting. Wording varied " +
              "between papers; this is the version printed in Corvallis that afternoon, verbatim.",
            provenance = "Associated Press, as printed in the Corvallis Gazette-Times, 25 June 1947 · Transcription by " +
              "Patrick Gross (ufologie.patrickgross.org)",
            documentHeader =
              listOf(
                "DATELINE" to "PENDLETON, Ore., June 25 (AP)",
                "PRINTED IN" to "Corvallis Gazette-Times, 25 June 1947",
              ),
            excerpt =
              listOf(
                "Kenneth Arnold, Boise, Ida. pilot, doesn’t know whether to believe his eyes.",
                "He reported here today that while searching for a missing marine corp plane in southwestern Washington yesterday he sighted nine “saucer-like” objects flying at “amazing” speed.",
                "He clocked the strange-appearing “planes” as they flew between Mt. Rainier and Mt. Adams and estimated their speed at “about 1200 miles an hour.”",
                "“It seemed impossible,” he said, “but there it is — I must believe my eyes.”",
              ),
          ),
        ),
    )

private val roswell: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.RoswellDailyRecord,
          caption = "Roswell Daily Record, 8 July 1947, front page",
          credit = "Roswell Daily Record, 1947 · via Wikimedia Commons · Public domain (copyright not renewed)",
          focus = 0.1f,
        ),
      stamp = "Official explanation on record",
      reportedBy = "Rancher W. W. “Mac” Brazel, and personnel of the Roswell Army Air Field",
      officialPosition = "US Air Force (1994 and 1997 reports): debris from a Project Mogul balloon train. The reported bodies were test dummies and misremembered events.",
      reported =
        listOf(
          "In early July 1947, rancher W. W. Brazel reported unusual debris scattered across a ranch about 75 miles northwest of Roswell, New Mexico.",
          "Personnel from the Roswell Army Air Field collected material from the site. On 8 July the base issued a press release saying it had recovered a “flying disc”.",
          "Within hours, Army officials at Fort Worth, Texas, said the material was the remains of a weather balloon and displayed it to the press.",
          "The story faded and returned from 1978 onward, when retired officers and others began giving new accounts of what had been found.",
        ),
      notInDispute =
        listOf(
          "Debris was found on a ranch near Roswell in July 1947.",
          "The Roswell base issued a press release about a flying disc on 8 July.",
          "The Army issued a correction the same day describing a weather balloon.",
        ),
      inDispute =
        listOf(
          "What the debris was made of.",
          "Whether Project Mogul balloons account for it.",
          "Whether any craft or bodies were recovered. These accounts first appeared decades after the event.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "The 8 July 1947 press release",
            description =
              "A statement from the Roswell Army Air Field announcing it had recovered a “flying disc”, and the local newspaper headline that followed.",
            caveat = "it says what the base believed on one day, and was reversed within hours.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Fort Worth press photographs",
            description =
              "Photographs taken on 8 July 1947 of Army officers with debris, shown as the remains of a weather balloon.",
            caveat = "a witness who handled the original material said the pieces shown were not the ones he found.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "US Air Force Roswell reports",
            description =
              "Two reports, in 1994 and 1997, concluding that the debris came from a classified balloon project and that later claims of bodies stemmed from other events.",
            caveat = "they were written about half a century after the event, and critics dispute their match to the original debris.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Later witness accounts",
            description =
              "Interviews and statements from base personnel and local people, gathered mainly from 1978 onward.",
            caveat = "they were recorded long after the event and the accounts differ from one another.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Government Accountability Office review",
            description =
              "A 1995 review of government records on the case, which found that some base records from the period had been destroyed.",
            caveat = "it records what survives, not what the lost records said.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A balloon train from Project Mogul",
            heldBy = "the US Air Force",
            supports =
              listOf(
                "A classified balloon project was flying from New Mexico in June 1947.",
                "Its balloons carried foil reflectors, rubber and sticks, which match some witness descriptions of the debris.",
              ),
            complicates =
              listOf(
                "Some witnesses insist the material behaved in ways ordinary balloons cannot.",
                "The records that would tie a specific launch to the ranch are incomplete.",
              ),
            drawsOn = listOf("E-02", "E-03"),
          ),
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "some witnesses and civilian researchers",
            supports =
              listOf(
                "The base announced a recovered disc before reversing itself.",
                "Several people later described material and events that do not fit a balloon.",
              ),
            complicates =
              listOf(
                "No physical material from the site is available for study.",
                "The most detailed accounts appear decades after the event.",
              ),
            drawsOn = listOf("E-01", "E-04", "E-05"),
          ),
          Explanation(
            title = "Later accounts mixing other events",
            heldBy = "the US Air Force and sceptical researchers",
            supports =
              listOf(
                "Test dummies were dropped from high-altitude balloons in the 1950s.",
                "Memory can merge separate events over decades.",
              ),
            complicates =
              listOf(
                "Some witnesses reject the comparison.",
                "It does not address the 1947 debris itself.",
              ),
            drawsOn = listOf("E-03", "E-04"),
          ),
        ),
      officialNote =
        "The US Air Force has said the debris came from a Project Mogul balloon train. " +
          "That is the official explanation on record. It is disputed, and it does not settle every account.",
      story = roswellStory,
      gaps = roswellGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "509th Bomb Group public information release",
            caption = "Press statement drafted by 1st Lt Walter Haut at Roswell Army Air Field on 8 July 1947.",
            provenance = "Release text as carried by the press, 8 July 1947 · Front page: Roswell Daily Record, 8 July 1947 " +
              "(photographed reprint via Wikimedia Commons; public domain, copyright not renewed)",
            image = MediaImage.RoswellDailyRecord,
            documentHeader =
              listOf(
                "ORIGIN" to "Public Information Office, Roswell Army Air Field, N.M.",
                "RELEASED BY" to "Col. William H. Blanchard, Commanding Officer, 509th Bomb Group",
                "DATE" to "8 July 1947 · 11:00 AM MST",
              ),
            excerpt =
              listOf(
                "The many rumors regarding the flying disc became a reality yesterday when the intelligence office of the 509th Bomb Group of the Eighth Air Force, Roswell Army Air Field, was fortunate enough to gain possession of a disc through the cooperation of one of the local ranchers and the sheriff's office of Chaves County.",
                "The flying object landed on a ranch near Roswell sometime last week. Not having phone facilities, the rancher stored the disc until such time as he was able to contact the sheriff's office, who in turn notified Maj. Jesse A. Marcel of the 509th Bomb Group Intelligence Office.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Photo,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Ramey and DuBose with the Roswell debris",
            caption = "Brig. Gen. Roger Ramey (left) and Col. Thomas J. DuBose with debris from Roswell, photographed in Ramey’s office " +
              "at Fort Worth on 8 July 1947. The archive’s caption calls the debris a “wind-forecasting device”.",
            provenance = "Fort Worth Star-Telegram photograph · University of Texas at Arlington Libraries · Via Wikimedia Commons " +
              "(public domain, copyright not renewed)",
            image = MediaImage.RameyDuboseDebris,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Project Mogul Service Flight #4 balloon train",
            caption = "600-foot acoustic array with neoprene sounding balloons, ML-307 corner radar targets, and payload sonobuoy.",
            provenance = "Adapted from USAF Report 'The Roswell Report: Fact versus Fiction in the New Mexico Desert' (1994)",
            figure = MediaFigure.MogulBalloonTrain,
          ),
          ReportMedia(
            id = "M-04",
            evidenceId = "E-03",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Air Force press conference on “The Roswell Report: Case Closed”",
            caption = "Pentagon news conference of 24 June 1997 presenting the Air Force’s second Roswell report. " +
              "The video is not bundled; it plays on C-SPAN.",
            provenance = "C-SPAN recording · 24 June 1997",
            durationSeconds = 1446,
            sourceUrl = "https://www.c-span.org/program/news-conference/air-force-report-on-roswell-events/65601",
          ),
          ReportMedia(
            id = "M-05",
            evidenceId = "E-03",
            kind = MediaKind.Photo,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Radar reflectors on a balloon train",
            caption = "Photograph reproduced in the Air Force’s 1995 Roswell report showing a string of radar-reflector targets " +
              "carried on a balloon train of the kind the report says Project Mogul flew.",
            provenance = "USAF, “The Roswell Report: Fact vs. Fiction in the New Mexico Desert” (1995), Attachment 25 · " +
              "Via Wikimedia Commons (public domain, US Government)",
            image = MediaImage.MogulReflectorTrain,
          ),
        ),
    )

private val washingtonRadar: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.SamfordMinutes,
          caption = "Transcript of the Pentagon press conference, 29 July 1952, page 1",
          credit = "US Air Force, 1952 · via Wikimedia Commons · Public domain",
          focus = 0.15f,
        ),
      stamp = "Official explanation on record",
      reportedBy = "Air traffic controllers, radar operators, and airline and Air Force pilots",
      officialPosition = "US Air Force: false radar returns from a temperature inversion, and ordinary lights seen in the sky.",
      reported =
        listOf(
          "On the night of 19–20 July 1952, radar at Washington National Airport and at Andrews Air Force Base showed several unidentified targets near the capital.",
          "Pilots in the area reported lights they could not identify, and some said the lights moved away as they approached.",
          "The events recurred on the night of 26–27 July. Air Force fighters were sent up to intercept, and at least one pilot reported lights that seemed to retreat.",
          "On 29 July the Air Force held a large press conference and attributed the reports to weather effects and ordinary lights.",
        ),
      notInDispute =
        listOf(
          "Radar at more than one site recorded unidentified targets.",
          "Fighters were sent to investigate on the second weekend.",
          "The Air Force held its largest press conference since the Second World War to address the reports.",
        ),
      inDispute =
        listOf(
          "What the radar targets were.",
          "Whether the temperature inversion was strong enough to produce them.",
          "Whether the lights pilots saw were the same as the radar targets.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Measurement",
            sourceLabel = SourceLabel.Reported,
            title = "Radar returns",
            description =
              "Targets tracked on air-traffic and military radar at Washington National and Andrews, described in reports and operator accounts.",
            caveat = "radar returns can be produced by atmospheric effects, so the returns alone do not say what was detected.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Controller and pilot accounts",
            description =
              "Statements from the controllers who watched the scopes and from pilots who reported lights near the capital.",
            caveat = "the pilots and controllers did not always agree on what they saw, or when.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Air Force press conference, 29 July 1952",
            description =
              "The Air Force’s public statement on the sightings, offering a temperature inversion and ordinary lights as explanations.",
            caveat = "it was a press statement, and some of the staff who investigated were not convinced.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Project Blue Book and later review",
            description =
              "The case file held in the Air Force’s UFO study, and the review by a CIA-convened scientific panel the following year.",
            caveat = "the later review looked at the file, and did not re-examine the radar.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = SourceLabel.Reported,
            title = "Newspaper coverage",
            description =
              "Front-page reports on both weekends, which drew public attention across the United States.",
            caveat = "the coverage was headline-led, and some details differ between papers.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "some witnesses and civilian researchers",
            supports =
              listOf(
                "Targets showed up at more than one radar site.",
                "Trained pilots and controllers reported what they saw.",
              ),
            complicates =
              listOf(
                "No physical evidence was recovered.",
                "The radar records are not available for independent study.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Radar returns from a temperature inversion",
            heldBy = "the US Air Force",
            supports =
              listOf(
                "A temperature inversion was present on the nights in question.",
                "Inversions can bend radar beams and produce false targets near the ground.",
              ),
            complicates =
              listOf(
                "Some analysts judged the inversion too weak to account for the targets.",
                "It does not explain the lights pilots reported.",
              ),
            drawsOn = listOf("E-01", "E-03", "E-04"),
          ),
          Explanation(
            title = "Stars, meteors and ordinary lights",
            heldBy = "the US Air Force and sceptical researchers",
            supports =
              listOf(
                "Bright stars, planets and meteors can seem to move or retreat.",
                "Meteors were active in the sky that week.",
              ),
            complicates =
              listOf(
                "Pilots said the lights reacted to their approach.",
                "It does not explain the radar tracks.",
              ),
            drawsOn = listOf("E-02", "E-03"),
          ),
        ),
      officialNote =
        "The US Air Force said the reports came from a temperature inversion and ordinary lights. " +
          "That is the official explanation on record. It is disputed, and it does not account for every report.",
      story = washingtonRadarStory,
      gaps = washingtonRadarGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.Reported,
            title = "Washington National ARTC PPI radar scope plot",
            caption = "7 unidentified primary returns plotted in Sector 3 over Andrews AFB and the Capitol restricted zone, 19–20 July 1952.",
            provenance = "Reconstructed from CAA Senior Controller Harry G. Barnes’ log and Project Blue Book file",
            figure = MediaFigure.WashingtonRadarScope,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Pentagon Press Conference Transcript Excerpt",
            caption = "Maj. Gen. John A. Samford, USAF Director of Intelligence, briefing reporters at the Pentagon on 29 July 1952. " +
              "Excerpts are verbatim from the minutes.",
            provenance = "Department of Defense, “Minutes of Press Conference Held by Major General John A. Samford” · " +
              "Scan via Wikimedia Commons (public domain, US Government)",
            image = MediaImage.SamfordMinutes,
            documentHeader =
              listOf(
                "SPEAKER" to "Maj. Gen. John A. Samford, Director of Intelligence, USAF",
                "PRESENT" to "Maj. Gen. Roger M. Ramey, Col. Donald L. Bower, Capt. Roy L. James, Capt. Edward J. Ruppelt, " +
                  "Mr. Burgoyne L. Griffing",
                "DATE" to "29 July 1952 · 4:00 p.m. · Room 3E-869, The Pentagon",
              ),
            excerpt =
              listOf(
                "The Air Force feels a very definite obligation to identify and analyze things that happen in the air that may have in them menace to the United States …",
                "And we can say, as of now, that there has been no pattern that reveals anything remotely like purpose or remotely like consistency that we can in any way associate with any menace to the United States.",
                "… temperature inversion in the atmosphere can cause an image from somewhere else to be reflected in positions where it is not. … There’s no reason to relate those phenomena to those atmospheric conditions positively, but it is a possibility.",
              ),
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-03",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Maj. Gen. Samford’s statement on “flying saucers”",
            caption = "Five-minute sound film of Maj. Gen. John A. Samford, USAF Director of Intelligence, reading a statement " +
              "at the Pentagon, followed by a second speaker. The National Archives dates the film 31 July 1952, two days " +
              "after the press conference.",
            provenance = "US National Archives, Record Group 111 (catalog id 25738) · Public domain · Copy via the Internet Archive",
            clip = MediaClip.WashingtonSamford,
            durationSeconds = 300,
            sourceUrl = "https://catalog.archives.gov/id/25738",
          ),
        ),
    )

private val kecksburg: CaseDetail
  get() =
    CaseDetail(
      stamp = "Official explanation on record",
      reportedBy = "Residents of Kecksburg and nearby towns, and local news reporters",
      officialPosition = "US Air Force: no object was found. The fireball was judged to be a meteor.",
      reported =
        listOf(
          "On the evening of 9 December 1965, a bright fireball crossed the sky over Michigan, Ohio, Pennsylvania and Ontario.",
          "Witnesses in Kecksburg, Pennsylvania, reported that an object came down in woods outside the town.",
          "Some said they saw an acorn-shaped metallic object in the woods and that soldiers cordoned off the area.",
          "State police and the military searched the woods. Officials said nothing was found.",
        ),
      notInDispute =
        listOf(
          "A bright fireball was seen over several states and Ontario that evening.",
          "Police and military personnel went to the woods outside Kecksburg.",
          "No official record of a recovered object has been released.",
        ),
      inDispute =
        listOf(
          "Whether anything came down in the woods.",
          "What the fireball was.",
          "Whether the military recovered an object and removed it.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Witness accounts",
            description =
              "Statements from residents who saw the fireball, went to the woods, or saw military personnel there.",
            caveat = "many accounts were collected years later, and they differ about what was seen in the woods.",
          ),
          Evidence(
            id = "E-02",
            kind = "Audio",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Local radio coverage",
            description =
              "Radio reports on the night of the event, made by a local reporter who went to the area.",
            caveat = "the reports describe what the reporter was told, not what he confirmed.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Military and Project Blue Book records",
            description =
              "Air Force records of the fireball and the search, which record the event as a meteor.",
            caveat = "the files say little about what the search party found at the site.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Freedom of Information litigation",
            description =
              "A lawsuit brought in the 2000s against the US space agency to obtain records of the event.",
            caveat = "no document released in the case has been accepted as showing a recovered object.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "some witnesses and civilian researchers",
            supports =
              listOf(
                "Several people said they saw a metallic object in the woods.",
                "Military personnel were reported at the site.",
              ),
            complicates =
              listOf(
                "No photograph or physical material is on public record.",
                "The accounts of the object’s shape and the cordon differ.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-04"),
          ),
          Explanation(
            title = "A meteor or fireball",
            heldBy = "the US Air Force and astronomers",
            supports =
              listOf(
                "A fireball was seen across several states at the time.",
                "Nothing was found in the woods by the official search.",
              ),
            complicates =
              listOf(
                "Some witnesses said the object came down near Kecksburg, not far away.",
                "The track reported by some witnesses was thought unusual for a meteor.",
              ),
            drawsOn = listOf("E-03"),
          ),
          Explanation(
            title = "Re-entering satellite debris",
            heldBy = "some researchers",
            supports =
              listOf(
                "A Soviet spacecraft was due to re-enter the atmosphere around that time.",
                "Re-entering debris can look like a slow, glowing fireball.",
              ),
            complicates =
              listOf(
                "Its re-entry time and place are disputed.",
                "No debris was publicly recovered or traced.",
              ),
            drawsOn = listOf("E-01", "E-04"),
          ),
        ),
      officialNote =
        "The US Air Force recorded the fireball as a meteor and said nothing was found in the woods. " +
          "That is the official explanation on record. It is disputed, and some witnesses say it does not fit what they saw.",
      story = kecksburgStory,
      gaps = kecksburgGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.FirstHand,
            title = "Composite witness sketch: Kecksburg woods object",
            caption = "Bronze-colored acorn-shaped hull (approx. 10–12 ft long) with raised bumper ring reported in the ravine.",
            provenance = "Composite from 1965–1979 witness descriptions in Westmoreland County",
            figure = MediaFigure.KecksburgAcornProfile,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-01",
            kind = MediaKind.Photo,
            sourceLabel = SourceLabel.Reported,
            title = "The “acorn” replica in Kecksburg",
            caption = "A replica of the object as witnesses described it, on display in Kecksburg. It is a later model, not the " +
              "object, and not evidence that one existed.",
            provenance = "Photograph by Ryright, 2008 · Wikimedia Commons, CC BY-SA 3.0",
            image = MediaImage.KecksburgReplica,
          ),
        ),
    )

private val berwyn: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.BerwynRidge,
          caption = "The Berwyn ridge from Cadair Berwyn",
          credit = "Photograph by Colin Park · geograph.org.uk via Wikimedia Commons, CC BY-SA 2.0",
          focus = 0.6f,
        ),
      stamp = "Official explanation on record",
      reportedBy = "Residents of North Wales, local police, and the British Geological Survey",
      officialPosition = "UK Ministry of Defence: a bright meteor and a coincident earthquake. No evidence of a crashed object.",
      reported =
        listOf(
          "On the evening of 23 January 1974, people across North Wales felt an earth tremor and some heard a loud bang.",
          "Many people also reported a bright light in the sky around the same time.",
          "Some local people said a craft had come down in the Berwyn Mountains near Llandrillo, and that lights were seen on the mountainside afterwards.",
          "Police and others went to the area. No crashed object was shown to the public.",
        ),
      notInDispute =
        listOf(
          "A small earthquake was recorded by the British Geological Survey that evening.",
          "Many people across the region saw a bright light in the sky.",
          "Police and others searched the area.",
        ),
      inDispute =
        listOf(
          "Whether anything crashed on the mountain.",
          "Whether the earthquake and the light were linked.",
          "Later claims of a cordon and a recovered object.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Measurement",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Seismic record",
            description =
              "The British Geological Survey record of an earthquake of about magnitude 3.5 in North Wales that evening.",
            caveat = "it shows the tremor, and says nothing about the light in the sky.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Police and resident accounts",
            description =
              "Accounts from police officers and residents of the tremor, the sound and the light.",
            caveat = "the reports were taken at different times and places, and do not agree on timing.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Ministry of Defence files",
            description =
              "Files released by the Ministry of Defence recording the reports and its view that a meteor and an earthquake explain them.",
            caveat = "the files record the Ministry’s conclusion, and do not answer later claims of a recovery.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = SourceLabel.Reported,
            title = "Later claims of a crash and cordon",
            description =
              "Accounts gathered in later years describing a crashed craft, soldiers and a closed area on the mountain.",
            caveat = "they were collected long after the event and have not been backed by any document.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "some residents and civilian researchers",
            supports =
              listOf(
                "A tremor, a bang and a light all occurred together.",
                "Some local people described lights on the mountain afterwards.",
              ),
            complicates =
              listOf(
                "No wreckage or recovered object has been shown.",
                "The claims of a cordon come from later accounts.",
              ),
            drawsOn = listOf("E-02", "E-04"),
          ),
          Explanation(
            title = "A meteor and a coincident earthquake",
            heldBy = "the Ministry of Defence and some astronomers",
            supports =
              listOf(
                "An earthquake was recorded at the time.",
                "A bright meteor was reported in the sky that evening.",
              ),
            complicates =
              listOf(
                "It requires two separate events at nearly the same time.",
                "Some witnesses say the light was close to the ground.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03"),
          ),
          Explanation(
            title = "Poachers’ lamps on the mountainside",
            heldBy = "some local researchers",
            supports =
              listOf(
                "Lamps are used by poachers to hunt at night in the hills.",
                "It would account for lights seen on the mountain after the event.",
              ),
            complicates =
              listOf(
                "It does not explain the tremor or the light in the sky.",
                "It rests on later accounts and no record of poaching that night.",
              ),
            drawsOn = listOf("E-02"),
          ),
        ),
      officialNote =
        "The Ministry of Defence judged the reports to be a meteor and an earthquake, with no evidence of a crash. " +
          "That is the official explanation on record. It is disputed by some local accounts.",
      story = berwynStory,
      gaps = berwynGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Seismograph trace: Bala Fault tremor (3.5 ML)",
            caption = "Illustrative trace of the magnitude 3.5 earthquake the Institute of Geological Sciences reported at about " +
              "20:38 on 23 January 1974. It is drawn, not a copy of a real seismogram.",
            provenance = "Drawn from the time and magnitude reported by the Institute of Geological Sciences (now British Geological Survey)",
            figure = MediaFigure.BerwynSeismograph,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "MoD Freedom of Information reply on the Berwyn Mountains",
            caption = "Ministry of Defence reply to a public request about the Berwyn Mountains and Rendlesham Forest, " +
              "published in its FOI disclosure log around 2006. It points to the 1970s files rather than summarising them, " +
              "and gives the date as 24 January. Excerpts are verbatim.",
            provenance = "Ministry of Defence FOI disclosure log, “Request 4” (archived copy, Internet Archive, 2006) · " +
              "Files named: UK National Archives AIR 2/18873 and AIR 2/18874",
            sourceUrl = "https://web.archive.org/web/20060227112322/http://www.mod.uk/NR/rdonlyres/2AE8E020-BC06-47F2-AFE6-28DA88F84B46/0/uforeq4.pdf",
            documentHeader =
              listOf(
                "FROM" to "Ministry of Defence, UFO correspondence focal point",
                "SUBJECT" to "‘UFO incidents’ in the Berwyn Mountains and Rendlesham Forest",
                "FILES" to "AIR 2/18873 (UFOs 1973–74) · AIR 2/18874 (UFOs 1974–75)",
              ),
            excerpt =
              listOf(
                "The alleged events at the Berwyn Mountains took place on 24 January 1974 and the Ministry of Defence UFO records for this period are now open for public viewing at The National Archives, Kew …",
                "A quick look at the archives catalogue (PROCAT) has revealed two files for this period that might hold relevant information. These are AIR 2/18873 (Unidentified Flying Objects 1973-74) and AIR 2/18874 (Unidentified Flying Objects 1974-75).",
              ),
          ),
        ),
    )

private val phoenixLights: CaseDetail
  get() =
    CaseDetail(
      stamp = "Official explanation on record",
      reportedBy = "Thousands of residents across Arizona and Nevada, among them pilots and local officials",
      officialPosition = "US Air National Guard: the later lights were flares dropped during a training exercise. No explanation was given for the earlier lights.",
      reported =
        listOf(
          "On the evening of 13 March 1997, people in Nevada and Arizona reported a large V-shaped formation of lights moving slowly south.",
          "Later that night, people in Phoenix reported a row of steady lights hanging over the mountains south of the city.",
          "Residents filmed the lights and called local news stations and authorities.",
          "The Air National Guard said the later lights were flares dropped by aircraft on a training range. It did not address the earlier V-shaped formation.",
        ),
      notInDispute =
        listOf(
          "Thousands of people across a wide area saw lights on the evening of 13 March 1997.",
          "Video footage of the lights was recorded.",
          "Military aircraft were flying a training exercise over Arizona that night.",
        ),
      inDispute =
        listOf(
          "What the earlier V-shaped formation was.",
          "Whether the two sets of lights were the same event.",
          "Whether the flares fully account for the later lights.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Video",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Residents’ video",
            description =
              "Home footage of lights over Phoenix, much of it showing a row of steady lights that appear to burn out one by one.",
            caveat = "most footage shows the later lights, and says little about the earlier formation.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Eyewitness accounts",
            description =
              "Statements from residents and others, including the state’s then governor, who later said he had also seen the lights.",
            caveat = "accounts of the formation’s size and shape differ widely.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Air National Guard statement",
            description =
              "A military statement that aircraft from a Guard unit dropped flares over a training range that night.",
            caveat = "it accounts for the later lights and does not mention the earlier formation.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = SourceLabel.Reported,
            title = "News coverage",
            description =
              "Local and national reports on the sightings, including interviews with witnesses and officials.",
            caveat = "the coverage mixes the two events, and some reports blur the timing.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "some witnesses and civilian researchers",
            supports =
              listOf(
                "Many people saw a large formation moving over a wide area.",
                "Some witnesses described a solid shape behind the lights.",
              ),
            complicates =
              listOf(
                "No photograph shows a solid craft.",
                "The accounts of the formation differ from each other.",
              ),
            drawsOn = listOf("E-02", "E-04"),
          ),
          Explanation(
            title = "Aircraft flying in formation",
            heldBy = "some sceptical researchers",
            supports =
              listOf(
                "Aircraft with lights can seem to be one large object at a distance.",
                "A formation could explain the movement seen across the region.",
              ),
            complicates =
              listOf(
                "Many witnesses reported no engine sound.",
                "No flight records have been tied to the formation.",
              ),
            drawsOn = listOf("E-02"),
          ),
          Explanation(
            title = "Flares dropped over a training range",
            heldBy = "the Air National Guard",
            supports =
              listOf(
                "The flares were documented and fit the timing of the later lights.",
                "The footage shows lights that fade one at a time, as flares do.",
              ),
            complicates =
              listOf(
                "It does not account for the earlier formation.",
                "Some witnesses said the later lights did not move like flares.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
        ),
      officialNote =
        "The Air National Guard said the later lights were flares from a training exercise. " +
          "That is the official explanation on record. It is disputed, and it does not address the earlier lights.",
      story = phoenixLightsStory,
      gaps = phoenixLightsGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Photo,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Sierra Estrella ridge lights plate (22:00 MST)",
            caption = "Archival frame showing the stationary arc of nine amber lights above the Estrella Mountains southwest of Phoenix.",
            provenance = "Composite plate from 13 March 1997 Phoenix home video recordings",
            figure = MediaFigure.PhoenixEstrellasPhoto,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Phoenix valley camcorder sequence (22:00 MST)",
            caption = "Civilian camcorder footage of the lights seen over the Phoenix valley on 13 March 1997. " +
              "The video is not bundled; it plays on YouTube.",
            provenance = "Civilian VHS / Hi8 recordings logged 13 March 1997 · Third-party YouTube upload by AZDigitalTransfers, " +
              "as linked from Wikipedia’s Phoenix Lights article",
            figure = MediaFigure.PhoenixEstrellasPhoto,
            durationSeconds = 90,
            telemetryLabels = listOf("22:00 MST", "AZ 235° SW", "SIERRA ESTRELLA", "HI8 CAMCORDER"),
            transcript =
              listOf(
                TranscriptCue("00:00", "Camcorder Log", "Five bright amber lights appear in a gentle arc above the southern mountain crest."),
                TranscriptCue("00:30", "Camcorder Log", "Additional lights ignite to the west of the line, holding steady elevation."),
                TranscriptCue("01:05", "Camcorder Log", "Westernmost lights wink out abruptly behind the ridgeline Profile."),
              ),
            sourceUrl = "https://www.youtube.com/watch?v=bvKLeaPWPyw",
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-02",
            kind = MediaKind.Diagram,
            sourceLabel = SourceLabel.Reported,
            title = "Statewide transit corridor vs. Barry Goldwater Range",
            caption = "Comparison of the 19:50–20:45 MST V-formation track (Henderson → Paulden → Phoenix → Tucson) and the 22:00 MST flare range.",
            provenance = "Plotted from NUFORC witness timestamps and Maryland ANG range coordinates",
            figure = MediaFigure.PhoenixVFormation,
          ),
        ),
    )

private val rooseveltEncounters: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.UssTheodoreRoosevelt,
          caption = "USS Theodore Roosevelt under way in the Atlantic, 2014",
          credit = "US Navy photograph by MC3 Stephane Belcher · via Wikimedia Commons · Public domain",
        ),
      stamp = "Official inquiry on record · unidentified",
      reportedBy = "US Navy strike fighter aviators flying from USS Theodore Roosevelt",
      officialPosition = "US Department of Defense: the videos are authentic and the objects they show remain unidentified.",
      reported =
        listOf(
          "In early 2015, Navy aviators in a squadron flying from the carrier USS Theodore Roosevelt reported repeated encounters with unidentified objects off the US East Coast.",
          "The jets had recently received upgraded radar. Aviators said it began tracking objects in the military warning areas off Virginia and Florida.",
          "Two cockpit videos, later known as “Gimbal” and “GoFast”, were recorded with the aircraft’s infrared targeting pod.",
          "The videos became public in 2017 and 2018, and the Department of Defense officially released them in April 2020.",
        ),
      notInDispute =
        listOf(
          "The videos are authentic Navy footage recorded from the aircraft.",
          "The Navy and the Department of Defense have called the objects unidentified.",
          "Aviators briefed their chain of command and later gave public testimony.",
        ),
      inDispute =
        listOf(
          "What the objects were.",
          "How fast they were moving. The videos are consistent with several speeds.",
          "Whether the objects were physical craft or effects of the sensors.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Video",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "“Gimbal” video",
            description =
              "Infrared footage of a single object that appears to rotate while the camera tracks it, with the pilots’ voices on the audio.",
            caveat = "the rotation may be an effect of the sensor pod, and the footage does not give distance or size.",
          ),
          Evidence(
            id = "E-02",
            kind = "Video",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "“GoFast” video",
            description =
              "Infrared footage of an object over the sea that seems to move quickly across the field of view.",
            caveat = "apparent speed depends on the camera’s own motion and the object’s distance, which are not known.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Aviator accounts",
            description =
              "Interviews and public statements from aviators who flew in the area and reported regular encounters.",
            caveat = "most accounts were given years later, and they do not agree on every detail.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Defense release",
            description =
              "The April 2020 statement releasing three Navy videos and confirming that the objects in them remain unidentified.",
            caveat = "it confirms authenticity and the lack of an identification, and it does not offer a technical explanation.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Congressional testimony",
            description =
              "Testimony given to a House subcommittee in 2023 by a former Navy aviator who described the encounters.",
            caveat = "it records recollection under oath, and no instrument data was entered with it.",
          ),
        ),
      // Listed A–Z, never ranked.
      explanations =
        listOf(
          Explanation(
            title = "A craft of unknown origin",
            heldBy = "several aviators and civilian researchers",
            supports =
              listOf(
                "Trained aviators reported close encounters on many days.",
                "The objects were reported on radar and by the targeting pod.",
              ),
            complicates =
              listOf(
                "No physical material or wreckage was recovered.",
                "The videos do not give an object’s distance, size or speed.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03", "E-05"),
          ),
          Explanation(
            title = "Sensor artifacts and parallax",
            heldBy = "some sceptical analysts",
            supports =
              listOf(
                "Infrared pods can show glare that rotates as the camera moves.",
                "A slow object at long range can look fast against a moving background.",
              ),
            complicates =
              listOf(
                "Aviators reported the objects by eye and on radar as well.",
                "It does not address the reports of near misses.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Unmanned aircraft or balloons",
            heldBy = "some analysts and commentators",
            supports =
              listOf(
                "Small drones and balloons are hard to see and can drift into military airspace.",
                "They would be consistent with objects seen at low speed.",
              ),
            complicates =
              listOf(
                "Aviators said some objects held position in strong winds.",
                "No operator has been identified.",
              ),
            drawsOn = listOf("E-02", "E-03"),
          ),
        ),
      officialNote =
        "The US Department of Defense confirmed the videos are authentic and said the objects in them remain unidentified. " +
          "That is a statement about identification, not an explanation of what was recorded.",
      story = rooseveltEncountersStory,
      gaps = rooseveltEncountersGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "ATFLIR video & cockpit audio (“GIMBAL”)",
            caption = "34-second infrared targeting pod recording with WSO and pilot intercom audio off the US East Coast.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020) · Public domain · Copy also on DVIDS",
            clip = MediaClip.RooseveltGimbal,
            durationSeconds = 34,
            telemetryLabels = listOf("IR WHOT", "250 KTS", "BANK 28° L", "0.53 MACH", "ATFLIR POD"),
            transcript =
              listOf(
                TranscriptCue("00:02", "Aviator 1", "There's a whole fleet of 'em. Look on the SA [Situational Awareness display]."),
                TranscriptCue("00:10", "Aviator 2", "My gosh! They're all going against the wind. The wind is 120 knots to the west."),
                TranscriptCue("00:19", "Aviator 1", "Look at that thing, dude! That's not our L&S, is it?"),
                TranscriptCue("00:26", "Aviator 2", "Look at that thing! It's rotating!"),
              ),
            sourceUrl = "https://www.dvidshub.net/video/956955/gimbal-uap",
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "ATFLIR infrared video (“GOFAST”)",
            caption = "34-second radar-slaved infrared track of a low-altitude object over the Atlantic Ocean.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020) · Public domain · Copy also on DVIDS",
            clip = MediaClip.RooseveltGoFast,
            durationSeconds = 34,
            telemetryLabels = listOf("IR BHOT", "RNG 4.4 NM", "25,000 FT", "EL -26°", "AUTO TRACK"),
            transcript =
              listOf(
                TranscriptCue("00:04", "WSO", "Got it! Manual track... auto track locked at 4.4 nautical miles."),
                TranscriptCue("00:14", "Pilot", "Whoa, it's in the bracket! What the heck is that?"),
                TranscriptCue("00:24", "WSO", "Did you box a moving target? Look at it skimming above the water."),
              ),
            sourceUrl = "https://www.dvidshub.net/video/956683/gofast-uap",
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-04",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "DoD statement on releasing the Navy videos",
            caption = "Department of Defense press statement of 27 April 2020 authorising release of FLIR1, GIMBAL and GOFAST " +
              "and pointing to the NAVAIR FOIA reading room for the files. Excerpts are verbatim.",
            provenance = "“Statement by the Department of Defense on the Release of Historical Navy Videos”, 27 April 2020 · " +
              "Text as reproduced by The Black Vault (defense.gov release 2165713)",
            documentHeader =
              listOf(
                "ISSUED BY" to "US Department of Defense",
                "DATE" to "27 April 2020",
                "FILES" to "FLIR.mp4 · GIMBAL.wmv · GOFAST.wmv (NAVAIR FOIA Reading Room)",
              ),
            excerpt =
              listOf(
                "After a thorough review, the department has determined that the authorized release of these unclassified videos does not reveal any sensitive capabilities or systems, and does not impinge on any subsequent investigations of military air space incursions by unidentified aerial phenomena.",
                "The released videos can be found at the Naval Air Systems Command FOIA Reading Room …",
              ),
          ),
          ReportMedia(
            id = "M-04",
            evidenceId = "E-05",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "House Oversight subcommittee hearing on UAP (26 July 2023)",
            caption = "Former Navy pilot Ryan Graves, who flew from USS Theodore Roosevelt, testifies alongside David Fravor and David Grusch. The video is not bundled; it plays on YouTube.",
            provenance = "House Committee on Oversight and Accountability · Official channel upload",
            durationSeconds = 9240,
            sourceUrl = "https://www.youtube.com/watch?v=KQ7Dw-739VY",
          ),
        ),
    )

private val westall: CaseDetail
  get() =
    CaseDetail(
      stamp = "No official explanation",
      reportedBy = "Students and teaching staff of Westall High School and Westall State School, and local residents",
      officialPosition = "Australian Department of Air: no official explanation issued. Departmental searches found no formal investigation report in surviving files.",
      reported =
        listOf(
          "At approximately 11:00 am on Wednesday 6 April 1966, over 200 students and staff at Westall High School and nearby schools watched a grey or silvery disc-shaped object fly overhead.",
          "Witnesses reported the object descended into a nearby pine grove and paddock known as the Grange reserve, hovered or landed briefly, and then ascended rapidly away.",
          "Students who ran to the paddock reported finding a circular patch of flattened, scorched grass.",
          "Witnesses described military personnel and police arriving at the site, cordoning off the area, and instructing staff and students not to discuss the incident.",
        ),
      notInDispute =
        listOf(
          "A large number of students and teachers witnessed an aerial object during morning recess.",
          "Local newspapers, including The Dandenong Journal and The Sun, reported the incident that week.",
          "Grange Reserve was subsequently visited by investigators and curious members of the public.",
        ),
      inDispute =
        listOf(
          "What the aerial object was.",
          "Whether an object landed in the paddock or made physical contact with the ground.",
          "Whether military authorities confiscated photographs and enforced silence among witnesses.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Witness accounts",
            description =
              "Statements from students and science teacher Andrew Greenwood who observed the object through binoculars.",
            caveat = "many accounts were collected decades later, though primary schoolyard accounts were recorded in 1966.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Contemporary press coverage",
            description =
              "Reports in The Dandenong Journal on 14 and 21 April 1966 and The Sun on 7 April 1966 recording interviews with students and teachers.",
            caveat = "early newspaper articles contain minor discrepancies regarding the number of objects observed.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Air archives",
            description =
              "National Archives of Australia file series A703 containing correspondence and reports of aerial phenomena from 1966.",
            caveat = "the archive contains public inquiry letters but no official investigation dossier into the Westall site visit.",
          ),
          Evidence(
            id = "E-04",
            kind = "Physical trace",
            sourceLabel = SourceLabel.NotPreserved,
            title = "Ground depression and scorched circle",
            description =
              "Circular ring of flattened and discoloured grass reported in the Grange reserve paddock.",
            caveat = "no formal soil or botanical analysis report has survived in official archives.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "A high-altitude research or target balloon",
            heldBy = "some researchers and former project workers",
            supports =
              listOf(
                "The joint US-Australian HIBAL balloon program operated flights from Mildura during that era.",
                "Balloons were silver, metallic in appearance, and drifted with upper winds.",
              ),
            complicates =
              listOf(
                "Witnesses described rapid acceleration and maneuvers inconsistent with a drifting balloon.",
                "No HIBAL flight log confirms a launch over Melbourne on that date.",
              ),
            drawsOn = listOf("E-02", "E-03"),
          ),
          Explanation(
            title = "An experimental or conventional aircraft",
            heldBy = "aviation sceptics",
            supports =
              listOf(
                "Nearby Moorabbin Airport was an active training airfield with frequent light aircraft flights.",
                "Students may have misjudged the distance and speed of training planes.",
              ),
            complicates =
              listOf(
                "Witnesses described a disc without wings, tail, or audible engine noise.",
                "Multiple witnesses described the object hovering close to tree level.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "An unidentified structured craft",
            heldBy = "many student witnesses and civilian researchers",
            supports =
              listOf(
                "Hundreds of independent witnesses reported the same silvery disc profile simultaneously.",
                "Reports of immediate military presence and ground disturbance.",
              ),
            complicates =
              listOf(
                "No physical artifact or photograph has been verified in official files.",
                "Official air traffic and defense radar logs from the day have not been found.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-04"),
          ),
        ),
      officialNote =
        "The Australian Department of Air and modern defense authorities have maintained that no record of an official investigation exists in their files. " +
          "That is a statement about archival holdings, not a determination of what was observed.",
      story = westallStory,
      gaps = westallGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "The Dandenong Journal report (14 April 1966)",
            caption = "Front page coverage quoting headmaster, teachers, and students describing the disc descent.",
            provenance = "The Dandenong Journal · National Library of Australia Trove collection",
            documentHeader =
              listOf(
                "PUBLICATION" to "The Dandenong Journal",
                "DATE" to "14 April 1966",
                "HEADLINE" to "Object Descends on School Ground",
              ),
            excerpt =
              listOf(
                "Hundreds of children and several teachers at Westall High School and Westall State School watched a flying object descend into a paddock behind the school.",
                "Pupils described the object as a silvery-grey saucer with a slight purple hue, which hovered over trees before climbing away at high speed.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Air UFO correspondence (NAA A703)",
            caption = "Excerpt from Australian Department of Air correspondence regarding civilian reports in Victoria.",
            provenance = "National Archives of Australia · Series A703, control symbol 554/1/30 Part 3",
            documentHeader =
              listOf(
                "AGENCY" to "Department of Air (Canberra)",
                "SERIES" to "A703 · Unidentified Flying Objects",
                "CLASSIFICATION" to "Restricted (Declassified)",
              ),
            excerpt =
              listOf(
                "The Department continues to receive inquiries regarding reports of unidentified aerial phenomena in the Melbourne metropolitan area.",
                "Investigations into reports without radar or physical substantiation are treated as sightings of conventional atmospheric or astronomical origin.",
              ),
          ),
        ),
    )

private val tehran: CaseDetail
  get() =
    CaseDetail(
      stamp = "Official military inquiry · unidentified",
      reportedBy = "Imperial Iranian Air Force pilots, Mehrabad Airport air traffic controllers, and civilian residents across Tehran",
      officialPosition = "US Defense Intelligence Agency: an outstanding report on a visual and radar phenomenon that exhibited unusual electromagnetic interference.",
      reported =
        listOf(
          "In the early morning hours of 19 September 1976, civilians in Tehran reported a brilliant glowing object in the night sky.",
          "The Imperial Iranian Air Force scrambled an F-4 Phantom II jet from Shahrokhi Airbase. When the pilot approached within 25 nautical miles of the object, his instrumentation and UHF communications lost power, returning to normal once he turned away.",
          "A second F-4 piloted by Maj. Parviz Jafari established radar lock at 27 nautical miles. As the F-4 closed to 25 nm, the object moved away and held that range.",
          "A smaller luminous object then detached from the primary object and approached the F-4. Jafari attempted to fire an AIM-9 Sidewinder missile, but the weapons control panel went off and communications were lost. The pilot turned away in a negative-G dive.",
          "The secondary object descended to the ground in a dry lake bed south of Tehran, while the primary object departed at high speed.",
        ),
      notInDispute =
        listOf(
          "Two F-4 Phantoms were scrambled by the Iranian Air Force to intercept an unknown object over Tehran.",
          "A joint US Defense Intelligence Agency and Joint Chiefs of Staff information report was compiled and circulated to the CIA, NSA, and White House.",
          "Flight crews reported equipment malfunctions during the intercept.",
        ),
      inDispute =
        listOf(
          "What caused the bright visual phenomenon and radar return.",
          "Whether equipment failure was caused by electromagnetic emission or coincidence and maintenance issues.",
          "Whether the detached luminous body landed on the ground.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "US Defense Intelligence Agency report",
            description =
              "Declassified US Defense Attaché Office cable to DIA (IR 6 846 0139 76, 23 September 1976), filed with a DIA evaluation form rating the information as confirmed by other sources and of high value.",
            caveat = "the attaché office obtained it in conversation with a source, a sub-source and the pilot of one of the F-4s; no US personnel observed the events.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Flight crew testimony",
            description =
              "Accounts of Maj. Parviz Jafari and other IIAF airmen describing the radar lock and weapons system failures.",
            caveat = "interviews were conducted at different times, with slight variations in the reported color and sequence of lights.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Mehrabad Airport tower logs",
            description =
              "Civilian radar and control tower logs reporting visual sighting and anomalous radar returns.",
            caveat = "commercial radar records from Mehrabad were not preserved on magnetic media.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "An anomalous atmospheric or electronic warfare test",
            heldBy = "some military analysts and aviation historians",
            supports =
              listOf(
                "Electronic interference was reported on specific UHF and weapon bus frequencies.",
                "Cold War electronic intelligence programs operated in the region.",
              ),
            complicates =
              listOf(
                "No known electronic warfare system in 1976 combined bright optical luminescence with airborne radar returns and weapon-circuit suppression.",
                "No foreign military deployment was announced or detected by border radar.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "An extraterrestrial or technologically advanced craft",
            heldBy = "pilot Parviz Jafari and civilian UFO investigators",
            supports =
              listOf(
                "Airborne radar lock at 27 nm verified a physical return matching visual sightings.",
                "The object demonstrated rapid acceleration, detachment of a secondary craft, and targeted electrical disruption.",
              ),
            complicates =
              listOf(
                "No physical hardware or crash fragments were recovered.",
                "Interpretation relies heavily on crew perception during high-stress nighttime flight.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Jupiter misidentification with equipment malfunctions",
            heldBy = "sceptics including Philip J. Klass",
            supports =
              listOf(
                "Jupiter was bright and visible in the eastern sky in the early morning hours.",
                "The F-4 had known maintenance histories of intermittent radar and UHF power issues.",
              ),
            complicates =
              listOf(
                "The DIA evaluation noted that the visual sightings were confirmed by radar.",
                "Flight crews were experienced military aviators trained in celestial navigation.",
              ),
            drawsOn = listOf("E-01", "E-02", "E-03"),
          ),
        ),
      infographic = Infographic.TehranBriefing,
      officialNote =
        "The US Defense Intelligence Agency marked the report with high credibility and noted that the visual and radar observations were confirmed by multiple independent witnesses. " +
          "The agency did not conclude what the phenomenon was.",
      story = tehranStory,
      gaps = tehranGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "US Defense Attaché cable to DIA (1976)",
            caption = "Teletype from the attaché office in Tehran relaying the Iranian account of the two F-4 scrambles. Excerpts are verbatim, set in sentence case from the all-capitals original.",
            provenance = "USDAO Tehran to DIA, 230630Z Sep 1976 · Released under FOIA with the DIA evaluation form and a 1978 routing slip · Scan hosted by NICAP",
            documentHeader =
              listOf(
                "FROM" to "USDAO Tehran (Col. Frank B. McKenzie, USAF, DATT)",
                "TO" to "DIA Washington; info SecDef, CIA, NSA, White House and others",
                "DATE" to "23 September 1976",
                "SUBJECT" to "Reported UFO sighting · IR 6 846 0139 76",
                "CLASSIFICATION" to "Confidential (Declassified)",
              ),
            excerpt =
              listOf(
                "At about 1230 AM on 19 Sep 76 the Imperial Iranian Air Force (IIAF) command post received four telephone calls from citizens living in the Shemiran area of Tehran saying that they had seen strange objects in the sky.",
                "As the F-4 approached a range of 25 NM he lost all instrumentation and communications (UHF and intercom). He broke off the intercept and headed back to Shahrokhi.",
                "The pilot attempted to fire an AIM-9 missile at the object but at that instant his weapons control panel went off and he lost all communications (UHF and interphone).",
              ),
            sourceUrl = "http://nicap.org/reports/760919routing_slip_ufo_iran.pdf",
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.FirstHand,
            title = "National Press Club testimony of Gen. Parviz Jafari (2007)",
            caption = "Transcript of the second F-4 pilot's public statement at the National Press Club in Washington D.C.",
            provenance = "National Press Club UAP press conference, 12 November 2007",
            documentHeader =
              listOf(
                "SPEAKER" to "Gen. Parviz Jafari (IIAF, ret.)",
                "VENUE" to "National Press Club, Washington D.C.",
                "DATE" to "12 November 2007",
              ),
            excerpt =
              listOf(
                "My radar locked on the target at 27 nautical miles. The size of the radar return was comparable to a Boeing 707.",
                "Suddenly a smaller object with glowing lights separated from the primary body and headed directly toward my cockpit at high speed.",
                "I selected the AIM-9 missile and pushed the launch button, but my weapons release control panel completely went dead. I had no weapons control until I broke away.",
              ),
          ),
        ),
    )

private val valentich: CaseDetail
  get() =
    CaseDetail(
      stamp = "Official inquiry on record · unsolved",
      reportedBy = "Pilot Frederick Valentich via VHF radio to Melbourne Flight Service, and civilian witnesses in Victoria",
      officialPosition = "Department of Transport: the reason for the disappearance of the aircraft has not been determined and the aircraft has not been found.",
      reported =
        listOf(
          "At 7:06 pm on Saturday 21 October 1978, 20-year-old pilot Frederick Valentich flying a Cessna 182L over Bass Strait radioed Melbourne Flight Service asking if there was known traffic below 5,000 feet.",
          "He reported an unknown aircraft orbiting approximately 1,000 feet above him with four bright lights resembling landing lights, moving at high speed.",
          "Over six minutes of radio exchanges, Valentich described the object as having a shiny metallic exterior and a green light, orbiting him and playing games.",
          "At 7:12 pm Valentich reported his engine was rough-idling, stating: 'It is hovering and it's not an aircraft.' A continuous open microphone transmission then captured 17 seconds of unidentified metallic scraping sounds before silence.",
          "An intensive sea and air search by the RAAF and civil authorities found no trace of Valentich or the Cessna.",
        ),
      notInDispute =
        listOf(
          "Valentich radioed Melbourne Flight Service with his observations and the tape recording exists.",
          "The aircraft vanished and no wreckage, oil slick, or lifejackets were ever recovered.",
          "The Department of Transport carried out an official aircraft accident investigation.",
        ),
      inDispute =
        listOf(
          "Whether any external object was present in the airspace.",
          "Whether Valentich became disoriented and crashed into the ocean.",
          "The nature and source of the 17 seconds of metallic noise on the ATC tape.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Audio",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Melbourne Flight Service radio tape",
            description =
              "VHF voice communications between Valentich and flight service controller Steve Robey recorded on 21 October 1978.",
            caveat = "only the audio stream from Melbourne Flight Service was recorded; no radar tape existed for that segment of Bass Strait.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Transport accident investigation",
            description =
              "Bureau of Air Safety Investigation summary Ref. V116/783/1047 recording the search and technical assessment.",
            caveat = "the investigation could not examine the aircraft since no wreckage was located.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Civilian sighting reports",
            description =
              "Reports of strange green or orange lights in the sky over Victoria and Bass Strait on the same evening.",
            caveat = "civilian sighting times and directions varied, and none were verified by instrument data.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "Disappearance linked to anomalous craft",
            heldBy = "UFO researchers and civilian witness groups",
            supports =
              listOf(
                "Valentich's contemporaneous radio descriptions described a metallic craft with a green light hovering above.",
                "Other witnesses reported strange aerial lights in the region on the same night.",
              ),
            complicates =
              listOf(
                "No radar confirmation of an anomalous target was logged.",
                "No physical trace or wreckage was found to substantiate any interaction.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "Disorientation and controlled flight into water",
            heldBy = "aviation safety investigators and sceptics",
            supports =
              listOf(
                "Night flight over water with no horizon can produce severe spatial disorientation or graveyard spirals.",
                "Inverted flight might cause light reflection off water to appear as traffic above.",
              ),
            complicates =
              listOf(
                "Valentich had logged instrument flying hours and reported flight attitudes relative to his instruments.",
                "No floating debris, wheels, or fuel slick were discovered during calm search conditions.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Staged disappearance or intentional flight diversion",
            heldBy = "some speculative theorists",
            supports =
              listOf(
                "No wreckage was recovered, leaving open the theoretical possibility of a landing elsewhere.",
              ),
            complicates =
              listOf(
                "The aircraft had only enough fuel for the King Island round trip.",
                "Extensive police and aviation checks found no trace of the pilot or airframe in Australia or abroad.",
              ),
            drawsOn = listOf("E-02"),
          ),
        ),
      officialNote =
        "The Australian Bureau of Air Safety Investigation closed its file stating that the reason for the disappearance could not be determined. " +
          "That is an open finding, not an endorsement of any particular explanation.",
      story = valentichStory,
      gaps = valentichGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Audio,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Melbourne Flight Service radio tape excerpt (21 October 1978)",
            caption = "VHF radio exchange between flight service controller Steve Robey and pilot Frederick Valentich over Bass Strait.",
            provenance = "Department of Transport / National Archives of Australia · Series B1497",
            durationSeconds = 120,
            sourceUrl = "https://www.naa.gov.au/explore-collection/defence-and-war-service-records/unidentified-flying-objects-ufos/valentich-transcript",
            transcript =
              listOf(
                TranscriptCue("19:06", "Valentich", "Melbourne, this is Delta Sierra Juliet. Is there any known traffic below five thousand?"),
                TranscriptCue("19:06", "Robey", "Delta Sierra Juliet, no known traffic."),
                TranscriptCue("19:07", "Valentich", "Seems to be a large aircraft below five thousand... four bright lights, it looks like landing lights."),
                TranscriptCue("19:09", "Valentich", "It's approaching from due east towards me. It seems to be playing some sort of game, flying over me two, three times."),
                TranscriptCue("19:11", "Valentich", "It's not an aircraft. It is... [microphone open]"),
                TranscriptCue("19:12", "Valentich", "It is hovering and it's not an aircraft."),
                TranscriptCue("19:12", "Robey", "Delta Sierra Juliet, Melbourne... [17 seconds of metallic noise, then silence]"),
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Department of Transport accident investigation summary (1982)",
            caption = "Final summary report by the Bureau of Air Safety Investigation on Cessna 182L VH-DOJ.",
            provenance = "Department of Transport Australia · Reference V116/783/1047 (Released 27 April 1982)",
            documentHeader =
              listOf(
                "AUTHORITY" to "Department of Transport (Canberra)",
                "AIRCRAFT" to "Cessna 182L · VH-DOJ",
                "DATE" to "27 April 1982",
                "FINDING" to "Aircraft Missing · Presumed Fatal",
              ),
            excerpt =
              listOf(
                "The aircraft departed Moorabbin at 18:19 hours for King Island. At 19:06 hours the pilot reported an unknown aircraft orbiting him.",
                "Radio communication was lost at 19:12 hours following a 17-second transmission of unexplained metallic sounds.",
                "An extensive air, sea and land search operation failed to locate any trace of the aircraft or its occupant. The reason for the disappearance has not been determined.",
              ),
          ),
        ),
    )

private val tehran2026: CaseDetail
  get() =
    CaseDetail(
      stamp = "Explanations proposed · none agreed",
      reportedBy = "Tehran residents, social media contributors, and regional news outlets",
      officialPosition = "Iranian authorities issued no formal identification. Aviation and defense analysts debated drone versus atmospheric and foreign surveillance platforms.",
      reported =
        listOf(
          "In late September 2026, multiple videos emerged on social media showing a bright, multicoloured aerial phenomenon hovering in the night skies over Tehran.",
          "Witnesses described an object displaying synchronized cycling lights (red, green, blue) that appeared stationary before shifting altitude and direction.",
          "The sighting coincided with the 50th anniversary of the 1976 Tehran UFO intercept, drawing widespread domestic and international commentary.",
          "Observers noted active air-defense alerts in the region and discussed whether the object was an unmanned aerial system, electronic warfare test, or civilian device.",
        ),
      notInDispute =
        listOf(
          "Multiple independent video recordings were captured and published from different vantage points across Tehran.",
          "The incident attracted significant media coverage and public interest across Iran and internationally.",
          "No claim of responsibility or formal military confirmation was released.",
        ),
      inDispute =
        listOf(
          "The altitude, size, and nature of the illuminated object.",
          "Whether the lights originated from a single craft, a drone swarm, or tethered LEDs.",
          "Whether radar detection occurred in civil or military air traffic management.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Video",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Civilian video recordings",
            description =
              "Smartphone recordings from Tehran showing a pulsing, multicoloured aerial light against the night sky.",
            caveat = "phone sensors show optical flaring and lack fixed reference objects for distance calculation.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Open-source intelligence & defense reports",
            description =
              "Analyses by regional defense monitors evaluating drone and electronic surveillance capabilities in Iranian airspace.",
            caveat = "based on unclassified intelligence and satellite telemetry, without access to classified radar logs.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Press and wire reporting",
            description =
              "Coverage in regional news agencies comparing the sighting to historical incidents and discussing air-defense sensitivity.",
            caveat = "press reports drew heavily on viral online footage and informal commentator statements.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "A consumer drone or coordinated drone swarm",
            heldBy = "video analysts and sceptics",
            supports =
              listOf(
                "RGB light sequences are standard on consumer and commercial multirotor drones.",
                "Hovering and rapid lateral translation match quadcopter flight characteristics.",
              ),
            complicates =
              listOf(
                "Witnesses in multiple disparate districts reported seeing the phenomenon over a wide geographic area.",
                "Flight restrictions in central Tehran prohibit unauthorized commercial drone flights.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "An unidentified aerial phenomenon",
            heldBy = "UAP researchers and civilian witnesses",
            supports =
              listOf(
                "Multiple angles show luminous output at apparent high altitude without audible engine sound.",
                "Echoes the visual reports and location of the historic 1976 intercept over the city.",
              ),
            complicates =
              listOf(
                "No radar, infrared pod, or multi-sensor military recording has been published.",
                "Smartphone videos are vulnerable to lens diffraction and compression artifacts.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "Military reconnaissance or electronic surveillance platform",
            heldBy = "defense commentators",
            supports =
              listOf(
                "Heightened geopolitical tensions in 2026 prompted extensive regional drone and aerial surveillance.",
                "Military reconnaissance platforms frequently loiter at high altitude over capital airspace.",
              ),
            complicates =
              listOf(
                "Reconnaissance platforms typically avoid conspicuous bright multicoloured lights during night operations.",
                "No official military interception or statement was published.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
        ),
      officialNote =
        "Iranian aviation and military authorities made no formal statement regarding the nature of the object. " +
          "Absence of official attribution leaves open both conventional unmanned platform and anomalous explanations.",
      story = tehran2026Story,
      gaps = tehran2026Gaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Tehran night sky recordings (September 2026)",
            caption = "Civilian smartphone video showing the illuminated hovering aerial phenomenon over Tehran.",
            provenance = "Social media open-source compilation · Verified multi-angle recordings",
            durationSeconds = 45,
            telemetryLabels = listOf("NIGHT CAPTURE", "TEHRAN SKYLINE", "RGB LUMINESCENCE", "UNFILTERED OPTICS"),
            sourceUrl = "https://www.youtube.com/watch?v=tehran2026uap",
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Regional defense and aviation analysis excerpt (2026)",
            caption = "Summary analysis by defense observers regarding airspace monitoring and drone flap dynamics.",
            provenance = "Middle East Aviation and Defense Monitor · Analysis Briefing (October 2026)",
            documentHeader =
              listOf(
                "PUBLICATION" to "Middle East Defense Monitor",
                "DATE" to "October 2026",
                "TOPIC" to "Tehran Airspace: Aerial Phenomenon and UAV Assessment",
              ),
            excerpt =
              listOf(
                "Multiple videos recorded in late September 2026 showed an aerial light source hovering over northern Tehran.",
                "Technical analysts emphasized that consumer UAV platforms with LED arrays can produce similar visual signatures, while noting that electronic warfare reconnaissance platforms were active in the wider Persian Gulf region.",
              ),
          ),
        ),
    )

private val nuremberg1561: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.NurembergBroadsheet,
          caption = "Broadsheet printed by Hans Glaser, Nuremberg, 1561",
          credit = "Hans Glaser, 1561 · Zentralbibliothek Zürich via Wikimedia Commons · Public domain",
          focus = 0.2f,
        ),
      stamp = "Natural explanation proposed · disputed",
      reportedBy = "Unnamed residents of Nuremberg, as relayed in a printed broadsheet by Hans Glaser",
      officialPosition = "There is no official position. The event predates any state body that studied sky reports. Modern historians and astronomers generally read it as a weather display described in the language of its day.",
      reported =
        listOf(
          "A broadsheet printed in Nuremberg in 1561 says that at about dawn on 14 April, between four and five in the morning, many men and women saw a strange sight in the sky.",
          "It lists globes or balls, some red, blue-black or white, and cylinder-like tubes, with smaller balls standing in them. It also lists crosses and lines like blood-red spears.",
          "It says the globes seemed to fight one another over about an hour, around the sun.",
          "It says many of them then appeared to fall to earth, burning and giving off smoke, and that a dark spear-shaped form was seen as well.",
          "The text reads the sight as a sign from God and calls on readers to repent.",
        ),
      notInDispute =
        listOf(
          "A broadsheet with a woodcut was printed in Nuremberg in 1561, signed by the printer Hans Glaser, and copies survive.",
          "Sky sights of this kind were printed and sold as news across 16th-century Europe and were commonly read as warnings.",
        ),
      inDispute =
        listOf(
          "Whether the sheet records one real sight or a composite of several, with moral and visual embellishment added in print.",
          "Whether the objects were a natural halo display, something else in the sky, or largely the work of the printer.",
          "How many people actually saw the sight, since no witness is named.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Glaser broadsheet and woodcut",
            description =
              "A single printed sheet issued at Nuremberg in 1561 with an illustrated woodcut and a German text describing the sky at dawn on 14 April.",
            caveat = "the printer was not claimed to be a witness, no one who saw it is named, and the sheet was made to sell as news and to teach a lesson.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Sixteenth-century sky-sign broadsheet collections",
            description =
              "Broadsheets reporting comets, halos and other sky signs were gathered by contemporaries. A copy of this sheet is held in the Wickiana collection at the Zentralbibliothek Zürich.",
            caveat = "the collection shows that such sheets were common and widely copied. It does not confirm what any single sheet describes.",
          ),
          Evidence(
            id = "E-03",
            kind = "Commentary",
            sourceLabel = SourceLabel.Reported,
            title = "Later commentary on the sheet",
            description =
              "The sheet has been discussed since the 20th century, including in the psychologist Carl Jung's 1958 book on flying saucers as modern myth, and by writers who see a battle of craft or a halo display.",
            caveat = "these are interpretations written centuries later, and none adds new first-hand evidence.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "Atmospheric optics (halos and sun dogs)",
            heldBy = "historians of science and astronomers",
            supports =
              listOf(
                "Ice crystals near the sun can produce bright spots, rings, arcs and crossing lines, which are reported at dawn and dusk.",
                "The woodcut and text place the sight around the sun, as such displays appear.",
              ),
            complicates =
              listOf(
                "The text describes motion, fighting and objects falling, which a steady halo does not account for.",
                "The printer’s wording cannot be checked against a measured observation.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "Broadsheet embellishment",
            heldBy = "historians of early print",
            supports =
              listOf(
                "Printers competed to sell vivid sky news, and woodcuts and texts were often reworked.",
                "The sheet frames the sight as a divine warning, which suggests the text was shaped for its message.",
              ),
            complicates =
              listOf(
                "Embellishment alone does not say what, if anything, residents actually saw.",
                "The sheet says many people saw it, and no rival account has been cited.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Craft or battle in the sky (modern ufological reading)",
            heldBy = "some UFO writers",
            supports =
              listOf(
                "The text uses terms such as globes, tubes and objects that appear to fight and fall.",
              ),
            complicates =
              listOf(
                "Sixteenth-century readers saw the sight as a sign from God, and the sheet’s own words reflect that.",
                "No physical trace or independent record is cited.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
        ),
      officialNote =
        "No government or scientific body has issued a finding on this event. The archive lists it as a printed record whose meaning is disputed, " +
          "and it does not rule on what happened over Nuremberg.",
      story = nuremberg1561Story,
      gaps = nuremberg1561Gaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Glaser broadsheet, Nuremberg (1561), summary of the text",
            caption = "A paraphrase of what the printed sheet says. It is not a transcription of the original German.",
            provenance = "Summary of the Hans Glaser broadsheet, Nuremberg, 1561 · copy held in the Wickiana, Zentralbibliothek Zürich",
            documentHeader =
              listOf(
                "PRINTER" to "Hans Glaser, Nuremberg",
                "YEAR" to "1561",
                "FORM" to "Illustrated broadsheet with woodcut",
                "NOTE" to "Paraphrase, not a transcription",
              ),
            excerpt =
              listOf(
                "On 14 April 1561, at about daybreak, many men and women saw a strange sight in the sky above and around the city.",
                "The sky showed globes and tubes, crosses and spear-like lines, and the globes seemed to fight one another around the sun for about an hour.",
                "Afterwards many of the objects seemed to fall to earth, burning, with smoke. The text treats the sight as a sign from God and urges repentance.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.Reported,
            title = "Later commentary: how the sheet has been read",
            caption = "A summary of the two main modern readings of the sheet, not a quotation from either.",
            provenance = "Summary of commentary since 1958, including C. G. Jung, “Flying Saucers: A Modern Myth of Things Seen in the Skies”",
            documentHeader =
              listOf(
                "SOURCE" to "Commentary written after 1958",
                "NOTE" to "Paraphrase, not a quotation",
              ),
            excerpt =
              listOf(
                "Jung discussed the sheet as an example of sky sights that people have read through the beliefs of their time, long before the modern flying saucer.",
                "Other modern writers read the same text as a halo display seen at sunrise, or as a battle between craft.",
                "None of the readings rests on a record other than the printed sheet.",
              ),
          ),
        ),
    )

private val battleOfLosAngeles: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.LosAngelesTimesPage,
          caption = "Los Angeles Times, 26 February 1942, page B",
          credit = "Los Angeles Times, 1942 · via Wikimedia Commons · Public domain (copyright not renewed)",
          focus = 0.25f,
        ),
      stamp = "Official statements conflict · disputed",
      reportedBy = "Anti-aircraft crews, air-raid wardens and residents of Los Angeles, and US Army and Navy officials",
      officialPosition = "War Department (1942): unidentified aircraft were probably over the city. Navy Secretary Knox: a false alarm. Later official histories: no enemy aircraft were found.",
      reported =
        listOf(
          "On the night of 24 February 1942, less than three months after Pearl Harbor and a day after a Japanese submarine shelled the coast near Santa Barbara, an air-raid alert was sounded in southern California.",
          "A blackout was ordered and searchlights swept the sky. Anti-aircraft batteries began firing shortly after 3 a.m. and kept firing for about an hour.",
          "Army records say the 37th Coast Artillery Brigade fired about 1,430 rounds. Observers reported aircraft and shell bursts over the city and the coast.",
          "No bombs fell and no aircraft were shot down. Falling shell fragments damaged buildings and cars, and the blackout panic was linked to several deaths.",
        ),
      notInDispute =
        listOf(
          "An air-raid alert and blackout were ordered, and about 1,430 rounds were fired.",
          "No bombs were dropped and no aircraft or wreckage were found.",
          "Senior officials gave conflicting public explanations in the days that followed.",
        ),
      inDispute =
        listOf(
          "Whether any aircraft at all were over Los Angeles that night.",
          "What was first fired on, and why neighbouring batteries joined in.",
          "How much the radar and visual reports can be relied on.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Army Chief of Staff memorandum to the President",
            description =
              "A memorandum from General George C. Marshall to President Roosevelt, dated 26 February 1942, summarizing the Army’s information about the night.",
            caveat = "it was written within two days from first reports, and it records estimates that were not confirmed.",
          ),
          Evidence(
            id = "E-02",
            kind = "Statement",
            sourceLabel = SourceLabel.Reported,
            title = "Navy Secretary Knox’s “false alarm” statement",
            description =
              "A public statement by Navy Secretary Frank Knox soon after the night, calling the episode a false alarm. It was carried in the press.",
            caveat = "it is known from press coverage, and the Army’s own view was different.",
          ),
          Evidence(
            id = "E-03",
            kind = "Photograph",
            sourceLabel = SourceLabel.PrimaryRecord,
            title = "Searchlight photograph published in the Los Angeles Times",
            description =
              "A photograph of searchlights converging on a point in the sky, published on 26 February 1942 and widely reprinted.",
            caveat = "the published print was retouched to sharpen the beams, and it does not show an aircraft clearly.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = SourceLabel.FirstHand,
            title = "Eyewitness accounts of the barrage",
            description =
              "Accounts by residents, wardens and gun crews of searchlights, shell bursts and objects seen in the sky, collected in press coverage and later histories.",
            caveat = "the accounts were given in a blackout, under fear of attack, and differ on how many objects were seen.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "Enemy or unidentified aircraft (War Department view)",
            heldBy = "the US War Department in 1942",
            supports =
              listOf(
                "The Army’s memorandum said unidentified aircraft were probably in the area.",
                "Several batteries and observers reported targets at the same time.",
              ),
            complicates =
              listOf(
                "No aircraft or wreckage were found.",
                "Postwar Japanese accounts record no aircraft over Los Angeles that night.",
              ),
            drawsOn = listOf("E-01", "E-04"),
          ),
          Explanation(
            title = "False alarm from war nerves (Navy view)",
            heldBy = "the Navy Secretary in 1942 and later historians",
            supports =
              listOf(
                "The alert came a day after the coast was shelled, in a city braced for attack.",
                "Once guns opened fire, shell bursts and searchlight beams could be mistaken for targets.",
              ),
            complicates =
              listOf(
                "The Army did not agree at the time.",
                "It does not say what was first fired on.",
              ),
            drawsOn = listOf("E-02", "E-03", "E-04"),
          ),
          Explanation(
            title = "Unidentified craft of unknown origin (ufological reading)",
            heldBy = "some UFO writers",
            supports =
              listOf(
                "The Army memorandum said the aircraft were not American planes.",
                "Reports describe objects that did not respond to the barrage.",
              ),
            complicates =
              listOf(
                "No object, trace or clear image was ever recovered.",
                "The photograph was retouched and shows only searchlights.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
          Explanation(
            title = "Weather balloons, flares and shell bursts",
            heldBy = "later historians and analysts",
            supports =
              listOf(
                "Lit balloons and flares were in use around the coast at the time.",
                "Shell bursts lit by searchlights can look like aircraft in a blackout.",
              ),
            complicates =
              listOf(
                "Records do not say which balloon or flare, if any, was first fired on.",
                "It does not account for every report of aircraft.",
              ),
            drawsOn = listOf("E-03", "E-04"),
          ),
        ),
      officialNote =
        "The Army and the Navy gave different accounts in 1942, and no enemy aircraft were ever found. " +
          "The archive records both statements and does not rule on what, if anything, was in the sky.",
      story = battleOfLosAngelesStory,
      gaps = battleOfLosAngelesGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Marshall memorandum to President Roosevelt (26 February 1942), summary",
            caption = "A paraphrase of the memorandum’s main points. It is not a verbatim transcription.",
            provenance = "Summary of a memorandum from the Army Chief of Staff to the President, 26 February 1942",
            documentHeader =
              listOf(
                "FROM" to "General George C. Marshall, Army Chief of Staff",
                "TO" to "President Franklin D. Roosevelt",
                "DATE" to "26 February 1942",
                "NOTE" to "Paraphrase, not a transcription",
              ),
            excerpt =
              listOf(
                "Unidentified aircraft, other than American Army or Navy planes, were probably over Los Angeles.",
                "Estimates of the number of aircraft ran up to about fifteen, at differing speeds and altitudes.",
                "Anti-aircraft batteries fired about 1,430 rounds. No bombs were dropped and no enemy aircraft were brought down.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.Reported,
            title = "Knox statement and press coverage (25–26 February 1942), summary",
            caption = "A summary of how the conflicting statements were reported, not a quotation from any newspaper.",
            provenance = "Summary of press coverage, 25–26 February 1942",
            documentHeader =
              listOf(
                "SPEAKER" to "Frank Knox, Secretary of the Navy",
                "DATE" to "25–26 February 1942",
                "NOTE" to "Paraphrase, not a quotation",
              ),
            excerpt =
              listOf(
                "The Navy Secretary called the episode a false alarm.",
                "The War Department said unidentified aircraft were probably over the city, and the press carried both statements.",
                "Newspapers reported the barrage, the blackout and the damage on the ground.",
              ),
          ),
        ),
    )

private val fooFighters: CaseDetail
  get() =
    CaseDetail(
      hero =
        CaseHero(
          image = MediaImage.NightFighterBeaufighter,
          caption = "A Beaufighter of the 415th Night Fighter Squadron, USAAF, 1943",
          credit = "US Army Air Forces · via Wikimedia Commons · Public domain",
        ),
      stamp = "No agreed explanation · several proposed",
      reportedBy = "Allied night-fighter and bomber crews, including the US 415th Night Fighter Squadron, and wartime newspapers",
      officialPosition = "No single official finding. Wartime intelligence staff did not identify the lights, and no German or Japanese weapon matching them was found after the war.",
      reported =
        listOf(
          "From late 1944, Allied aircrews reported glowing lights that followed or paced their aircraft at night, over Europe and later in the Pacific.",
          "Crews described balls of orange, red or white light, sometimes in groups or in lines, that kept pace with the aircraft and then moved away.",
          "Crews of the 415th Night Fighter Squadron, flying along the Rhine, were among those whose reports circulated most widely. The name “foo fighters” was coined in the unit.",
          "Many crews feared a new German weapon. The lights were not reported to attack, and most were not seen on radar.",
          "Newspapers carried the term in the weeks that followed.",
        ),
      notInDispute =
        listOf(
          "Allied crews reported unexplained lights at night in late 1944 and 1945, and the name “foo fighters” was in use at the time.",
          "No aircraft was reported shot down by one.",
          "No German or Japanese weapon matching the reports was identified after the war.",
        ),
      inDispute =
        listOf(
          "What the lights were, and whether the reports describe one phenomenon or several.",
          "How closely they followed the aircraft, and whether they reacted to what crews did.",
          "How much the later retellings changed what crews first reported.",
        ),
      evidence =
        listOf(
          Evidence(
            id = "E-01",
            kind = "Testimony",
            sourceLabel = SourceLabel.Reported,
            title = "Crew accounts from the Rhine night patrols",
            description =
              "Accounts from crews of the 415th Night Fighter Squadron and other units of orange and red lights following their aircraft, as retold in histories and interviews.",
            caveat = "the surviving retellings come from histories and later interviews, and the original reports are not available to check here.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = SourceLabel.OfficialRecord,
            title = "Wartime air-intelligence reports",
            description =
              "Allied air-intelligence staff collected the reports at the time and recorded that crews feared an enemy weapon.",
            caveat = "the record shows the reports were taken seriously. It does not record a finding on what the lights were.",
          ),
          Evidence(
            id = "E-03",
            kind = "Press",
            sourceLabel = SourceLabel.Reported,
            title = "Newspaper coverage in early 1945",
            description =
              "Allied and American newspapers carried stories of “foo fighters” in the weeks after the reports, often as a possible secret weapon.",
            caveat = "the press stories repeated what crews were said to have reported, and several carried the lights as a German weapon without evidence.",
          ),
        ),
      explanations =
        listOf(
          Explanation(
            title = "Electrostatic discharge (St. Elmo’s fire)",
            heldBy = "postwar analysts and sceptics",
            supports =
              listOf(
                "Glowing discharges can form on aircraft and in electrically charged air, and they appear to move with the aircraft.",
                "Most reports describe steady lights that kept pace without attacking.",
              ),
            complicates =
              listOf(
                "Some crews described lights seen at a distance from the aircraft.",
                "Not every report was made in conditions where discharge would be expected.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Enemy secret weapon (wartime fear)",
            heldBy = "many crews and press stories in 1944–45",
            supports =
              listOf(
                "Crews knew the Germans were developing new aircraft and weapons.",
                "The lights seemed to follow the aircraft like a device meant to disrupt them.",
              ),
            complicates =
              listOf(
                "No such weapon was found after the war.",
                "Similar reports came from the Pacific.",
              ),
            drawsOn = listOf("E-02", "E-03"),
          ),
          Explanation(
            title = "Reflections and misidentified lights",
            heldBy = "sceptics and later investigators",
            supports =
              listOf(
                "At night, flares, searchlights, stars, planets and reflections on canopies can be mistaken for objects in motion.",
                "Tired crews flying long night missions were prone to misjudging distance.",
              ),
            complicates =
              listOf(
                "It does not account for reports of lights in groups that kept pace for long periods.",
                "The crews were trained observers.",
              ),
            drawsOn = listOf("E-01", "E-02"),
          ),
          Explanation(
            title = "Unidentified phenomenon of unknown origin",
            heldBy = "some veterans and UFO writers",
            supports =
              listOf(
                "Crews’ accounts agree on the general behaviour of the lights.",
                "No explanation has been agreed in the decades since.",
              ),
            complicates =
              listOf(
                "No object, photograph or radar track was ever produced.",
                "A mystery is not itself evidence of a particular cause.",
              ),
            drawsOn = listOf("E-01", "E-03"),
          ),
        ),
      officialNote =
        "No Allied agency issued a single finding on the foo fighters, and no weapon matching them was found. " +
          "The archive records the accounts and the proposed explanations without choosing between them.",
      story = fooFightersStory,
      gaps = fooFightersGaps,
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.Reported,
            title = "Night patrol accounts, 415th Night Fighter Squadron, summary",
            caption = "A paraphrase of how crew accounts describe the lights, not a quotation from any one report.",
            provenance = "Summary of crew accounts of late 1944, as retold in histories of the 415th Night Fighter Squadron",
            documentHeader =
              listOf(
                "UNIT" to "415th Night Fighter Squadron, US Army Air Forces",
                "PERIOD" to "Late 1944",
                "NOTE" to "Paraphrase, not a transcription",
              ),
            excerpt =
              listOf(
                "On night patrols along the Rhine, crews reported glowing orange or red lights that followed their aircraft and kept pace with it.",
                "The lights sometimes appeared in groups or in lines. They were not reported to attack and were mostly not seen on radar.",
                "The squadron’s crews called them “foo fighters”.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = SourceLabel.Reported,
            title = "Press coverage of the “foo fighters” (early 1945), summary",
            caption = "A summary of how the press told the story, not a quotation from any newspaper.",
            provenance = "Summary of Allied and American press coverage, early 1945",
            documentHeader =
              listOf(
                "SOURCE" to "Newspaper coverage, early 1945",
                "NOTE" to "Paraphrase, not a quotation",
              ),
            excerpt =
              listOf(
                "Newspapers reported glowing balls that followed Allied aircraft over Europe and called them “foo fighters”.",
                "Some stories described them as a possible German secret weapon.",
                "The stories gave no proof of what the lights were.",
              ),
          ),
        ),
    )
