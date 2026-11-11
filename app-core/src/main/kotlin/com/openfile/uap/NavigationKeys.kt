package com.openfile.uap

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Files : NavKey

@Serializable data object EventMap : NavKey

@Serializable data object Timeline : NavKey

@Serializable data object Method : NavKey

@Serializable data object Threads : NavKey

@Serializable data class CaseFile(val id: String) : NavKey
