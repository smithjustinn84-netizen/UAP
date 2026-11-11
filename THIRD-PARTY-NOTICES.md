# Third-party material

## Fonts

The font files in `app-core/src/main/res/font/` were downloaded from Google Fonts.

| Files | Family | License |
|:---|:---|:---|
| `special_elite_regular.ttf` | Special Elite (Astigmatic) | Apache License 2.0 — https://www.apache.org/licenses/LICENSE-2.0 |
| `ibm_plex_sans_regular.ttf`, `ibm_plex_sans_semibold.ttf` | IBM Plex Sans (IBM Corp.) | SIL Open Font License 1.1 — https://openfontlicense.org |
| `ibm_plex_mono_regular.ttf`, `ibm_plex_mono_medium.ttf` | IBM Plex Mono (IBM Corp.) | SIL Open Font License 1.1 — https://openfontlicense.org |

Both licenses ask that the license text travel with the fonts when they are redistributed. Before
publishing the app, include the full license texts (for example on an in-app licenses screen).

## Map outlines

`app-core/src/main/res/raw/map_north_america.txt` and `map_british_isles.txt` are clipped and simplified
from Natural Earth 1:50m land (`ne_50m_land`), via the nvkelso/natural-earth-vector repository. Natural Earth
is in the public domain; no attribution is required, but it is credited here. The Great Lakes and other inland
water are not drawn, because only the land layer was used.

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

The Roswell items rest on the published-and-not-renewed argument. UT Arlington offers its own scans under CC BY-NC,
so do not swap in UTA's copies.

## External links (not bundled)

These open outside the app and are not redistributed: the Phoenix Lights camcorder video (YouTube, AZDigitalTransfers),
the Halt tape (Internet Archive upload, copyright status unknown), the 24 June 1997 Air Force Roswell press conference
(C-SPAN), and the 26 July 2023 House Oversight subcommittee hearing (official committee YouTube channel).

## Libraries

AndroidX Media3 (ExoPlayer, UI) plays the video clips. Apache License 2.0.
