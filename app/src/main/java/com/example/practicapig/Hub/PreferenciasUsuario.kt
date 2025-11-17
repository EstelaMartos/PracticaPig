package com.example.practicapig.Hub

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("preferencias_login")

class PreferenciasUsuario(private val context: Context) {

    companion object {
        val NOMBRE_CLAVE = stringPreferencesKey("nombre_usuario")
        val CONTRASENIA_CLAVE = stringPreferencesKey("contraseña_usuario")
        val RECORDAR_CLAVE = booleanPreferencesKey("recordar")
    }

    // guardar datos
    suspend fun guardarLogin(nombre: String, contraseña: String, recordar: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOMBRE_CLAVE] = nombre
            prefs[CONTRASENIA_CLAVE] = contraseña
            prefs[RECORDAR_CLAVE] = recordar
        }
    }

    // leer nombre
    val nombreUsuario: Flow<String> = context.dataStore.data.map {
        it[NOMBRE_CLAVE] ?: ""
    }

    // leer contraseña
    val contraseñaUsuario: Flow<String> = context.dataStore.data.map {
        it[CONTRASENIA_CLAVE] ?: ""
    }

    // leer si debe recordar
    val recordar: Flow<Boolean> = context.dataStore.data.map {
        it[RECORDAR_CLAVE] ?: false
    }

    //borrar todo
    suspend fun borrar() {
        context.dataStore.edit { it.clear() }
    }
}
