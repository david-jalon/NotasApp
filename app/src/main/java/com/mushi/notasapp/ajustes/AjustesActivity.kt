package com.mushi.notasapp.ajustes

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.observe
import com.mushi.notasapp.data.local.SettingsManager
import com.mushi.notasapp.databinding.ActivityAjustesBinding // Asegúrate de que este nombre es correcto

class AjustesActivity : AppCompatActivity() {

    // Declara la variable para ViewBinding
    private lateinit var binding: ActivityAjustesBinding

    // Inicializa el ViewModel usando 'by viewModels' y nuestro Factory
    private val ajustesViewModel: AjustesViewModel by viewModels {
        AjustesViewModelFactory(SettingsManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y lo establece como el contenido de la actividad
        binding = ActivityAjustesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- LÓGICA PRINCIPAL ---

        // 1. Observamos los cambios del modo oscuro desde el ViewModel
        ajustesViewModel.isDarkModeEnabled.observe(this) { isEnabled ->
            // Este bloque se ejecutará cuando la app inicie y cada vez que el valor cambie.

            // a) Actualizamos la UI para que el switch refleje el estado actual
            binding.switchDarkMode.isChecked = isEnabled

            // b) Aplicamos el tema (oscuro o claro) a toda la aplicación
            val mode = if (isEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // 2. Configuramos el listener para reaccionar a los clics del usuario en el switch
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Cuando el usuario pulsa el switch, le decimos al ViewModel que guarde el nuevo estado.
            // El observador de arriba se encargará de aplicar el cambio visual.
            ajustesViewModel.setDarkMode(isChecked)
        }

        // 1. Observamos el estado del switch "Recordar Usuario"
        ajustesViewModel.isRememberUserEnabled.observe(this) { isEnabled ->
            // Sincronizamos el switch con el valor guardado
            binding.switchRecordarUsuario.isChecked = isEnabled
        }

// 2. Configuramos el listener para cuando el usuario lo pulse
        binding.switchRecordarUsuario.setOnCheckedChangeListener { _, isChecked ->
            // Notificamos al ViewModel que guarde el nuevo estado
            ajustesViewModel.setRememberUser(isChecked)
        }
    }
}

