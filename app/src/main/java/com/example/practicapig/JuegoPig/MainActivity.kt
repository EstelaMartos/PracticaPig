package com.example.practicapig.JuegoPig

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityMainBinding
import android.content.Intent
import com.example.practicapig.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //estos valores ya llegan decididos por la pantalla anterior
    private var jugadoresSeleccionados: Int = 0
    private var rondasSeleccionadas: Int = 0
    var contadorRondas = 0 //contador de rondas

    private var indiceJugador = 0 //índice del jugador al que le toca
    private lateinit var listaJugadores: List<String> //lista de nombres en ORDEN DE TURNO (barajado)
    private lateinit var puntosTotales: IntArray //acumulado de puntos por jugador
    private var puntosTurno = 0 //puntos acumulados en el turno actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ------- recojo  del Intent que envía la SegundaActivity
        jugadoresSeleccionados = intent.getIntExtra("jugadores", 0)
        rondasSeleccionadas = intent.getIntExtra("rondas", 0)
        val nombresSeleccionados = intent.getStringArrayExtra("nombresSeleccionados")
        //------hasta aquii los intent

        // esto queda obsoleto porque obviamente los datos son correctos porque ya lo compruebo en la anterior activity
        //pero lo mantengo porque sino me obliga a cambiar cosas para el suffled
        if (jugadoresSeleccionados !in 2..4 || rondasSeleccionadas <= 0 || nombresSeleccionados == null || nombresSeleccionados.size != jugadoresSeleccionados) {
            Toast.makeText(this, "Datos de partida inválidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // -----------barajo el orden de turno los jugadores jugarán en un orden aleatorio
        listaJugadores = nombresSeleccionados.toList().shuffled()  //nombresSeleccionados son los nombres que me vienen de la segunda activity

        // ---------pongo nombres en cabeceras y visibilidad según numero de jugadores
        aplicarNombresEnCabeceras(listaJugadores, jugadoresSeleccionados)
        when (jugadoresSeleccionados) {
            2 -> visibilidadJugadores(false, false) // no hago visible ni el jugador 3 ni 4
            3 -> visibilidadJugadores(true,  false) // muestro jugador 3, oculto jugador 4
            4 -> visibilidadJugadores(true,  true)  //muestro jugador 3 y 4
        }

        // -----------guardo la lista de jugadores y arranco la partida
        iniciarPartida(jugadoresSeleccionados, rondasSeleccionadas)
    }

    private fun iniciarPartida(jugadores: Int, rondas: Int) {// se inicia la partida con numero de rondas y de jugadores
        //esto ahora queda un poco obsoleto porque ya no hay que esperar a que seleccionen nada para habilitar los botones pero lo mantengo por si acaso
        binding.buttonPasarTurno.isEnabled = true    // ahora si se puede pasar turno y tirar dado
        binding.imageView.isEnabled = true           // inhabilitado antes, habilitado al empezar

        // creo el array de puntos totales según el numero de jugadores
        puntosTotales = IntArray(listaJugadores.size) { 0 }


        //tambien queda un poco obsoleto pero mantengo por si acaso
        contadorRondas = 1 // para que se reinicie la partida
        indiceJugador = 0  // para que se reinicie la partida
        puntosTurno = 0    // para que se reinicie la partida

        // vuelve todo a 0 para iniciar una nueva partida
        binding.textNumeroRondaActual.text = contadorRondas.toString()
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador] //muestra el nombre del jugador según el orden barajado
        binding.textPuntuacionActual2.text = "0"
        binding.textPuntuacionJ1.text = "0"
        binding.textPuntuacionJ2.text = "0"
        binding.textPuntuacionJ3.text = "0"
        binding.textPuntuacionJ4.text = "0"

        // se inician las rondas
        //-------- tirar el dado
        binding.imageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val tirada = (1..6).random() // cada vez que pulsas el dado sale un valor random que va de 1 a 6
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
                    binding.textPuntuacionActual2.text = puntosTurno.toString() // se muestra en el textview
                }
            }
        })

        // paso turno (guardar puntos y avanzar)
        binding.buttonPasarTurno.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // sumo puntos del turno al total del jugador actual
                puntosTotales[indiceJugador] += puntosTurno

                // actualizo marcador total del jugador que corresponda
                when (indiceJugador) {
                    0 -> binding.textPuntuacionJ1.text = puntosTotales[0].toString()
                    1 -> binding.textPuntuacionJ2.text = puntosTotales[1].toString()
                    2 -> binding.textPuntuacionJ3.text = puntosTotales[2].toString()
                    3 -> binding.textPuntuacionJ4.text = puntosTotales[3].toString()
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


        val rondasMaximas = rondasSeleccionadas

        if (contadorRondas > rondasMaximas) { // si el contador supera el número de rondas, se acaba la partida
            binding.buttonPasarTurno.isEnabled = false
            binding.imageView.isEnabled = false
            // ahora calculo los ganadores
            val maxPuntos = puntosTotales.maxOrNull() // max or null es una funcion que calcula el número máximo de la lista de totales
            val ganadores = mutableListOf<String>() // lista de los ganadores(en caso de que haya mas de uno)

            // en el array de puntos totales se va comparando siempre con el maximo y si es igual se añade a la lista de ganadores
            var pos = 0
            while (pos < puntosTotales.size) {
                if (puntosTotales[pos] == maxPuntos) {
                    ganadores.add(listaJugadores[pos]) // posicion de la lista de jugadores, es la misma posicion que en el array de puntos totales
                }
                pos++
            }

            // muestro el texto de los ganadores y activo la visibilidad de ese texto
            val mensaje = if (ganadores.size == 1) { // creo una variable con el mensaje para el caso de un solo ganador
                "Ganador: ${ganadores[0]} con $maxPuntos puntos" // solo hay un ganador por lo tanto esta en la posicion 0 de la lista
            } else {
                "Empate entre: ${ganadores.joinToString(", ")} con $maxPuntos puntos" // creo una variable con el mensaje para el caso de un empate
            }


            val intentResultado = Intent(this, UltimaActivity::class.java)
            // paso el orden de jugadores
            intentResultado.putExtra("nombresOrden", ArrayList(listaJugadores))
            // paso las puntuaciones
            intentResultado.putExtra("puntosOrden", puntosTotales)
            // paso el mensaje final ya formateado
            intentResultado.putExtra("mensajeFinal", mensaje)

            startActivity(intentResultado)
            finish() // cierro esta activity
            return   //hago que no siga ejecutando y pase ya a la siguiente activity
        }

        binding.textNumeroRondaActual.text = contadorRondas.toString() // muestro la ronda por la que se va

        // muestro a quien le corresponde el turno
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador] //  nombre según orden barajado
    }

    private fun visibilidadJugadores(visible3: Boolean, visible4: Boolean) { // control de visibilidad
        binding.textJugador3.visibility = if (visible3) View.VISIBLE else View.GONE  // maneja la visibilidad del texto de jugador 3 si se selecciona la opcion de 3 jugadores
        binding.textPuntuacionJ3.visibility = if (visible3) View.VISIBLE else View.GONE // maneja la visibilidad del marcador del tercer jugador
        binding.textJugador4.visibility = if (visible4) View.VISIBLE else View.GONE // maneja la visibilidad del del texto del jugador 4 si se seleccionan 4 jugadores
        binding.textPuntuacionJ4.visibility = if (visible4) View.VISIBLE else View.GONE // maneja la visibilidad del marcador del cuarto jugador
    }


    //este metodo es mas bien para volver a iniciar otra nueva partida, en este momento ya no se hace pero por si en un futuro vuelve
    //a hacer falta, lo mantengo, además también se usa de inicio sin reiniciar
    private fun aplicarNombresEnCabeceras(nombres: List<String>, jugadores: Int){
        binding.textJugador1.text = nombres[0]
        binding.textJugador2.text = nombres[1]
        if (jugadores >= 3) binding.textJugador3.text = nombres[2]
        if (jugadores == 4) binding.textJugador4.text = nombres[3]
    }
}
