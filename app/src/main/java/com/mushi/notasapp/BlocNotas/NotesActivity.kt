package com.mushi.notasapp.BlocNotas

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mushi.notasapp.R
import com.mushi.notasapp.ajustes.AjustesActivity
import com.mushi.notasapp.data.database.AppDatabase
import com.mushi.notasapp.data.database.entities.Note
import com.mushi.notasapp.databinding.ActivityNotesBinding
import kotlinx.coroutines.launch

/**
 * Activity para gestionar las notas del usuario.
 * Permite:
 *  - Cargar y mostrar las notas del usuario.
 *  - Permitir añadir, editar y eliminar notas.
 *  - Navegar a la pantalla de Ajustes.
 */
class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: NoteAdapter
    private var username: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Obtenemos la instancia de la base de datos
        database = AppDatabase.getDatabase(this)
        //Obtenemos el nombre de usuario del intent
        username = intent.getStringExtra("USERNAME") ?: ""


        //Boton ajustes
        binding.btnAjustes.setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            startActivity(intent)
        }

        // ReciclerView
        adapter = NoteAdapter(
            notes = emptyList(),
            onEditClick = { note -> editNote(note) },
            onDeleteClick = { note -> deleteNote(note) }
        )

        // Configura el RecyclerView
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter

        // Botón añadir nota
        binding.btnAddNote.setOnClickListener {
            addNote()
        }

        // Cargar notas
        loadNotes()
    }

    //Añade una nota
    private fun addNote() {
        // Obtén el texto de la nota
        val text = binding.etNewNote.text.toString().trim()

        // Comprueba si la nota está vacía
        if (text.isEmpty()) {
            Toast.makeText(this, "Nota vacía", Toast.LENGTH_SHORT).show()
            return
        }

        // Inserta la nota en la base de datos
        lifecycleScope.launch {
            val newNote = Note(
                text = text,
                nombreUsuario = username,
            )
            database.noteDao().insertNote(newNote)

            runOnUiThread {
                binding.etNewNote.text.clear()
                Toast.makeText(this@NotesActivity, "Nota añadida", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
        }
    }

    //Carga las notas del usuario
    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = database.noteDao().getUserNotes(username)

            runOnUiThread {
                adapter.updateData(notes)
            }
        }
    }

    //Edita una nota
    private fun editNote(note: Note) {
        val editText = EditText(this)
        editText.setText(note.text)

        // Abre un diálogo para editar la nota
        AlertDialog.Builder(this)
            .setTitle("Editar Nota")
            .setView(editText) // Muestra el EditText
            .setPositiveButton("Guardar") { _, _ ->
                //Cuando pulsas guardar, actualiza la nota
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    lifecycleScope.launch {
                        // Actualiza la nota en la base de datos
                        val updatedNote = note.copy(text = newText)
                        database.noteDao().updateNote(updatedNote)

                        runOnUiThread {
                            Toast.makeText(this@NotesActivity, "Nota actualizada", Toast.LENGTH_SHORT).show()
                            loadNotes() // Carga las notas actualizadas
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //Elimina una nota
    private fun deleteNote(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Borrar Nota")
            .setMessage("¿Estás seguro de que quieres eliminar esta nota?")
            .setPositiveButton("Borrar") { _, _ ->
                //Cuando el usuario pulsa borrar
                lifecycleScope.launch {
                    database.noteDao().deleteNote(note)

                    runOnUiThread {
                        Toast.makeText(this@NotesActivity, "Nota Eliminada", Toast.LENGTH_SHORT).show()
                        loadNotes() // Carga las notas actualizadas
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}