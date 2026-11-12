package com.openfile.uap.ui.casefile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openfile.uap.data.SourceLabel
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Paper
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.ScreenPadding
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TypeTitle
import kotlinx.coroutines.launch

/**
 * What a source label means, shown where the label was tapped so the reader does not have to leave the file for the
 * Method screen. [inThisFile] lists the evidence in the open file that carries the label.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceLabelSheet(label: SourceLabel, inThisFile: List<String>, onDismiss: () -> Unit) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    shape = RectangleShape,
    containerColor = Paper,
    contentColor = Ink,
    tonalElevation = 0.dp,
    scrimColor = Ink.copy(alpha = 0.5f),
    dragHandle = null,
  ) {
    Box(Modifier.fillMaxWidth().height(2.dp).background(Ink))
    Column(
      Modifier.verticalScroll(rememberScrollState()).padding(start = ScreenPadding, end = ScreenPadding, top = 20.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      MonoLabel("Source label")
      TypeTitle(label.label, size = 24.sp)
      Text(label.meaning, style = MaterialTheme.typography.bodyLarge, color = Ink)
      PaperSheet(dashed = true) {
        Text(
          "A label says where an item comes from, not whether it is true.",
          modifier = Modifier.padding(12.dp),
          style = MaterialTheme.typography.bodySmall,
          color = InkBody,
        )
      }
      if (inThisFile.isNotEmpty()) {
        Text("In this file: ${inThisFile.joinToString(" · ")}", style = MaterialTheme.typography.labelMedium, color = InkSoft)
      }
      TapRow(
        // Let the sheet slide away before it leaves the composition.
        onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } },
        modifier = Modifier.padding(top = 4.dp).defaultMinSize(minHeight = 52.dp).background(Ink),
      ) {
        Text(
          "Close",
          modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.labelLarge,
          color = Paper,
        )
      }
    }
  }
}
