# Open File

An Android archive of well-known UAP (UFO) reports, laid out like a set of manila case files. Each file records
what was reported, what is on record and every explanation that has been seriously put forward — without ruling
on any of it.

## What's in the app

- **Files** — the case list, searchable by name, place, year or witness and filterable by decade.
- **Threads** — patterns that recur across the files (late-arriving details, missing records, changed official
  positions), each with what to keep in mind about it. Opened from a tile at the top of the case list.
- **Map** — every case plotted on hand-drawn sheets of North America, Britain and Ireland, South-East Australia
  and the Middle East, shown one sheet at a time.
- **Timeline** — sightings alongside the inquiries that followed them and the books, films and broadcasts that
  retold them, filterable by kind, with a jump to each decade.
- **Method** — how to read a file: a sample item with its parts explained, the source labels, and how the archive
  treats status, explanations, lore and missing records.
- **Case file** — the full write-up: what was reported, what is and isn't in dispute, evidence with source labels,
  media exhibits (diagrams, plates, audio/video transcripts, document excerpts), explanations listed A–Z, the
  records known to be missing, and the lore: how the story has been told since, in date order.

Cases on file include Kenneth Arnold (1947), Roswell (1947), the Washington, D.C. radar sightings (1952),
Kecksburg (1965), Westall (1966), Shag Harbour (1967), Berwyn Mountains (1974), Tehran (1976),
the Valentich disappearance (1978), Rendlesham Forest (1980), the Phoenix Lights (1997), USS Nimitz (2004),
USS Theodore Roosevelt (2015) and the Tehran aerial phenomenon (2026).

## Tech

- Kotlin 2.3, Jetpack Compose, Material 3
- Navigation 3
- AndroidX Media3 (ExoPlayer) for video playback, Coil for photographs and document scans
- [Lightbuild](https://developer.android.com/r/lightbuild) (experimental) instead of Gradle
- minSdk 24, compileSdk 36, targetSdk 34, Java 17

All content is bundled in the app; there is no network access or backend.

## Project layout

```
project.lightbuild.yaml       Project config (modules, repositories, Lightbuild/Kotlin/Java versions)
app/                          Application module — just the manifest and applicationId (com.openfile.uap)
app-core/                     Library module with all code and resources
  src/main/kotlin/com/openfile/uap/
    MainActivity.kt
    Navigation.kt             NavDisplay, tab switching, case-file routing
    NavigationKeys.kt         Files, EventMap, Timeline, Method, Threads, CaseFile(id)
    data/Archive.kt           Every case, its write-up, media and the timeline
    data/Lore.kt              How each case has been retold, what is missing from its record, cross-file threads
    data/MapSheets.kt         Map sheet bounds, projection and outline parser
    theme/                    Colours, typography (Special Elite, IBM Plex), theme
    ui/                       One file per screen, shared components, infographics
  src/main/res/raw/           Map outlines (Natural Earth, simplified) and bundled video clips
  src/main/res/drawable/      Vector "photographic plates" used as media exhibits
  src/main/res/drawable-nodpi/ Bundled photographic plates and document scans
  src/test/                   JVM unit tests for the archive and map sheets
```

## Building

You need the Android SDK and the [Android CLI](https://developer.android.com/tools) (`android`). Lightbuild
downloads the version pinned in `project.lightbuild.yaml` on first use. To install the `lightbuild` CLI itself,
see [AGENTS.md](AGENTS.md).

```shell
lightbuild build app                 # debug APK -> app/build/app-debug.apk
lightbuild test app-core:hostTest    # JVM unit tests
android run app/build/app-debug.apk  # install and launch on a connected device or emulator
```

Add `--plain` for log-friendly output. Note that `lightbuild test app-core` also tries to run instrumentation
tests on a device and currently fails because there are none; use `app-core:hostTest` for unit tests.

## Code quality

```shell
scripts/check          # ktlint, detekt, unit tests and a debug build
scripts/check lint     # ktlint and detekt only
scripts/check format   # auto-format Kotlin with ktlint
```

Formatting rules live in `.editorconfig` and detekt overrides in `config/detekt.yml`. The script downloads pinned,
checksum-verified ktlint and detekt CLIs into `build/quality-tools/` on first run (needs Java and `curl`).
Kotlin compiler warnings fail the `app-core` build.

## Third-party material

Fonts are from Google Fonts (Apache 2.0 and SIL OFL 1.1), map outlines are derived from Natural Earth (public
domain), and video clips, photographs and document scans are from government releases, archives and Wikimedia Commons.
See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
