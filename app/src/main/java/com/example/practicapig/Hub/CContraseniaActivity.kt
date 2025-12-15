package com.example.practicapig.Hub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.databinding.ActivityCcontraseniaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CContraseniaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCcontraseniaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCcontraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //------------------------------INTENT-------------------------
        // recibo el usuario   INTENT
        val usuario = intent.getParcelableCompat<Usuario>("usuario")

        if (usuario == null) {
            finish()  // por si falla el paso del intent, no peta la aplicacion
            return
        }


        //----------------mensaje----------------------
        var mensaje= " Cambia aquí tu contraseña ${usuario.nombre}"
        binding.texto.text = mensaje

        // si doy al boton de guardar cambios se inicia todas las comprobaciones
        binding.guardarCambios.setOnClickListener {

            val contraseniaVieja = binding.viejaContrasenia.text.toString().trim()
            val contraseniaNueva = binding.nuevaContrasenia.text.toString().trim()
            val repetirContrasenia = binding.repiteContrasenia.text.toString().trim()

            // oculto errores antiguos
            binding.textView7.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.textView6.visibility = View.GONE
            binding.textErrorVacio.visibility = View.GONE

            lifecycleScope.launch {

                var hayFallos=false

                // verifico que no hay campos vacíos
                if (contraseniaVieja.isEmpty() || contraseniaNueva.isEmpty() || repetirContrasenia.isEmpty()) {
                    binding.textErrorVacio.visibility = View.VISIBLE
                    hayFallos=true
                }

                // verifico qie la contraseña que introduce en contraseña vieja es la contraseña que pone en la base de datos
                if (usuario.contraseña != contraseniaVieja) {
                    binding.textView7.visibility = View.VISIBLE
                    hayFallos=true
                }

                // valido nueva contraseña, tiene el formato correcto
                var contieneNumero = false
                for (c in contraseniaNueva) if (c.isDigit()) contieneNumero = true

                //verifico que la contraseña nueva tiene el largo adecuado
                if (contraseniaNueva.length !in 4..10 || !contieneNumero) {
                    binding.textView5.visibility = View.VISIBLE
                    hayFallos=true
                }
                //verifico que la nueva contraseña no es igual la que ponia en la base de datos
                if(contraseniaNueva == usuario.contraseña){
                    binding.textView3.visibility = View.VISIBLE
                    hayFallos=true
                }

                // confirmo la contraseña, si esta igual o no
                if (contraseniaNueva != repetirContrasenia) {
                    binding.textView6.visibility = View.VISIBLE
                    hayFallos=true
                }

                //paro la ejecucuion, muestro todos los errores si hay algo mal
                if (hayFallos) return@launch


                // introduzco la nueva contraseña en la base de datos
                withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@CContraseniaActivity).usuarioDao()
                    dao.actualizarContrasenia(usuario.nombre, contraseniaNueva)
                }


                //-----------------------------------INTENT----------------------------------
                // vuelvo al menu con el usuario actualizado, INTENT con usuario
                val intent = Intent(this@CContraseniaActivity, MenuActivity::class.java)
                val usuarioActualizado = Usuario(usuario.nombre, contraseniaNueva, usuario.fecha_nacimiento)
                intent.putExtra("usuario", usuarioActualizado)
                startActivity(intent)

            }
        }
    }
}



