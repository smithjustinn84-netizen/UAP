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
) {
  fun mediaForEvidence(evidenceId: String): List<ReportMedia> = media.filter { it.evidenceId == evidenceId }
}

/** [sourceLabel] says where an item comes from, never whether it is true. */
data class Evidence(
  val id: String,
  val kind: String,
  val sourceLabel: String,
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

enum class MediaFigure {
  ArnoldCascadeTrack,
  ArnoldOriginalSketch,
  MogulBalloonTrain,
  FortWorthDebrisPhoto,
  WashingtonRadarScope,
  KecksburgAcornProfile,
  ShagHarbourImpactChart,
  BerwynSeismograph,
  RendleshamLandingTriangle,
  PhoenixVFormation,
  PhoenixEstrellasPhoto,
  NimitzAltitudeDrop,
  NimitzFlirFrame,
  RooseveltGimbalFrame,
  RooseveltGoFastFrame,
}

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
  val sourceLabel: String,
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
)

data class Explanation(
  val title: String,
  val heldBy: String,
  val supports: List<String>,
  val complicates: List<String>,
  val drawsOn: List<String>,
)

enum class TimelineKind(val label: String) {
  Sighting("Sighting"),
  Inquiry("Inquiry or record"),
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
    )

  val decades: List<Int> = cases.map { it.decade }.distinct().sorted()

  fun case(id: String): CaseRecord? = cases.firstOrNull { it.id == id }

  /** Filters by decade (null means all) and by a free-text query over title, place, date and file number. */
  fun search(query: String, decade: Int?): List<CaseRecord> {
    val q = query.trim()
    return cases.filter { c ->
      (decade == null || c.decade == decade) &&
        (q.isEmpty() || listOf(c.title, c.place, c.date, c.id).any { it.contains(q, ignoreCase = true) })
    }
  }

  val timeline: List<TimelineEntry> =
    listOf(
      TimelineEntry(1947, TimelineKind.Sighting, "Kenneth Arnold sighting", "24 June · Mount Rainier, Washington, USA", "OF-1947-01"),
      TimelineEntry(1947, TimelineKind.Sighting, "Roswell incident", "July · Roswell, New Mexico, USA", "OF-1947-02"),
      TimelineEntry(1952, TimelineKind.Inquiry, "Project Blue Book begins", "US Air Force study of UFO reports"),
      TimelineEntry(1952, TimelineKind.Sighting, "Washington, D.C. radar sightings", "19–27 July · Washington, D.C., USA", "OF-1952-01"),
      TimelineEntry(1965, TimelineKind.Sighting, "Kecksburg incident", "9 December · Kecksburg, Pennsylvania, USA", "OF-1965-01"),
      TimelineEntry(1967, TimelineKind.Sighting, "Shag Harbour incident", "4 October · Shag Harbour, Nova Scotia, Canada", "OF-1967-01"),
      TimelineEntry(
        1968,
        TimelineKind.Inquiry,
        "Condon Committee report",
        "Scientific study commissioned by the US Air Force concludes no evidence of extraterrestrials",
      ),
      TimelineEntry(1969, TimelineKind.Inquiry, "Project Blue Book closes", "The Air Force ends its study"),
      TimelineEntry(1974, TimelineKind.Sighting, "Berwyn Mountains incident", "23 January · Berwyn Mountains, Wales, UK", "OF-1974-01"),
      TimelineEntry(1980, TimelineKind.Sighting, "Rendlesham Forest incident", "26–28 December · Suffolk, England", "OF-1980-01"),
      TimelineEntry(1997, TimelineKind.Sighting, "Phoenix Lights", "13 March · Arizona, USA", "OF-1997-01"),
      TimelineEntry(2004, TimelineKind.Sighting, "USS Nimitz encounter", "14 November · Pacific, southwest of San Diego", "OF-2004-01"),
      TimelineEntry(2015, TimelineKind.Sighting, "USS Theodore Roosevelt encounters", "January–March · Atlantic Coast, USA", "OF-2015-01"),
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
        2023,
        TimelineKind.Inquiry,
        "Congressional UAP hearing",
        "House Oversight subcommittee hears public testimony from military aviators and former intelligence officials",
      ),
    )
}

private val rendlesham: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "Primary record",
            title = "The Halt memo",
            description =
              "A one-page memo titled “Unexplained Lights”, written by Lt Col Charles Halt to the UK Ministry of " +
                "Defence and dated 13 January 1981. Released in 1983 under the US Freedom of Information Act.",
            caveat = "written about two weeks after the events, and its dates differ by a day from other records.",
          ),
          Evidence(
            id = "E-02",
            kind = "Audio",
            sourceLabel = "Primary record",
            title = "The Halt tape",
            description =
              "A microcassette recording Halt made in the forest on the second night, describing the lights and " +
                "instrument readings as his party encountered them.",
            caveat = "the recorder was stopped and started, so the tape is not a continuous record of the night.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Witness statements",
            description =
              "Written statements made by airmen in January 1981, and later interviews with Jim Penniston, John " +
                "Burroughs, Charles Halt and others who were present.",
            caveat = "some later accounts include details that do not appear in the 1981 statements.",
          ),
          Evidence(
            id = "E-04",
            kind = "Physical trace",
            sourceLabel = "Not preserved",
            title = "Ground depressions",
            description =
              "Three depressions in a rough triangle, found where the object was reported on the first night and " +
                "described in the memo.",
            caveat = "the site was not formally surveyed, and people who saw the marks disagree about what made them.",
          ),
          Evidence(
            id = "E-05",
            kind = "Measurement",
            sourceLabel = "Reported",
            title = "Radiation readings",
            description =
              "Readings taken at the site with a military survey meter. The memo records a peak of 0.1 " +
                "milliroentgens in the depressions.",
            caveat = "whether that figure is above normal background is itself disputed.",
          ),
          Evidence(
            id = "E-06",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = "Primary record",
            title = "Memorandum: “Unexplained Lights” (13 Jan 1981)",
            caption = "Typed one-page report from Lt Col Charles I. Halt to RAF Community Relations Officer.",
            provenance = "Released 1983 under US FOIA · UK National Archives DEFE 24/1948",
            documentHeader =
              listOf(
                "FROM" to "Lt Col Charles I. Halt, Deputy Base Commander, 81st CSG",
                "TO" to "RAF/CC (Ministry of Defence)",
                "DATE" to "13 Jan 1981",
                "SUBJECT" to "Unexplained Lights",
              ),
            excerpt =
              listOf(
                "1. Early in the morning of 27 Dec 80 (approx 0300L), two USAF security police patrolmen saw unusual lights outside the back gate at RAF Woodbridge.",
                "2. The individuals reported seeing a strange glowing object in the forest. The object was described as being metallic in appearance and triangular in shape, approximately two to three meters across the base.",
                "3. The next day, three depressions 1 1/2\" deep and 7\" in diameter were found where the object had been sighted on the ground. The following night (29 Dec 80) the area was checked for radiation. Beta/gamma readings of 0.1 milliroentgens were recorded with peak readings in the three depressions.",
                "4. Later in the night a red sun-like light was seen through the trees. It moved about and pulsed. At one point it appeared to throw off glowing particles and then broke into five separate white objects and then disappeared.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Audio,
            sourceLabel = "Primary record",
            title = "The Halt microcassette field recording",
            caption = "18-minute Lanier pocket tape recorded in Rendlesham Forest on the night of 28 December 1980.",
            provenance = "Recorded on site by Lt Col Charles Halt · Public copy released 1984",
            durationSeconds = 108,
            transcript =
              listOf(
                TranscriptCue("00:00", "Lt Col Halt", "1:05 AM. We're still in the forest investigating the site from the first night."),
                TranscriptCue("00:22", "Sgt Nevills", "Wait a minute—we're getting a deflection on the AN/PDR-27 meter inside the depression."),
                TranscriptCue("00:44", "Lt Col Halt", "Okay, we're seeing a strange red light out through the trees to the east, moving and pulsing."),
                TranscriptCue("01:08", "Airman Englund", "There it goes again... about every five seconds right through the pines."),
                TranscriptCue("01:31", "Lt Col Halt", "Now we are observing what appears to be a beam coming down to the ground. This is unreal."),
              ),
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-04",
            kind = MediaKind.Diagram,
            sourceLabel = "Reported",
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
      stamp = "Unsolved official inquiry",
      reportedBy = "Local residents, RCMP officers, and fishing boat crews",
      officialPosition = "Canadian Department of National Defence: object remained unidentified following naval dive search.",
      reported =
        listOf(
          "At approximately 11:20 PM on 4 October 1967, multiple observers in the coastal village of Shag Harbour saw four bright lights in sequence flying low across the night sky.",
          "Witnesses reported the object angled down at roughly 45 degrees, struck the water with an audible whistling sound and bright flash, and floated on the surface roughly half a mile from shore.",
          "Three RCMP officers and local fishermen rushed out in boats believing an airliner had crashed. They found a thick patch of glowing yellowish foam spreading across the water, but no wreckage.",
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
            sourceLabel = "Primary record",
            title = "RCMP Priority Telex",
            description =
              "A priority telex sent on 5 October 1967 by the RCMP detachment in Barrington Passage to Air Force Headquarters in Ottawa, reporting that an unknown object with four lights had crashed into the ocean.",
            caveat = "describes initial reports taken under emergency conditions when officers believed an airliner had crashed.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Department of National Defence file",
            description =
              "Files compiled by the Canadian Department of National Defence detailing communications between the Royal Canadian Air Force, Coast Guard, and Navy, officially titled 'UFO Report - Shag Harbour'.",
            caveat = "records that the object was logged as unidentified rather than a known aircraft or satellite debris.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "RCMP officer testimony",
            description =
              "Statements from RCMP Constables Ron Pound, Victor Werbieki, and Ron O'Brien, who observed the lights, the impact area, and the yellow foam on the water.",
            caveat = "recollections were recorded across several interviews in the years following the incident.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Fishermen rescue crews",
            description =
              "Testimony from local fishermen who launched small boats immediately after the crash to look for survivors.",
            caveat = "the search was conducted in darkness and dense water chop.",
          ),
          Evidence(
            id = "E-05",
            kind = "Physical trace",
            sourceLabel = "Reported",
            title = "Yellow surface foam",
            description =
              "A wide patch of yellowish foam with an unusual odour reported by boat crews and police on the surface where the object entered the water.",
            caveat = "no chemical samples of the foam were collected or preserved for laboratory analysis.",
          ),
          Evidence(
            id = "E-06",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = "Primary record",
            title = "RCMP Barrington Passage Priority Telex",
            caption = "Telex dispatched to RCAF Air Defence Command Headquarters, Ottawa, 5 October 1967.",
            provenance = "Library and Archives Canada · RG 24 (Department of National Defence)",
            documentHeader =
              listOf(
                "FROM" to "RCMP DETACHMENT BARRINGTON PASSAGE NS",
                "TO" to "CANFORCEHED OTTAWA // AIR DEFENCE COMMAND",
                "DATE" to "05 OCT 1967 · PRIORITY",
                "FILE" to "UFO REPORT - SHAG HARBOUR",
              ),
            excerpt =
              listOf(
                "AT APPROX 2320 HRS 4 OCT 67 NUMEROUS CALLS RECEIVED FROM RESIDENTS OF SHAG HARBOUR REPORTING LARGE OBJECT WITH FOUR BRIGHT LIGHTS CRASHING INTO WATERS OF GULF OF MAINE.",
                "THREE MEMBERS THIS DETACHMENT ATTENDED IMMEDIATELY AND OBSERVED PALE YELLOW LIGHT FLOATING ON SURFACE HALF MILE OFFSHORE.",
                "LOCAL FISHING BOATS AND COAST GUARD CUTTER SEARCHED AREA OF THICK YELLOW FOAM APPROX 80 FEET WIDE. OBJECT SUBMERGED PRIOR TO ARRIVAL OF BOATS. NO WRECKAGE RECOVERED.",
              ),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-05",
            kind = MediaKind.Diagram,
            sourceLabel = "Official record",
            title = "Harbour descent track & yellow foam drift chart",
            caption = "Sightline from Highway 3, impact bearing off Bon Portage Island, and tidal drift of the foam slick.",
            provenance = "Compiled from RCMP detachment notes and HMCS Granby dive search coordinates",
            figure = MediaFigure.ShagHarbourImpactChart,
          ),
        ),
    )

private val nimitz: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "Primary record",
            title = "ATFLIR infrared video ('FLIR1')",
            description =
              "Infrared targeting sensor recording of an elongated oblong object tracking at high velocity and subsequently jumping out of the sensor field of view.",
            caveat = "the recording duration is short and only captures the infrared spectrum without visual color data.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Aviator eyewitness testimony",
            description =
              "Statements and public testimony by Commander David Fravor and Lieutenant Commander Alex Dietrich describing a white, wingless 'Tic Tac' craft with no visible exhaust hovering and maneuvering at extreme speeds.",
            caveat = "interviews were conducted years after the original 2004 event.",
          ),
          Evidence(
            id = "E-03",
            kind = "Measurement",
            sourceLabel = "Reported",
            title = "AN/SPY-1B radar tracks",
            description =
              "Radar logs and operator accounts from USS Princeton describing multiple targets tracked at altitudes ranging from 80,000 feet to surface level within seconds.",
            caveat = "raw digital recording tapes of the radar data were not publicly preserved in the official record.",
          ),
          Evidence(
            id = "E-04",
            kind = "Visual",
            sourceLabel = "First-hand",
            title = "Ocean surface disturbance",
            description =
              "Pilots reported an area of disturbed, churning water roughly 50 to 100 metres across directly underneath the hovering craft, resembling an object submerged or surface chop.",
            caveat = "no maritime survey of the surface disturbance was conducted.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = "Primary record",
            title = "ATFLIR targeting pod video (“FLIR1”)",
            caption = "76-second infrared recording captured from an F/A-18F Super Hornet operating from USS Nimitz.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020)",
            figure = MediaFigure.NimitzFlirFrame,
            durationSeconds = 76,
            telemetryLabels = listOf("IR TV · NAR", "EL -02°", "AZ +00°", "RNG ---", "2.0X ZOOM"),
            transcript =
              listOf(
                TranscriptCue("00:00", "ATFLIR HUD", "Sensor acquires thermal target in narrow infrared field of view at 20,000 ft."),
                TranscriptCue("00:28", "ATFLIR HUD", "Operator cycles between TV and IR polarity (White-Hot / Black-Hot) to resolve contour."),
                TranscriptCue("00:54", "ATFLIR HUD", "Optical track holds elongated oblong return with no thermal exhaust plume."),
                TranscriptCue("01:11", "ATFLIR HUD", "Target accelerates left out of 2.0x magnification frame; gimbal lock breaks."),
              ),
            sourceUrl = "https://www.defense.gov/News/Releases/Release/Article/2165713/statement-by- the-department-of-defense-on-the-release-of-historical-navy-videos/".replace(" ", ""),
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = "Reported",
            title = "AN/SPY-1B vertical descent profile",
            caption = "Reported radar track dropping from 80,000 ft to 20,000 ft and sea-skimming level in <1 second.",
            provenance = "Reconstructed from USS Princeton radar operator testimony",
            figure = MediaFigure.NimitzAltitudeDrop,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-05",
            kind = MediaKind.Document,
            sourceLabel = "Official record",
            title = "DoD Immediate Release: Historical Navy Videos",
            caption = "Formal Pentagon statement accompanying the official release of FLIR1, Gimbal, and GoFast.",
            provenance = "Office of the Secretary of Defense · 27 April 2020",
            documentHeader =
              listOf(
                "ISSUED BY" to "Department of Defense · Office of the Spokesperson",
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
        ),
    )

private val arnold: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "First-hand",
            title = "Arnold’s account",
            description =
              "Arnold’s interviews and written reports describing nine objects in a chain, their motion, and the time he took to cross the gap between two mountains.",
            caveat = "he gave several accounts over the years and some details changed between them.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = "Press report",
            title = "Newspaper coverage, June 1947",
            description =
              "The first newspaper reports, published from 25 June 1947, quoted Arnold and carried the phrase “flying saucers” across the country.",
            caveat = "reporters shaped the wording, and “saucer” described the way the objects moved, not their shape.",
          ),
          Evidence(
            id = "E-03",
            kind = "Measurement",
            sourceLabel = "Reported",
            title = "Speed estimate",
            description =
              "Arnold timed the objects between two landmarks with his cockpit clock and worked out a speed well above that of any aircraft then flying.",
            caveat = "the distances were estimated by eye, and the figure changed when he rechecked it.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Photo,
            sourceLabel = "Primary record",
            title = "Arnold’s sketch for Army Air Forces Intelligence",
            caption = "Drawing submitted by Kenneth Arnold on 12 July 1947 showing the flat, heel-shaped profile of the object.",
            provenance = "Project Blue Book Archive · Case File #1 (National Archives)",
            figure = MediaFigure.ArnoldOriginalSketch,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = "Reported",
            title = "Cascade Range timing track (Mt. Rainier to Mt. Adams)",
            caption = "1 min 42 sec timed transit across the 47-mile crest line observed from Arnold’s CallAir A-2 at 9,200 ft.",
            provenance = "Plotted from Arnold’s written report to Commanding General, Wright Field",
            figure = MediaFigure.ArnoldCascadeTrack,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-02",
            kind = MediaKind.Document,
            sourceLabel = "Press report",
            title = "Associated Press wire dispatch (25 June 1947)",
            caption = "First national wire report from Pendleton, Oregon, introducing the term “saucer-like aircraft”.",
            provenance = "East Oregonian / Associated Press wire copy · 25 June 1947",
            documentHeader =
              listOf(
                "DATELINE" to "PENDLETON, Ore., June 25 (AP)",
                "WITNESS" to "Kenneth Arnold, Boise, Idaho (Fire Control Equipment Co.)",
                "FLIGHT" to "Chehalis to Yakima, Wash. · 24 June 1947, 3:00 PM PST",
              ),
            excerpt =
              listOf(
                "Nine bright saucer-like objects flying at 'incredible' speed at 10,000 feet altitude were reported here today by Kenneth Arnold, Boise, Ida., pilot who said he could not hazard a guess as to what they were.",
                "Arnold, a United States Forest Service flyer engaged in searching for a missing Marine plane, said he sighted the mysterious objects yesterday at 3 p.m. They were flying between Mount Rainier and Mount Adams, in Washington State, he said, and appeared to weave in and out of formation.",
              ),
          ),
        ),
    )

private val roswell: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "Primary record",
            title = "The 8 July 1947 press release",
            description =
              "A statement from the Roswell Army Air Field announcing it had recovered a “flying disc”, and the local newspaper headline that followed.",
            caveat = "it says what the base believed on one day, and was reversed within hours.",
          ),
          Evidence(
            id = "E-02",
            kind = "Document",
            sourceLabel = "Primary record",
            title = "Fort Worth press photographs",
            description =
              "Photographs taken on 8 July 1947 of Army officers with debris, shown as the remains of a weather balloon.",
            caveat = "a witness who handled the original material said the pieces shown were not the ones he found.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = "Official record",
            title = "US Air Force Roswell reports",
            description =
              "Two reports, in 1994 and 1997, concluding that the debris came from a classified balloon project and that later claims of bodies stemmed from other events.",
            caveat = "they were written about half a century after the event, and critics dispute their match to the original debris.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Later witness accounts",
            description =
              "Interviews and statements from base personnel and local people, gathered mainly from 1978 onward.",
            caveat = "they were recorded long after the event and the accounts differ from one another.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Document,
            sourceLabel = "Primary record",
            title = "509th Bomb Group public information release",
            caption = "Press statement drafted by 1st Lt Walter Haut at Roswell Army Air Field on 8 July 1947.",
            provenance = "Roswell Daily Record · 8 July 1947 afternoon edition",
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
            sourceLabel = "Primary record",
            title = "Fort Worth Army Air Field debris plate",
            caption = "Archival plate of foil-backed radar reflector and neoprene fragments photographed in Brig. Gen. Roger Ramey’s office.",
            provenance = "Fort Worth Star-Telegram / US National Archives · 8 July 1947",
            figure = MediaFigure.FortWorthDebrisPhoto,
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-03",
            kind = MediaKind.Diagram,
            sourceLabel = "Official record",
            title = "Project Mogul Service Flight #4 balloon train",
            caption = "600-foot acoustic array with neoprene sounding balloons, ML-307 corner radar targets, and payload sonobuoy.",
            provenance = "Adapted from USAF Report 'The Roswell Report: Fact versus Fiction in the New Mexico Desert' (1994)",
            figure = MediaFigure.MogulBalloonTrain,
          ),
        ),
    )

private val washingtonRadar: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "Reported",
            title = "Radar returns",
            description =
              "Targets tracked on air-traffic and military radar at Washington National and Andrews, described in reports and operator accounts.",
            caveat = "radar returns can be produced by atmospheric effects, so the returns alone do not say what was detected.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Controller and pilot accounts",
            description =
              "Statements from the controllers who watched the scopes and from pilots who reported lights near the capital.",
            caveat = "the pilots and controllers did not always agree on what they saw, or when.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Air Force press conference, 29 July 1952",
            description =
              "The Air Force’s public statement on the sightings, offering a temperature inversion and ordinary lights as explanations.",
            caveat = "it was a press statement, and some of the staff who investigated were not convinced.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Project Blue Book and later review",
            description =
              "The case file held in the Air Force’s UFO study, and the review by a CIA-convened scientific panel the following year.",
            caveat = "the later review looked at the file, and did not re-examine the radar.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = "Press report",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = "Reported",
            title = "Washington National ARTC PPI radar scope plot",
            caption = "7 unidentified primary returns plotted in Sector 3 over Andrews AFB and the Capitol restricted zone, 19–20 July 1952.",
            provenance = "Reconstructed from CAA Senior Controller Harry G. Barnes’ log and Project Blue Book file",
            figure = MediaFigure.WashingtonRadarScope,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = "Official record",
            title = "Pentagon Press Conference Transcript Excerpt",
            caption = "Maj. Gen. John A. Samford, USAF Director of Intelligence, briefing reporters at the Pentagon on 29 July 1952.",
            provenance = "Department of the Air Force · Public Relations Transcript, 29 July 1952",
            documentHeader =
              listOf(
                "SPEAKER" to "Maj. Gen. John A. Samford, Director of Intelligence, USAF",
                "PRESENT" to "Capt. Edward J. Ruppelt (Project Blue Book)",
                "DATE" to "29 July 1952 · Room 3E-869, The Pentagon",
              ),
            excerpt =
              listOf(
                "Our real interest in this problem has always been not the question of 'what is it', our problem is 'what can it do?' And we have found to date no pattern that indicates anything roughly translatable as hostility or even potential menace.",
                "With regard to the radar contacts in the Washington area, radar beams can be deflected downward by a layer of warm air sitting above a cooler layer—an inversion—returning ground targets as apparent aerial blips.",
              ),
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
            sourceLabel = "First-hand",
            title = "Witness accounts",
            description =
              "Statements from residents who saw the fireball, went to the woods, or saw military personnel there.",
            caveat = "many accounts were collected years later, and they differ about what was seen in the woods.",
          ),
          Evidence(
            id = "E-02",
            kind = "Audio",
            sourceLabel = "Primary record",
            title = "Local radio coverage",
            description =
              "Radio reports on the night of the event, made by a local reporter who went to the area.",
            caveat = "the reports describe what the reporter was told, not what he confirmed.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Military and Project Blue Book records",
            description =
              "Air Force records of the fireball and the search, which record the event as a meteor.",
            caveat = "the files say little about what the search party found at the site.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = "First-hand",
            title = "Composite witness sketch: Kecksburg woods object",
            caption = "Bronze-colored acorn-shaped hull (approx. 10–12 ft long) with raised bumper ring reported in the ravine.",
            provenance = "Composite from 1965–1979 witness descriptions in Westmoreland County",
            figure = MediaFigure.KecksburgAcornProfile,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Audio,
            sourceLabel = "Primary record",
            title = "WHJB Greensburg field broadcast (“Object in the Woods”)",
            caption = "On-scene interviews recorded by WHJB News Director John Murphy on the night of 9 December 1965.",
            provenance = "WHJB 620 AM radio broadcast archive · Greensburg, Pennsylvania",
            durationSeconds = 64,
            transcript =
              listOf(
                TranscriptCue("00:00", "John Murphy (WHJB)", "This is John Murphy reporting from the road junction above Kecksburg."),
                TranscriptCue("00:18", "Volunteer Fireman", "State Police and Army personnel from Oakdale just closed off the hollow road."),
                TranscriptCue("00:39", "Local Resident", "It came down slow with a blue vapor trail and settled right into the trees."),
                TranscriptCue("00:54", "John Murphy (WHJB)", "Military officials on scene state they have found nothing in the woods at this hour."),
              ),
          ),
        ),
    )

private val berwyn: CaseDetail
  get() =
    CaseDetail(
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
            sourceLabel = "Official record",
            title = "Seismic record",
            description =
              "The British Geological Survey record of an earthquake of about magnitude 3.5 in North Wales that evening.",
            caveat = "it shows the tremor, and says nothing about the light in the sky.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Police and resident accounts",
            description =
              "Accounts from police officers and residents of the tremor, the sound and the light.",
            caveat = "the reports were taken at different times and places, and do not agree on timing.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Ministry of Defence files",
            description =
              "Files released by the Ministry of Defence recording the reports and its view that a meteor and an earthquake explain them.",
            caveat = "the files record the Ministry’s conclusion, and do not answer later claims of a recovery.",
          ),
          Evidence(
            id = "E-04",
            kind = "Testimony",
            sourceLabel = "Reported",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Diagram,
            sourceLabel = "Official record",
            title = "Seismograph trace: Bala Fault tremor (3.5 ML)",
            caption = "Institute of Geological Sciences waveform at 20:38:15 GMT compared with fireball sightings across North Wales.",
            provenance = "British Geological Survey / UK National Archives AIR 2/18961",
            figure = MediaFigure.BerwynSeismograph,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-03",
            kind = MediaKind.Document,
            sourceLabel = "Official record",
            title = "MoD S4 (Air) Inquiry Minute: Llandrillo Incident",
            caption = "Declassified Ministry of Defence summary cross-checking RAF Valley mountain rescue and police logs.",
            provenance = "UK National Archives · File AIR 2/18961 (Released 2005)",
            documentHeader =
              listOf(
                "DEPARTMENT" to "Ministry of Defence · S4 (Air)",
                "REFERENCE" to "AIR 2/18961 · North Wales Sightings",
                "DATE" to "January 1974",
              ),
            excerpt =
              listOf(
                "1. At 2038 hrs on 23 January 1974 an earth tremor measuring 3.5 on the Richter scale was felt across Merionethshire and Denbighshire.",
                "2. Concurrently a brilliant green fireball descended on a NNW–SSE trajectory and was observed from Ireland to the Midlands.",
                "3. A search of the Berwyn range near Llandrillo by Gwynedd Constabulary and an RAF Valley team found no wreckage on the mountain.",
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
            sourceLabel = "Primary record",
            title = "Residents’ video",
            description =
              "Home footage of lights over Phoenix, much of it showing a row of steady lights that appear to burn out one by one.",
            caveat = "most footage shows the later lights, and says little about the earlier formation.",
          ),
          Evidence(
            id = "E-02",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Eyewitness accounts",
            description =
              "Statements from residents and others, including the state’s then governor, who later said he had also seen the lights.",
            caveat = "accounts of the formation’s size and shape differ widely.",
          ),
          Evidence(
            id = "E-03",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Air National Guard statement",
            description =
              "A military statement that aircraft from a Guard unit dropped flares over a training range that night.",
            caveat = "it accounts for the later lights and does not mention the earlier formation.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = "Press report",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Photo,
            sourceLabel = "Primary record",
            title = "Sierra Estrella ridge lights plate (22:00 MST)",
            caption = "Archival frame showing the stationary arc of nine amber lights above the Estrella Mountains southwest of Phoenix.",
            provenance = "Composite plate from 13 March 1997 Phoenix home video recordings",
            figure = MediaFigure.PhoenixEstrellasPhoto,
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = "Primary record",
            title = "Phoenix valley camcorder sequence (22:00 MST)",
            caption = "Video telemetry reconstruction showing the illumination and sequential drop-out behind the Sierra Estrella ridgeline.",
            provenance = "Civilian VHS / Hi8 recordings logged 13 March 1997",
            figure = MediaFigure.PhoenixEstrellasPhoto,
            durationSeconds = 90,
            telemetryLabels = listOf("22:00 MST", "AZ 235° SW", "SIERRA ESTRELLA", "HI8 CAMCORDER"),
            transcript =
              listOf(
                TranscriptCue("00:00", "Camcorder Log", "Five bright amber lights appear in a gentle arc above the southern mountain crest."),
                TranscriptCue("00:30", "Camcorder Log", "Additional lights ignite to the west of the line, holding steady elevation."),
                TranscriptCue("01:05", "Camcorder Log", "Westernmost lights wink out abruptly behind the ridgeline Profile."),
              ),
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-02",
            kind = MediaKind.Diagram,
            sourceLabel = "Reported",
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
            sourceLabel = "Primary record",
            title = "“Gimbal” video",
            description =
              "Infrared footage of a single object that appears to rotate while the camera tracks it, with the pilots’ voices on the audio.",
            caveat = "the rotation may be an effect of the sensor pod, and the footage does not give distance or size.",
          ),
          Evidence(
            id = "E-02",
            kind = "Video",
            sourceLabel = "Primary record",
            title = "“GoFast” video",
            description =
              "Infrared footage of an object over the sea that seems to move quickly across the field of view.",
            caveat = "apparent speed depends on the camera’s own motion and the object’s distance, which are not known.",
          ),
          Evidence(
            id = "E-03",
            kind = "Testimony",
            sourceLabel = "First-hand",
            title = "Aviator accounts",
            description =
              "Interviews and public statements from aviators who flew in the area and reported regular encounters.",
            caveat = "most accounts were given years later, and they do not agree on every detail.",
          ),
          Evidence(
            id = "E-04",
            kind = "Document",
            sourceLabel = "Official record",
            title = "Department of Defense release",
            description =
              "The April 2020 statement releasing three Navy videos and confirming that the objects in them remain unidentified.",
            caveat = "it confirms authenticity and the lack of an identification, and it does not offer a technical explanation.",
          ),
          Evidence(
            id = "E-05",
            kind = "Document",
            sourceLabel = "Official record",
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
      media =
        listOf(
          ReportMedia(
            id = "M-01",
            evidenceId = "E-01",
            kind = MediaKind.Video,
            sourceLabel = "Primary record",
            title = "ATFLIR video & cockpit audio (“GIMBAL”)",
            caption = "34-second infrared targeting pod recording with WSO and pilot intercom audio off the US East Coast.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020)",
            figure = MediaFigure.RooseveltGimbalFrame,
            durationSeconds = 34,
            telemetryLabels = listOf("IR WHOT", "250 KTS", "BANK 28° L", "0.53 MACH", "ATFLIR POD"),
            transcript =
              listOf(
                TranscriptCue("00:02", "Aviator 1", "There's a whole fleet of 'em. Look on the SA [Situational Awareness display]."),
                TranscriptCue("00:10", "Aviator 2", "My gosh! They're all going against the wind. The wind is 120 knots to the west."),
                TranscriptCue("00:19", "Aviator 1", "Look at that thing, dude! That's not our L&S, is it?"),
                TranscriptCue("00:26", "Aviator 2", "Look at that thing! It's rotating!"),
              ),
            sourceUrl = "https://www.navair.navy.mil/foia/documents",
          ),
          ReportMedia(
            id = "M-02",
            evidenceId = "E-02",
            kind = MediaKind.Video,
            sourceLabel = "Primary record",
            title = "ATFLIR infrared video (“GOFAST”)",
            caption = "34-second radar-slaved infrared track of a low-altitude object over the Atlantic Ocean.",
            provenance = "US Department of Defense / NAVAIR FOIA release (27 April 2020)",
            figure = MediaFigure.RooseveltGoFastFrame,
            durationSeconds = 34,
            telemetryLabels = listOf("IR BHOT", "RNG 4.4 NM", "25,000 FT", "EL -26°", "AUTO TRACK"),
            transcript =
              listOf(
                TranscriptCue("00:04", "WSO", "Got it! Manual track... auto track locked at 4.4 nautical miles."),
                TranscriptCue("00:14", "Pilot", "Whoa, it's in the bracket! What the heck is that?"),
                TranscriptCue("00:24", "WSO", "Did you box a moving target? Look at it skimming above the water."),
              ),
            sourceUrl = "https://www.navair.navy.mil/foia/documents",
          ),
          ReportMedia(
            id = "M-03",
            evidenceId = "E-04",
            kind = MediaKind.Document,
            sourceLabel = "Official record",
            title = "NAVAIR FOIA Release & Classification Review Note",
            caption = "Naval Air Systems Command release summary for the three ATFLIR cockpit recordings.",
            provenance = "Naval Air Systems Command (NAVAIR) FOIA Reading Room · April 2020",
            documentHeader =
              listOf(
                "AGENCY" to "Department of the Navy · Naval Air Systems Command",
                "RECORDS" to "FLIR.mp4 · GIMBAL.wmv · GOFAST.wmv",
                "CLASSIFICATION" to "UNCLASSIFIED // CLEARED FOR PUBLIC RELEASE",
              ),
            excerpt =
              listOf(
                "After a thorough review, the department has determined that the authorized release of these unclassified videos does not reveal any sensitive capabilities or systems, and does not impinge on any subsequent investigations of military air space incursions by unidentified aerial phenomena.",
              ),
          ),
        ),
    )
