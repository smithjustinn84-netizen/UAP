# CLAUDE.md

Guidance for Claude Code when working in this repository. See [README.md](README.md) for an overview of the app.

@AGENTS.md

## Build system

- **Lightbuild only. Do not add Gradle files** (`build.gradle*`, `settings.gradle*`, `gradlew`). This is a
  deliberate choice by the project owner.
- Lightbuild is experimental and pinned to `0.0.30`. Run `lightbuild skill` for the YAML schema and commands before
  editing `project.lightbuild.yaml` or any module's `lightbuild.yaml`.
- If `lightbuild` is not on `PATH`, call it directly:
  `"$(android info sdk)/build/lightbuild/0.0.30/cli/lightbuild"`.
- Always pass `--plain`.

| Task | Command |
|:---|:---|
| Build debug APK | `lightbuild --plain build app` → `app/build/app-debug.apk` |
| Unit tests | `lightbuild --plain test app-core:hostTest` |
| Run on device | `android run app/build/app-debug.apk` |
| Update lockfiles after dependency changes | `lightbuild --plain resolve` (commit the `resolved.deps` files) |

`lightbuild test app-core` fails ("No XML test reports found") because it also runs instrumentation tests and there
are none. Use `app-core:hostTest`.

Build targets have no `//` prefix (`app-core`); module dependencies inside `lightbuild.yaml` do (`//app-core`).

## Modules

- `app` — application shell: manifest and `applicationId` only. Don't put code here.
- `app-core` — everything else, package `com.openfile.uap`. All Maven dependencies are declared here.

## Architecture

- Compose + Material 3, single activity. Navigation 3 with `@Serializable` keys in `NavigationKeys.kt`.
  Four top-level tabs (`Files`, `EventMap`, `Timeline`, `Method`) each wrapped in `AppFrame`; switching tabs
  replaces the back stack. `CaseFile(id)` is pushed on top and keeps the tab it was opened from highlighted.
- No ViewModels, network or persistence. All content is static Kotlin data in `data/Archive.kt`:
  `Archive.cases` (oldest first), `Archive.timeline`, `Archive.search(query, decade)`, `Archive.case(id)`.
  Each case's write-up is a private `CaseDetail` val further down the same file.
- Media exhibits (`ReportMedia`) link to evidence by `evidenceId`. Drawn figures are `MediaFigure` enum values
  rendered in `ui/ReportMediaComponents.kt`; photographic plates are vector drawables `res/drawable/plate_*.xml`.
- Map: `data/MapSheets.kt` defines sheet bounds and projection; outlines are `res/raw/map_*.txt`
  (one ring per line of `lon,lat` pairs). Sheet bounds must match the clipping used to make those files.
- Styling: manila/paper/ink palette in `theme/Color.kt`; fonts in `theme/Type.kt`. Reuse the shared composables
  in `ui/Components.kt` (`PaperSheet`, `MonoLabel`, `TypeTitle`, `Stamp`, `RecordField`, `BulletList`, rules,
  `TapRow`, `ScreenPadding`) instead of raw Material components.

## Editorial rules for content

The app's premise is neutrality; keep it when adding or editing cases.

- Record what was reported and what is on record. Don't rule on whether anything happened or what it was.
- Every `Evidence` has a source label from the Method screen ("Primary record", "First-hand", "Official record",
  "Reported", "Not preserved"). The label says where the item comes from, never whether it is true.
- Status text uses the Method screen terms ("Official explanation on record", "No official explanation",
  "Disputed").
- Explanations are listed A–Z, never ranked, each with who holds it, what supports it, what complicates it and
  which evidence ids it draws on.
- `MapPoint.note` says what the pin stands for, since many locations are approximate.
- Keep facts sourced and accurate; give media a `provenance` and `sourceUrl` where one exists.

`ArchiveTest` enforces several of these (alphabetical explanations, valid evidence references, timeline case ids
that resolve, media linked to real evidence). Run the unit tests after any content change.

## Code style

- Kotlin, 2-space indent, lines up to ~140 characters, trailing commas in multi-line argument lists.
- Comments are sparse and explain why; KDoc on data types where a field's meaning isn't obvious.
- New third-party assets (fonts, map data, images) must be added to `THIRD-PARTY-NOTICES.md` with their licence.
