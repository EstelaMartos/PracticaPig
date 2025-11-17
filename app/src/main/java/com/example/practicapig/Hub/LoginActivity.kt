package com.example.practicapig.Hub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: PreferenciasUsuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferenciasUsuario(applicationContext)

        cargarPreferencias()

        // si pulso el boton de iniciar sesion
        binding.botonIniciarSesion.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {

        val nombreUsuario = binding.nombreUsuario.text.toString()
        val contrasenia = binding.contrasenia.text.toString()

        lifecycleScope.launch {

            // consultar base de datos, solo aqui uso el withContext(Dispatchers.IO)
            val usuarioExistente = withContext(Dispatchers.IO) {
                val usuarioDao = DatabaseUsuarios.getDatabase(this@LoginActivity).usuarioDao()
                usuarioDao.buscarPorNombre(nombreUsuario)
            }

            // si no existe el usuario
            if (usuarioExistente == null) {
                binding.textUsuarioRegistrado.visibility = View.VISIBLE
                return@launch
            }

            // si la contraseña es incorrecta
            if (usuarioExistente.contraseña != contrasenia) {
                binding.textErrorLogin.visibility = View.VISIBLE
                return@launch
            }

            // si todo esta bien
            if (binding.checkRecordar.isChecked) {
                prefs.guardarLogin(nombreUsuario, contrasenia, true)
            } else {
                prefs.borrar()
            }

            // si todo esta bien voy al menu
            val intent = Intent(this@LoginActivity, MenuActivity::class.java)
            Log.d("Estela", "no se que pasa aqui")
            intent.putExtra("usuario", usuarioExistente)
            startActivity(intent)
        }
    }

    private fun cargarPreferencias() {

        // cargar check recordar
        lifecycleScope.launch {
            prefs.recordar.collect { recordar ->
                binding.checkRecordar.isChecked = recordar
            }
        }

        // cargar nombre
        lifecycleScope.launch {
            prefs.nombreUsuario.collect { nombreGuardado ->
                if (binding.checkRecordar.isChecked) {
                    binding.nombreUsuario.setText(nombreGuardado)
                }
            }
        }

        // cargar contraseña
        lifecycleScope.launch {
            prefs.contraseñaUsuario.collect { passGuardada ->
                if (binding.checkRecordar.isChecked) {
                    binding.contrasenia.setText(passGuardada)
                }
            }
        }
    }

}
