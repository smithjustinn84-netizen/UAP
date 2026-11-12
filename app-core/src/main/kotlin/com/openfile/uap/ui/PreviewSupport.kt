package com.openfile.uap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.OpenFileTheme
import com.openfile.uap.theme.Paper

/** Renders preview content inside [OpenFileTheme] on the manila desk background. */
@Composable
internal fun DeskPreview(padding: Dp = 0.dp, content: @Composable BoxScope.() -> Unit) {
  OpenFileTheme {
    Box(Modifier.background(Manila).padding(padding), content = content)
  }
}

/** Renders preview content inside [OpenFileTheme] on a paper sheet background. */
@Composable
internal fun PaperPreview(padding: Dp = 16.dp, content: @Composable BoxScope.() -> Unit) {
  OpenFileTheme {
    Box(Modifier.background(Paper).padding(padding), content = content)
  }
}
