package com.example.practicapig.JuegoPig

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import com.example.practicapig.R
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //estos valores ya llegan decididos por la pantalla anterior
    private lateinit var juego: Juego
    private lateinit var listaJugadores: ArrayList<Jugador>

    // datos propios de esta activity
    private var contadorRondas = 1
    private var indiceJugador = 0
    private var puntosTurno = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ------- recojo datos del Intent que envía la SegundaActivity
        juego = intent.getParcelableCompat<Juego>("juego")!!
        listaJugadores = intent.getParcelableArrayListCompat<Jugador>("jugadoresLista") ?: arrayListOf()


        //------hasta aquii los intent

        // esto queda obsoleto porque obviamente los datos son correctos porque ya lo compruebo en la anterior activity
        //pero lo mantengo porque sino me obliga a cambiar cosas para el suffled
        if (juego.numJugadores !in 2..4 || juego.numRondas <= 0 || listaJugadores.size != juego.numJugadores) {
            Toast.makeText(this, "Datos de partida inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // -----------barajo el orden de turno los jugadores jugarán en un orden aleatorio
        listaJugadores.shuffle()  //nombresSeleccionados son los nombres que me vienen de la segunda activity

        // ---------pongo nombres en cabeceras y visibilidad según numero de jugadores
        aplicarNombresEnCabeceras(listaJugadores)
        when (juego.numJugadores) {
            2 -> visibilidadJugadores(false, false) // no hago visible ni el jugador 3 ni 4
            3 -> visibilidadJugadores(true, false) // muestro jugador 3, oculto jugador 4
            4 -> visibilidadJugadores(true, true)  //muestro jugador 3 y 4
        }

        binding.textIdTurnoJugador.text = listaJugadores[0].nombre
        binding.textNumeroRondaActual.text = contadorRondas.toString()

        // -----------guardo la lista de jugadores y arranco la partida
        iniciarPartida()
    }

    private fun iniciarPartida() {// se inicia la partida con numero de rondas y de jugadores
        //esto ahora queda un poco obsoleto porque ya no hay que esperar a que seleccionen nada para habilitar los botones pero lo mantengo por si acaso
        binding.buttonPasarTurno.isEnabled = true    // ahora si se puede pasar turno y tirar dado
        binding.imageView.isEnabled = true           // inhabilitado antes, habilitado al empezar

        // creo el array de puntos totales según el numero de jugadores
        //puntosTotales = IntArray(listaJugadores.size) { 0 }


        //reiniciarMarcadores()  de momento lo desactivo porque no es necesario

        // se inician las rondas
        //-------- tirar el dado
        binding.imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val tirada =
                    (1..6).random() // cada vez que pulsas el dado sale un valor random que va de 1 a 6
                // actualizo la imagen del dado si segun 1,2,3...
                when (tirada) {
                    1 -> binding.imageView.setImageResource(R.drawable.dado_n1)
                    2 -> binding.imageView.setImageResource(R.drawable.dado_n2)
                    3 -> binding.imageView.setImageResource(R.drawable.dado_n3)
                    4 -> binding.imageView.setImageResource(R.drawable.dado_n4)
                    5 -> binding.imageView.setImageResource(R.drawable.dado_n5)
                    6 -> binding.imageView.setImageResource(R.drawable.dado_n6)
                    else -> binding.imageView.setImageResource(R.drawable.dado_cerdos)
                }

                if (tirada == 1) {
                    // pierde puntos del turno
                    puntosTurno = 0
                    binding.textPuntuacionActual2.text = "0"
                    // pasa turno automáticamente
                    avanzarTurno()
                } else {
                    // suma y muestra puntos del turno
                    puntosTurno += tirada // los puntos del turno son los equivalentes al número del dado
                    binding.textPuntuacionActual2.text =
                        puntosTurno.toString() // se muestra en el textview
                }
            }
        })

        // paso turno (guardar puntos y avanzar)
        binding.buttonPasarTurno.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // sumo puntos del turno al total del jugador actual
                listaJugadores[indiceJugador].puntos += puntosTurno

                // actualizo marcador total del jugador que corresponda
                when (indiceJugador) {
                    0 -> binding.textJugador1.text = listaJugadores[0].toString()
                    1 -> binding.textJugador2.text = listaJugadores[1].toString()
                    2 -> binding.textJugador3.text = listaJugadores[2].toString()
                    3 -> binding.textJugador4.text = listaJugadores[3].toString()
                }

                // reseteo puntos de turno
                puntosTurno = 0
                binding.textPuntuacionActual2.text = "0"

                // avanza el turno
                avanzarTurno()
            }
        })
    }


    private fun avanzarTurno() { // le toca su turno a otro jugador si sale 1 o pasan turno
        // siguiente jugador
        if (indiceJugador == listaJugadores.size - 1) {  // si el indice del jugador es igual a 0 entonces se vuelve a J1 y se suma una ronda
            indiceJugador = 0
            contadorRondas++
        } else {
            indiceJugador++ // sino, se pasa al siguiente jugador
        }


        //val rondasMaximas = rondasSeleccionadas

        if (contadorRondas > juego.numRondas) { // si el contador supera el número de rondas, se acaba la partida
            binding.buttonPasarTurno.isEnabled = false
            binding.imageView.isEnabled = false

            //hago los intent para pasar la informacion a la ultima activity

            val intentResultado = Intent(this, UltimaActivity::class.java)
            // paso el orden de jugadores
            intentResultado.putExtra("juego", juego)
            // paso las puntuaciones
            intentResultado.putParcelableArrayListExtra("jugadoresFinales", listaJugadores)
            startActivity(intentResultado)
            finish() // cierro esta activity
            return   //hago que no siga ejecutando y pase ya a la siguiente activity
        }

        binding.textNumeroRondaActual.text = contadorRondas.toString() // muestro la ronda por la que se va

        // muestro a quien le corresponde el turno
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador].nombre //  nombre según orden barajado
    }


    private fun reiniciarMarcadores() {
        //tambien queda un poco obsoleto pero mantengo por si acaso
        contadorRondas = 1 // para que se reinicie la partida
        indiceJugador = 0  // para que se reinicie la partida
        puntosTurno = 0    // para que se reinicie la partida

        // vuelve todo a 0 para iniciar una nueva partida
        binding.textNumeroRondaActual.text = contadorRondas.toString()
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador].nombre //muestra el nombre del jugador según el orden barajado
        binding.textPuntuacionActual2.text = "0"

        for (jugador in listaJugadores) {
            jugador.puntos = 0
        }
        binding.textJugador1.text = listaJugadores[0].toString()
        binding.textJugador2.text = listaJugadores[1].toString()
        binding.textJugador3.text = listaJugadores[2].toString()
        binding.textJugador4.text = listaJugadores[3].toString()
    }

    private fun visibilidadJugadores(
        visible3: Boolean,
        visible4: Boolean
    ) { // control de visibilidad
        binding.textJugador3.visibility = if (visible3) View.VISIBLE else View.GONE  // maneja la visibilidad del texto de jugador 3 si se selecciona la opcion de 3 jugadores

        binding.textJugador4.visibility = if (visible4) View.VISIBLE else View.GONE // maneja la visibilidad del del texto del jugador 4 si se seleccionan 4 jugadores

    }

    //este metodo es mas bien para volver a iniciar otra nueva partida, en este momento ya no se hace pero por si en un futuro vuelve
    //a hacer falta, lo mantengo, además también se usa de inicio sin reiniciar
    private fun aplicarNombresEnCabeceras(jugadores: List<Jugador>) {
        binding.textJugador1.text = jugadores[0].toString()
        binding.textJugador2.text = jugadores[1].toString()
        if (jugadores.size >= 3) binding.textJugador3.text = jugadores[2].toString()
        if (jugadores.size == 4) binding.textJugador4.text = jugadores[3].toString()
    }

}
inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableExtra(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelableExtra(key)


inline fun <reified T : Parcelable> Intent.getParcelableArrayListCompat(key: String): ArrayList<T>? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableArrayListExtra(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelableArrayListExtra(key)


    //necesario para crear el objeto

