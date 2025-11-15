package com.example.smsautomationapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smsautomationapp.data.QuickMessageStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickMessageStoreTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun saveAndRetrieveMessage() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val store = QuickMessageStore(context)
        store.save("Hello World")
        val value = store.quickMessageFlow.first()
        assertEquals("Hello World", value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun blankClearsMessage() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val store = QuickMessageStore(context)
        store.save("Temp")
        store.save("")
        val value = store.quickMessageFlow.first()
        assertEquals("", value)
    }
}
