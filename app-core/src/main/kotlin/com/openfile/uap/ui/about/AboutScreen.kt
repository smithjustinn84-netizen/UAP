package com.openfile.uap.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openfile.uap.ui.ActionRow
import com.openfile.uap.ui.BackLink
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.RecordField
import com.openfile.uap.ui.ScreenHeader
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.StatusBarSpacer
import com.openfile.uap.ui.TitledBlock

@Composable
fun AboutRoute(version: String, onOpenLicences: () -> Unit, onBack: () -> Unit) {
  AboutScreen(version = version, onOpenLicences = onOpenLicences, onBack = onBack)
}

@Composable
fun AboutScreen(version: String, onOpenLicences: () -> Unit, onBack: () -> Unit) {
  Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    StatusBarSpacer()
    BackLink("Method", onBack, Modifier.padding(start = 8.dp, top = 8.dp))
    ScreenHeader(
      label = "About",
      title = "Open File",
      intro = "An archive of well-known UAP reports, laid out as case files.",
      top = 12.dp,
    )

    Column(
      Modifier.padding(start = ScreenPadding, end = ScreenPadding, top = 8.dp, bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      TitledBlock("This copy") {
        PaperSheet {
          Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
            RecordField("Version", version.ifBlank { "Unknown" })
            RecordField(
              "Privacy",
              "The app has no network access and collects nothing. Every file, image and clip is bundled with it. " +
                "Sources open in your browser only when you tap them.",
              showRule = false,
            )
          }
        }
      }
      TitledBlock("What the archive does") {
        PaperSheet {
          Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
            RecordField(
              "Records",
              "What was reported, what is on record and every explanation that has been seriously put forward.",
            )
            RecordField("Does not", "Rule on whether anything happened or what it was.", showRule = false)
          }
        }
      }
      TitledBlock("Sources and credits") {
        PaperSheet {
          Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
            RecordField(
              "Material",
              "Photographs, scans and clips are public-domain or openly licensed. Each names where it came from, " +
                "and openly licensed images are credited under the image.",
              showRule = false,
            )
          }
        }
        ActionRow("Licences", onOpenLicences)
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1000)
@Composable
private fun AboutScreenPreview() {
  DeskPreview {
    AboutScreen(version = "1.0", onOpenLicences = {}, onBack = {})
  }
}
