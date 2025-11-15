package com.mushi.notasapp.data

import android.R.attr.enabled
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.settingsDataStore by preferencesDataStore(name = "ajustes")

object SettingsKeys {
    val DARK_MODE = booleanPreferencesKey("modo_oscuro")
}

class SettingsDataStore(private val context: Context) {

    val darkModeFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[SettingsKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(value: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE] = value
        }
    }
}