package com.openfile.uap.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role

// Shared motion for the whole app. Every animation here is short, one-shot and identical for every item it is applied
// to, so motion never singles out one file or one explanation. Compose scales all of it by the system animator
// duration setting, so turning animations off in the system makes every change instant.

/** Duration of colour changes and quick fades, in milliseconds. */
const val MOTION_SHORT = 160

/** Duration of opening and closing a section of a sheet, in milliseconds. */
const val MOTION_MEDIUM = 240

private const val PRESSED_SCALE = 0.97f

/** How an expandable part of a sheet opens: it unfolds downward as it fades in. */
val UnfoldIn: EnterTransition = expandVertically(tween(MOTION_MEDIUM)) + fadeIn(tween(MOTION_MEDIUM))

/** How an expandable part of a sheet closes. */
val FoldOut: ExitTransition = shrinkVertically(tween(MOTION_MEDIUM)) + fadeOut(tween(MOTION_SHORT))

/** A colour that eases between its idle and selected values, for tabs, chips and other inverted-ink selections. */
@Composable
fun selectionColor(selected: Boolean, selectedColor: Color, idleColor: Color): Color {
  val color by animateColorAsState(if (selected) selectedColor else idleColor, tween(MOTION_SHORT), label = "selection")
  return color
}

/**
 * Clickable that presses in slightly under the finger, like a sheet pushed into the desk. Place it before any
 * background or border in the chain so the whole surface moves together.
 */
@Composable
fun Modifier.pressable(onClick: () -> Unit, onClickLabel: String? = null, role: Role = Role.Button): Modifier {
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()
  val scale by animateFloatAsState(
    if (pressed) PRESSED_SCALE else 1f,
    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    label = "press",
  )
  return graphicsLayer {
    scaleX = scale
    scaleY = scale
  }.clickable(
    interactionSource = interaction,
    indication = LocalIndication.current,
    onClickLabel = onClickLabel,
    role = role,
    onClick = onClick,
  )
}

/** A light tick for a change of state (a tab, a filter, a fold), not for every tap. */
@Composable
fun rememberTick(): () -> Unit {
  val haptics = LocalHapticFeedback.current
  return remember(haptics) { { haptics.performHapticFeedback(HapticFeedbackType.SegmentTick) } }
}
