# Third-party material

## Fonts

The font files in `app-core/src/main/res/font/` were downloaded from Google Fonts.

| Files | Family | License |
|:---|:---|:---|
| `special_elite_regular.ttf` | Special Elite (Astigmatic) | Apache License 2.0 — https://www.apache.org/licenses/LICENSE-2.0 |
| `ibm_plex_sans_regular.ttf`, `ibm_plex_sans_semibold.ttf` | IBM Plex Sans (IBM Corp.) | SIL Open Font License 1.1 — https://openfontlicense.org |
| `ibm_plex_mono_regular.ttf`, `ibm_plex_mono_medium.ttf` | IBM Plex Mono (IBM Corp.) | SIL Open Font License 1.1 — https://openfontlicense.org |

Both licenses ask that the license text travel with the fonts when they are redistributed. The full texts are
bundled as `app-core/src/main/res/raw/licence_apache_2_0.txt` (from apache.org) and `licence_ofl_1_1.txt` (IBM Plex's
own `LICENSE.txt`, with its copyright line) and shown on the in-app Licences screen (Method > About and licences).

Everything in this file is also listed in `app-core/src/main/kotlin/com/openfile/uap/data/Notices.kt`, which the
Licences screen reads. Keep the two in step.

## Map outlines

`app-core/src/main/res/raw/map_north_america.txt`, `map_british_isles.txt`, `map_south_east_australia.txt`,
`map_middle_east.txt` and `map_central_europe.txt` are clipped and simplified from Natural Earth land data (public domain).
Natural Earth is in the public domain; no attribution is required, but it is credited here.

## Video clips

Files in `app-core/src/main/res/raw/`:

| File | What | Source | Licence |
|:---|:---|:---|:---|
| `video_nimitz_flir1.mp4` | FLIR1 (2004) | DoD/NAVAIR release, 27 April 2020, via The Black Vault mirror; also on DVIDS (dvidshub.net/video/955825) | US Government work, public domain |
| `video_roosevelt_gimbal.mp4` | GIMBAL (2015) | Same release (dvidshub.net/video/956955); WMV re-encoded to H.264/AAC MP4 | Public domain |
| `video_roosevelt_gofast.mp4` | GOFAST (2015) | Same release (dvidshub.net/video/956683); WMV re-encoded to H.264/AAC MP4 | Public domain |
| `video_washington_samford.mp4` | Maj. Gen. Samford's statement on "flying saucers", 31 July 1952 | US National Archives, RG 111, catalog id 25738, via archive.org item `111-LC-30875`; re-encoded smaller | US Government work, public domain |

NAVAIR's own download URLs returned 403 when these were fetched, so mirrors were used.

## Photographs and scans

Files in `app-core/src/main/res/drawable-nodpi/`, all from Wikimedia Commons (resized; Commons standard thumbnails
were used where the originals were rate-limited):

| File | Commons file | Licence as tagged on Commons |
|:---|:---|:---|
| `scan_halt_memo.jpg` | File:Halt_Memorandum.jpg (Lt Col Charles Halt, 13 Jan 1981) | Public domain, US Air Force work |
| `scan_roswell_daily_record.jpg` | File:Roswell_Daily_Record._July_8,_1947. … Full_front_page.jpg | Public domain, copyright not renewed |
| `photo_roswell_ramey_dubose.jpg` | File:Ramey-dubose-debris.jpg (Fort Worth Star-Telegram, 8 July 1947, via UT Arlington Libraries) | Public domain, copyright not renewed |
| `fig_mogul_balloon_train.jpg` | File:Mogul_balloon_train_USAF_1995.png (USAF, *The Roswell Report*, 1995) | Public domain, US Government work |
| `photo_kecksburg_replica.jpg` | File:Kecksburg_UFO.JPG (photo by Ryright, 2008) | CC BY-SA 3.0 — https://creativecommons.org/licenses/by-sa/3.0/ (credit shown in the app) |
| `scan_samford_minutes.jpg` | Page 1 of File:General_Samford_Press_Conference,_July_29,_1952.pdf | Public domain, US Government work |
| `hero_nuremberg.jpg` | File:Himmelserscheinung_über_Nürnberg_vom_14._April_1561.jpg (broadsheet by Hanns Glaser, 1561; Zentralbibliothek Zürich) | Public domain (age) |
| `hero_los_angeles.jpg` | File:Battle_of_Los_Angeles_LATimes.jpg (Los Angeles Times, 26 Feb 1942) | Public domain, copyright not renewed |
| `hero_beaufighter.jpg` | File:415th_Night_Fighter_Squadron_-_Bristol_Beaufighter.jpg (USAAF, 1943) | Public domain, US Government work |
| `hero_rainier.jpg` | File:Mount_Rainier_from_west.jpg (photo by Stan Shebs, 2005) | CC BY-SA 3.0 — https://creativecommons.org/licenses/by-sa/3.0/ (credit shown in the app) |
| `hero_shag_harbour.jpg` | File:Shag_Harbour.jpg (photo by J5255, 2017) | CC BY-SA 4.0 — https://creativecommons.org/licenses/by-sa/4.0/ (credit shown in the app) |
| `hero_berwyn.jpg` | File:Cadair_Berwyn_from_summit_at_830_metres_-_geograph.org.uk_-_6678353.jpg (photo by Colin Park) | CC BY-SA 2.0 — https://creativecommons.org/licenses/by-sa/2.0/ (credit shown in the app) |
| `hero_nimitz.jpg` | File:USS_Nimitz_(CVN-68).jpg (US Navy photo 090729-N-3038W-459) | Public domain, US Navy work |
| `hero_roosevelt.jpg` | File:140918-N-PG340-937_USS_Theodore_Roosevelt_(CVN-71)_underway_Atlantic_Ocean_Sept_18_2014.jpg (US Navy photo by MC3 Stephane Belcher) | Public domain, US Navy work |

The `hero_*` files head a case file. Each is either a record made at the time or a picture of the setting, never a
later reconstruction of what was reported. Their CC BY-SA credits are shown under the image. They were resized; nothing
else was changed.

The Roswell items rest on the published-and-not-renewed argument. UT Arlington offers its own scans under CC BY-NC,
so do not swap in UTA's copies.

## External links (not bundled)

These open outside the app and are not redistributed: the Phoenix Lights camcorder video (YouTube, AZDigitalTransfers),
the Halt tape (Internet Archive upload, copyright status unknown), the 24 June 1997 Air Force Roswell press conference
(C-SPAN), the 26 July 2023 House Oversight subcommittee hearing (official committee YouTube channel), and the
September 2026 Tehran aerial recordings (YouTube).

## Libraries

AndroidX Media3 (ExoPlayer, UI) plays the video clips. Apache License 2.0.

Coil (`io.coil-kt.coil3:coil-compose`) loads the bundled photographs and scans. Apache License 2.0.
