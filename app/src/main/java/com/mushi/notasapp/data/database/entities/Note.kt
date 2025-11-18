package com.mushi.notasapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clase que representa una nota en la aplicación.
 *
 * @property id El identificador único de la nota (generado automáticamente).
 * @property text El texto de la nota.
 * @property nombreUsuario El nombre del usuario al que pertenece la nota.
 */
@Entity(tableName = "notas")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val nombreUsuario: String
)