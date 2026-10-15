package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ChecklistItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("QuickNote", appName)
  }

  @Test
  fun `checklist serialization roundtrip`() {
    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
    val adapter = moshi.adapter<List<ChecklistItem>>(type)

    val items = listOf(
      ChecklistItem(id = "1", text = "Buy oat milk", isChecked = false),
      ChecklistItem(id = "2", text = "Ship package", isChecked = true)
    )

    val json = adapter.toJson(items)
    val restored = adapter.fromJson(json)

    assertEquals(2, restored?.size)
    assertEquals("Buy oat milk", restored?.get(0)?.text)
    assertTrue(restored?.get(1)?.isChecked == true)
  }
}
