package com.mushi.notasapp.BlocNotas

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
import com.mushi.notasapp.data.database.AppDatabase
import com.mushi.notasapp.data.database.entities.Note
import com.mushi.notasapp.databinding.ActivityNotesBinding
import kotlinx.coroutines.launch

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

        database = AppDatabase.getDatabase(this)
        username = intent.getStringExtra("USERNAME") ?: ""

        // ReciclerView
        adapter = NoteAdapter(
            notes = emptyList(),
            onEditClick = { note -> editNote(note) },
            onDeleteClick = { note -> deleteNote(note) }
        )

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter

        // Botón añadir nota
        binding.btnAddNote.setOnClickListener {
            addNote()
        }

        // Cargar notas
        loadNotes()
    }

    private fun addNote() {
        val text = binding.etNewNote.text.toString().trim()

        if (text.isEmpty()) {
            Toast.makeText(this, "Nota vacía", Toast.LENGTH_SHORT).show()
            return
        }

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

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = database.noteDao().getUserNotes(username)

            runOnUiThread {
                adapter.updateData(notes)
            }
        }
    }

    private fun editNote(note: Note) {
        val editText = EditText(this)
        editText.setText(note.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Note")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    lifecycleScope.launch {
                        val updatedNote = note.copy(text = newText)
                        database.noteDao().updateNote(updatedNote)

                        runOnUiThread {
                            Toast.makeText(this@NotesActivity, "Note updated", Toast.LENGTH_SHORT).show()
                            loadNotes()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNote(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    database.noteDao().deleteNote(note)

                    runOnUiThread {
                        Toast.makeText(this@NotesActivity, "Note deleted", Toast.LENGTH_SHORT).show()
                        loadNotes()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}