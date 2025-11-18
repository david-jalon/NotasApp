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
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.mushi.notasapp.BlocNotas.NotesActivity
import com.mushi.notasapp.ajustes.AjustesViewModel
import com.mushi.notasapp.ajustes.AjustesViewModelFactory
import com.mushi.notasapp.data.database.AppDatabase
import com.mushi.notasapp.data.database.entities.User
import com.mushi.notasapp.data.local.SettingsManager
import com.mushi.notasapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    private val ajustesViewModel: AjustesViewModel by viewModels {
        AjustesViewModelFactory(SettingsManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuramos el modo de noche
        ajustesViewModel.isDarkModeEnabled.observe(this) { isEnabled ->
            val mode = if (isEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            // Aplicar el modo de noche en toda la app
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Configuramos el modo de pantalla completa
        enableEdgeToEdge()
        // Configuramos el binding, inflate convierte el layout en un objeto
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Establece la vista principal del activity
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Instanciamos la base de datos
        database = AppDatabase.getDatabase(this)

        // Boton login
        binding.btnLogin.setOnClickListener {
            // Obtenemos los datos del usuario
            val user = binding.etUser.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            //Validamos los datos
            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Iniciamos sesion a través del método
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

            // Registrar a través del metodo
            registrarNuevoUsuario(user, password)
        }

        //Recodar el usuario
        lifecycleScope.launch {
            // Leemos si el switch "Recordar Usuario" está activado
            val rememberUser = ajustesViewModel.isRememberUserEnabled.asFlow().first()

            if (rememberUser) {
                // Si está activado, leemos el último usuario que inició sesión
                val lastUser = SettingsManager(this@MainActivity).lastUserFlow.first()
                // Y lo ponemos en el EditText
                binding.etUser.setText(lastUser)
            }
        }
    }

    // Metodo para iniciar sesión
    private fun iniciarSesion(user: String, password: String) {
        // Lanzamos una corrutina en el ciclo de vida de la actividad
        // Room necesita que se ejecute en otro hilo
        lifecycleScope.launch {
            // Buscamos al susuario en la base de datos definida en el DAO
            val usuarioEncontrado = database.userDao().buscarUsuario(user, password)

            //Leemos el valor del switch "Recordar Usuario" desde el DataStore
            val rememberUser = SettingsManager(this@MainActivity).rememberUserFlow.first()

            if (usuarioEncontrado != null) {
                if (rememberUser) {
                    // Si el login es exitoso Y el switch está activado, guardamos el usuario
                    SettingsManager(this@MainActivity).saveLastUser(user)
                }
            } else {
                if (rememberUser) {
                    SettingsManager(this@MainActivity).saveLastUser("")
                }
            }

            // Volvemos al hilo principal
            runOnUiThread {
                if (usuarioEncontrado != null) {
                    // Si el usuario existe
                    Toast.makeText(this@MainActivity, "Bienvenido $user", Toast.LENGTH_SHORT).show()

                    // Iniciamos la actividad de notas
                    val intent = Intent(this@MainActivity, NotesActivity::class.java)
                    intent.putExtra("username", user)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "$user no encontrado", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // Metodo para registrar un nuevo usuario
    private fun registrarNuevoUsuario(user: String, password: String) {
        lifecycleScope.launch {
            // Buscamos al usuario en la base de datos
            val usuarioExistente = database.userDao().buscarNombreUsuario(user)

            // Si el usuario ya existe, mostramos un mensaje y no lo registramos
            if (usuarioExistente != null) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "El usuario existe, inicie sesión.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Si el usuario no existe, lo registramos
                val nuevoUsuario = User(
                    nombreUsuario = user,
                    contrasena = password
                )

                // Insertamos el nuevo usuario en la base de datos
                database.userDao().insertarUsuario(nuevoUsuario)

                // Mostramos un mensaje de éxito
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