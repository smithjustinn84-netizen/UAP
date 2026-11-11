package com.openfile.uap

import android.content.Context
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.RawResourceMapOutlineRepository

/** Manual dependency injection: the one place that decides which implementation of each repository the app uses. */
class AppContainer(context: Context) {
  val archiveRepository: ArchiveRepository = BundledArchiveRepository

  val mapOutlineRepository: MapOutlineRepository = RawResourceMapOutlineRepository(context.applicationContext.resources)
}
