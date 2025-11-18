package com.mushi.notasapp.ajustes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mushi.notasapp.data.local.SettingsManager
import kotlinx.coroutines.launch


/**
 * ViewModel para la pantalla de Ajustes y para gestionar los ajustes de la app.
 *
 * Actua como un puente entre la lógica de datos (SettingsManager)
 * y la interfaz de usuario
 */
class AjustesViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    //Dice el estado actual del modo oscuro, tipo LiveData para que la activity lo pueda observar
    val isDarkModeEnabled: LiveData<Boolean> = settingsManager.darkModeFlow.asLiveData()

    //Guarda el estado del modo oscuro
    fun setDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkMode(isEnabled)
        }
    }

    //Dice el estado actual de recordar usuario, tipo LiveData para que la activity lo pueda observar
    val isRememberUserEnabled: LiveData<Boolean> = settingsManager.rememberUserFlow.asLiveData()

    //Guarda el estado de recordar usuario
    fun setRememberUser(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setRememberUser(isEnabled)
        }
    }
}

// Clase Factory para crear una instancia de AjustesViewModel con SettingsManager como dependencia
class AjustesViewModelFactory(private val settingsManager: SettingsManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjustesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AjustesViewModel(settingsManager) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida")
    }
}
    