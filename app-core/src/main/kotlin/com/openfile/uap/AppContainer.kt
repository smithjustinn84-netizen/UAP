package com.openfile.uap

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.openfile.uap.data.ArchiveRepository
import com.openfile.uap.data.BundledArchiveRepository
import com.openfile.uap.data.MapOutlineRepository
import com.openfile.uap.data.NoticesRepository
import com.openfile.uap.data.RawResourceMapOutlineRepository
import com.openfile.uap.data.RawResourceNoticesRepository

/** Manual dependency injection: the one place that decides which implementation of each repository the app uses. */
class AppContainer(context: Context) {
  val archiveRepository: ArchiveRepository = BundledArchiveRepository

  val mapOutlineRepository: MapOutlineRepository = RawResourceMapOutlineRepository(context.applicationContext.resources)

  val noticesRepository: NoticesRepository = RawResourceNoticesRepository(context.applicationContext.resources)

  /** The versionName set in the app module, so About never disagrees with the installed build. */
  val appVersion: String = versionName(context)
}

private fun versionName(context: Context): String {
  val pm = context.packageManager
  val info =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
      // The flags overload only exists from API 33.
      @Suppress("DEPRECATION")
      pm.getPackageInfo(context.packageName, 0)
    }
  return info.versionName.orEmpty()
}
