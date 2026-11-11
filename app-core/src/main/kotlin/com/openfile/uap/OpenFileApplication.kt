package com.openfile.uap

import android.app.Application

/** Owns the [AppContainer], so repositories and their caches outlive the activity across configuration changes. */
class OpenFileApplication : Application() {
  val container: AppContainer by lazy { AppContainer(this) }
}
