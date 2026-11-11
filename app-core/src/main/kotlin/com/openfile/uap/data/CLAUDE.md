# Editorial rules for content

The app's premise is neutrality; keep it when adding or editing cases.

- Record what was reported and what is on record. Don't rule on whether anything happened or what it was.
- Every `Evidence` and `ReportMedia` has a `SourceLabel` (Primary record, First-hand, Official record, Reported,
  Not preserved). The label says where the item comes from, never whether it is true. The enum also holds the
  definitions the Method screen shows, so adding a label means writing its definition there.
- Status text uses the Method screen terms ("Official explanation on record", "No official explanation",
  "Disputed").
- Explanations are listed A–Z, never ranked, each with who holds it, what supports it, what complicates it and
  which evidence ids it draws on.
- Lore entries are dated by when a detail first became public and say who introduced it. A `StoryKind` says how
  it arrived, never whether it is true. Each case's story opens with an "At the time" entry.
- Every lore entry and gap carries a `Citation` whose URL has been opened and read against the text. If the
  original is unreachable, cite what was actually read and say so in the label ("transcription", "as syndicated by").
- A gap says that a record is missing and who says so, never what it would have shown. Threads describe the
  records, list their cases oldest first and carry a `keepInMind` that guards against reading a verdict into them.
- `MapPoint.note` says what the pin stands for, since many locations are approximate.
- Keep facts sourced and accurate; give media a `provenance` and `sourceUrl` where one exists.

`ArchiveTest` enforces several of these (alphabetical explanations, valid evidence references, timeline case ids
that resolve, media linked to real evidence, lore and timeline in date order, threads that resolve). Run the unit tests after any content change.
