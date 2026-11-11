package com.openfile.uap.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The dossier look is a single fixed paper-and-ink palette, so it ignores dark mode and dynamic color.
private val DossierColors =
  lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    secondary = StampRed,
    onSecondary = Paper,
    background = Manila,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    outline = Ink,
    outlineVariant = Rule,
  )

@Composable
fun OpenFileTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = DossierColors, typography = Typography, content = content)
}
