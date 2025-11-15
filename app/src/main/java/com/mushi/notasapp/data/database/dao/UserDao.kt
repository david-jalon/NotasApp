package com.mushi.notasapp.data.database.dao

import androidx.room.*
import com.mushi.notasapp.data.database.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertarUsuario(usuario: User)

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :username AND contrasena = :password LIMIT 1")
    suspend fun buscarUsuario(username: String, password: String): User?

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :username LIMIT 1")
    suspend fun buscarNombreUsuario(username: String): User?
}