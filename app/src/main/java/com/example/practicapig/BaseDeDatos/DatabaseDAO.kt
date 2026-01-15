package com.example.practicapig.BaseDeDatos

import androidx.room.*
import com.example.practicapig.BaseDeDatos.Usuario

@Dao
interface DatabaseDAO {
    @Query("SELECT * FROM usuarios")
    fun obtenerUsuarios(): List<Usuario>
    @Query("SELECT * FROM usuarios WHERE nombre IN (:nombres)")
    fun leerPorNombres(nombres: IntArray): List<Usuario>
    @Query("SELECT * FROM usuarios WHERE nombre LIKE :nombreBuscar  LIMIT 1")
    fun buscarPorNombre (nombreBuscar: String): Usuario
    @Query("UPDATE usuarios SET contraseña = :contraseña WHERE nombre = :nombre")
    fun actualizarContrasenia(nombre: String, contraseña: String)
    @Insert
    fun insertarUsuario(usuario: Usuario)
    @Update
    fun modificarUsuario(usuario: Usuario)
    @Query("SELECT avatar FROM usuarios WHERE nombre = :nombreUsuario LIMIT 1")
    fun obtenerAvatar(nombreUsuario: String): String?

    @Query("UPDATE usuarios SET avatar = :avatar WHERE nombre = :nombreUsuario")
    fun actualizarAvatar(nombreUsuario: String, avatar: String)
    @Delete
    fun borrarUsuario(usuario: Usuario)
    @Query("DELETE FROM usuarios")
    fun borrarTodos()
}
