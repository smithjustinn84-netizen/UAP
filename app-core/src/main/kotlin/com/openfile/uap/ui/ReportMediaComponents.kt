package com.openfile.uap.ui

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.openfile.uap.R
import com.openfile.uap.data.MediaClip
import com.openfile.uap.data.MediaFigure
import com.openfile.uap.data.MediaImage
import com.openfile.uap.data.MediaKind
import com.openfile.uap.data.ReportMedia
import com.openfile.uap.data.TranscriptCue
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.theme.StampRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

/**
 * Full archival media exhibit card used in the Media tab and inline on Evidence cards.
 */
@Composable
fun ReportMediaCard(
  media: ReportMedia,
  evidenceTitle: String? = null,
  onOpenEvidence: (() -> Unit)? = null,
) {
  val uriHandler = LocalUriHandler.current
  PaperSheet {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("${media.id} · ${media.kind.label}", color = Ink)
      MonoLabel(media.sourceLabel, color = Ink)
    }
    SolidRule(color = Ink)
    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      TypeTitle(media.title, size = 19.sp)
      when (media.kind) {
        MediaKind.Diagram -> ArchivalDiagramExhibit(media)
        MediaKind.Photo -> ArchivalPhotoExhibit(media)
        MediaKind.Audio -> ArchivalAudioExhibit(media)
        MediaKind.Video -> ArchivalVideoExhibit(media)
        MediaKind.Document -> ArchivalDocumentExhibit(media)
      }
      Text(media.caption, style = MaterialTheme.typography.bodyMedium, color = Ink)
      DashedRule()
      Text(
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Provenance: ") }
          append(media.provenance)
        },
        style = MaterialTheme.typography.bodySmall,
        color = InkBody,
      )
    }
    // Linked recordings carry their own play button, so only bundled or static exhibits get this row.
    val linkedRecording = media.kind in setOf(MediaKind.Audio, MediaKind.Video) && media.clip == null
    if (media.sourceUrl != null && !linkedRecording) {
      SolidRule(color = Ink)
      TapRow(
        onClick = { runCatching { uriHandler.openUri(media.sourceUrl) } },
        onClickLabel = "Open official archive record",
      ) {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            "Open public archive source",
            style = MaterialTheme.typography.labelMedium,
            color = Ink,
          )
          Chevron()
        }
      }
    }
    if (evidenceTitle != null && onOpenEvidence != null) {
      SolidRule(color = Ink)
      TapRow(onClick = onOpenEvidence, onClickLabel = "See linked evidence item") {
        Row(
          Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            "Filed under ${media.evidenceId} · $evidenceTitle",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            color = Ink,
          )
          Chevron()
        }
      }
    }
  }
}

@DrawableRes
private fun MediaFigure?.toDrawableRes(): Int? =
  when (this) {
    MediaFigure.ArnoldOriginalSketch -> R.drawable.plate_arnold_sketch
    MediaFigure.PhoenixEstrellasPhoto -> R.drawable.plate_phoenix_estrellas
    else -> null
  }

@DrawableRes
private fun MediaImage.toDrawableRes(): Int =
  when (this) {
    MediaImage.HaltMemo -> R.drawable.scan_halt_memo
    MediaImage.RoswellDailyRecord -> R.drawable.scan_roswell_daily_record
    MediaImage.RameyDuboseDebris -> R.drawable.photo_roswell_ramey_dubose
    MediaImage.MogulReflectorTrain -> R.drawable.fig_mogul_balloon_train
    MediaImage.KecksburgReplica -> R.drawable.photo_kecksburg_replica
    MediaImage.SamfordMinutes -> R.drawable.scan_samford_minutes
  }

@Composable
private fun ArchivalPhotoExhibit(media: ReportMedia) {
  val realImage = media.image?.toDrawableRes()
  val resId = realImage ?: media.figure.toDrawableRes()
  var viewing by rememberSaveable(media.id) { mutableStateOf(false) }
  Column(
    Modifier.fillMaxWidth().background(Manila).border(1.dp, Ink).padding(8.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(
      Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("ARCHIVAL PLATE · ${media.id}", color = Ink)
      MonoLabel(if (realImage != null) "TAP TO ENLARGE" else "DRAWN PLATE", color = InkSoft)
    }
    if (resId != null) {
      Image(
        painter = painterResource(resId),
        contentDescription = media.title,
        contentScale = ContentScale.FillWidth,
        modifier =
          if (realImage != null) {
            Modifier.fillMaxWidth().border(1.dp, Ink).clickable(onClickLabel = "Enlarge photograph") { viewing = true }
          } else {
            Modifier.fillMaxWidth().aspectRatio(16f / 9f).border(1.dp, Ink)
          },
      )
    }
  }
  if (viewing && realImage != null) ZoomableImageDialog(realImage, media.title, onDismiss = { viewing = false })
}

/** Full-screen viewer with pinch-to-zoom and pan, for reading scans on a phone. */
@Composable
private fun ZoomableImageDialog(@DrawableRes resId: Int, description: String, onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize().background(Ink)) {
      Image(
        painter = painterResource(resId),
        contentDescription = description,
        contentScale = ContentScale.Fit,
        modifier =
          Modifier.fillMaxSize()
            .pointerInput(Unit) {
              detectTransformGestures { _, pan, zoom, _ ->
                scale = (scale * zoom).coerceIn(1f, 6f)
                offset = if (scale == 1f) Offset.Zero else offset + pan
              }
            }
            .pointerInput(Unit) {
              detectTapGestures(onDoubleTap = {
                scale = if (scale > 1f) 1f else 2.5f
                offset = Offset.Zero
              })
            }
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
              translationX = offset.x
              translationY = offset.y
            },
      )
      Box(
        Modifier.align(Alignment.TopEnd)
          .padding(16.dp)
          .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .clickable(role = Role.Button, onClickLabel = "Close", onClick = onDismiss)
          .background(Paper)
          .border(1.dp, Ink)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
      ) {
        MonoLabel("CLOSE [X]", color = Ink)
      }
      MonoLabel(
        "PINCH TO ZOOM · DOUBLE-TAP TO RESET",
        color = Paper,
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
      )
    }
  }
}

@Composable
private fun ArchivalDiagramExhibit(media: ReportMedia) {
  Column(
    Modifier.fillMaxWidth().background(Paper).border(1.dp, Ink),
  ) {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("FIG. ${media.id} · TECHNICAL RECONSTRUCTION", color = Ink)
      MonoLabel("NOT TO SCALE", color = InkSoft)
    }
    SolidRule(color = Ink)
    Canvas(
      Modifier.fillMaxWidth()
        .height(176.dp)
        .semantics { contentDescription = "${media.title}. ${media.caption}" },
    ) {
      drawDraftingGrid()
      when (media.figure) {
        MediaFigure.ArnoldCascadeTrack -> drawArnoldCascadeDiagram()
        MediaFigure.MogulBalloonTrain -> drawMogulTrainDiagram()
        MediaFigure.WashingtonRadarScope -> drawWashingtonRadarDiagram()
        MediaFigure.KecksburgAcornProfile -> drawKecksburgAcornProfile()
        MediaFigure.ShagHarbourImpactChart -> drawShagHarbourDiagram()
        MediaFigure.BerwynSeismograph -> drawBerwynSeismographDiagram()
        MediaFigure.RendleshamLandingTriangle -> drawRendleshamTriangleDiagram()
        MediaFigure.PhoenixVFormation -> drawPhoenixFormationDiagram()
        MediaFigure.NimitzAltitudeDrop -> drawNimitzDropDiagram()
        else -> drawArnoldCascadeDiagram()
      }
    }
    SolidRule(color = Rule)
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      MonoLabel(diagramPrimaryLegend(media.figure), color = InkSoft)
      MonoLabel(diagramSecondaryLegend(media.figure), color = StampRed)
    }
  }
}

private fun diagramPrimaryLegend(figure: MediaFigure?): String =
  when (figure) {
    MediaFigure.ArnoldCascadeTrack -> "DIST: 47 MI (RAINIER → ADAMS)"
    MediaFigure.MogulBalloonTrain -> "LENGTH: ~600 FT · NEOPRENE + ML-307"
    MediaFigure.WashingtonRadarScope -> "RANGE RINGS: 10 / 20 / 30 NM"
    MediaFigure.KecksburgAcornProfile -> "EST. HULL: 10–12 FT · BRONZE TONE"
    MediaFigure.ShagHarbourImpactChart -> "OFFSHORE: 250 M–½ MI · ACCOUNTS DIFFER"
    MediaFigure.BerwynSeismograph -> "c. 20:38 · MAGNITUDE 3.5 (IGS)"
    MediaFigure.RendleshamLandingTriangle -> "BASE: ~2.5 M · PODS: 7\" DIA × 1.5\" DEEP"
    MediaFigure.PhoenixVFormation -> "TRACK: NNW → SSE · ~200 MI CORRIDOR"
    MediaFigure.NimitzAltitudeDrop -> "ALT: 80,000 FT → 20,000 FT → 50 FT"
    else -> "ARCHIVAL PLOT"
  }

private fun diagramSecondaryLegend(figure: MediaFigure?): String =
  when (figure) {
    MediaFigure.ArnoldCascadeTrack -> "TIME: 1M 42S"
    MediaFigure.MogulBalloonTrain -> "NYU FLIGHT #4"
    MediaFigure.WashingtonRadarScope -> "7 TARGETS LOGGED"
    MediaFigure.KecksburgAcornProfile -> "RAISED BAND"
    MediaFigure.ShagHarbourImpactChart -> "FOAM ~80 FT WIDE"
    MediaFigure.BerwynSeismograph -> "ILLUSTRATIVE TRACE"
    MediaFigure.RendleshamLandingTriangle -> "PEAK: 0.1 mR/HR"
    MediaFigure.PhoenixVFormation -> "19:50–22:00 MST"
    MediaFigure.NimitzAltitudeDrop -> "DROP: ~0.78 SEC"
    else -> "EXHIBIT"
  }

@RawRes
private fun MediaClip.toRawRes(): Int =
  when (this) {
    MediaClip.NimitzFlir1 -> R.raw.video_nimitz_flir1
    MediaClip.RooseveltGimbal -> R.raw.video_roosevelt_gimbal
    MediaClip.RooseveltGoFast -> R.raw.video_roosevelt_gofast
    MediaClip.WashingtonSamford -> R.raw.video_washington_samford
  }

@Composable
private fun ArchivalVideoExhibit(media: ReportMedia) {
  val clip = media.clip
  if (clip != null) RecordedVideoExhibit(media, clip) else LinkedVideoExhibit(media)
}

/** Media3 playback of a bundled clip, exposed as Compose state for the archive-styled controls. */
@Stable
private class ClipPlayback(val player: ExoPlayer, val totalSeconds: Int) {
  var playing by mutableStateOf(false)
  var positionMs by mutableLongStateOf(0L)
  val elapsed: Int get() = (positionMs / 1000).toInt().coerceIn(0, totalSeconds)

  fun toggle() = if (playing) player.pause() else player.play()

  fun seekToMs(target: Long) {
    val clamped = target.coerceIn(0L, totalSeconds * 1000L)
    player.seekTo(clamped)
    positionMs = clamped
  }

  /** Jumps ahead 10 s, wrapping to the start at the end of the tape like the old simulated players. */
  fun stepForward() = seekToMs(if (positionMs + 10_000 >= totalSeconds * 1000L) 0L else positionMs + 10_000)
}

@Composable
private fun rememberClipPlayback(media: ReportMedia, clip: MediaClip): ClipPlayback {
  val context = LocalContext.current
  var savedPositionMs by rememberSaveable(media.id) { mutableLongStateOf(0L) }
  val playback =
    remember(clip) {
      val player =
        ExoPlayer.Builder(context)
          .setAudioAttributes(AudioAttributes.DEFAULT, true)
          .build()
          .apply {
            setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${clip.toRawRes()}"))
            prepare()
          }
      ClipPlayback(player, media.durationSeconds ?: 60)
    }

  DisposableEffect(playback) {
    val player = playback.player
    val listener =
      object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
          playback.playing = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
          if (playbackState == Player.STATE_ENDED) {
            player.pause()
            playback.seekToMs(0L)
          }
        }
      }
    player.addListener(listener)
    if (savedPositionMs > 0) playback.seekToMs(savedPositionMs)
    onDispose {
      savedPositionMs = player.currentPosition
      player.removeListener(listener)
      player.release()
    }
  }
  LifecycleEventEffect(Lifecycle.Event.ON_STOP) { playback.player.pause() }
  LaunchedEffect(playback, playback.playing) {
    while (playback.playing) {
      playback.positionMs = playback.player.currentPosition
      delay(250)
    }
  }
  return playback
}

/** Plays a bundled video clip. The control bar and transcript keep the archive styling. */
@Composable
private fun RecordedVideoExhibit(media: ReportMedia, clip: MediaClip) {
  val playback = rememberClipPlayback(media, clip)
  val player = playback.player
  val totalSeconds = playback.totalSeconds
  val playing = playback.playing
  val elapsed = playback.elapsed
  Column(Modifier.fillMaxWidth().border(1.dp, Ink).background(Ink)) {
    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentAlignment = Alignment.Center) {
      AndroidView(
        factory = { ctx ->
          PlayerView(ctx).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShutterBackgroundColor(Ink.toArgb())
            contentDescription = media.title
          }
        },
        update = { it.player = player },
        onRelease = { it.player = null },
        modifier = Modifier.fillMaxSize(),
      )
      Box(
        Modifier.align(Alignment.BottomStart)
          .padding(8.dp)
          .background(Ink.copy(alpha = 0.85f))
          .border(1.dp, Paper)
          .padding(horizontal = 8.dp, vertical = 3.dp),
      ) {
        MonoLabel(
          text = "${if (playing) "PLAY" else "STILL"} · ${formatSeconds(elapsed)} / ${formatSeconds(totalSeconds)}",
          color = Paper,
        )
      }
    }
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 10.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .clickable(
            role = Role.Button,
            onClickLabel = if (playing) "Pause video" else "Play video",
            onClick = playback::toggle,
          )
          .background(Ink)
          .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
      ) {
        MonoLabel(if (playing) "PAUSE [||]" else "PLAY [>]", color = Paper)
      }
      MonoLabel("TAPE ${formatSeconds(elapsed)} / ${formatSeconds(totalSeconds)}", color = Ink)
      Box(
        Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
          .clickable(
            role = Role.Button,
            onClickLabel = "Step forward 10 seconds",
            onClick = playback::stepForward,
          )
          .border(1.dp, Ink)
          .background(Paper)
          .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
      ) {
        MonoLabel("+10S >>", color = Ink)
      }
    }
    if (media.transcript.isNotEmpty()) {
      SolidRule(color = Ink)
      TranscriptList(
        cues = media.transcript,
        elapsedSeconds = elapsed,
        onSeek = { targetSec -> playback.seekToMs(targetSec * 1000L) },
      )
    }
  }
}

/** Where an external recording plays, for button labels. */
private fun externalHost(url: String): String =
  when {
    "youtube.com" in url || "youtu.be" in url -> "YouTube"
    "archive.org" in url -> "the Internet Archive"
    "c-span.org" in url -> "C-SPAN"
    else -> "the source site"
  }

/** Archive-styled button that opens [url] outside the app. */
@Composable
private fun ExternalPlayRow(url: String, verb: String) {
  val uriHandler = LocalUriHandler.current
  val host = externalHost(url)
  Row(
    Modifier.fillMaxWidth().background(Manila).padding(horizontal = 10.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .clickable(
          role = Role.Button,
          onClickLabel = "$verb on $host",
          onClick = { runCatching { uriHandler.openUri(url) } },
        )
        .background(Ink)
        .padding(horizontal = 14.dp, vertical = 8.dp),
      contentAlignment = Alignment.Center,
    ) {
      MonoLabel("${verb.uppercase()} ON ${host.removePrefix("the ").uppercase()} [>]", color = Paper)
    }
    MonoLabel("NOT BUNDLED", color = Ink)
  }
}

/** A video that is not bundled: shows its still plate, if any, and opens [ReportMedia.sourceUrl] to watch it. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinkedVideoExhibit(media: ReportMedia) {
  val resId = media.figure.toDrawableRes()
  Column(Modifier.fillMaxWidth().border(1.dp, Ink).background(Ink)) {
    if (media.telemetryLabels.isNotEmpty()) {
      FlowRow(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        media.telemetryLabels.forEach { label -> MonoLabel(label, color = Paper) }
      }
    }
    if (resId != null) {
      Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentAlignment = Alignment.Center) {
        Image(
          painter = painterResource(resId),
          contentDescription = media.title,
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.fillMaxSize(),
        )
        Box(
          Modifier.align(Alignment.BottomStart)
            .padding(8.dp)
            .background(Ink.copy(alpha = 0.85f))
            .border(1.dp, Paper)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
          MonoLabel("STILL · EXTERNAL VIDEO", color = Paper)
        }
      }
    } else {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        MonoLabel("EXTERNAL VIDEO · ${media.id}", color = Paper)
        media.durationSeconds?.let { MonoLabel("RUNTIME ${formatSeconds(it)}", color = Paper) }
      }
    }
    media.sourceUrl?.let { ExternalPlayRow(it, verb = "Watch") }
    if (media.transcript.isNotEmpty()) {
      SolidRule(color = Ink)
      TranscriptList(cues = media.transcript, elapsedSeconds = 0, onSeek = null)
    }
  }
}

@Composable
private fun ArchivalAudioExhibit(media: ReportMedia) {
  // No audio is bundled yet; recordings open at their source.
  Column(Modifier.fillMaxWidth().border(1.dp, Ink).background(Paper)) {
    Row(
      Modifier.fillMaxWidth().background(Manila).padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("MAGNETIC TAPE LOG · ${media.id}", color = Ink)
      media.durationSeconds?.let { MonoLabel("RUNTIME ${formatSeconds(it)}", color = Ink) }
    }
    SolidRule(color = Ink)
    media.sourceUrl?.let { ExternalPlayRow(it, verb = "Listen") }
    if (media.transcript.isNotEmpty()) {
      SolidRule(color = Ink)
      TranscriptList(cues = media.transcript, elapsedSeconds = 0, onSeek = null)
    }
  }
}

@Composable
private fun TranscriptList(
  cues: List<TranscriptCue>,
  elapsedSeconds: Int,
  onSeek: ((Int) -> Unit)?,
) {
  val cueSeconds = cues.map { parseTimestampSeconds(it.timestamp) }
  val activeIndex = cueSeconds.indexOfLast { it <= elapsedSeconds }.coerceAtLeast(0)

  Column(Modifier.fillMaxWidth().background(Paper).padding(vertical = 4.dp)) {
    cues.forEachIndexed { index, cue ->
      val isActive = onSeek != null && index == activeIndex
      val rowModifier = Modifier.background(if (isActive) Manila.copy(alpha = 0.55f) else Color.Transparent)
      val cueRow: @Composable () -> Unit = {
          Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
          ) {
            MonoLabel(
              cue.timestamp,
              color = if (isActive) StampRed else InkSoft,
              modifier = Modifier.width(44.dp).padding(top = 2.dp),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              MonoLabel(cue.speaker, color = Ink)
              Text(cue.text, style = MaterialTheme.typography.bodySmall, color = InkBody)
            }
          }
      }
      if (onSeek != null) {
        TapRow(onClick = { onSeek(cueSeconds[index]) }, onClickLabel = "Jump to ${cue.timestamp}", modifier = rowModifier, content = cueRow)
      } else {
        Box(rowModifier) { cueRow() }
      }
      if (index < cues.lastIndex) DashedRule()
    }
  }
}

@Composable
private fun ArchivalDocumentExhibit(media: ReportMedia) {
  val scan = media.image?.toDrawableRes()
  var viewing by rememberSaveable(media.id) { mutableStateOf(false) }
  Column(
    Modifier.fillMaxWidth().background(Manila.copy(alpha = 0.35f)).border(1.dp, Ink),
  ) {
    if (scan != null) {
      Box(
        Modifier.fillMaxWidth()
          .height(220.dp)
          .background(Paper)
          .clickable(onClickLabel = "View full scan") { viewing = true },
      ) {
        Image(
          painter = painterResource(scan),
          contentDescription = "Scan of ${media.title}",
          contentScale = ContentScale.Crop,
          alignment = Alignment.TopCenter,
          modifier = Modifier.fillMaxSize(),
        )
        Box(
          Modifier.align(Alignment.BottomStart)
            .padding(8.dp)
            .background(Ink.copy(alpha = 0.85f))
            .border(1.dp, Paper)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
          MonoLabel("FACSIMILE · TAP TO READ FULL SCAN", color = Paper)
        }
      }
      SolidRule(color = Ink)
      if (viewing) ZoomableImageDialog(scan, "Scan of ${media.title}", onDismiss = { viewing = false })
    }
    if (media.documentHeader.isNotEmpty()) {
      Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        media.documentHeader.forEach { (field, value) ->
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("$field:", color = InkSoft, modifier = Modifier.width(86.dp))
            Text(value, style = MaterialTheme.typography.labelMedium, color = Ink)
          }
        }
      }
      SolidRule(color = Ink)
    }
    Column(
      Modifier.fillMaxWidth().background(Paper).padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      media.excerpt.forEach { paragraph ->
        Text(
          text = "“$paragraph”",
          style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
          color = Ink,
        )
      }
    }
  }
}

private fun formatSeconds(sec: Int): String {
  val m = sec / 60
  val s = sec % 60
  return "%02d:%02d".format(m, s)
}

private fun parseTimestampSeconds(timestamp: String): Int {
  val parts = timestamp.split(":")
  if (parts.size != 2) return 0
  return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
}

// --- Archival Canvas Diagram Renderers ---

private fun DrawScope.drawDraftingGrid() {
  val step = 22.dp.toPx()
  var x = step
  while (x < size.width) {
    drawLine(Rule.copy(alpha = 0.45f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
    x += step
  }
  var y = step
  while (y < size.height) {
    drawLine(Rule.copy(alpha = 0.45f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
    y += step
  }
}

private fun DrawScope.drawArnoldCascadeDiagram() {
  val w = size.width
  val h = size.height
  val stroke = 2.dp.toPx()
  // Mt. Rainier (left peak) and Mt. Adams (right peak)
  val rainier = Path().apply {
    moveTo(w * 0.08f, h * 0.82f)
    lineTo(w * 0.22f, h * 0.24f)
    lineTo(w * 0.36f, h * 0.82f)
    close()
  }
  val adams = Path().apply {
    moveTo(w * 0.62f, h * 0.82f)
    lineTo(w * 0.77f, h * 0.32f)
    lineTo(w * 0.92f, h * 0.82f)
    close()
  }
  drawPath(rainier, color = Manila)
  drawPath(rainier, color = Ink, style = Stroke(width = stroke))
  drawPath(adams, color = Manila)
  drawPath(adams, color = Ink, style = Stroke(width = stroke))

  // Dashed chain trajectory of 9 echelon objects across the crest line
  val dash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
  drawLine(
    color = StampRed,
    start = Offset(w * 0.14f, h * 0.48f),
    end = Offset(w * 0.86f, h * 0.56f),
    strokeWidth = 1.6.dp.toPx(),
    pathEffect = dash,
  )
  for (i in 0 until 9) {
    val t = i / 8f
    val cx = w * (0.18f + t * 0.64f)
    val cy = h * (0.48f + t * 0.08f + (if (i % 2 == 0) -0.03f else 0.03f))
    drawCircle(color = Ink, radius = 3.5.dp.toPx(), center = Offset(cx, cy))
  }
}

private fun DrawScope.drawMogulTrainDiagram() {
  val w = size.width
  val h = size.height
  // Diagonal tether line representing 600-ft array
  val start = Offset(w * 0.12f, h * 0.82f)
  val end = Offset(w * 0.88f, h * 0.20f)
  drawLine(Ink, start, end, strokeWidth = 1.5.dp.toPx())
  // Neoprene sounding balloons along upper tether
  for (i in 0 until 5) {
    val t = 0.45f + i * 0.11f
    val bx = start.x + (end.x - start.x) * t
    val by = start.y + (end.y - start.y) * t - 12.dp.toPx()
    drawCircle(color = Manila, radius = 7.dp.toPx(), center = Offset(bx, by))
    drawCircle(color = Ink, radius = 7.dp.toPx(), center = Offset(bx, by), style = Stroke(width = 1.5.dp.toPx()))
    drawLine(Ink, Offset(bx, by + 7.dp.toPx()), Offset(bx, by + 12.dp.toPx()), strokeWidth = 1.dp.toPx())
  }
  // ML-307 corner radar reflectors (diamonds)
  listOf(0.24f, 0.36f).forEach { t ->
    val rx = start.x + (end.x - start.x) * t
    val ry = start.y + (end.y - start.y) * t + 10.dp.toPx()
    val r = 9.dp.toPx()
    val diamond = Path().apply {
      moveTo(rx, ry - r)
      lineTo(rx + r, ry)
      lineTo(rx, ry + r)
      lineTo(rx - r, ry)
      close()
    }
    drawPath(diamond, color = StampRed, style = Stroke(width = 1.6.dp.toPx()))
  }
}

private fun DrawScope.drawWashingtonRadarDiagram() {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val maxR = size.minDimension * 0.42f
  listOf(0.35f, 0.68f, 1f).forEach { frac ->
    drawCircle(color = Ink, radius = maxR * frac, center = Offset(cx, cy), style = Stroke(width = 1.2.dp.toPx()))
  }
  drawLine(InkSoft, Offset(cx - maxR, cy), Offset(cx + maxR, cy), strokeWidth = 1.dp.toPx())
  drawLine(InkSoft, Offset(cx, cy - maxR), Offset(cx, cy + maxR), strokeWidth = 1.dp.toPx())
  // 7 primary radar returns in SW and NE sectors
  val targets = listOf(
    Offset(-0.45f, -0.30f),
    Offset(-0.32f, -0.48f),
    Offset(-0.20f, -0.22f),
    Offset(0.28f, -0.35f),
    Offset(0.52f, 0.18f),
    Offset(0.35f, 0.44f),
    Offset(-0.38f, 0.36f),
  )
  targets.forEach { rel ->
    val pt = Offset(cx + rel.x * maxR, cy + rel.y * maxR)
    drawCircle(color = StampRed, radius = 4.dp.toPx(), center = pt)
    drawLine(StampRed, pt, Offset(pt.x + 8.dp.toPx(), pt.y - 5.dp.toPx()), strokeWidth = 1.4.dp.toPx())
  }
}

private fun DrawScope.drawKecksburgAcornProfile() {
  val cx = size.width / 2f
  val cy = size.height * 0.48f
  val w = size.width * 0.34f
  val h = size.height * 0.56f
  val acorn = Path().apply {
    moveTo(cx, cy - h * 0.55f)
    cubicTo(cx + w * 0.65f, cy - h * 0.45f, cx + w * 0.55f, cy + h * 0.25f, cx + w * 0.42f, cy + h * 0.38f)
    lineTo(cx - w * 0.42f, cy + h * 0.38f)
    cubicTo(cx - w * 0.55f, cy + h * 0.25f, cx - w * 0.65f, cy - h * 0.45f, cx, cy - h * 0.55f)
    close()
  }
  drawPath(acorn, color = Manila)
  drawPath(acorn, color = Ink, style = Stroke(width = 2.dp.toPx()))
  // Raised lower band with stylized markings
  val bandTop = cy + h * 0.22f
  val bandBottom = cy + h * 0.38f
  drawRect(
    color = StampRed,
    topLeft = Offset(cx - w * 0.46f, bandTop),
    size = Size(w * 0.92f, bandBottom - bandTop),
    style = Stroke(width = 1.8.dp.toPx()),
  )
  for (i in -3..3) {
    val mx = cx + i * (w * 0.11f)
    drawLine(StampRed, Offset(mx, bandTop + 4.dp.toPx()), Offset(mx, bandBottom - 4.dp.toPx()), strokeWidth = 1.4.dp.toPx())
  }
}

private fun DrawScope.drawShagHarbourDiagram() {
  val w = size.width
  val h = size.height
  // Sea surface line
  val seaY = h * 0.58f
  drawLine(Ink, Offset(0f, seaY), Offset(w, seaY), strokeWidth = 2.dp.toPx())
  // 45-degree descent trajectory with 4 lights
  val start = Offset(w * 0.14f, h * 0.16f)
  val impact = Offset(w * 0.52f, seaY)
  drawLine(
    color = StampRed,
    start = start,
    end = impact,
    strokeWidth = 2.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
  )
  for (i in 0 until 4) {
    val t = i / 4f
    val lx = start.x + (impact.x - start.x) * t
    val ly = start.y + (impact.y - start.y) * t
    drawCircle(color = StampRed, radius = 3.5.dp.toPx(), center = Offset(lx, ly))
  }
  // Foam patch reported on the surface (schematic, not to scale)
  drawOval(
    color = Manila,
    topLeft = Offset(w * 0.48f, seaY - 8.dp.toPx()),
    size = Size(w * 0.36f, 18.dp.toPx()),
  )
  drawOval(
    color = Ink,
    topLeft = Offset(w * 0.48f, seaY - 8.dp.toPx()),
    size = Size(w * 0.36f, 18.dp.toPx()),
    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))),
  )
}

private fun DrawScope.drawBerwynSeismographDiagram() {
  val w = size.width
  val h = size.height
  val midY = h * 0.52f
  val path = Path().apply {
    moveTo(0f, midY)
    val steps = 64
    for (i in 1..steps) {
      val t = i / steps.toFloat()
      val x = w * t
      val envelope =
        when {
          t < 0.35f -> 0.04f
          t < 0.48f -> 0.85f * (1f - (t - 0.35f) / 0.13f) + 0.15f
          t < 0.72f -> 0.45f * (1f - (t - 0.48f) / 0.24f)
          else -> 0.05f
        }
      val dy = sin(i * 1.9f) * (h * 0.40f) * envelope
      lineTo(x, midY + dy)
    }
  }
  drawPath(path, color = Ink, style = Stroke(width = 1.6.dp.toPx()))
  // P-wave onset marker
  val onsetX = w * 0.35f
  drawLine(
    color = StampRed,
    start = Offset(onsetX, h * 0.10f),
    end = Offset(onsetX, h * 0.90f),
    strokeWidth = 1.5.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())),
  )
}

private fun DrawScope.drawRendleshamTriangleDiagram() {
  val cx = size.width / 2f
  val cy = size.height * 0.54f
  val r = size.minDimension * 0.34f
  val p1 = Offset(cx, cy - r)
  val p2 = Offset(cx - r * 0.92f, cy + r * 0.65f)
  val p3 = Offset(cx + r * 0.92f, cy + r * 0.65f)

  val tri = Path().apply {
    moveTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    close()
  }
  drawPath(
    path = tri,
    color = Ink,
    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))),
  )
  listOf(p1, p2, p3).forEach { pod ->
    drawCircle(color = Manila, radius = 9.dp.toPx(), center = pod)
    drawCircle(color = StampRed, radius = 9.dp.toPx(), center = pod, style = Stroke(width = 2.dp.toPx()))
    drawCircle(color = Ink, radius = 2.5.dp.toPx(), center = pod)
  }
}

private fun DrawScope.drawPhoenixFormationDiagram() {
  val w = size.width
  val h = size.height
  val cx = w * 0.48f
  val apexY = h * 0.22f
  val wingSpan = w * 0.34f
  val wingDrop = h * 0.44f
  // V-chevron outline
  val vPath = Path().apply {
    moveTo(cx - wingSpan, apexY + wingDrop)
    lineTo(cx, apexY)
    lineTo(cx + wingSpan, apexY + wingDrop)
  }
  drawPath(vPath, color = Ink, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
  // 5 lights along the V-formation
  listOf(-1f, -0.5f, 0f, 0.5f, 1f).forEach { f ->
    val lx = cx + f * wingSpan
    val ly = apexY + kotlin.math.abs(f) * wingDrop
    drawCircle(color = Manila, radius = 6.dp.toPx(), center = Offset(lx, ly))
    drawCircle(color = StampRed, radius = 6.dp.toPx(), center = Offset(lx, ly), style = Stroke(width = 1.8.dp.toPx()))
  }
  // Heading arrow southward
  drawLine(
    color = Dash,
    start = Offset(cx, apexY + 14.dp.toPx()),
    end = Offset(cx, h * 0.88f),
    strokeWidth = 1.5.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx())),
  )
}

private fun DrawScope.drawNimitzDropDiagram() {
  val w = size.width
  val h = size.height
  val topY = h * 0.16f
  val midY = h * 0.62f
  val seaY = h * 0.84f
  // Sea level baseline
  drawLine(Ink, Offset(w * 0.08f, seaY), Offset(w * 0.92f, seaY), strokeWidth = 2.dp.toPx())
  // 80,000 ft track -> vertical drop -> 20,000 ft hold -> drop to sea level
  val track = Path().apply {
    moveTo(w * 0.12f, topY)
    lineTo(w * 0.38f, topY)
    lineTo(w * 0.42f, midY)
    lineTo(w * 0.66f, midY)
    lineTo(w * 0.70f, seaY - 6.dp.toPx())
    lineTo(w * 0.88f, seaY - 6.dp.toPx())
  }
  drawPath(track, color = StampRed, style = Stroke(width = 2.2.dp.toPx()))
  listOf(
    Offset(w * 0.38f, topY),
    Offset(w * 0.42f, midY),
    Offset(w * 0.70f, seaY - 6.dp.toPx()),
  ).forEach { pt ->
    drawCircle(color = Ink, radius = 4.dp.toPx(), center = pt)
  }
}
