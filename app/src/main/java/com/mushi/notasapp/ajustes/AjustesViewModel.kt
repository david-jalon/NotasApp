package com.mushi.notasapp.ajustes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mushi.notasapp.data.local.SettingsManager
import kotlinx.coroutines.launch

// --- EL VIEWMODEL ---
class AjustesViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    val isDarkModeEnabled: LiveData<Boolean> = settingsManager.darkModeFlow.asLiveData()

    fun setDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkMode(isEnabled)
        }
    }

    val isRememberUserEnabled: LiveData<Boolean> = settingsManager.rememberUserFlow.asLiveData()

    fun setRememberUser(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setRememberUser(isEnabled)
        }
    }
}

// --- EL FACTORY ---
class AjustesViewModelFactory(private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjustesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AjustesViewModel(settingsManager) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida")
    }
}
    