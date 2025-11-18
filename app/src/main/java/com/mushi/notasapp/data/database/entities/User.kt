package com.mushi.notasapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Clase que representa a un usuario en la base de datos.
 * @property ind El identificador único del usuario.
 * @property nombreUsuario El nombre de usuario del usuario.
 * @property contrasena La contraseña del usuario.
 */
@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey(autoGenerate = true)
    val ind:Int = 0,
    val nombreUsuario: String,
    val contrasena: String,
)