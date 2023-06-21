package com.aldajo92.joystickwebsocket.repository.url

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UrlRepositoryImpl(
    private val context: Context
) : UrlRepository {

    private val urlFlow = context.dataStore.data.map { preferences ->
        preferences[URL_KEY].orEmpty()
    }

    override suspend fun getStoredUrl(): String = context.dataStore.data.first()[URL_KEY].orEmpty()

    override fun saveUrl(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[URL_KEY] = url
            }
        }
    }

    override fun getStoredUrlFlow(): Flow<String> = urlFlow

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "url")
        private val URL_KEY = stringPreferencesKey("url")
    }

}
