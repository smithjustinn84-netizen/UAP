package com.openfile.uap

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Files : NavKey

@Serializable data object EventMap : NavKey

@Serializable data object Timeline : NavKey

@Serializable data object Method : NavKey

@Serializable data object Threads : NavKey

@Serializable data object About : NavKey

@Serializable data object Licences : NavKey

@Serializable data class CaseFile(val id: String) : NavKey

/** A destination on the bottom tab strip. [label] is also how a file's back link names the section it came from. */
data class TopLevelSection(val key: NavKey, val label: String)

val TopLevelSections: List<TopLevelSection> =
  listOf(
    TopLevelSection(Files, "Files"),
    TopLevelSection(EventMap, "Map"),
    TopLevelSection(Timeline, "Timeline"),
    TopLevelSection(Method, "Method"),
  )
