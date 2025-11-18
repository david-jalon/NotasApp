package com.mushi.notasapp.BlocNotas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mushi.notasapp.data.database.entities.Note
import com.mushi.notasapp.R

/**
 * Adapter del ReciclerView de notas.
 *
 * Este componente conecta los datos de la lista de notas con la vista
 * Crea y gestiona los elementos de la lista.
 *
 * @param notes Lista de notas a mostrar.
 * @param onEditClick Función a ejecutar al hacer click en el botón de edición.
 * @param onDeleteClick Función a ejecutar al hacer click en el botón de eliminación.
 */
class NoteAdapter (
    private var notes: List<Note>,
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    // ViewHolder que contiene las vistas de cada elemento de la lista.
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTextNote: TextView = view.findViewById(R.id.tvTextNote)
        val btnEditNote: Button = view.findViewById(R.id.btnEditNote)
        val btnDelNote: Button = view.findViewById(R.id.btnDelNote)
    }

    // Crea un nuevo ViewHolder cuando no hay suficientes para reutilizar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)

    }

    // Vincula los datos del elemento en la posición dada con la vista del ViewHolder.
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTextNote.text = note.text
        holder.btnEditNote.setOnClickListener { onEditClick(note) }
        holder.btnDelNote.setOnClickListener { onDeleteClick(note) }
    }

    // Devuelve el número total de elementos en la lista.
    override fun getItemCount(): Int {
        return notes.size
    }

    // Actualiza la lista de notas y notifica a los observadores.
    fun updateData(newNotas: List<Note>) {
        notes = newNotas
        notifyDataSetChanged()
    }

}