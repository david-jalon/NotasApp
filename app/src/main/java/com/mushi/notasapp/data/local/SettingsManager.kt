package com.mushi.notasapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Crea la DataStore con el archivo ajustes_app
 * Se usa by preferencesDataStore para asegurarnos de que solo haya una instancia de DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_app")

//Gestiona la lectura y escritura de ajustes de la aplicación
class SettingsManager(private val context: Context) {

    //Contiene las keys para acceder a los ajustes de la DataStore
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        val REMEMBER_USER_KEY = booleanPreferencesKey("remember_user_enabled")
        val LAST_LOGGED_IN_USER_KEY = stringPreferencesKey("last_logged_in_user")
    }

    //Guarda el estado del modo oscuro en la DataStore
    suspend fun setDarkMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isEnabled
        }
    }

    //Lee el estado del modo oscuro desde la DataStore
    //Usa flow porque es un flujo asincrono que puede cambiar en cualquier momento
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        // Si el valor no existe todavía (la primera vez que se abre la app),
        // devolvemos 'false' como valor predeterminado.
        preferences[DARK_MODE_KEY] ?: false
    }

    // Función para guardar el estado de "Recordar Usuario" en la DataStore
    suspend fun setRememberUser(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_USER_KEY] = isEnabled
        }
    }

    // Flow para leer el estado de "Recordar Usuario" desde la DataStore
    val rememberUserFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_USER_KEY] ?: true // Por defecto, estará activado
    }

    // Función para guardar el nombre del último usuario que inició sesión
    suspend fun saveLastUser(username: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_LOGGED_IN_USER_KEY] = username
        }
    }

    // Flow para leer el último nombre de usuario guardado
    val lastUserFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_LOGGED_IN_USER_KEY]
    }
}
    