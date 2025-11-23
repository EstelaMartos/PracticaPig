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

        // cuando doy al boton de registrar comienzo todas las comprobaciones

        binding.botonRegistro.setOnClickListener {

            val nombre = binding.nombreUsuario.text.toString().trim()
            val contrasenia = binding.contrasenia.text.toString().trim()
            val repetirContrasenia = binding.repetirContrasenia.text.toString().trim()
            val fecha = binding.fechaNacimiento.text.toString().trim()
            val checkCondiciones = binding.checkBoxCondiciones.isChecked

            // oculto errores anteriores
            binding.textoErrorUsuario.visibility = View.GONE
            binding.textoErrorCampos.visibility = View.GONE
            binding.textoErrorEdad.visibility = View.GONE
            binding.textoErrorContrasenia.visibility = View.GONE
            binding.textoContrasenia.visibility = View.GONE
            binding.textoErrorCheckbox.visibility = View.GONE

            lifecycleScope.launch {

                if (nombre.isEmpty() || contrasenia.isEmpty() || repetirContrasenia.isEmpty() || fecha.isEmpty()) {
                    binding.textoErrorCampos.visibility = View.VISIBLE
                    binding.textoContrasenia.visibility = View.VISIBLE
                    return@launch
                }

                var hayErrores = false

                // compruebo campos vacios
                if (nombre.isEmpty() || contrasenia.isEmpty() || repetirContrasenia.isEmpty() || fecha.isEmpty()) {
                    binding.textoErrorCampos.visibility = View.VISIBLE
                    hayErrores = true
                }

                // compruebo el check de condiciones
                if (!checkCondiciones) {
                    binding.textoErrorCheckbox.visibility = View.VISIBLE
                    hayErrores = true
                }

                // compruebo contraseña formato correcto
                var contieneNumero = false
                for (d in contrasenia) if (d.isDigit()) contieneNumero = true

                if (contrasenia.length !in 4..10 || !contieneNumero) {
                    binding.textoContrasenia.visibility = View.VISIBLE
                    hayErrores = true
                }else{
                    binding.textoContrasenia.visibility = View.GONE
                }

                // compruebo segunda contraseña=primera contraseña
                if (contrasenia != repetirContrasenia) {
                    binding.textoErrorContrasenia.visibility = View.VISIBLE
                    hayErrores = true
                }

                // compruebo que es mayor de edad
                if (!esMayorEdad(fecha)) {
                    binding.textoErrorEdad.visibility = View.VISIBLE
                    hayErrores = true
                }

                // compruebo si existe el usuario
                val usuarioExistente = withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()
                    dao.buscarPorNombre(nombre)
                }

                if (usuarioExistente != null||nombre.length < 4 || nombre.length > 10) {
                    binding.textoErrorUsuario.visibility = View.VISIBLE
                    hayErrores = true
                }

                // si se han encontrado errores arriba, el programa no avanza y saltan los errores
                if (hayErrores) return@launch

                // si todo va bien, introduzco en nuevo usuario en la base de datos
                withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()
                    dao.insertarUsuario(Usuario(nombre, contrasenia, fecha))
                }

                //---------------------INTENT-----------------------------
                // paso a la pantalla de login con INTENT vacio, no hace falta pasar nada aqui
                //debe iniciar sesion una vez alli con el usuario que quiera
                val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        //-----------------------INTENT----------------------------------
        // para ir al login si ya estas registrado con INTENT vacio tambien porque no has hecho nada el usuario ya estaba creado
        binding.botonInicioSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }

    // muestro el calendario
    private fun mostrarDatePicker() {
        val calendarioActual = Calendar.getInstance()

        val anioActual = calendarioActual.get(Calendar.YEAR)
        val mesActual = calendarioActual.get(Calendar.MONTH)
        val diaActual = calendarioActual.get(Calendar.DAY_OF_MONTH)

        val selectorFecha = DatePickerDialog(
            this,
            { _: DatePicker, anioSeleccionado: Int, mesSeleccionado: Int, diaSeleccionado: Int ->
                binding.fechaNacimiento.setText("$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado")
            },
            anioActual,
            mesActual,
            diaActual
        )

        selectorFecha.show()
    }

    // compruebo que sea mayor de edad
    private fun esMayorEdad(fecha: String): Boolean {

        val partesFecha = fecha.split("/")

        if (partesFecha.size != 3) return false

        val diaNacimiento = partesFecha[0].toInt()
        val mesNacimiento = partesFecha[1].toInt() - 1   // meses va de 0 a 11
        val anioNacimiento = partesFecha[2].toInt()

        val hoy = Calendar.getInstance()

        //introduzco en una variable la fecha de usuario
        val fechaNacimiento = Calendar.getInstance()
        fechaNacimiento.set(anioNacimiento, mesNacimiento, diaNacimiento)

        //calculo la edad cogiendo la fecha actual y restandole la edad que ha introducido el usuario
        var edad = hoy.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR)

        //comparo los dias de ambas fechas para ver si ha pasado el cumpleaños
        val diaDelAñoActual = hoy.get(Calendar.DAY_OF_YEAR)
        val diaDelAñoNacimiento = fechaNacimiento.get(Calendar.DAY_OF_YEAR)

        // si aun no ha pasado su cumpleaños, resto 1 a la edad
        if (diaDelAñoActual < diaDelAñoNacimiento) {
            edad--
        }

        // aqui ya devuelvo si es mayor o no
        return edad >= 18
    }
}
