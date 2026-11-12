package com.openfile.uap.ui.casefile

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.openfile.uap.R
import com.openfile.uap.data.Archive
import com.openfile.uap.data.MediaClip
import com.openfile.uap.data.MediaFigure
import com.openfile.uap.data.MediaImage
import com.openfile.uap.data.MediaKind
import com.openfile.uap.data.ReportMedia
import com.openfile.uap.data.SourceLabel
import com.openfile.uap.data.TranscriptCue
import com.openfile.uap.theme.Dash
import com.openfile.uap.theme.Ink
import com.openfile.uap.theme.InkBody
import com.openfile.uap.theme.InkSoft
import com.openfile.uap.theme.Manila
import com.openfile.uap.theme.Paper
import com.openfile.uap.theme.Rule
import com.openfile.uap.theme.StampRed
import com.openfile.uap.ui.CardHeader
import com.openfile.uap.ui.Chevron
import com.openfile.uap.ui.DashedRule
import com.openfile.uap.ui.DeskPreview
import com.openfile.uap.ui.LeadText
import com.openfile.uap.ui.MonoLabel
import com.openfile.uap.ui.PaperSheet
import com.openfile.uap.ui.SolidRule
import com.openfile.uap.ui.TapRow
import com.openfile.uap.ui.TypeTitle
import kotlin.math.sin
import kotlinx.coroutines.delay

/**
 * Full archival media exhibit card used in the Media tab and inline on Evidence cards.
 */
@Composable
fun ReportMediaCard(
  media: ReportMedia,
  onLabelClick: (SourceLabel) -> Unit,
  evidenceTitle: String? = null,
  onOpenEvidence: (() -> Unit)? = null,
) {
  val uriHandler = LocalUriHandler.current
  PaperSheet {
    CardHeader("${media.id} · ${media.kind.label}", media.sourceLabel.label, onEndClick = { onLabelClick(media.sourceLabel) })
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
      LeadText("Provenance", media.provenance)
    }
    // Linked recordings carry their own play button, so only bundled or static exhibits get this row.
    val linkedRecording = (media.kind == MediaKind.Audio || media.kind == MediaKind.Video) && media.clip == null
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

    // Drawn figures have no drawable.
    else -> null
  }

@DrawableRes
internal fun MediaImage.toDrawableRes(): Int =
  when (this) {
    MediaImage.HaltMemo -> R.drawable.scan_halt_memo
    MediaImage.RoswellDailyRecord -> R.drawable.scan_roswell_daily_record
    MediaImage.RameyDuboseDebris -> R.drawable.photo_roswell_ramey_dubose
    MediaImage.MogulReflectorTrain -> R.drawable.fig_mogul_balloon_train
    MediaImage.KecksburgReplica -> R.drawable.photo_kecksburg_replica
    MediaImage.SamfordMinutes -> R.drawable.scan_samford_minutes
    MediaImage.NurembergBroadsheet -> R.drawable.hero_nuremberg
    MediaImage.LosAngelesTimesPage -> R.drawable.hero_los_angeles
    MediaImage.NightFighterBeaufighter -> R.drawable.hero_beaufighter
    MediaImage.MountRainier -> R.drawable.hero_rainier
    MediaImage.ShagHarbour -> R.drawable.hero_shag_harbour
    MediaImage.BerwynRidge -> R.drawable.hero_berwyn
    MediaImage.UssNimitz -> R.drawable.hero_nimitz
    MediaImage.UssTheodoreRoosevelt -> R.drawable.hero_roosevelt
  }

/**
 * Width over height of the bundled file. Photographs load off the main thread, so the layout reserves their space
 * from this and not from the decoded image.
 */
private fun MediaImage.aspectRatio(): Float =
  when (this) {
    MediaImage.HaltMemo -> 1100f / 1562f
    MediaImage.RoswellDailyRecord -> 1280f / 1542f
    MediaImage.RameyDuboseDebris -> 1100f / 1445f
    MediaImage.MogulReflectorTrain -> 960f / 1017f
    MediaImage.KecksburgReplica -> 1000f / 1155f
    MediaImage.SamfordMinutes -> 1100f / 1423f
    MediaImage.NurembergBroadsheet -> 800f / 900f
    MediaImage.LosAngelesTimesPage -> 617f / 565f
    MediaImage.NightFighterBeaufighter -> 1280f / 561f
    MediaImage.MountRainier -> 1280f / 853f
    MediaImage.ShagHarbour -> 1280f / 719f
    MediaImage.BerwynRidge -> 1280f / 719f
    MediaImage.UssNimitz -> 1280f / 850f
    MediaImage.UssTheodoreRoosevelt -> 1280f / 853f
  }

@Composable
private fun ArchivalPhotoExhibit(media: ReportMedia) {
  val image = media.image
  val realImage = image?.toDrawableRes()
  val plate = media.figure.toDrawableRes()
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
    if (image != null && realImage != null) {
      BundledImage(
        resId = realImage,
        contentDescription = media.title,
        contentScale = ContentScale.FillWidth,
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(image.aspectRatio())
            .border(1.dp, Ink)
            .clickable(onClickLabel = "Enlarge photograph") { viewing = true },
      )
    } else if (plate != null) {
      Image(
        painter = painterResource(plate),
        contentDescription = media.title,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).border(1.dp, Ink),
      )
    }
  }
  if (viewing && realImage != null) ZoomableImageDialog(realImage, media.title, onDismiss = { viewing = false })
}

/** Full-screen viewer with pinch-to-zoom and pan, for reading scans on a phone. */
@Composable
internal fun ZoomableImageDialog(@DrawableRes resId: Int, description: String, onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    // Full-screen viewer: draw behind the system bars and keep the controls clear of them.
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
  ) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    // Full resolution here, so a scan stays legible when zoomed in.
    val request = remember(context, resId) { ImageRequest.Builder(context).data(resId).size(coil3.size.Size.ORIGINAL).build() }
    Box(Modifier.fillMaxSize().background(Ink)) {
      AsyncImage(
        model = request,
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
          .safeDrawingPadding()
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
        modifier = Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(bottom = 32.dp),
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
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      MonoLabel("FIG. ${media.id} · TECHNICAL RECONSTRUCTION", color = Ink, modifier = Modifier.weight(1f))
      MonoLabel("NOT TO SCALE", color = InkSoft)
    }
    SolidRule(color = Ink)
    val diagram = media.figure?.diagram()
    Spacer(
      Modifier.fillMaxWidth()
        .height(176.dp)
        .semantics { contentDescription = "${media.title}. ${media.caption}" }
        // Paths and dash effects are built once per size, not on every draw.
        .drawWithCache {
          val drawFigure = diagram?.build?.invoke(this)
          onDrawBehind {
            drawDraftingGrid()
            drawFigure?.invoke(this)
          }
        },
    )
    SolidRule(color = Rule)
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      MonoLabel(diagram?.legend ?: "ARCHIVAL PLOT", color = InkSoft, modifier = Modifier.weight(1f))
      MonoLabel(diagram?.note ?: "EXHIBIT", color = StampRed)
    }
  }
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

/**
 * Media3 playback of a bundled clip, exposed as Compose state for the archive-styled controls.
 *
 * The player is built on the first press of play, not when the exhibit is shown: a file can hold several clips, and
 * each player holds a decoder.
 */
@Stable
private class ClipPlayback(val totalSeconds: Int, initialPositionMs: Long, private val createPlayer: () -> ExoPlayer) {
  var player by mutableStateOf<ExoPlayer?>(null)
    private set
  var playing by mutableStateOf(false)
    private set
  var positionMs by mutableLongStateOf(initialPositionMs)
    private set

  // The position is polled several times a second; derived so readers recompose only when the shown second changes.
  val elapsed: Int by derivedStateOf { (positionMs / 1000).toInt().coerceIn(0, totalSeconds) }

  private val listener =
    object : Player.Listener {
      override fun onIsPlayingChanged(isPlaying: Boolean) {
        playing = isPlaying
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) {
          pause()
          seekToMs(0L)
        }
      }
    }

  private fun load(): ExoPlayer =
    createPlayer().also {
      it.addListener(listener)
      if (positionMs > 0) it.seekTo(positionMs)
      player = it
    }

  fun toggle() {
    val current = player ?: load()
    if (playing) current.pause() else current.play()
  }

  fun pause() {
    player?.pause()
  }

  fun seekToMs(target: Long) {
    val clamped = target.coerceIn(0L, totalSeconds * 1000L)
    player?.seekTo(clamped)
    positionMs = clamped
  }

  /** Jumps ahead 10 s, wrapping to the start at the end of the tape like the old simulated players. */
  fun stepForward() = seekToMs(if (positionMs + 10_000 >= totalSeconds * 1000L) 0L else positionMs + 10_000)

  fun syncPosition() {
    player?.let { positionMs = it.currentPosition }
  }

  fun release() {
    player?.let {
      it.removeListener(listener)
      it.release()
    }
    player = null
    playing = false
  }
}

@Composable
private fun rememberClipPlayback(media: ReportMedia, clip: MediaClip): ClipPlayback {
  val context = LocalContext.current
  var savedPositionMs by rememberSaveable(media.id) { mutableLongStateOf(0L) }
  val playback =
    remember(context, clip) { ClipPlayback(media.durationSeconds ?: 60, savedPositionMs) { buildClipPlayer(context, clip) } }

  DisposableEffect(playback) {
    onDispose {
      playback.syncPosition()
      savedPositionMs = playback.positionMs
      playback.release()
    }
  }
  LifecycleEventEffect(Lifecycle.Event.ON_STOP) { playback.pause() }
  LaunchedEffect(playback, playback.playing) {
    while (playback.playing) {
      playback.syncPosition()
      delay(250)
    }
  }
  return playback
}

private fun buildClipPlayer(context: Context, clip: MediaClip): ExoPlayer =
  ExoPlayer.Builder(context)
    .setAudioAttributes(AudioAttributes.DEFAULT, true)
    .build()
    .apply {
      setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/${clip.toRawRes()}"))
      prepare()
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
      if (player != null) {
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
      } else {
        MonoLabel("PRESS PLAY TO LOAD TAPE", color = Paper)
      }
      OverlayTag(
        "${if (playing) "PLAY" else "STILL"} · ${formatSeconds(elapsed)} / ${formatSeconds(totalSeconds)}",
        Modifier.align(Alignment.BottomStart),
      )
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
        OverlayTag("STILL · EXTERNAL VIDEO", Modifier.align(Alignment.BottomStart))
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
private fun TranscriptList(cues: List<TranscriptCue>, elapsedSeconds: Int, onSeek: ((Int) -> Unit)?) {
  val cueSeconds = remember(cues) { cues.map { parseTimestampSeconds(it.timestamp) } }
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
        BundledImage(
          resId = scan,
          contentDescription = "Scan of ${media.title}",
          contentScale = ContentScale.Crop,
          alignment = Alignment.TopCenter,
          modifier = Modifier.fillMaxSize(),
        )
        OverlayTag("FACSIMILE · TAP TO READ FULL SCAN", Modifier.align(Alignment.BottomStart))
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

/** A label pinned over a corner of a picture or player. */
@Composable
private fun OverlayTag(text: String, modifier: Modifier = Modifier) {
  Box(modifier.padding(8.dp).background(Ink.copy(alpha = 0.85f)).border(1.dp, Paper).padding(horizontal = 8.dp, vertical = 3.dp)) {
    MonoLabel(text, color = Paper)
  }
}

// Padded by hand rather than with String.format, which would use the locale's digits.
private fun formatSeconds(sec: Int): String = "${(sec / 60).toString().padStart(2, '0')}:${(sec % 60).toString().padStart(2, '0')}"

private fun parseTimestampSeconds(timestamp: String): Int {
  val parts = timestamp.split(":")
  if (parts.size != 2) return 0
  return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
}

// --- Archival Diagram Renderers ---
//
// Each builder allocates its paths, strokes and dash effects once for the current size and returns the draw step.

private typealias FigureDraw = DrawScope.() -> Unit

/** A drawn figure: the two legend lines under it and the builder that draws it. */
private class Diagram(val legend: String, val note: String, val build: CacheDrawScope.() -> FigureDraw)

/** Null for the photographic plates, which are drawables and not drawn here. */
private fun MediaFigure.diagram(): Diagram? =
  when (this) {
    MediaFigure.ArnoldCascadeTrack -> Diagram("DIST: 47 MI (RAINIER → ADAMS)", "TIME: 1M 42S", CacheDrawScope::arnoldCascadeDiagram)

    MediaFigure.MogulBalloonTrain -> Diagram("LENGTH: ~600 FT · NEOPRENE + ML-307", "NYU FLIGHT #4", CacheDrawScope::mogulTrainDiagram)

    MediaFigure.WashingtonRadarScope -> Diagram("RANGE RINGS: 10 / 20 / 30 NM", "7 TARGETS LOGGED", CacheDrawScope::washingtonRadarDiagram)

    MediaFigure.KecksburgAcornProfile -> Diagram("EST. HULL: 10–12 FT · BRONZE TONE", "RAISED BAND", CacheDrawScope::kecksburgAcornProfile)

    MediaFigure.ShagHarbourImpactChart ->
      Diagram("OFFSHORE: 250 M–½ MI · ACCOUNTS DIFFER", "FOAM ~80 FT WIDE", CacheDrawScope::shagHarbourDiagram)

    MediaFigure.BerwynSeismograph -> Diagram(
      "c. 20:38 · MAGNITUDE 3.5 (IGS)",
      "ILLUSTRATIVE TRACE",
      CacheDrawScope::berwynSeismographDiagram,
    )

    MediaFigure.RendleshamLandingTriangle ->
      Diagram("BASE: ~2.5 M · PODS: 7\" DIA × 1.5\" DEEP", "PEAK: 0.1 mR/HR", CacheDrawScope::rendleshamTriangleDiagram)

    MediaFigure.PhoenixVFormation -> Diagram(
      "TRACK: NNW → SSE · ~200 MI CORRIDOR",
      "19:50–22:00 MST",
      CacheDrawScope::phoenixFormationDiagram,
    )

    MediaFigure.NimitzAltitudeDrop -> Diagram("ALT: 80,000 FT → 20,000 FT → 50 FT", "DROP: ~0.78 SEC", CacheDrawScope::nimitzDropDiagram)

    MediaFigure.ArnoldOriginalSketch, MediaFigure.PhoenixEstrellasPhoto -> null
  }

private val GridLine = Rule.copy(alpha = 0.45f)

private fun DrawScope.drawDraftingGrid() {
  val step = 22.dp.toPx()
  var x = step
  while (x < size.width) {
    drawLine(GridLine, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
    x += step
  }
  var y = step
  while (y < size.height) {
    drawLine(GridLine, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
    y += step
  }
}

private fun CacheDrawScope.arnoldCascadeDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  val outline = Stroke(width = 2.dp.toPx())
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
  val dash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
  return {
    drawPath(rainier, color = Manila)
    drawPath(rainier, color = Ink, style = outline)
    drawPath(adams, color = Manila)
    drawPath(adams, color = Ink, style = outline)

    // Dashed chain trajectory of 9 echelon objects across the crest line
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
}

private fun CacheDrawScope.mogulTrainDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  // Diagonal tether line representing 600-ft array
  val start = Offset(w * 0.12f, h * 0.82f)
  val end = Offset(w * 0.88f, h * 0.20f)
  val balloonOutline = Stroke(width = 1.5.dp.toPx())
  // ML-307 corner radar reflectors (diamonds)
  val reflectorOutline = Stroke(width = 1.6.dp.toPx())
  val reflectors =
    listOf(0.24f, 0.36f).map { t ->
      val rx = start.x + (end.x - start.x) * t
      val ry = start.y + (end.y - start.y) * t + 10.dp.toPx()
      val r = 9.dp.toPx()
      Path().apply {
        moveTo(rx, ry - r)
        lineTo(rx + r, ry)
        lineTo(rx, ry + r)
        lineTo(rx - r, ry)
        close()
      }
    }
  return {
    drawLine(Ink, start, end, strokeWidth = 1.5.dp.toPx())
    // Neoprene sounding balloons along upper tether
    for (i in 0 until 5) {
      val t = 0.45f + i * 0.11f
      val bx = start.x + (end.x - start.x) * t
      val by = start.y + (end.y - start.y) * t - 12.dp.toPx()
      drawCircle(color = Manila, radius = 7.dp.toPx(), center = Offset(bx, by))
      drawCircle(color = Ink, radius = 7.dp.toPx(), center = Offset(bx, by), style = balloonOutline)
      drawLine(Ink, Offset(bx, by + 7.dp.toPx()), Offset(bx, by + 12.dp.toPx()), strokeWidth = 1.dp.toPx())
    }
    reflectors.forEach { drawPath(it, color = StampRed, style = reflectorOutline) }
  }
}

// 7 primary radar returns in SW and NE sectors, as fractions of the outer range ring
private val WashingtonRadarTargets =
  listOf(
    Offset(-0.45f, -0.30f),
    Offset(-0.32f, -0.48f),
    Offset(-0.20f, -0.22f),
    Offset(0.28f, -0.35f),
    Offset(0.52f, 0.18f),
    Offset(0.35f, 0.44f),
    Offset(-0.38f, 0.36f),
  )

private fun CacheDrawScope.washingtonRadarDiagram(): FigureDraw {
  val cx = size.width / 2f
  val cy = size.height / 2f
  val maxR = size.minDimension * 0.42f
  val ring = Stroke(width = 1.2.dp.toPx())
  val targets = WashingtonRadarTargets.map { rel -> Offset(cx + rel.x * maxR, cy + rel.y * maxR) }
  return {
    drawCircle(color = Ink, radius = maxR * 0.35f, center = Offset(cx, cy), style = ring)
    drawCircle(color = Ink, radius = maxR * 0.68f, center = Offset(cx, cy), style = ring)
    drawCircle(color = Ink, radius = maxR, center = Offset(cx, cy), style = ring)
    drawLine(InkSoft, Offset(cx - maxR, cy), Offset(cx + maxR, cy), strokeWidth = 1.dp.toPx())
    drawLine(InkSoft, Offset(cx, cy - maxR), Offset(cx, cy + maxR), strokeWidth = 1.dp.toPx())
    targets.forEach { pt ->
      drawCircle(color = StampRed, radius = 4.dp.toPx(), center = pt)
      drawLine(StampRed, pt, Offset(pt.x + 8.dp.toPx(), pt.y - 5.dp.toPx()), strokeWidth = 1.4.dp.toPx())
    }
  }
}

private fun CacheDrawScope.kecksburgAcornProfile(): FigureDraw {
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
  val hull = Stroke(width = 2.dp.toPx())
  val band = Stroke(width = 1.8.dp.toPx())
  // Raised lower band with stylized markings
  val bandTop = cy + h * 0.22f
  val bandBottom = cy + h * 0.38f
  return {
    drawPath(acorn, color = Manila)
    drawPath(acorn, color = Ink, style = hull)
    drawRect(
      color = StampRed,
      topLeft = Offset(cx - w * 0.46f, bandTop),
      size = Size(w * 0.92f, bandBottom - bandTop),
      style = band,
    )
    for (i in -3..3) {
      val mx = cx + i * (w * 0.11f)
      drawLine(StampRed, Offset(mx, bandTop + 4.dp.toPx()), Offset(mx, bandBottom - 4.dp.toPx()), strokeWidth = 1.4.dp.toPx())
    }
  }
}

private fun CacheDrawScope.shagHarbourDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  // Sea surface line
  val seaY = h * 0.58f
  // 45-degree descent trajectory with 4 lights
  val start = Offset(w * 0.14f, h * 0.16f)
  val impact = Offset(w * 0.52f, seaY)
  val descentDash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
  val foamOutline = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())))
  val foamTopLeft = Offset(w * 0.48f, seaY - 8.dp.toPx())
  val foamSize = Size(w * 0.36f, 18.dp.toPx())
  return {
    drawLine(Ink, Offset(0f, seaY), Offset(w, seaY), strokeWidth = 2.dp.toPx())
    drawLine(color = StampRed, start = start, end = impact, strokeWidth = 2.dp.toPx(), pathEffect = descentDash)
    for (i in 0 until 4) {
      val t = i / 4f
      val lx = start.x + (impact.x - start.x) * t
      val ly = start.y + (impact.y - start.y) * t
      drawCircle(color = StampRed, radius = 3.5.dp.toPx(), center = Offset(lx, ly))
    }
    // Foam patch reported on the surface (schematic, not to scale)
    drawOval(color = Manila, topLeft = foamTopLeft, size = foamSize)
    drawOval(color = Ink, topLeft = foamTopLeft, size = foamSize, style = foamOutline)
  }
}

private fun CacheDrawScope.berwynSeismographDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  val midY = h * 0.52f
  val trace = Path().apply {
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
  val pen = Stroke(width = 1.6.dp.toPx())
  val onsetDash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
  return {
    drawPath(trace, color = Ink, style = pen)
    // P-wave onset marker
    val onsetX = w * 0.35f
    drawLine(
      color = StampRed,
      start = Offset(onsetX, h * 0.10f),
      end = Offset(onsetX, h * 0.90f),
      strokeWidth = 1.5.dp.toPx(),
      pathEffect = onsetDash,
    )
  }
}

private fun CacheDrawScope.rendleshamTriangleDiagram(): FigureDraw {
  val cx = size.width / 2f
  val cy = size.height * 0.54f
  val r = size.minDimension * 0.34f
  val pods = listOf(Offset(cx, cy - r), Offset(cx - r * 0.92f, cy + r * 0.65f), Offset(cx + r * 0.92f, cy + r * 0.65f))
  val tri = Path().apply {
    moveTo(pods[0].x, pods[0].y)
    lineTo(pods[1].x, pods[1].y)
    lineTo(pods[2].x, pods[2].y)
    close()
  }
  val triOutline = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx())))
  val podOutline = Stroke(width = 2.dp.toPx())
  return {
    drawPath(path = tri, color = Ink, style = triOutline)
    pods.forEach { pod ->
      drawCircle(color = Manila, radius = 9.dp.toPx(), center = pod)
      drawCircle(color = StampRed, radius = 9.dp.toPx(), center = pod, style = podOutline)
      drawCircle(color = Ink, radius = 2.5.dp.toPx(), center = pod)
    }
  }
}

private fun CacheDrawScope.phoenixFormationDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  val cx = w * 0.48f
  val apexY = h * 0.22f
  val wingSpan = w * 0.34f
  val wingDrop = h * 0.44f
  // V-chevron outline
  val chevron = Path().apply {
    moveTo(cx - wingSpan, apexY + wingDrop)
    lineTo(cx, apexY)
    lineTo(cx + wingSpan, apexY + wingDrop)
  }
  val chevronOutline = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
  val lightOutline = Stroke(width = 1.8.dp.toPx())
  // 5 lights along the V-formation
  val lights = listOf(-1f, -0.5f, 0f, 0.5f, 1f).map { f -> Offset(cx + f * wingSpan, apexY + kotlin.math.abs(f) * wingDrop) }
  val headingDash = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))
  return {
    drawPath(chevron, color = Ink, style = chevronOutline)
    lights.forEach { light ->
      drawCircle(color = Manila, radius = 6.dp.toPx(), center = light)
      drawCircle(color = StampRed, radius = 6.dp.toPx(), center = light, style = lightOutline)
    }
    // Heading arrow southward
    drawLine(
      color = Dash,
      start = Offset(cx, apexY + 14.dp.toPx()),
      end = Offset(cx, h * 0.88f),
      strokeWidth = 1.5.dp.toPx(),
      pathEffect = headingDash,
    )
  }
}

private fun CacheDrawScope.nimitzDropDiagram(): FigureDraw {
  val w = size.width
  val h = size.height
  val topY = h * 0.16f
  val midY = h * 0.62f
  val seaY = h * 0.84f
  val lowY = seaY - 6.dp.toPx()
  // 80,000 ft track -> vertical drop -> 20,000 ft hold -> drop to sea level
  val track = Path().apply {
    moveTo(w * 0.12f, topY)
    lineTo(w * 0.38f, topY)
    lineTo(w * 0.42f, midY)
    lineTo(w * 0.66f, midY)
    lineTo(w * 0.70f, lowY)
    lineTo(w * 0.88f, lowY)
  }
  val trackStroke = Stroke(width = 2.2.dp.toPx())
  val turns = listOf(Offset(w * 0.38f, topY), Offset(w * 0.42f, midY), Offset(w * 0.70f, lowY))
  return {
    // Sea level baseline
    drawLine(Ink, Offset(w * 0.08f, seaY), Offset(w * 0.92f, seaY), strokeWidth = 2.dp.toPx())
    drawPath(track, color = StampRed, style = trackStroke)
    turns.forEach { pt -> drawCircle(color = Ink, radius = 4.dp.toPx(), center = pt) }
  }
}

/**
 * Uses Coil off the main thread at runtime, and [painterResource] in Compose previews where async loaders do not run.
 */
@Composable
internal fun BundledImage(
  @DrawableRes resId: Int,
  contentDescription: String,
  contentScale: ContentScale,
  modifier: Modifier = Modifier,
  alignment: Alignment = Alignment.Center,
  colorFilter: ColorFilter? = null,
) {
  if (LocalInspectionMode.current) {
    Image(
      painter = painterResource(resId),
      contentDescription = contentDescription,
      contentScale = contentScale,
      alignment = alignment,
      colorFilter = colorFilter,
      modifier = modifier,
    )
  } else {
    AsyncImage(
      model = resId,
      contentDescription = contentDescription,
      contentScale = contentScale,
      alignment = alignment,
      colorFilter = colorFilter,
      modifier = modifier,
    )
  }
}

@Composable
private fun ReportMediaCardPreview(media: ReportMedia) {
  DeskPreview(padding = 16.dp) {
    ReportMediaCard(
      media = media,
      onLabelClick = {},
      evidenceTitle = "Sample linked evidence",
      onOpenEvidence = {},
    )
  }
}

private fun findPreviewMedia(predicate: (ReportMedia) -> Boolean): ReportMedia =
  Archive.cases.asSequence().mapNotNull { it.detail }.flatMap { it.media }.first(predicate)

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaDiagramPreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Diagram })
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaPhotoPreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Photo && it.image != null })
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaPlatePreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Photo && it.figure?.plate == true })
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaDocumentPreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Document && it.image != null })
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaAudioPreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Audio })
}

@Preview(showBackground = true, widthDp = 412)
@Composable
private fun ReportMediaVideoPreview() {
  ReportMediaCardPreview(findPreviewMedia { it.kind == MediaKind.Video && it.clip != null })
}
