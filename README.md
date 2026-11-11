# Open File

An Android archive of well-known UAP (UFO) reports, laid out like a set of manila case files. Each file records
what was reported, what is on record and every explanation that has been seriously put forward — without ruling
on any of it.

## What's in the app

- **Files** — the case list, searchable by name, place or year and filterable by decade.
- **Map** — every case plotted on hand-drawn sheets of North America and Britain and Ireland.
- **Timeline** — sightings alongside the inquiries and records that followed them.
- **Method** — how the archive labels sources, status and explanations.
- **Case file** — the full write-up: what was reported, what is and isn't in dispute, evidence with source labels,
  media exhibits (diagrams, plates, audio/video transcripts, document excerpts) and explanations listed A–Z.

Cases on file include Kenneth Arnold (1947), Roswell (1947), the Washington, D.C. radar sightings (1952),
Kecksburg (1965), Shag Harbour (1967), Berwyn Mountains (1974), Rendlesham Forest (1980), the Phoenix Lights
(1997), USS Nimitz (2004) and USS Theodore Roosevelt (2015).

## Tech

- Kotlin 2.3, Jetpack Compose, Material 3
- Navigation 3
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
    NavigationKeys.kt         Files, EventMap, Timeline, Method, CaseFile(id)
    data/Archive.kt           Every case, its write-up, media and the timeline
    data/MapSheets.kt         Map sheet bounds, projection and outline parser
    theme/                    Colours, typography (Special Elite, IBM Plex), theme
    ui/                       One file per screen, plus shared components
  src/main/res/raw/           Map outlines (Natural Earth, simplified)
  src/main/res/drawable/      Vector "photographic plates" used as media exhibits
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

## Third-party material

Fonts are from Google Fonts (Apache 2.0 and SIL OFL 1.1) and map outlines are derived from Natural Earth (public
domain). See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
