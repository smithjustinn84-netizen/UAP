package com.openfile.uap.data

import org.junit.Assert.assertTrue
import org.junit.Test

class NoticesTest {
  @Test
  fun everyNoticeNamesAWorkAndACredit() {
    Notices.all.forEach { n ->
      assertTrue("work", n.work.isNotBlank())
      assertTrue("${n.work} credit", n.credit.isNotBlank())
    }
  }

  @Test
  fun everyLicenceHasANoticeAndALink() {
    val used = Notices.all.map { it.licence }.toSet()
    Licence.entries.forEach { licence ->
      assertTrue("${licence.title} is listed but nothing uses it", licence in used)
      assertTrue("${licence.title} url", licence.url.startsWith("https://"))
    }
  }
}
