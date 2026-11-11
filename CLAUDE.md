# CLAUDE.md

Guidance for Claude Code when working in this repository. See [README.md](README.md) for an overview of the app.

Install steps if the `lightbuild` CLI is missing: `AGENTS.md`.

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
| Full quality gate (lint, tests, build) | `scripts/check` |
| Lint only (ktlint + detekt) | `scripts/check lint` |
| Auto-format Kotlin | `scripts/check format` |

`lightbuild test app-core` fails ("No XML test reports found") because it also runs instrumentation tests and there
are none. Use `app-core:hostTest`.

Build targets have no `//` prefix (`app-core`); module dependencies inside `lightbuild.yaml` do (`//app-core`).

## Modules

- `app` — application shell: manifest and `applicationId` only. Don't put code here.
- `app-core` — everything else, package `com.openfile.uap`. All Maven dependencies are declared here.

## Architecture

- No network or persistence. All content is static Kotlin data in `data/Archive.kt`:
  `Archive.cases` (oldest first), `Archive.timeline`, `Archive.search(query, decade)`, `Archive.case(id)`.
  Each case's write-up is a private `CaseDetail` val further down the same file.
- UI reads data only through repositories: `ArchiveRepository` (synchronous; the archive is compiled in) and
  `MapOutlineRepository` (suspend; parses `res/raw` off the main thread and caches). `AppContainer` is the manual DI
  root, owned by `OpenFileApplication` so it survives configuration changes. Don't add a DI framework.
- Each feature is a package `ui/<feature>/` with a `<Feature>Route` (gets the ViewModel, collects state), a stateless
  `<Feature>Screen` and a `<Feature>ViewModel` exposing an immutable `UiState`. Filters and selections that should
  survive process death go in `SavedStateHandle`; pure UI element state (scroll, open section) stays in
  `rememberSaveable`. `Navigation.kt` gives each back-stack entry its own ViewModelStore. ViewModel tests live in
  `ViewModelTest` and use fakes of the repository interfaces.
- `data/Lore.kt` holds each case's `story` (how it has been retold, oldest first, each step with a `StoryKind`),
  its `gaps` (records lost, destroyed, withheld or never made) and `Archive.threads` (patterns across files).
- Media exhibits (`ReportMedia`) link to evidence by `evidenceId`. Formats include drawn vector diagrams
  (`MediaFigure` in `ui/casefile/ReportMediaComponents.kt` and `res/drawable/plate_*.xml`), photographic and document
  scans (`MediaImage` in `res/drawable-nodpi/` loaded with Coil), bundled video clips (`MediaClip` in `res/raw/` played
  with Media3 ExoPlayer), audio/video transcripts, verbatim document excerpts and external links.
- A file-specific briefing graphic is selected by `CaseDetail.infographic` (an `Infographic` entry), never by checking
  a case id in the UI; each entry maps to a composable in `ui/casefile/` (e.g. `NimitzInfographic.kt`).
- Map: `data/MapSheets.kt` defines sheet bounds and projection (North America, Britain & Ireland, South-East Australia,
  Middle East); outlines are `res/raw/map_*.txt` (one ring per line of `lon,lat` pairs). Sheet bounds must match the
  clipping used to make those files.
- Styling: manila/paper/ink palette in `theme/Color.kt`; fonts in `theme/Type.kt`. Reuse the shared composables
  in `ui/Components.kt` (`PaperSheet`, `MonoLabel`, `TypeTitle`, `Stamp`, `RecordField`, `BulletList`, rules,
  `TapRow`, `ScreenPadding`) and `ui/SheetParts.kt` (`ScreenHeader`, `BackLink`, `TitledBlock`, `CardHeader`,
  `ActionRow`, `LeadText`, `countOf`) instead of raw Material components.
- Top-level tabs and their labels are defined once, as `TopLevelSections` in `NavigationKeys.kt`.

## Editorial rules for content

The app's premise is neutrality: record what was reported and what is on record, and never rule on whether
anything happened or what it was. The full rules are in `app-core/src/main/kotlin/com/openfile/uap/data/CLAUDE.md`,
which loads when you work on files in that directory; read it before writing case, lore, thread or timeline content
anywhere else. Run the unit tests after any content change.

## Code style

- Run `scripts/check` before finishing a change. ktlint (rules in `.editorconfig`) owns formatting and detekt
  (`config/detekt.yml`) owns complexity, naming and likely bugs; both run as pinned standalone CLIs because Lightbuild
  has no plugins (so no Spotless). Fix findings rather than suppressing them; relax a rule in config only with a
  comment saying why.
- Kotlin warnings are errors in `app-core` (`kotlin.allWarningsAsErrors`).
- Comments are sparse and explain why; KDoc on data types where a field's meaning isn't obvious.
- New third-party assets (fonts, map data, images) must be added to `THIRD-PARTY-NOTICES.md` with their licence.
