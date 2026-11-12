package com.openfile.uap.data

import android.content.res.Resources
import androidx.annotation.RawRes
import com.openfile.uap.R
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Supplies the third-party notices and the licence texts that have to ship with them. */
interface NoticesRepository {
  val notices: List<Notice>

  /** The full licence text, or null for a licence that is only linked. Main-safe. */
  suspend fun fullText(licence: Licence): String?
}

/** Reads the licence texts bundled in res/raw once each and keeps them. */
class RawResourceNoticesRepository(private val resources: Resources, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
  NoticesRepository {
  private val cache = ConcurrentHashMap<Licence, String>()

  override val notices: List<Notice> = Notices.all

  override suspend fun fullText(licence: Licence): String? {
    val res = rawResourceFor(licence) ?: return null
    return cache[licence]
      ?: withContext(ioDispatcher) {
        cache.getOrPut(licence) { resources.openRawResource(res).bufferedReader().use { it.readText() } }
      }
  }

  @RawRes
  private fun rawResourceFor(licence: Licence): Int? =
    when (licence) {
      Licence.Apache2 -> R.raw.licence_apache_2_0
      Licence.Ofl11 -> R.raw.licence_ofl_1_1
      else -> null
    }
}
