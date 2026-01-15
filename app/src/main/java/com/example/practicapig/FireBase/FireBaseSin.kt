package com.example.practicapig.FireBase

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.databinding.ActivityFirebasesinBinding
import com.squareup.picasso.Picasso

import android.view.View

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private lateinit var binding: ActivityFirebasesinBinding


class FireBaseSin : AppCompatActivity() {
    private var usuario: Usuario? = null
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityFirebasesinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //-----------------------------toolbar---------------------------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //-----------------------------------------recojo el intent-----------------------------
        usuario = intent.getParcelableCompat("usuario")
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario no encontrado"

        //cargo el avatar del usuario
        usuario?.avatar?.let {
            Picasso.get().load(it).into(binding.avatarToolbar)
        }

        //la conexion sin autenticacion en firebase
        auth = Firebase.auth

        //--------------------------------------del profe, no tocar---------------------------------
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // si el login va bien
                    val user = auth.currentUser
                    updateUI(user)
                    Log.d("Estela", "signInAnonymously:success ${user?.uid}")

                } else {

                    // si login da error
                    Log.w("Estela", "signInAnonymously:failure", task.exception)
                    binding.resultadoArtistas.text = "Error de autenticación con Firebase"
                }
            }

        // establezco visibilidad
        binding.layoutArtistas.visibility = View.VISIBLE
        binding.layoutValoraciones.visibility = View.GONE

       //boton de opcion por artista
        binding.botonArtistas.setOnClickListener {
            binding.layoutArtistas.visibility = View.VISIBLE
            binding.layoutValoraciones.visibility = View.GONE
        }

        //boton de opcion por valoración
        binding.botonValoracion.setOnClickListener {
            binding.layoutArtistas.visibility = View.GONE
            binding.layoutValoraciones.visibility = View.VISIBLE
        }

        //busco por artista
        binding.botonBuscarArtista.setOnClickListener {
            buscarArtista()
        }

        //busco por valoración
        binding.botonBuscarValoracion.setOnClickListener {
            buscarPorValoracion()
        }

    }


    private fun buscarArtista() {

        val textoBusqueda = binding.buscarArtista.text.toString().trim()

        //-----------------esto me saca todos los documentos que hay en la base de datos-----------
        db.collection("musicfyEMG")
            .get()
            .addOnSuccessListener { result ->

                if (result.isEmpty) {

                    binding.resultadoArtistas.text = "No hay canciones en la base de datos"

                } else {

                    var textoResultado = ""
                    var hayResultados = false

                    //aqui estoy extrayendo los datos de la base de datos
                    for (documentos in result) {
                        //leo los datos del documento
                        val artista = documentos.getString("artista") ?: ""  //leo el campo x del documento si no exiete pone ""
                        val cancion = documentos.getString("canción") ?: ""
                        val duracion = documentos.getLong("duración")
                        val reproducciones = documentos.getLong("reproducciones")
                        val valoracion = documentos.getLong("valoración")
                        //si esta vacio muestro all y si hay coincidencias lo muetsro
                        if (textoBusqueda.isEmpty() || artista.contains(textoBusqueda, ignoreCase = true)
                        ) {
                            hayResultados = true
                            //voy construyendo un texto largo concatenando
                            textoResultado += "Artista: $artista\n"
                            textoResultado += "Canción: $cancion\n"
                            textoResultado += "Duración: ${formatearDuracion(duracion)}\n"
                            textoResultado += "Reproducciones: $reproducciones\n"
                            textoResultado += "Valoración: $valoracion\n\n"
                        }
                    }

                    if (!hayResultados) {  //si no coincide la busqueda
                        binding.resultadoArtistas.text = "No se han encontrado resultados para su búsqueda"
                    } else {
                        binding.resultadoArtistas.text = textoResultado
                    }
                }
            }
            .addOnFailureListener {
                binding.resultadoArtistas.text = "Error al consultar la base de datos"
            }
    }


    private fun buscarPorValoracion() {

        val textoBusqueda = binding.buscarValoracion.text.toString().trim()

        if (textoBusqueda.isEmpty()) {

            binding.resultadoValoraciones.text = "Introduce una valoración"

        } else {

            val valoracion = textoBusqueda.toIntOrNull()

            if (valoracion == null) {
                binding.resultadoValoraciones.text = "Valoración no válida"
            } else {

                db.collection("musicfyEMG")
                    .get()
                    .addOnSuccessListener { result ->

                        var textoResultado = ""
                        var hayResultados = false

                        for (doc in result) {
                            val valoracionBD = doc.getLong("valoración") ?: 0

                            if (valoracionBD.toString().contains(textoBusqueda)) {
                                hayResultados = true
                                //estoy mostrando por pantalla los resultados
                                textoResultado += "Artista: ${doc.getString("artista")}\n"
                                textoResultado += "Canción: ${doc.getString("canción")}\n"
                                textoResultado += "Duración: ${formatearDuracion(doc.getLong("duración"))}\n"
                                textoResultado += "Reproducciones: ${doc.getLong("reproducciones")}\n"
                                textoResultado += "Valoración: $valoracionBD\n\n"
                            }
                        }

                        if (!hayResultados) {
                            binding.resultadoValoraciones.text = "No se han encontrado documentos con esa valoración"
                        } else {
                            binding.resultadoValoraciones.text = textoResultado
                        }
                    }
                    .addOnFailureListener {
                        binding.resultadoValoraciones.text = "Error al consultar la base de datos"
                    }
            }
        }
    }

    private fun formatearDuracion(segundos: Long?): String {

        if (segundos == null) return "00:00"

        val minutos = segundos / 60
        val restoSegundos = segundos % 60

        return String.format("%02d:%02d", minutos, restoSegundos)
    }

    //-----------------------son del profe no tocar, poner siempre----------------------------
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
    }

}
