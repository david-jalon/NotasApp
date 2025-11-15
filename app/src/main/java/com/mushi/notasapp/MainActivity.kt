package com.mushi.notasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mushi.notasapp.BlocNotas.NotesActivity
import com.mushi.notasapp.ajustes.AjustesViewModel
import com.mushi.notasapp.ajustes.AjustesViewModelFactory
import com.mushi.notasapp.data.database.AppDatabase
import com.mushi.notasapp.data.database.entities.User
import com.mushi.notasapp.data.local.SettingsManager
import com.mushi.notasapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    private val ajustesViewModel: AjustesViewModel by viewModels {
        AjustesViewModelFactory(SettingsManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ajustesViewModel.isDarkModeEnabled.observe(this) { isEnabled ->
            val mode = if (isEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)

        // Boton login
        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            iniciarSesion(user, password)
        }

        // Boton Registro
        binding.btnRegister.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarNuevoUsuario(user, password)
        }
    }

    private fun iniciarSesion(user: String, password: String) {
        lifecycleScope.launch {
            val usuarioEncontrado = database.userDao().buscarUsuario(user, password)

            runOnUiThread {
                if (usuarioEncontrado != null) {
                    // Si el usuario existe
                    Toast.makeText(this@MainActivity, "Bienvenido $user", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity, NotesActivity::class.java)
                    intent.putExtra("username", user)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "$user no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registrarNuevoUsuario(user: String, password: String) {
        lifecycleScope.launch {
            val usuarioExistente = database.userDao().buscarNombreUsuario(user)

            // ✅ CORRECCIÓN: Todo dentro de la misma corrutina
            if (usuarioExistente != null) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "El usuario existe, inicie sesión.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Insertar directamente sin crear otra corrutina
                val nuevoUsuario = User(
                    nombreUsuario = user,
                    contrasena = password
                )

                database.userDao().insertarUsuario(nuevoUsuario)

                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Usuario registrado exitosamente, inicie sesión.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}