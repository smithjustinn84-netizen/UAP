package com.openfile.uap.theme

import androidx.compose.material3.Typography
import com.openfile.uap.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Special Elite is Apache 2.0; IBM Plex Sans and Mono are SIL Open Font License 1.1 (see THIRD-PARTY-NOTICES.md at the project root).
val TypewriterFamily: FontFamily = FontFamily(Font(R.font.special_elite_regular, FontWeight.Normal))
val MonoFamily: FontFamily =
  FontFamily(Font(R.font.ibm_plex_mono_regular, FontWeight.Normal), Font(R.font.ibm_plex_mono_medium, FontWeight.Medium))
val BodyFamily: FontFamily =
  FontFamily(Font(R.font.ibm_plex_sans_regular, FontWeight.Normal), Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold))

val Typography =
  Typography(
    bodyLarge = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = BodyFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp),
    labelSmall =
      TextStyle(fontFamily = MonoFamily, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 1.1.sp),
    labelMedium = TextStyle(fontFamily = MonoFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = MonoFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
  )
