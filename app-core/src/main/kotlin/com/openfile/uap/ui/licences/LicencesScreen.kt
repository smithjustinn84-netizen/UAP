package com.openfile.uap.ui.licences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openfile.uap.data.Licence
import com.openfile.uap.data.Notices
import com.openfile.uap.data.NoticesRepository
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.ui.BackLink
import com.openfile.uap.ui.CardHeader
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.FoldOut
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.UnfoldIn
import com.openfile.uap.ui.countOf
import com.openfile.uap.ui.rememberTick

@Composable
fun LicencesRoute(
  notices: NoticesRepository,
  onBack: () -> Unit,
  viewModel: LicencesViewModel = viewModel { LicencesViewModel(notices) },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  LicencesScreen(uiState = uiState, onBack = onBack)
}

@Composable
fun LicencesScreen(uiState: LicencesUiState, onBack: () -> Unit) {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    BackLink("About", onBack, Modifier.padding(start = 8.dp, top = 8.dp))
    ScreenHeader(
      label = "Licences",
      title = "Third-party material",
      intro = "The fonts, libraries, photographs, scans, clips and map data bundled in the app, and the terms each is used under.",
      top = 12.dp,
    )

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      uiState.groups.forEach { LicenceCard(it) }
    }
  }
}

@Composable
private fun LicenceCard(group: LicenceGroup) {
  val uriHandler = LocalUriHandler.current
  val tick = rememberTick()
  // Keyed per licence so each card remembers its own fold across rotation.
  var open by rememberSaveable(group.licence) { mutableStateOf(false) }
  PaperSheet {
    CardHeader(group.licence.title, countOf(group.notices.size, "item"))
    Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
      group.notices.forEachIndexed { index, notice ->
        if (index > 0) SolidRule()
        Column(Modifier.padding(vertical = 11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Text(notice.work, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Ink)
          Text(notice.credit, style = MaterialTheme.typography.labelMedium, color = InkSoft)
        }
      }
    }
    SolidRule()
    val text = group.fullText
    if (text == null) {
      TapRow(onClick = { runCatching { uriHandler.openUri(group.licence.url) } }, onClickLabel = "Open licence") {
        LinkRow("Read the licence") { Chevron() }
      }
    } else {
      TapRow(
        onClick = {
          tick()
          open = !open
        },
        onClickLabel = if (open) "Close" else "Open",
        modifier = Modifier.semantics { stateDescription = if (open) "Open" else "Closed" },
      ) {
        LinkRow("Full text") { Chevron(down = !open, up = open) }
      }
      AnimatedVisibility(visible = open, enter = UnfoldIn, exit = FoldOut) {
        Text(
          text,
          modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.sp),
          color = InkBody,
        )
      }
    }
  }
}

@Composable
private fun LinkRow(text: String, end: @Composable () -> Unit) {
  Row(
    Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).padding(horizontal = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    MonoLabel(text, color = Ink, modifier = Modifier.weight(1f))
    end()
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1600)
@Composable
private fun LicencesScreenPreview() {
  val groups =
    Notices.all.groupBy { it.licence }.toSortedMap().map { (licence, items) ->
      LicenceGroup(licence, items, if (licence == Licence.Ofl11) "SIL OPEN FONT LICENSE Version 1.1" else null)
    }
  DeskPreview {
    LicencesScreen(LicencesUiState(groups), onBack = {})
  }
}
