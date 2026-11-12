package com.openfile.uap.data

// How each case has been told since it was first reported, and what is missing from its record.
// The same editorial rules apply as in Archive.kt: say who said what and when, and don't rule on it.
// Every entry cites a source that was opened and read against the text. Where the best source could not be
// reached online the citation names what was read instead (a syndicated copy, a transcription, a summary).

private fun atTheTime(year: Int, date: String, title: String, detail: String, source: Citation) = StoryEntry(year, date, StoryKind.AtTheTime, title, detail, source)

private fun newAccount(year: Int, date: String, title: String, detail: String, source: Citation) = StoryEntry(year, date, StoryKind.NewAccount, title, detail, source)

private fun printOrScreen(year: Int, date: String, title: String, detail: String, source: Citation) = StoryEntry(year, date, StoryKind.PrintOrScreen, title, detail, source)

private fun inquiry(year: Int, date: String, title: String, detail: String, source: Citation) = StoryEntry(year, date, StoryKind.Inquiry, title, detail, source)

private fun revised(year: Int, date: String, title: String, detail: String, source: Citation) = StoryEntry(year, date, StoryKind.Revised, title, detail, source)

internal val arnoldStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1947,
      "24–25 Jun 1947",
      "Arnold lands and talks",
      "After landing at Yakima, Washington, and flying on to Pendleton, Oregon, Arnold described the nine objects to other pilots and then to the East Oregonian newspaper.",
      Citation("Ted Bloecher, Report on the UFO Wave of 1947 (1967)", "https://www.theblackvault.com/casefiles/wp-content/uploads/2015/05/ReportOnWaveOf1947.pdf"),
    ),
    printOrScreen(
      1947,
      "25–26 Jun 1947",
      "The wire story and the word “saucer”",
      "The East Oregonian’s report, picked up by the Associated Press, called the objects “saucer-like”. Within days headlines were using “flying saucer” and “flying disc”. Arnold later said he had used a saucer to describe how the objects moved, not what they looked like.",
      Citation("Project 1947, East Oregonian text of 25 June 1947", "http://www.project1947.com/fig/1947a.htm"),
    ),
    atTheTime(
      1947,
      "Jun–Jul 1947",
      "A wave of reports",
      "Newspapers across North America carried hundreds of further sightings in the weeks that followed. A catalogue compiled twenty years later by the researcher Ted Bloecher lists more than 850 for June and July 1947.",
      Citation("Ted Bloecher, Report on the UFO Wave of 1947 (1967)", "https://www.theblackvault.com/casefiles/wp-content/uploads/2015/05/ReportOnWaveOf1947.pdf"),
    ),
    printOrScreen(
      1948,
      "Spring 1948",
      "Arnold in Fate magazine",
      "The first issue of Fate, a magazine co-founded by the publisher Ray Palmer, ran Arnold’s own account of the sighting as its cover story.",
      Citation("Fate magazine, “About”", "https://fatemag.com/about"),
    ),
    printOrScreen(
      1952,
      "1952",
      "“The Coming of the Saucers”",
      "Arnold and Palmer published a book on the sighting and what followed. It carried a photograph of a model showing one of the objects as a crescent, which differs from the rounded outline in the sketch Arnold made for Army Air Forces intelligence in 1947. In the book Arnold wrote that one of the nine had looked different from the rest, and that he had not mentioned it before.",
      Citation("Kenneth Arnold and Ray Palmer, The Coming of the Saucers (1952), reprint scan", "https://archive.org/details/the-coming-of-the-saucers_202210"),
    ),
  )

internal val arnoldGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No photograph or instrument record",
      "The sighting rests on Arnold’s account and his timing of the objects against the clock on his instrument panel. He wrote that he would have given almost anything for a movie camera. No photograph, film or radar record was made.",
      Citation("Kenneth Arnold, report to Army Air Forces intelligence, 1947 (Project 1947 transcription)", "http://www.project1947.com/fig/ka.htm"),
    ),
  )

internal val roswellStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1947,
      "8 Jul 1947",
      "A disc announced, then withdrawn",
      "The Roswell base announced it had recovered a “flying disc”. Later the same day Brig. Gen. Roger Ramey, at Fort Worth, identified the material as a weather balloon and its radar target.",
      Citation("USAF, Report on Project Mogul: Synopsis of Balloon Research Findings (1995), university-hosted copy", "https://muller.lbl.gov/teaching/physics10/Roswell/USMogulReport.html"),
    ),
    atTheTime(
      1947,
      "9 Jul 1947",
      "The rancher’s interview",
      "The Roswell Daily Record printed W. W. Brazel’s description of what he found: rubber strips, tinfoil, tough paper and sticks. The headline said he was sorry he had told about it. He was also quoted as saying he was sure it was not a weather observation balloon. The story then left the news for three decades.",
      Citation("Roswell Daily Record, 9 July 1947 (transcription)", "https://ufology.patrickgross.org/rw/p/roswelldailyrecord9jul1947.htm"),
    ),
    newAccount(
      1978,
      "1978",
      "Jesse Marcel speaks",
      "The retired intelligence officer who had collected the debris told the researcher Stanton Friedman that it had not come from a weather balloon.",
      Citation("Wikipedia, “Roswell incident”", "https://en.wikipedia.org/wiki/Roswell_incident"),
    ),
    printOrScreen(
      1980,
      "1980",
      "“The Roswell Incident”",
      "The first book on the case, by Charles Berlitz and William Moore, put accounts of a crashed craft and bodies in print. It drew in part on a second-hand story set on the Plains of San Agustin, well to the west of the ranch.",
      Citation("Wikipedia, “The Roswell Incident (1980 book)”", "https://en.wikipedia.org/wiki/The_Roswell_Incident_(1980_book)"),
    ),
    newAccount(
      1989,
      "1989",
      "The mortician’s story",
      "Glenn Dennis, a Roswell mortician in 1947, described calls from the base asking about child-sized coffins, and a nurse who told him of bodies. The Air Force found no record of the nurse he named, and he is reported to have said later that the name was not her real one. The same year the television series Unsolved Mysteries dramatised the case.",
      Citation("USAF, The Roswell Report: Case Closed (1997)", "https://www.gutenberg.org/ebooks/63659"),
    ),
    inquiry(
      1994,
      "1994–97",
      "Air Force and GAO reports",
      "At the request of Congressman Steven Schiff of New Mexico, the General Accounting Office (now the Government Accountability Office) searched for records and reported in July 1995. The Air Force attributed the debris to Project Mogul in 1994, and in 1997 attributed the accounts of bodies to test dummies dropped in the 1950s.",
      Citation("GAO report NSIAD-95-187, July 1995", "https://www.gao.gov/assets/nsiad-95-187.pdf"),
    ),
    printOrScreen(
      1995,
      "1995",
      "The “alien autopsy” film",
      "A film presented as 1947 footage of an autopsy was broadcast around the world. In 2006 its promoter, Ray Santilli, said it had been staged, and described it as a restoration of footage he said he had once seen.",
      Citation("Time, “How an Alien Autopsy Hoax Captured the World’s Imagination for a Decade”, 2016", "https://time.com/4376871/alien-autopsy-hoax-history/"),
    ),
  )

internal val roswellGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "Base records destroyed",
      "The General Accounting Office reported in 1995 that the Roswell base’s administrative records for March 1945 to December 1949, and its outgoing messages for October 1946 to December 1949, had been destroyed. It said the disposal form does not show who destroyed them, when, or on what authority.",
      Citation("GAO report NSIAD-95-187, July 1995, p. 2", "https://www.gao.gov/assets/nsiad-95-187.pdf"),
    ),
    RecordGap(
      "The debris itself",
      "The General Accounting Office’s 1995 search turned up two documents from 1947 and no physical material. No piece of the debris is known to be available for examination.",
      Citation("GAO report NSIAD-95-187, July 1995", "https://www.gao.gov/assets/nsiad-95-187.pdf"),
    ),
    RecordGap(
      "The paper in Ramey’s hand",
      "In one of the Fort Worth photographs Ramey holds a sheet of paper with some of its text facing the camera. The University of Texas at Arlington, which holds the negative, describes the text as undeciphered, and researchers’ readings of enlargements conflict.",
      Citation("UTA Magazine, Spring 2017, “Collected”", "https://www.uta.edu/utamagazine/spring-2017/stories/collected.php"),
    ),
  )

internal val washingtonRadarStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1952,
      "20–28 Jul 1952",
      "Front pages across the country",
      "The radar reports and the fighter scrambles made front-page news across the United States. Edward Ruppelt, then head of Project Blue Book, wrote that they pushed the Democratic National Convention out of headline space.",
      Citation("Edward J. Ruppelt, The Report on Unidentified Flying Objects (1956), ch. 12", "https://www.gutenberg.org/cache/epub/17346/pg17346-images.html"),
    ),
    atTheTime(
      1952,
      "29 Jul 1952",
      "The Pentagon press conference",
      "Maj. Gen. John Samford, the Air Force’s director of intelligence, attributed the radar targets to a temperature inversion. Ruppelt later described it as the largest and longest press conference the Air Force had held since the Second World War.",
      Citation("Edward J. Ruppelt, The Report on Unidentified Flying Objects (1956), ch. 12", "https://www.gutenberg.org/cache/epub/17346/pg17346-images.html"),
    ),
    inquiry(
      1953,
      "Jan 1953",
      "The Robertson Panel",
      "After the volume of reports in 1952, the CIA convened a panel of scientists. It found no direct threat to national security in the reports and recommended a policy of debunking and public education to reduce interest in them.",
      Citation("Gerald K. Haines, “CIA’s Role in the Study of UFOs, 1947–90”, Studies in Intelligence (1997)", "https://www.cia.gov/resources/csi/static/cia-role-study-UFOs.pdf"),
    ),
    printOrScreen(
      1956,
      "1956",
      "Ruppelt’s inside account",
      "Ruppelt published his account of the episode. He wrote that Maj. Dewey Fournet and Lt. Holcomb, a Navy electronics specialist, who had been in the radar room during the sightings, were absent from the press conference and were not convinced the targets were caused by weather. He added that the sightings were still carried as unknowns.",
      Citation("Edward J. Ruppelt, The Report on Unidentified Flying Objects (1956), ch. 12", "https://www.gutenberg.org/cache/epub/17346/pg17346-images.html"),
    ),
    inquiry(
      1969,
      "1969",
      "The Condon report",
      "The University of Colorado study, released in January 1969, reviewed the case in its chapter on optical and radar analyses of field cases. It concluded that the radar returns were most likely the result of anomalous propagation.",
      Citation("Condon report (1969), Section III, ch. 5, G. D. Thayer", "https://files.ncas.org/condon/text/s3chap05.htm"),
    ),
  )

internal val washingtonRadarGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "What the scopes showed",
      "The Condon study worked from track reports, the operators’ accounts and weather data, and mentions no photographs or recordings of the scopes. None is known in the public file.",
      Citation("Condon report (1969), Section III, ch. 5, G. D. Thayer", "https://files.ncas.org/condon/text/s3chap05.htm"),
    ),
  )

internal val kecksburgStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1965,
      "10 Dec 1965",
      "Two headlines in one morning",
      "The county edition of the Greensburg Tribune-Review carried a front-page banner headline, “‘Unidentified Flying Object’ Falls Near Kecksburg”, with “Army ropes off area” beneath it. The city edition of the same day quoted the state police as saying that nothing had been found.",
      Citation("R. R. Young, Skeptical Inquirer 15, Spring 1991", "https://cdn.centerforinquiry.org/wp-content/uploads/sites/29/1991/04/22165214/p63.pdf"),
    ),
    inquiry(
      1967,
      "1967",
      "The fireball’s path is calculated",
      "The astronomers Von Del Chamberlain and David Krause published a trajectory for the fireball in the Journal of the Royal Astronomical Society of Canada, worked out from photographs taken at two places. They concluded it was a meteor whose path ended over land south-east of Windsor, Ontario.",
      Citation("Chamberlain & Krause, JRASC 61 (1967), 184–190, as summarised by R. Sheaffer", "https://www.debunker.com/Kecksburg.html"),
    ),
    printOrScreen(
      1990,
      "19 Sep 1990",
      "Unsolved Mysteries",
      "The NBC series dramatised the case. The sceptical writer R. R. Young reported that the account of an acorn-shaped object had surfaced a few months earlier, after an appeal for witnesses, and that more than 50 residents petitioned against the broadcast. A model of the object is on display in Kecksburg, where the volunteer fire department holds an annual UFO festival.",
      Citation("R. R. Young, Skeptical Inquirer 15, Spring 1991", "https://cdn.centerforinquiry.org/wp-content/uploads/sites/29/1991/04/22165214/p63.pdf"),
    ),
    inquiry(
      2003,
      "2003–09",
      "The NASA lawsuit",
      "The journalist Leslie Kean sued NASA under the Freedom of Information Act. In 2005 a NASA spokesman said fragments from the area had been examined and judged to be from a Russian satellite, and that the supporting documents had been misplaced. A search ordered under a 2007 settlement ended in 2009 without producing a document on Kecksburg.",
      Citation("Space.com via NBC News, 24 Nov 2009", "https://www.nbcnews.com/id/wbna34135497"),
    ),
  )

internal val kecksburgGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "NASA boxes not found",
      "During the lawsuit a NASA official is reported to have testified, at a 2007 hearing, that two boxes of records from the period were missing.",
      Citation("The Black Vault, Kecksburg case file", "https://www.theblackvault.com/casefiles/the-vault-files-the-1965-kecksburg-pennsylvania-crash/"),
    ),
    RecordGap(
      "No field report from the search",
      "An Air Force memo on the search of 9 December 1965 records that the team “could not find anything”. No fuller field report from the night has been published.",
      Citation("Associated Press via ABC News, 27 Oct 2007", "https://abcnews.com/Technology/story?id=3785376"),
    ),
  )

internal val westallStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1966,
      "6–14 Apr 1966",
      "Headlines in suburban Melbourne",
      "The Dandenong Journal ran front-page reports under headlines including “Object Descends on School Ground”, quoting students and science teacher Andrew Greenwood. The Sun and regional papers also covered the accounts of hundreds of children watching a disc hover and land near the Grange reserve.",
      Citation("The Dandenong Journal, 14 April 1966 (Trove archive)", "https://trove.nla.gov.au/newspaper/article/61340798"),
    ),
    inquiry(
      1973,
      "1973",
      "The McDonald interview recording",
      "An audio recording made in 1971 by the American atmospheric physicist Dr James E. McDonald surfaced, in which teacher Andrew Greenwood described observing a silvery craft through binoculars and alleged that military officers later warned him not to discuss it.",
      Citation("ABC News, “Westall '66: 50 years on from Australia’s largest UFO mystery”", "https://www.abc.net.au/news/2016-04-06/westall-ufo-mystery-50-years-on/7302484"),
    ),
    printOrScreen(
      2010,
      "2010",
      "“Westall '66” documentary",
      "A television documentary directed by Rosie Jones brought together dozens of former students and teachers who stood by their accounts of the landing and subsequent military presence.",
      Citation("Screen Australia, “Westall '66: A Suburban UFO Mystery”", "https://screenaustralia.gov.au/the-find/title/27538/westall-66-a-suburban-ufo-mystery"),
    ),
    inquiry(
      2014,
      "2014",
      "The HIBAL balloon proposal",
      "Researcher Keith Basterfield searched declassified Department of Supply records and proposed that the object may have been a stray high-altitude balloon from the joint US-Australian HIBAL atmospheric testing project based at Mildura.",
      Citation("Keith Basterfield, “The Westall '66 Incident and HIBAL Balloons” (research paper)", "https://www.theblackvault.com/casefiles/the-1966-westall-australia-incident/"),
    ),
  )

internal val westallGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "Missing official investigation dossier",
      "Former witnesses described uniformed military and police personnel examining the paddock and taking soil samples. Exhaustive searches of Department of Air and RAAF files in the National Archives of Australia have produced no official investigation file or report on the site visit.",
      Citation("National Archives of Australia, UFO records collection", "https://www.naa.gov.au/explore-collection/defence-and-war-service-records/unidentified-flying-objects-ufos"),
    ),
    RecordGap(
      "Confiscated student photographs",
      "Several student witnesses recalled that a classmate took photographs of the object with a camera and that these were collected by visiting authorities. No photographs from the schoolyard or paddock exist in the public record.",
      Citation("ABC News, “Westall '66: 50 years on from Australia’s largest UFO mystery”", "https://www.abc.net.au/news/2016-04-06/westall-ufo-mystery-50-years-on/7302484"),
    ),
  )

internal val shagHarbourStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1967,
      "5–9 Oct 1967",
      "A search in the newspapers",
      "The search was front-page news in Halifax. On 7 October the Chronicle-Herald ran the headline “Could Be Something Concrete in Shag Harbour UFO – RCAF” and quoted an air force officer as saying the service was “very interested”. On 9 October it reported that the navy had called off the dive after finding nothing.",
      Citation("Wikipedia, “Shag Harbour UFO incident”, citing the Chronicle-Herald of 7 and 9 Oct 1967", "https://en.wikipedia.org/wiki/Shag_Harbour_UFO_incident"),
    ),
    inquiry(
      1969,
      "1969",
      "A note in the Condon report",
      "The University of Colorado study recorded the report and the fruitless search as its Case 34, from telephone interviews, and concluded that further investigation was not justified.",
      Citation("Condon report (1969), Case 34, “North Atlantic, Fall 1967”", "https://files.ncas.org/condon/text/case34.htm"),
    ),
    newAccount(
      1993,
      "1990s",
      "The file is reopened",
      "Chris Styles, who at the age of 12 had seen a glowing orange object over Halifax harbour on the same night, began tracing the original witnesses and the government documents held in Canadian archives.",
      Citation("B. Wark, The Coast (Halifax), 25 Sep 2008", "https://www.thecoast.ca/news-opinion/upfront-992721/"),
    ),
    printOrScreen(
      2001,
      "2001",
      "“Dark Object”",
      "A book by Don Ledger and Chris Styles added accounts from unnamed former military sources that the object had moved underwater to a point off Shelburne and been watched there. The Canadian Press reported in 2017 that there is no hard evidence for this.",
      Citation("M. MacDonald, The Canadian Press via Global News, 21 Sep 2017", "https://globalnews.ca/news/3761270/canadas-best-documented-ufo-sighting-still-intrigues-50-years-on"),
    ),
    printOrScreen(
      2019,
      "2019",
      "A coin, a museum and a festival",
      "The Royal Canadian Mint issued a glow-in-the-dark silver coin marking the incident. The village keeps a small museum and holds an annual festival.",
      Citation("Royal Canadian Mint news release, 1 Oct 2019", "https://www.mint.ca/en/company/media-room/the-royal-canadian-mint-makes-a-splash-with-a-coin-illustrating-canadas-famous-ufo-crash-story"),
    ),
  )

internal val shagHarbourGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No analysis of the foam on record",
      "The Condon study’s summary describes the foam and mentions no sample or analysis. Later claims that samples were taken cite no document.",
      Citation("Condon report (1969), Case 34, “North Atlantic, Fall 1967”", "https://files.ncas.org/condon/text/case34.htm"),
    ),
    RecordGap(
      "Sources for the Shelburne account",
      "The military sources for the later account of an underwater object off Shelburne are unnamed, so what they said cannot be checked against service records.",
      Citation("M. MacDonald, The Canadian Press via Global News, 21 Sep 2017", "https://globalnews.ca/news/3761270/canadas-best-documented-ufo-sighting-still-intrigues-50-years-on"),
    ),
  )

internal val berwynStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1974,
      "23–24 Jan 1974",
      "Treated as a possible air crash",
      "Police took calls about an explosion and lights and treated the event as a possible aircraft crash. An RAF mountain rescue team searched the mountain from first light the next day and found nothing. Regional and national newspapers reported it.",
      Citation("R. M. W. Musson, “The enigmatic Bala earthquake of 23 January 1974”, Astronomy & Geophysics 47 (2006)", "https://nora.nerc.ac.uk/id/eprint/1531/1/Bala_paper_Musson.pdf"),
    ),
    atTheTime(
      1974,
      "26–30 Jan 1974",
      "Scientists in the villages",
      "A team from the Institute of Geological Sciences in Edinburgh worked around Bala, interviewing residents and handing out questionnaires about what they had felt. Over the same weekend university geologists and amateur searchers looked for a meteorite on the mountain and found nothing.",
      Citation("R. M. W. Musson, “The enigmatic Bala earthquake of 23 January 1974”, Astronomy & Geophysics 47 (2006)", "https://nora.nerc.ac.uk/id/eprint/1531/1/Bala_paper_Musson.pdf"),
    ),
    newAccount(
      1996,
      "1996",
      "Accounts of a recovery",
      "UFO Magazine published an account, passed on by the investigator Tony Dodd, from an anonymous former soldier who said his unit had collected boxes from the area and driven them to the Porton Down research establishment, and that the boxes held bodies. Claims that a craft had crashed on the mountain had circulated among UFO writers before this.",
      Citation("Higgypop, “Berwyn Mountains UFO Crash”, 20 Mar 2017", "http://www.higgypop.com/news/berwyn-mountains-ufo-crash"),
    ),
    printOrScreen(
      2010,
      "By 2010",
      "“The Welsh Roswell”",
      "The press and UFO writers came to call the case “the Welsh Roswell”, and the name has stayed with it.",
      Citation("The Register, 23 Aug 2010", "https://www.theregister.com/2010/08/23/welsh_roswell/"),
    ),
    printOrScreen(
      2010,
      "2010",
      "“UFO Down?”",
      "After years of interviews and archive work, the researcher Andy Roberts published a book tracing the story to an earthquake, a bright meteor and poachers’ lamps on the hillside. Some witnesses continue to reject that explanation.",
      Citation("J. Rimmer, review of “UFO Down?”, Magonia Review, Sep 2010", "https://pelicanist.blogspot.com/2010/09/ufo-down.html"),
    ),
  )

internal val berwynGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No document for a cordon or recovery",
      "The Gwynedd police log for the night and a Ministry of Defence file on the event have been released. They record searches by police and an RAF mountain rescue team, and no cordon or recovery. No document supporting the recovery account has come to light.",
      Citation("R. M. W. Musson, “The enigmatic Bala earthquake of 23 January 1974”, Astronomy & Geophysics 47 (2006)", "https://nora.nerc.ac.uk/id/eprint/1531/1/Bala_paper_Musson.pdf"),
    ),
    RecordGap(
      "Sources for the recovery account",
      "The military source for the recovery account is anonymous and cannot be traced or questioned.",
      Citation("Higgypop, “Berwyn Mountains UFO Crash”, 20 Mar 2017", "http://www.higgypop.com/news/berwyn-mountains-ufo-crash"),
    ),
  )

internal val tehranStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1976,
      "19 Sep 1976",
      "Scramble over the capital",
      "Tehran newspapers and international wires reported that the Imperial Iranian Air Force scrambled two F-4 Phantom interceptors over the city after civilian reports of an intensely brilliant light. Both flight crews experienced loss of communications and weapons avionics as they attempted interception.",
      Citation("Tehran Journal, 20 September 1976 (transcription via Project 1947)", "http://www.project1947.com/fig/1976tehran.htm"),
    ),
    inquiry(
      1976,
      "Oct 1976",
      "The DIA evaluation memo",
      "A joint US Defense Intelligence Agency and Joint Chiefs of Staff cable evaluated the Iranian intercept as an outstanding report with high credibility, noting simultaneous radar tracking and electronic interference.",
      Citation("US Defense Intelligence Agency, Information Report: An Outstanding Report of a UFO, 1976 (National Security Archive)", "https://nsarchive.gwu.edu/document/27982-document-1-defense-intelligence-agency-information-report-outstanding-report-ufo"),
    ),
    printOrScreen(
      1979,
      "1979",
      "The Jupiter hypothesis",
      "Aviation journalist Philip J. Klass argued that the pilots had sighted the planet Jupiter and experienced coincidental mechanical malfunctions compounded by pilot error.",
      Citation("Philip J. Klass, UFOs: The Public Deceived (1983), ch. 18", "https://archive.org/details/ufospublicdeceiv0000klas"),
    ),
    newAccount(
      2007,
      "12 Nov 2007",
      "General Jafari at the National Press Club",
      "Retired Brigadier General Parviz Jafari, the pilot of the second F-4, spoke publicly in Washington, D.C., describing the primary object, the release of the smaller glowing craft, and the total shutdown of his weapons release console.",
      Citation("Maj. Gen. Parviz Jafari, National Press Club statement, Washington D.C., 12 Nov 2007", "https://www.theblackvault.com/casefiles/the-1976-tehran-iran-military-ufo-incident-case-file/"),
    ),
  )

internal val tehranGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No radar tape recordings preserved",
      "While the DIA report states that the second F-4 locked onto the target with its APQ-120 airborne radar at 27 nautical miles and Mehrabad control tower tracked the object, no magnetic tape or scope photographs of the radar returns have been published.",
      Citation("US Defense Intelligence Agency, Information Report: An Outstanding Report of a UFO, 1976 (National Security Archive)", "https://nsarchive.gwu.edu/document/27982-document-1-defense-intelligence-agency-information-report-outstanding-report-ufo"),
    ),
    RecordGap(
      "Dry lake bed search report",
      "The DIA memo noted that Iranian military personnel were dispatched by helicopter the following morning to inspect the dry lake bed where the secondary object landed. No report of their findings has been released.",
      Citation("The Black Vault, “The 1976 Tehran UFO Incident Declassified Files”", "https://www.theblackvault.com/documentarchive/the-1976-tehran-iran-ufo-incident/"),
    ),
  )

internal val valentichStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1978,
      "22–25 Oct 1978",
      "Search across Bass Strait",
      "Newspapers throughout Australia and overseas reported the disappearance of Frederick Valentich and his Cessna 182 after his final radio transmission to Melbourne Flight Service reporting an unknown craft hovering above him. An extensive sea and air search found no trace.",
      Citation("The Age (Melbourne), 23 October 1978 (Google News Archive)", "https://news.google.com/newspapers?id=valentich_disappearance_the_age_1978"),
    ),
    inquiry(
      1982,
      "May 1982",
      "The Department of Transport report",
      "The Australian Department of Transport Bureau of Air Safety Investigation published its aircraft accident investigation summary, concluding that the reason for the disappearance had not been determined and the aircraft was presumed crashed at sea.",
      Citation("Department of Transport, Bureau of Air Safety Investigation, Summary Ref V116/783/1047", "https://www.naa.gov.au/learn/learning-resources/results-with-get-wild/frederick-valentich-disappearance"),
    ),
    inquiry(
      2012,
      "2012",
      "Release of the government investigation file",
      "The National Archives of Australia released Department of Transport file series B1497, containing witness reports of unusual lights over Bass Strait on the evening of the flight and documentation of the search operations.",
      Citation("National Archives of Australia, file series B1497, Frederick Valentich inquiry", "https://recordsearch.naa.gov.au/SearchNRetrieve/Interface/DetailsReports/ItemDetail.aspx?Barcode=1049105"),
    ),
  )

internal val valentichGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No wreckage or floating debris found",
      "Despite calm sea conditions and four days of intensive maritime and aerial searching covering thousands of square miles of Bass Strait, neither the Cessna 182L (VH-DOJ), nor its lifejackets or emergency locator beacon were ever located.",
      Citation("Department of Transport, Bureau of Air Safety Investigation, Summary Ref V116/783/1047", "https://www.naa.gov.au/learn/learning-resources/results-with-get-wild/frederick-valentich-disappearance"),
    ),
    RecordGap(
      "Source of final metallic transmission unknown",
      "The 17 seconds of unidentified metallic pulsing and scraping sounds heard on the open microphone before transmission ceased were examined by government and civilian laboratories, but no conclusive identification of the sound source was reached.",
      Citation("ABC News, “Forty years on, pilot Frederick Valentich’s disappearance still mystifies”", "https://www.abc.net.au/news/2018-10-21/valentich-disappearance-40-years-on/10398686"),
    ),
  )

internal val rendleshamStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1981,
      "Jan 1981",
      "Statements and a memo, no headlines",
      "Statements were taken from the first night’s witnesses and Halt sent his memo to the Ministry of Defence. Nothing appeared in the press at the time.",
      Citation("David Clarke, “Rendlesham Forest UFOs”, drdavidclarke.co.uk", "https://drdavidclarke.co.uk/rendlesham-forest-ufos/"),
    ),
    printOrScreen(
      1983,
      "2 Oct 1983",
      "“UFO lands in Suffolk”",
      "The memo was released under the US Freedom of Information Act. The News of the World ran it on its front page under the headline “UFO lands in Suffolk – and that’s official”. The story quoted a former airman, Larry Warren, under the name “Art Wallace”.",
      Citation("Ian Ridpath, “Front page news” (News of the World, 2 Oct 1983)", "http://www.ianridpath.com/ufo/headline.html"),
    ),
    printOrScreen(
      1983,
      "7 Oct 1983",
      "The lighthouse is proposed",
      "The forester Vince Thurkettle put forward the Orford Ness lighthouse as the source of the pulsing light, in an interview with the astronomy writer Ian Ridpath broadcast on the BBC’s Breakfast Time.",
      Citation("Ian Ridpath, “Vince Thurkettle – the original interview”", "http://www.ianridpath.com/ufo/rendlesham1c.html"),
    ),
    printOrScreen(
      1984,
      "1984",
      "“Sky Crash” and the tape",
      "“Sky Crash”, by Brenda Butler, Dot Street and Jenny Randles, was the first book on the case. Copies of Halt’s tape reached UFO researchers the same year. Warren described a landed craft and beings. Others involved dispute his account.",
      Citation("David Clarke, “Dropped Object”, 30 Dec 2024", "https://drclarke.substack.com/p/dropped-object"),
    ),
    inquiry(
      2001,
      "2001",
      "The Ministry’s file is released",
      "In May 2001 the Ministry of Defence released its file on the incident to the researcher David Clarke, under the Code of Practice on Access to Government Information. In 2005 the Forestry Commission opened a “UFO trail” through the forest.",
      Citation("David Clarke, “Rendlesham Forest UFOs”, drdavidclarke.co.uk", "https://drdavidclarke.co.uk/rendlesham-forest-ufos/"),
    ),
    newAccount(
      2010,
      "2010",
      "An affidavit and a binary code",
      "In June Halt signed an affidavit saying he believed the objects were extraterrestrial and that the security services of both countries had tried to play down what happened through disinformation. In December Jim Penniston, one of the first night’s witnesses, who had for some years said he touched a landed craft, said he had afterwards written down pages of binary code. Neither detail is in his 1981 statement.",
      Citation("Ian Ridpath, “Did Jim Penniston touch a landed craft in Rendlesham Forest?”", "http://www.ianridpath.com/ufo/pennistonnotebook.html"),
    ),
  )

internal val rendleshamGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "Defence Intelligence files missing",
      "A Ministry of Defence search in 2000, made public in 2011, found a gap where the Defence Intelligence UFO files for 1980 to 1982 should have been. Press reports of the release said they had been destroyed. The Ministry’s own file on the incident survives and has been released.",
      Citation("BBC News, 3 Mar 2011", "https://www.bbc.co.uk/news/uk-12613690"),
    ),
    RecordGap(
      "No official site survey",
      "No official survey of the site is on record. What exists is informal: photographs taken by an airman on the morning of 26 December 1980, which surfaced years later, plaster casts kept by Penniston, and the radiation readings on Halt’s tape. The spot was not marked at the time and its location is disputed.",
      Citation("Ian Ridpath, “What caused the landing marks?”", "http://www.ianridpath.com/ufo/rendlesham5.html"),
    ),
  )

internal val phoenixLightsStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1997,
      "Mar–May 1997",
      "Phone calls and local news",
      "Callers from Prescott Valley to Tucson reported the lights to the National UFO Reporting Center in Seattle. Residents’ video of the later lights was shown on television. In May, Phoenix councilwoman Frances Emma Barwood asked city staff to look into the sightings. Coverage stayed mostly local until June.",
      Citation("Arizona Republic, 11 May 1997 (transcription)", "https://ufologie.patrickgross.org/htm/phoepap1.htm"),
    ),
    printOrScreen(
      1997,
      "18–19 Jun 1997",
      "National coverage and a costume",
      "A USA Today report made the lights national news. The next day Governor Fife Symington called a press conference at which his chief of staff appeared dressed as an alien.",
      Citation("Associated Press in Deseret News, 20 Jun 1997", "https://www.deseret.com/1997/6/20/19318941/alien-encounter-leaves-media-agog/"),
    ),
    inquiry(
      1997,
      "25 Jul 1997",
      "The flares statement",
      "A Maryland Air National Guard spokesman said eight of its A-10 aircraft had dropped high-intensity flares over the Barry M. Goldwater Range on training flights that night. The Associated Press noted that this did not account for the earlier sightings further north.",
      Citation("Associated Press in Deseret News, 26 Jul 1997", "https://www.deseret.com/1997/7/26/19325702/flares-not-ufos-caused-light-show-military-says/"),
    ),
    printOrScreen(
      2005,
      "2004–05",
      "“The Phoenix Lights”",
      "Lynne Kitei, a Phoenix physician who had photographed the lights, came forward in 2004, published a book and made a documentary under that title.",
      Citation("Phoenix Magazine, 3 Mar 2017", "https://www.phoenixmag.com/?p=13139"),
    ),
    revised(
      2007,
      "Mar–Nov 2007",
      "The governor changes his account",
      "In March Symington said he had seen a large craft himself that night and called it “otherworldly”. Writing for CNN in November, he said the 1997 press conference had been meant to lighten the mood.",
      Citation("Associated Press via Fox News, 24 Mar 2007", "https://www.foxnews.com/story/former-arizona-governor-comes-forward-about-ufo-sighting-from-10-years-ago"),
    ),
  )

internal val phoenixLightsGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No report on the earlier formation",
      "Phoenix city staff were asked to look into the sightings in May 1997 and Governor Symington announced a state inquiry on 19 June 1997. No report from either, or from any federal body, has been published. Symington wrote in 2007 that his office’s inquiries went unanswered.",
      Citation("Fife Symington, CNN, 9 Nov 2007", "https://www.cnn.com/2007/TECH/science/11/09/simington.ufocommentary/index.html"),
    ),
    RecordGap(
      "No radar record published",
      "No radar data for the earlier formation has been published. Phoenix New Times reported that Federal Aviation Administration radar records were not requested before they were routinely deleted.",
      Citation("Phoenix New Times, 14 Mar 2014", "https://www.phoenixnewtimes.com/news/the-phoenix-lights-are-no-mystery-6661825"),
    ),
  )

internal val nimitzStory: List<StoryEntry> =
  listOf(
    atTheTime(
      2004,
      "Nov 2004",
      "Kept within the strike group",
      "The encounter was talked about aboard the ships. Fravor later told Congress that it was never investigated and that after a couple of days it became a story to tell friends. No public statement was made.",
      Citation("David Fravor, written statement to the House Committee on Oversight and Accountability, 26 Jul 2023", "https://oversight.house.gov/wp-content/uploads/2023/07/David-Fravor-Statement-for-House-Oversight-Committee.pdf"),
    ),
    printOrScreen(
      2007,
      "4 Feb 2007",
      "A clip on a web forum",
      "The infrared video was posted to the Above Top Secret forum, as a link to a file on a German production company’s website. In 2019 a Navy spokesman said the post had come to the attention of Navy officials in 2009.",
      Citation("John Greenewald Jr., The Black Vault, 17 Sep 2019", "https://www.theblackvault.com/documentarchive/u-s-navy-investigated-circumstances-behind-leaked-ufo-footage/"),
    ),
    newAccount(
      2015,
      "14 Mar 2015",
      "The pilot’s account in print",
      "Cmdr David Fravor’s account was published on an aviation website by a fellow naval aviator. It already described the object as a white “tic-tac”.",
      Citation("Paco Chierici, “There I Was: The X-Files Edition”, FighterSweep, 14 Mar 2015", "https://fightersweep.com/1460/x-files-edition/"),
    ),
    printOrScreen(
      2017,
      "16 Dec 2017",
      "The New York Times",
      "The Times reported the encounter with the video, alongside its disclosure of a Pentagon programme that had studied such reports. Fravor’s comparison of the object to a Tic Tac mint became the case’s nickname.",
      Citation("The Aviationist, 17 Dec 2017, reporting the New York Times story of 16 Dec 2017", "https://theaviationist.com/2017/12/17/u-s-department-of-defense-video-shows-unknown-object-intercepted-by-u-s-navy-super-hornet-and-we-have-no-idea-what-it-was/"),
    ),
    inquiry(
      2019,
      "2019–20",
      "The videos are confirmed, then released",
      "In September 2019 a Navy spokesman confirmed the video was official Navy footage and said the Navy classed what it showed as unidentified. The Department of Defense released it officially on 27 April 2020.",
      Citation("The War Zone, “Navy Officially Releases Controversial UFO Videos”, 27 Apr 2020", "https://www.twz.com/33179/navy-officially-releases-infamous-ufo-videos"),
    ),
    inquiry(
      2023,
      "26 Jul 2023",
      "Testimony under oath",
      "Fravor described the encounter to a House Oversight subcommittee.",
      Citation("House Committee on Oversight and Accountability, hearing of 26 Jul 2023", "https://oversight.house.gov/hearing/unidentified-anomalous-phenomena-implications-on-national-security-public-safety-and-government-transparency/"),
    ),
  )

internal val nimitzGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "Radar data",
      "Gary Voorhis, a former petty officer on USS Princeton, told Popular Mechanics in 2019 that he was ordered to hand over the ship’s radar recordings shortly after the encounter. Fravor told Congress in 2023 that tapes were never taken. No radar data from the day has been released.",
      Citation("Futurism, 13 Nov 2019, reporting Popular Mechanics, “The Witnesses”", "https://futurism.com/the-byte/veterans-military-seized-video-ufo-encounter"),
    ),
    RecordGap(
      "Classified material not released",
      "In reply to a Freedom of Information request, the Office of Naval Intelligence said it holds briefing slides classified top secret and a video classified secret. A Pentagon spokesperson said the video was not expected to be released.",
      Citation("MJ Banias, Vice, 10 Jan 2020", "https://www.vice.com/en/article/the-navy-has-secret-classified-video-of-an-infamous-ufo-incident"),
    ),
  )

internal val rooseveltEncountersStory: List<StoryEntry> =
  listOf(
    atTheTime(
      2014,
      "2014–15",
      "A safety report",
      "Aviators in the squadron reported the objects over several months. After two jets flying close together had to take evasive action, the squadron submitted a safety report.",
      Citation("Ryan Graves, written testimony to the House Committee on Oversight and Accountability, 26 Jul 2023", "https://oversight.house.gov/wp-content/uploads/2023/07/Ryan-HOC-Testimony.pdf"),
    ),
    printOrScreen(
      2017,
      "Dec 2017 – Mar 2018",
      "The videos appear",
      "“Gimbal” was published with the New York Times report of December 2017. “GoFast” followed in March 2018, released by the To The Stars Academy.",
      Citation("Navy Times, 13 Mar 2018", "https://www.navytimes.com/news/your-navy/2018/03/13/video-shows-apparent-encounter-between-navy-pilot-and-ufo"),
    ),
    newAccount(
      2019,
      "26 May 2019",
      "Pilots on the record",
      "Lt Ryan Graves and Lt Danny Accoin described the encounters by name in a New York Times report, which said the objects had appeared almost daily from the summer of 2014 to March 2015.",
      Citation("New York Times, 26 May 2019, as syndicated by SBS News", "https://www.sbs.com.au/news/wow-what-is-that-us-navy-pilots-report-unexplained-flying-objects"),
    ),
    inquiry(
      2021,
      "2020–21",
      "Release and a first assessment",
      "The Department of Defense released the videos in April 2020. In June 2021 the Office of the Director of National Intelligence reported on 144 UAP reports from US government sources, most of them from military aviators, covering 2004 to 2021. One was identified, as a large deflating balloon.",
      Citation("ODNI, Preliminary Assessment: Unidentified Aerial Phenomena, 25 Jun 2021", "https://archive.dni.gov/files/ODNI/documents/assessments/Prelimary-Assessment-UAP-20210625.pdf"),
    ),
    inquiry(
      2023,
      "May–Sep 2023",
      "A NASA panel and a hearing",
      "At a public meeting of NASA’s independent UAP study team in May, a panel member presented a calculation from the figures on screen in “GoFast” that put the object at about 13,000 feet and moving at about 40 mph. The team’s final report in September repeated it and noted that it left the effect of wind on the aircraft out of account. In July Graves testified to a House Oversight subcommittee.",
      Citation("NASA, UAP Independent Study Team Report, 14 Sep 2023", "https://science.nasa.gov/wp-content/uploads/2023/09/uap-independent-study-team-final-report.pdf"),
    ),
  )

internal val rooseveltEncountersGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "Only short clips are public",
      "The official release consisted of the short clips already in circulation. Radar data and any longer recordings from the same flights have not been released. NASA’s study team wrote of “GoFast” that additional data would allow a firmer conclusion.",
      Citation("The War Zone, “Navy Officially Releases Controversial UFO Videos”, 27 Apr 2020", "https://www.twz.com/33179/navy-officially-releases-infamous-ufo-videos"),
    ),
  )

internal val tehran2026Story: List<StoryEntry> =
  listOf(
    atTheTime(
      2026,
      "Sep 2026",
      "Social video and anniversary comparisons",
      "Videos showing an illuminated aerial object with cycling multicoloured lights hovering over Tehran circulated on social media and regional news. Observers noted that the sightings occurred during the 50th anniversary of the September 1976 intercept.",
      Citation("IntelliNews, “Mysterious aerial lights over Tehran draw widespread public attention” (Sep 2026)", "https://www.intellinews.com/mysterious-aerial-lights-tehran-iran-344910/"),
    ),
    printOrScreen(
      2026,
      "Sep–Oct 2026",
      "Aviation and defense analysis",
      "Aviation commentators evaluated the footage, highlighting that the red, green, and blue light sequence was consistent with consumer or surveillance drone arrays, while considering heightened air defense vigilance in the region.",
      Citation("CyberNews, “Tehran night sky anomaly: Drone swarm and optical analysis” (Sep 2026)", "https://cybernews.com/news/tehran-ufo-drone-analysis-september-2026/"),
    ),
    inquiry(
      2026,
      "Oct 2026",
      "Civilian open-source verification",
      "Open-source intelligence groups attempted geolocation and triangulation from multiple camera angles across Tehran, finding that camera sensor flaring and lack of distance markers complicated speed and altitude estimates.",
      Citation("Israel Hayom, “Night skies over Tehran: Viral footage of glowing aerial object” (Sep 2026)", "https://www.israelhayom.com/2026/09/22/ufo-lights-tehran-50-year-anniversary/"),
    ),
  )

internal val tehran2026Gaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No official radar or tracking data released",
      "Civil aviation authorities and military air defense commands in Iran made no statement confirming or denying radar contact with the aerial phenomenon.",
      Citation("IntelliNews, “Mysterious aerial lights over Tehran draw widespread public attention” (Sep 2026)", "https://www.intellinews.com/mysterious-aerial-lights-tehran-iran-344910/"),
    ),
    RecordGap(
      "Uncalibrated civilian video only",
      "All public evidence consists of uncalibrated mobile phone videos with unknown zoom settings, compression artifacts, and absence of synchronized reference points.",
      Citation("CyberNews, “Tehran night sky anomaly: Drone swarm and optical analysis” (Sep 2026)", "https://cybernews.com/news/tehran-ufo-drone-analysis-september-2026/"),
    ),
  )

internal val fileThreads: List<FileThread> =
  listOf(
    FileThread(
      id = "T-01",
      title = "Details that arrive years later",
      summary =
        "In several files the most striking details are not in the records made at the time. They enter the account years or decades afterwards.",
      appearances =
        listOf(
          ThreadAppearance("OF-1947-02", "Accounts of bodies are first published in 1980, 33 years after the debris was found."),
          ThreadAppearance("OF-1965-01", "Many accounts of an object in the woods were collected decades later, around the 1990 broadcast."),
          ThreadAppearance("OF-1966-01", "The teacher's account of military warnings and binocular details emerged in a 1971 recorded interview."),
          ThreadAppearance("OF-1967-01", "The account of an object tracked underwater to Shelburne is first published in 2001."),
          ThreadAppearance("OF-1974-01", "The account of bodies driven to Porton Down is published in 1996, 22 years on."),
          ThreadAppearance("OF-1976-01", "Detailed public testimony from the intercepting pilot was delivered in 2007, 31 years after the incident."),
          ThreadAppearance("OF-1980-01", "The binary code is first described in 2010 and is not in the 1981 statements."),
        ),
      keepInMind =
        "A late detail is not thereby false: witnesses give reasons for having stayed silent, among them orders, careers and ridicule. " +
          "It does mean the detail cannot be checked against anything written down at the time.",
    ),
    FileThread(
      id = "T-02",
      title = "Names the press gave",
      summary = "The words these cases are known by often came from headlines and broadcasts, not from the people who made the reports.",
      appearances =
        listOf(
          ThreadAppearance("OF-1947-01", "“Flying saucer” is a headline phrase. Arnold said he was describing how the objects moved."),
          ThreadAppearance("OF-1966-01", "“Westall '66” became the universal shorthand created by later retrospectives and documentaries."),
          ThreadAppearance("OF-1974-01", "“The Welsh Roswell” is a label the press and UFO writers applied years afterwards."),
          ThreadAppearance("OF-1978-01", "“The Valentich Mystery” became the standard headline phrase across Australian press."),
          ThreadAppearance("OF-1980-01", "The memo’s own title is “Unexplained Lights”. The 1983 headline was “UFO lands in Suffolk”."),
          ThreadAppearance("OF-1997-01", "“The Phoenix Lights” covers two events, and the first was reported across the state and beyond."),
          ThreadAppearance("OF-2004-01", "“Tic Tac” is one pilot’s comparison, in print by 2015, which became the name of the case."),
        ),
      keepInMind = "A name can carry a shape, a place or a conclusion that the original report did not.",
    ),
    FileThread(
      id = "T-03",
      title = "Official positions that changed",
      summary = "In these files a public body or official said one thing and later another, or said nothing until the material was already public.",
      appearances =
        listOf(
          ThreadAppearance("OF-1947-02", "A flying disc and a weather balloon on the same day in 1947, then Project Mogul in 1994."),
          ThreadAppearance("OF-1997-01", "The governor made light of the reports in 1997 and in 2007 said he had been a witness."),
          ThreadAppearance("OF-2004-01", "The video circulated for years unacknowledged before the Navy confirmed it in 2019."),
          ThreadAppearance("OF-2015-01", "The videos were public for more than two years before their official release in 2020."),
        ),
      keepInMind =
        "A changed statement shows that the first one was incomplete or wrong. " +
          "It does not show which later account is right, and each side reads the change its own way.",
    ),
    FileThread(
      id = "T-04",
      title = "Records lost, destroyed or withheld",
      summary = "Each of these files has a known hole where a record should be.",
      appearances =
        listOf(
          ThreadAppearance("OF-1947-02", "The base’s outgoing messages for 1946 to 1949 were destroyed. The form does not say on whose authority."),
          ThreadAppearance("OF-1965-01", "Two boxes of NASA records from the period could not be found."),
          ThreadAppearance("OF-1966-01", "No official RAAF or Department of Air report on the Westall school site inspection has ever been found in Australian archives."),
          ThreadAppearance("OF-1976-01", "Original airborne radar scope film and Mehrabad ground radar logs are not in the public record."),
          ThreadAppearance("OF-1978-01", "No wreckage or floating debris from the Cessna was ever recovered from Bass Strait."),
          ThreadAppearance("OF-1980-01", "Defence Intelligence UFO files for 1980 to 1982 are missing, and were reported destroyed."),
          ThreadAppearance("OF-2004-01", "Radar data from the day has not been released, and the Navy holds material it will not release."),
        ),
      keepInMind =
        "Archives lose and destroy routine paper all the time, and governments classify sensor data as a matter of course. " +
          "A gap shows that a record is missing. It cannot show what the record said.",
    ),
    FileThread(
      id = "T-05",
      title = "Two events filed as one",
      summary =
        "Several cases join observations made at different times or by different means. Whether they belong together is often the point in dispute.",
      appearances =
        listOf(
          ThreadAppearance("OF-1952-01", "Radar targets and lights seen by pilots. Whether they were the same things is disputed."),
          ThreadAppearance("OF-1965-01", "A fireball seen from several states and Ontario, and an object reported in one wood."),
          ThreadAppearance("OF-1974-01", "An earth tremor, a light in the sky and lights seen on the mountainside."),
          ThreadAppearance("OF-1976-01", "Two separate F-4 scrambles, airborne electronics failures, and a reported ground landing site filed under the same incident."),
          ThreadAppearance("OF-1980-01", "Two nights and two groups of witnesses, two days apart."),
          ThreadAppearance("OF-1997-01", "A formation crossing the state at about 8 pm, and lights over Phoenix at about 10 pm."),
        ),
      keepInMind = "An explanation that fits one part of a case is sometimes offered, or rejected, as if it covered the whole.",
    ),
  )

internal val nuremberg1561Story: List<StoryEntry> =
  listOf(
    atTheTime(
      1561,
      "14 Apr 1561",
      "The broadsheet is printed",
      "Hans Glaser printed an illustrated sheet at Nuremberg describing a sight in the sky at dawn on 14 April, with globes, tubes, crosses and spear-like lines, and the globes seeming to fight one another.",
      Citation("Wikipedia, “1561 celestial phenomenon over Nuremberg”", "https://en.wikipedia.org/wiki/1561_celestial_phenomenon_over_Nuremberg"),
    ),
    atTheTime(
      1561,
      "1561",
      "Read as a sign from God",
      "The sheet presents the sight as a divine warning and urges repentance. Printed sky signs of the period were commonly read as omens.",
      Citation("Wikipedia, “1561 celestial phenomenon over Nuremberg”", "https://en.wikipedia.org/wiki/1561_celestial_phenomenon_over_Nuremberg"),
    ),
    printOrScreen(
      1958,
      "1958",
      "Jung and the modern myth",
      "The psychologist Carl Jung discussed the sheet in his book on flying saucers as a modern myth, as an example of sky sights read through the beliefs of the time.",
      Citation("Wikipedia, “Flying Saucers: A Modern Myth of Things Seen in the Skies”", "https://en.wikipedia.org/wiki/Flying_Saucers:_A_Modern_Myth_of_Things_Seen_in_the_Skies"),
    ),
  )

internal val nuremberg1561Gaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No named witness",
      "The sheet says many men and women saw the sight but names no one, so no individual account can be checked.",
      Citation("Wikipedia, “1561 celestial phenomenon over Nuremberg”", "https://en.wikipedia.org/wiki/1561_celestial_phenomenon_over_Nuremberg"),
    ),
    RecordGap(
      "No measurement or independent record",
      "Nothing in the sources reviewed records the sight by instrument, and the sheet is the main surviving account.",
      Citation("Wikipedia, “1561 celestial phenomenon over Nuremberg”", "https://en.wikipedia.org/wiki/1561_celestial_phenomenon_over_Nuremberg"),
    ),
  )

internal val battleOfLosAngelesStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1942,
      "24–25 Feb 1942",
      "Alert, blackout and a barrage",
      "An air-raid alert and blackout were ordered. Anti-aircraft batteries fired about 1,430 rounds in about an hour at objects reported over the city. No bombs fell and no aircraft were brought down.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
    atTheTime(
      1942,
      "25 Feb 1942",
      "Conflicting statements",
      "Navy Secretary Frank Knox called the episode a false alarm. The War Department said unidentified aircraft were probably over the city.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
    inquiry(
      1942,
      "26 Feb 1942",
      "Marshall’s memorandum to the President",
      "General George C. Marshall reported to President Roosevelt that unidentified aircraft, not American planes, were probably over Los Angeles, and gave the number of rounds fired.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
    printOrScreen(
      1942,
      "26 Feb 1942",
      "The searchlight photograph",
      "The Los Angeles Times published a photograph of searchlights converging on a point in the sky. The printed version had been retouched to sharpen the beams.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
    printOrScreen(
      1979,
      "1979",
      "“1941”",
      "Steven Spielberg’s comedy film 1941 is built around a fictional panic on the Los Angeles coast, drawing on the 1942 alarm.",
      Citation("Wikipedia, “1941 (film)”", "https://en.wikipedia.org/wiki/1941_(film)"),
    ),
    printOrScreen(
      2011,
      "2011",
      "“Battle: Los Angeles”",
      "A science-fiction film opens with the 1942 event as a pretext for its story of an alien invasion.",
      Citation("Wikipedia, “Battle: Los Angeles”", "https://en.wikipedia.org/wiki/Battle:_Los_Angeles"),
    ),
  )

internal val battleOfLosAngelesGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No aircraft, wreckage or bombs found",
      "Despite about 1,430 rounds fired at reported targets, no aircraft, wreckage or bomb was found, and no enemy flight over the city has been shown from postwar records.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
    RecordGap(
      "What was first fired on is unclear",
      "Accounts differ on what the batteries first fired at, and on how the firing spread from one battery to its neighbours.",
      Citation("Wikipedia, “Battle of Los Angeles”", "https://en.wikipedia.org/wiki/Battle_of_Los_Angeles"),
    ),
  )

internal val fooFightersStory: List<StoryEntry> =
  listOf(
    atTheTime(
      1944,
      "Late 1944",
      "Lights on the Rhine patrols",
      "Crews of the US 415th Night Fighter Squadron reported glowing lights that followed their aircraft on night patrols along the Rhine. A radar observer in the unit, Donald Meiers, is credited with the name “foo fighters”.",
      Citation("Wikipedia, “Foo fighter”", "https://en.wikipedia.org/wiki/Foo_fighter"),
    ),
    atTheTime(
      1945,
      "Early 1945",
      "The name reaches the newspapers",
      "Newspapers carried the term “foo fighters” and reports of glowing balls following Allied aircraft, some calling them a possible German secret weapon.",
      Citation("Wikipedia, “Foo fighter”", "https://en.wikipedia.org/wiki/Foo_fighter"),
    ),
    printOrScreen(
      1956,
      "1956",
      "Ruppelt’s account",
      "Edward Ruppelt, who had led the Air Force’s UFO study, wrote of the wartime reports in his book on the subject, placing them in the early history of flying-saucer sightings.",
      Citation("Wikipedia, “The Report on Unidentified Flying Objects”", "https://en.wikipedia.org/wiki/The_Report_on_Unidentified_Flying_Objects"),
    ),
    printOrScreen(
      1995,
      "1995",
      "A band takes the name",
      "The American rock band Foo Fighters took its name from the wartime term and released its first album that year.",
      Citation("Wikipedia, “Foo Fighters”", "https://en.wikipedia.org/wiki/Foo_Fighters"),
    ),
  )

internal val fooFightersGaps: List<RecordGap> =
  listOf(
    RecordGap(
      "No photograph or physical trace",
      "No object was captured, and no photograph or film of one is cited in the histories of the reports.",
      Citation("Wikipedia, “Foo fighter”", "https://en.wikipedia.org/wiki/Foo_fighter"),
    ),
    RecordGap(
      "No radar track for most reports",
      "Histories say most of the lights were not seen on radar, so there is no instrument record to test the crews’ accounts against.",
      Citation("Wikipedia, “Foo fighter”", "https://en.wikipedia.org/wiki/Foo_fighter"),
    ),
  )
