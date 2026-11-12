package com.openfile.uap.data

/**
 * A licence that something bundled in the app is released under. Only Apache 2.0 and the OFL ask for their full text
 * to travel with the work, so only those ship one in res/raw; the others are shown as a link.
 */
enum class Licence(val title: String, val url: String) {
  Apache2("Apache License 2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
  Ofl11("SIL Open Font License 1.1", "https://openfontlicense.org"),
  CcBySa20("CC BY-SA 2.0", "https://creativecommons.org/licenses/by-sa/2.0/"),
  CcBySa30("CC BY-SA 3.0", "https://creativecommons.org/licenses/by-sa/3.0/"),
  CcBySa40("CC BY-SA 4.0", "https://creativecommons.org/licenses/by-sa/4.0/"),
  PublicDomain("Public domain", "https://creativecommons.org/publicdomain/mark/1.0/"),
}

/**
 * One third-party work bundled in the app. [work] names it as the reader would recognise it; [credit] is who made or
 * released it, and for public-domain items, why it is public domain.
 */
data class Notice(val work: String, val credit: String, val licence: Licence)

/** Everything listed in THIRD-PARTY-NOTICES.md, in the same terms. Keep the two in step. */
object Notices {
  val all: List<Notice> =
    listOf(
      // Fonts
      Notice("IBM Plex Sans, IBM Plex Mono", "Copyright © 2017 IBM Corp., with Reserved Font Name \"Plex\"", Licence.Ofl11),
      Notice("Special Elite", "Astigmatic", Licence.Apache2),
      // Libraries
      Notice("AndroidX: Activity, Core, Lifecycle, Navigation 3, Compose, Material 3", "The Android Open Source Project", Licence.Apache2),
      Notice("AndroidX Media3 (ExoPlayer, UI)", "The Android Open Source Project", Licence.Apache2),
      Notice("Coil", "Coil Contributors", Licence.Apache2),
      Notice(
        "Kotlin standard library, kotlinx.coroutines, kotlinx.serialization",
        "JetBrains s.r.o. and Kotlin contributors",
        Licence.Apache2,
      ),
      // Openly licensed photographs, credited under each image in the app
      Notice("Kecksburg replica (2008)", "Photo by Ryright, via Wikimedia Commons", Licence.CcBySa30),
      Notice("Mount Rainier from the west (2005)", "Photo by Stan Shebs, via Wikimedia Commons", Licence.CcBySa30),
      Notice("Shag Harbour (2017)", "Photo by J5255, via Wikimedia Commons", Licence.CcBySa40),
      Notice("Cadair Berwyn from the summit", "Photo by Colin Park, geograph.org.uk, via Wikimedia Commons", Licence.CcBySa20),
      // Public domain
      Notice(
        "FLIR1, GIMBAL and GOFAST videos",
        "US Department of Defense / NAVAIR release, 2020. US Government work",
        Licence.PublicDomain,
      ),
      Notice("Maj. Gen. Samford press conference film, 1952", "US National Archives, RG 111. US Government work", Licence.PublicDomain),
      Notice(
        "Halt memorandum, Samford press conference minutes, Mogul balloon train figure",
        "US Air Force. US Government work, via Wikimedia Commons",
        Licence.PublicDomain,
      ),
      Notice(
        "USS Nimitz and USS Theodore Roosevelt photographs",
        "US Navy. US Government work, via Wikimedia Commons",
        Licence.PublicDomain,
      ),
      Notice(
        "Bristol Beaufighter, 415th Night Fighter Squadron",
        "USAAF, 1943. US Government work, via Wikimedia Commons",
        Licence.PublicDomain,
      ),
      Notice(
        "Roswell Daily Record front page and Ramey–DuBose photograph, 1947",
        "Roswell Daily Record; Fort Worth Star-Telegram. Copyright not renewed, via Wikimedia Commons",
        Licence.PublicDomain,
      ),
      Notice(
        "Los Angeles Times front page, 26 February 1942",
        "Los Angeles Times. Copyright not renewed, via Wikimedia Commons",
        Licence.PublicDomain,
      ),
      Notice("Nuremberg broadsheet, 1561", "Hanns Glaser; Zentralbibliothek Zürich. Public domain by age", Licence.PublicDomain),
      Notice("Map outlines", "Clipped and simplified from Natural Earth land data", Licence.PublicDomain),
    )
}
