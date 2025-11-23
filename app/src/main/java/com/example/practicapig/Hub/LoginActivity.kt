package com.example.practicapig.Hub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenciasUsuario: PreferenciasUsuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenciasUsuario = PreferenciasUsuario(applicationContext)

        cargarPreferencias()//cargo las preferencias al arrancar la pantalla

        configurarCheck() //para que se active y desactive el check según procede

        // si pulso el boton de iniciar sesion inicia todo
        binding.botonIniciarSesion.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val nombreUsuario = binding.nombreUsuario.text.toString().trim()
        val contrasenia = binding.contrasenia.text.toString().trim()

        lifecycleScope.launch {
            // consultar base de datos para ver si el usuario existe
            val usuarioExistente = withContext(Dispatchers.IO) {
                val usuarioDao = DatabaseUsuarios.getDatabase(this@LoginActivity).usuarioDao()
                usuarioDao.buscarPorNombre(nombreUsuario)
            }

            var hayErrores = false

            // primero valido si existe el usuario
            if (usuarioExistente == null) {
                binding.textUsuarioRegistrado.visibility = View.VISIBLE
                hayErrores = true
            } else {
                // solo si existe puedo comprobar la contraseña
                if (usuarioExistente.contraseña != contrasenia) {
                    binding.textErrorLogin.visibility = View.VISIBLE
                    hayErrores = true
                }
            }

            //si hay errores paro la app y muestro los errores, no sigo avanzando
            if (hayErrores) return@launch

            // para comprobar si el usuario desmarco voluntariamente el check
            val usuarioGuardado = preferenciasUsuario.nombreUsuario.first() //me devuelve lo que esta guardado ene se momento de la ejecucion
            val passGuardada = preferenciasUsuario.contraseñaUsuario.first()
            val checkGuardado = preferenciasUsuario.recordar.first()

            // borro las preferencias si el el propiousuario guardado el que elimina el check
            if (usuarioGuardado == nombreUsuario &&
                passGuardada == contrasenia &&
                checkGuardado &&
                !binding.checkRecordar.isChecked)
            {
                preferenciasUsuario.borrar()
            }

            // si seleccionan el check de recordar la contraseña, se guardan las preferencias
            if (binding.checkRecordar.isChecked) {
                // SOLO si está marcado se guardan las nuevas preferencias
                preferenciasUsuario.guardarLogin(nombreUsuario, contrasenia, true)
            }


            //-----------------------------------INTENT------------------------
            // voy al menu de nuevo y me llevo el usuario, INTENT ocn usuario
            val intent = Intent(this@LoginActivity, MenuActivity::class.java)
            intent.putExtra("usuario", usuarioExistente)
            startActivity(intent)

        }
    }

    private fun cargarPreferencias() {
        lifecycleScope.launch {
            // recojo todo lo guardado desde el datastore
            val checkRecordar = preferenciasUsuario.recordar.first() //recojo la preferencia del check
            val nombre = preferenciasUsuario.nombreUsuario.first() //recojo el nombre
            val contrasenia = preferenciasUsuario.contraseñaUsuario.first() //recojo la contraseña

            // check
            binding.checkRecordar.isChecked = checkRecordar

            // si recordar e strue, se rellenan todos los campos de golpe
            if (checkRecordar) {
                binding.nombreUsuario.setText(nombre)
                binding.contrasenia.setText(contrasenia)
            }
        }
    }

    private fun configurarCheck() {

        val actualizarCheck = {
            lifecycleScope.launch {
                val nombreGuardado = preferenciasUsuario.nombreUsuario.first()
                val contraseniaGuardada = preferenciasUsuario.contraseñaUsuario.first()

                val nombreActual = binding.nombreUsuario.text.toString().trim()
                val contraseniaActual = binding.contrasenia.text.toString().trim()

                val coincideUsuario = nombreActual == nombreGuardado
                val coincideContrasenia = contraseniaActual == contraseniaGuardada

                binding.checkRecordar.isChecked = coincideUsuario && coincideContrasenia
            }
        }

        binding.nombreUsuario.addTextChangedListener { actualizarCheck() }
        binding.contrasenia.addTextChangedListener { actualizarCheck() }
    }

}
