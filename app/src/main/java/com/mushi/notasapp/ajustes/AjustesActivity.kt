package com.mushi.notasapp.ajustes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mushi.notasapp.R
import com.mushi.notasapp.data.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AjustesActivity : AppCompatActivity() {

    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var settingsDS: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {

        // 🔥 Aplicar tema ANTES de cargar la Activity
        aplicarTemaGuardado()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsDS = SettingsDataStore(this)
        switchDarkMode = findViewById(R.id.switchDarkMode)

        // ⭐ Sincronizar el estado del switch con DataStore
        lifecycleScope.launch {
            settingsDS.darkModeFlow.collectLatest { isDark ->
                switchDarkMode.isChecked = isDark
            }
        }

        // ⭐ Cuando el usuario cambia el switch → guardar en DataStore
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsDS.setDarkMode(isChecked)

                // cambiar tema inmediatamente
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    private fun aplicarTemaGuardado() {
        val ds = SettingsDataStore(this)

        lifecycleScope.launch {
            ds.darkModeFlow.collectLatest { isDark ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }
}
