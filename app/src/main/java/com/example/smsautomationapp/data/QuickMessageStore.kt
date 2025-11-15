package com.example.smsautomationapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val QUICK_MESSAGE_PREFS = "quick_message_prefs"
private val Context.quickMessageDataStore by preferencesDataStore(name = QUICK_MESSAGE_PREFS)

class QuickMessageStore(private val context: Context) {
    private val KEY_MESSAGE = stringPreferencesKey("quick_message")

    val quickMessageFlow: Flow<String> = context.quickMessageDataStore.data.map { it[KEY_MESSAGE] ?: "" }

    suspend fun save(message: String) {
        context.quickMessageDataStore.edit { prefs ->
            if (message.isBlank()) prefs.remove(KEY_MESSAGE) else prefs[KEY_MESSAGE] = message
        }
    }
}
