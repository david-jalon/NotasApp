package com.mushi.notasapp.data.database.dao

import androidx.room.*
import com.mushi.notasapp.data.database.entities.Note

/**
 * Clase de acceso a datos para la entidad Note.
 *
 *  Esta interfaz define los métodos para interactuar con la tabla `notas` en la base de datos.
 *  Todas las funciones se marcan como `suspend` para asegurar que se ejecuten en un hilo secundario
 */
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