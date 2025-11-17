package com.example.practicapig.Hub

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.databinding.ActivityRegistroBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private lateinit var binding: ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // abro el calendario
        binding.fechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        // Botón de registrarse
        binding.botonRegistro.setOnClickListener {

            // recogo los datos
            val nombre = binding.nombreUsuario.text.toString()
            val pass = binding.contrasenia.text.toString()
            val repetir = binding.repetirContrasenia.text.toString()
            val fecha = binding.fechaNacimiento.text.toString()
            val check = binding.checkBoxCondiciones.isChecked

            //oculto errores anteriores
            binding.textoErrorUsuario.visibility = View.GONE
            binding.textoErrorCampos.visibility = View.GONE
            binding.textoErrorEdad.visibility = View.GONE
            binding.textoErrorContrasenia.visibility = View.GONE
            binding.textoContrasenia.visibility = View.VISIBLE
            binding.textoErrorCheckbox.visibility = View.GONE

            lifecycleScope.launch {

                // valido que no hay nada vacio
                if (nombre.isEmpty() || pass.isEmpty() || repetir.isEmpty() || fecha.isEmpty()) {
                    binding.textoErrorCampos.visibility = View.VISIBLE
                    return@launch
                }

                // valido que esta bien puesta la casilla de condiciones
                if (!check) {
                    binding.textoErrorCheckbox.visibility = View.VISIBLE
                    return@launch
                }

                // valido que la contraseña es correcta
                var contieneNumero = false
                for (d in pass) if (d.isDigit()) contieneNumero = true

                if (pass.length !in 4..10 || !contieneNumero) {
                    binding.textoContrasenia.visibility = View.VISIBLE
                    return@launch
                }

                // si las contraseñas son iguales esta bien
                if (pass != repetir) {
                    binding.textoErrorContrasenia.visibility = View.VISIBLE
                    return@launch
                }

                // valido que es mayor de edad
                if (!esMayorEdad(fecha)) {
                    binding.textoErrorEdad.visibility = View.VISIBLE
                    return@launch
                }

                // valido que el usuario existe
                val usuarioExistente = withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()
                    dao.buscarPorNombre(nombre)
                }

                if (usuarioExistente != null) {
                    binding.textoErrorUsuario.visibility = View.VISIBLE
                    return@launch
                }

                // inserto usuario en la base de datos
                withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()
                    dao.insertarUsuario(Usuario(nombre, pass, fecha))
                }

                // realizo intent para ir a la pantalla login
                val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Botón de ir a Login sin registrarse
        binding.botonInicioSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // --- CALENDARIO ---
    private fun mostrarDatePicker() {
        val c = Calendar.getInstance()
        val dp = DatePickerDialog(
            this,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                binding.fechaNacimiento.setText("$d/${m + 1}/$y")
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        dp.show()
    }

    // --- EDAD ---
    private fun esMayorEdad(fecha: String): Boolean {
        val p = fecha.split("/")
        if (p.size != 3) return false

        val d = p[0].toInt()
        val m = p[1].toInt() - 1
        val y = p[2].toInt()

        val hoy = Calendar.getInstance()
        val nac = Calendar.getInstance()
        nac.set(y, m, d)

        var edad = hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nac.get(Calendar.DAY_OF_YEAR)) edad--

        return edad >= 18
    }
}
