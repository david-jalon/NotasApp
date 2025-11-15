package com.mushi.notasapp.data.database.dao

import androidx.room.*
import com.mushi.notasapp.data.database.entities.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(nota: Note)

    @Update
    suspend fun updateNote(nota: Note)

    @Delete
    suspend fun deleteNote(nota: Note)

    @Query("SELECT * FROM notas WHERE nombreUsuario = :username")
    suspend fun getUserNotes(username: String): List<Note>
}