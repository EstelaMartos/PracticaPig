package com.example.practicapig.FireBase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.databinding.ActivityFirebaseconBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

private lateinit var binding: ActivityFirebaseconBinding

class FireBaseCon : AppCompatActivity() {

    private var usuario: Usuario? = null
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirebaseconBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //-------------------------toolbar---------------------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        usuario = intent.getParcelableCompat("usuario")
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario no encontrado"

        usuario?.avatar?.let {
            Picasso.get().load(it).into(binding.avatarToolbar)
        }

        //---------------------hasta aqui poner siempre que ponga la toolbar-----------------------
        auth = Firebase.auth
        updateUI(auth.currentUser)

        binding.botonAnadir.setOnClickListener {
            insertarCancion()
        }
    }

    private fun insertarCancion() {

        val usuarioGoogle = auth.currentUser

        if (usuarioGoogle == null) {

            binding.textEstado.text = "No estás autenticado con Google"

        } else {
            //leo lo que pone el usuario
            val artista = binding.editArtista.text.toString().trim()
            val cancion = binding.editCancion.text.toString().trim()
            val duracionTexto = binding.editDuracion.text.toString().trim()
            val valoracionTexto = binding.editValoracion.text.toString().trim()
            val reproduccionesTexto = binding.editReproducciones.text.toString().trim()

            if (
                artista.isEmpty() || cancion.isEmpty() || duracionTexto.isEmpty() || valoracionTexto.isEmpty() ||
                reproduccionesTexto.isEmpty()
            ) {
                binding.textEstado.text = "Todos los campos deben estar completados"
            } else {
                if (!formatoDuracionCorrecto(duracionTexto)) {
                    binding.textEstado.text = "El formato debe ser mm:ss"
                } else {

                    val duracionSegundos = convertirDuracionASegundos(duracionTexto)

                    if (duracionSegundos == null) {

                        binding.textEstado.text = "Los segundos deben estar entre 00 y 59"

                    } else {

                        val valoracion = valoracionTexto.toIntOrNull()

                        if (valoracion != null) {

                            if (valoracion <= 0 || valoracion >= 6) {

                                binding.textEstado.text = "La valoración solo puede ser entre 1 y 5"

                            } else {

                                val reproducciones = reproduccionesTexto.toLongOrNull()

                                if (reproducciones != null) {

                                    db.collection("musicfyEMG")
                                        .get()
                                        .addOnSuccessListener { resultados ->//resultados=lista docs(cada doc 1 cancion)

                                            var existe = false

                                            //recorro una a una las canciones
                                            for (documentos in resultados) {//bucle for para buscar duplicados
                                                val artistaBD = documentos.getString("artista") ?: ""
                                                val cancionBD = documentos.getString("canción") ?: ""

                                                if (
                                                    artistaBD.equals(artista, ignoreCase = true) &&
                                                    cancionBD.equals(cancion, ignoreCase = true)
                                                ) {
                                                    existe = true
                                                }

                                            }

                                            if (existe) {

                                                binding.textEstado.text = "Ya existe una canción con ese artista y ese nombre"

                                            } else {
                                                //representa 1 doc
                                                val cancionMap = hashMapOf(
                                                    "artista" to artista,
                                                    "canción" to cancion,
                                                    "duración" to duracionSegundos,
                                                    "valoración" to valoracion,
                                                    "reproducciones" to reproducciones
                                                )

                                                db.collection("musicfyEMG")
                                                    .add(cancionMap)//añado el mapa
                                                    .addOnSuccessListener {
                                                        binding.textEstado.text = "Canción añadida correctamente"
                                                    }
                                                    .addOnFailureListener {
                                                        binding.textEstado.text = "Error al insertar la canción"
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            binding.textEstado.text = "Error al consultar la base de datos"
                                        }
                                } else {
                                    binding.textEstado.text = "Las reproducciones deben ser numéricas"
                                }
                            }
                        } else {
                            binding.textEstado.text = "La valoración debe ser numérica"
                        }
                    }
                }
            }
        }
    }
//----------------------------------metodos para controlar la duracion---------------------------
    private fun formatoDuracionCorrecto(texto: String): Boolean {
        val regex = Regex("^\\d{2}:\\d{2}$")
        return regex.matches(texto)
    }

    private fun convertirDuracionASegundos(texto: String): Long? {
        val partes = texto.split(":")
        if (partes.size != 2) return null

        val minutos = partes[0].toLongOrNull()
        val segundos = partes[1].toLongOrNull()

        if (minutos == null || segundos == null) return null
        if (segundos !in 0..59) return null

        return minutos * 60 + segundos
    }

    //----------------------------------------metodods del profe, usar tal cual, no tocar--------------------------------
    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
    }
}
