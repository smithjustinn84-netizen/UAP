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
