package com.example.practicapig.Hub

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practicapig.BaseDeDatos.DatabaseUsuarios
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.ConsumoApis.ApiResponse
import com.example.practicapig.ConsumoApis.ApiService
import com.example.practicapig.databinding.ActivityRegistroBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private lateinit var binding: ActivityRegistroBinding




class RegistroActivity : AppCompatActivity() {

    private var avatarSeleccionado: String? = null
    private var generoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // abro el calendario
        binding.fechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        //selecciono genero
        seleccionarGenero()

        //seleccionar avatar
        binding.imagenAvatar1.setOnClickListener {
            avatarSeleccionado = binding.imagenAvatar1.tag as String
            marcarAvatarSeleccionado(1)
        }

        binding.imagenAvatar2.setOnClickListener {
            avatarSeleccionado = binding.imagenAvatar2.tag as String
            marcarAvatarSeleccionado(2)
        }

        //refrescar avatares
        binding.buttonRefrescar.setOnClickListener {
            avatarSeleccionado = null
            generoSeleccionado?.let { cargarAvatares(it) }
        }

        // cuando doy al boton de registrar comienzo todas las comprobaciones

        binding.botonRegistro.setOnClickListener {

            val nombre = binding.nombreUsuario.text.toString().trim()
            val contrasenia = binding.contrasenia.text.toString().trim()
            val repetirContrasenia = binding.repetirContrasenia.text.toString().trim()
            val fecha = binding.fechaNacimiento.text.toString().trim() //aqui se almacena la fecha seleccionada por el usuario
            val checkCondiciones = binding.checkBoxCondiciones.isChecked



            // oculto errores anteriores
            binding.textoErrorUsuario.visibility = View.GONE
            binding.textoErrorCampos.visibility = View.GONE
            binding.textoErrorEdad.visibility = View.GONE
            binding.textoErrorContrasenia.visibility = View.GONE
            binding.textoContrasenia.visibility = View.GONE
            binding.textoErrorCheckbox.visibility = View.GONE

            lifecycleScope.launch {

                //si hay algun campo vacio muestro mensaje de error y no sigo
                if (nombre.isEmpty() || contrasenia.isEmpty() || repetirContrasenia.isEmpty() || fecha.isEmpty() ) {
                    binding.textoErrorCampos.visibility = View.VISIBLE
                    binding.textoContrasenia.visibility = View.VISIBLE
                    return@launch
                }

                var hayErrores = false

                // compruebo el check de condiciones
                if (!checkCondiciones) {
                    binding.textoErrorCheckbox.visibility = View.VISIBLE

                }

                //compruebo que no dejen vacío el campo del avatar
                if (avatarSeleccionado == null) {
                    binding.textErrorAvatar.visibility = View.VISIBLE
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
                val usuarioExistente = withContext(Dispatchers.IO) {//cambio de hilo para lanzar la consulta
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()//obtiene la base de datos y accede a las consultas
                    dao.buscarPorNombre(nombre)//accede a la query buscar por nombre
                } //si el usuario existe devuelve su nombre y si no existe devuelve null

                if (usuarioExistente != null||nombre.length < 4 || nombre.length > 10) {
                    binding.textoErrorUsuario.visibility = View.VISIBLE
                    hayErrores = true
                }

                // si se han encontrado errores arriba, el programa no avanza y saltan los errores
                if (hayErrores) return@launch

                // si todo va bien, introduzco en nuevo usuario en la base de datos
                withContext(Dispatchers.IO) {
                    val dao = DatabaseUsuarios.getDatabase(this@RegistroActivity).usuarioDao()
                    dao.insertarUsuario(Usuario(nombre, contrasenia, fecha, avatarSeleccionado!!))//introduzco el objeto Usuario en la bbdd
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


    //bajo la opacidad al avatar no seleccionado
    private fun marcarAvatarSeleccionado(seleccion: Int) {
        binding.imagenAvatar1.alpha = if (seleccion == 1) 1f else 0.5f
        binding.imagenAvatar2.alpha = if (seleccion == 2) 1f else 0.5f
    }


    //funcion para seleccionar el genero entre hombre mujer deconocido o sin genero de la api
    private fun seleccionarGenero() {

        binding.textHombre.setOnClickListener {
            generoSeleccionado = "Male"
            cargarAvatares("Male")
            visibilidadApi()
        }

        binding.textMujer.setOnClickListener {
            generoSeleccionado = "Female"
            cargarAvatares("Female")
            visibilidadApi()
        }

        binding.textSinGenero.setOnClickListener {
            generoSeleccionado = "Genderless"
            cargarAvatares("Genderless")
            visibilidadApi()
        }

        binding.textDesconocido.setOnClickListener {
            generoSeleccionado = "unknown"
            cargarAvatares("unknown")
            visibilidadApi()
        }
    }




    //-------------------------------uso de apis para cargar los avatares--------------------------------
    private fun cargarAvatares(gender: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getCharactersByGender(gender)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {

                    val personajes = response.body()?.results ?: return
                    val seleccionados = personajes.shuffled().take(2)

                    Picasso.get().load(seleccionados[0].image).into(binding.imagenAvatar1)
                    Picasso.get().load(seleccionados[1].image).into(binding.imagenAvatar2)

                    binding.imagenAvatar1.tag = seleccionados[0].image
                    binding.imagenAvatar2.tag = seleccionados[1].image
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("Estela", "Error api")
            }
        })
    }

    //la visibilidad de los botones
    private fun visibilidadApi(){
        binding.imagenAvatar1.visibility = View.VISIBLE
        binding.imagenAvatar2.visibility = View.VISIBLE
        binding.buttonRefrescar.visibility = View.VISIBLE
        binding.textEligeAvatar.visibility = View.GONE
        binding.textHombre.visibility = View.GONE
        binding.textMujer.visibility = View.GONE
        binding.textSinGenero.visibility = View.GONE
        binding.textDesconocido.visibility = View.GONE
    }
}
