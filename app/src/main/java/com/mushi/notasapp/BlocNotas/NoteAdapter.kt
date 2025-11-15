package com.mushi.notasapp.BlocNotas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mushi.notasapp.data.database.entities.Note
import com.mushi.notasapp.R

class NoteAdapter (
    private var notes: List<Note>,
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTextNote: TextView = view.findViewById(R.id.tvTextNote)
        val btnEditNote: Button = view.findViewById(R.id.btnEditNote)
        val btnDelNote: Button = view.findViewById(R.id.btnDelNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTextNote.text = note.text
        holder.btnEditNote.setOnClickListener { onEditClick(note) }
        holder.btnDelNote.setOnClickListener { onDeleteClick(note) }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateData(newNotas: List<Note>) {
        notes = newNotas
        notifyDataSetChanged()
    }

}