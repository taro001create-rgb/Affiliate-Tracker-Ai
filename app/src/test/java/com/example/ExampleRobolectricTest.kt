package com.example

import android.content.Context
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.AffiliateRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context and validate app name length`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Affiliate Tracker", appName)
    assertTrue("App name must be <= 30 chars for Play compliance", appName.length <= 30)
  }

  @Test
  fun `file provider generates content uri for csv export`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val csvFile = File(exportDir, "test_report.csv")
    csvFile.writeText("Product,Commission\nLamp,12.50", Charsets.UTF_8)

    val uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      csvFile
    )
    assertEquals("content", uri.scheme)
    assertTrue(uri.toString().endsWith("test_report.csv"))
  }

  @Test
  fun `active account persists across repository instances`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo1 = AffiliateRepository(context)
    repo1.switchAccount("alex_tech")
    assertEquals("alex_tech", repo1.currentAccountId.value)

    val repo2 = AffiliateRepository(context)
    assertEquals("alex_tech", repo2.currentAccountId.value)
  }
}
