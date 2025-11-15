package com.mushi.notasapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey(autoGenerate = true)
    val ind:Int = 0,
    val nombreUsuario: String,
    val contrasena: String,
)