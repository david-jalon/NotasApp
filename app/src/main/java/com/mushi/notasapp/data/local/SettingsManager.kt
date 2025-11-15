package com.mushi.notasapp.data.local // O el paquete que elijas

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Extiende el Contexto de la app para tener una única instancia de DataStore
//    El nombre "ajustes_app" será el nombre del archivo donde se guardarán las preferencias.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_app")

class SettingsManager(private val context: Context) {

    // 2. Define una "llave" para acceder al valor. Es como la clave en un diccionario.
    //    Es buena práctica tenerlas en un objeto companion para que sean estáticas.
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }

    // 3. Crea una función para GUARDAR el estado del modo oscuro.
    //    La marcamos como 'suspend' porque DataStore trabaja de forma asíncrona.
    suspend fun setDarkMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isEnabled
        }
    }

    // 4. Crea un 'Flow' para LEER el estado del modo oscuro.
    //    Un 'Flow' es como un "chorro" de datos. Notificará automáticamente a quien lo escuche
    //    cada vez que el valor cambie.
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        // Si el valor no existe todavía (la primera vez que abres la app),
        // devolvemos 'false' como valor predeterminado.
        preferences[DARK_MODE_KEY] ?: false
    }
}
    