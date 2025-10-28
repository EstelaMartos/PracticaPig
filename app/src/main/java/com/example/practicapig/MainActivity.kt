package com.example.practicapig

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var jugadoresSeleccionados: Int? = null
    private var rondasSeleccionadas: Int? = null
    var contadorRondas= 0

    private var indiceJugador = 0
    private lateinit var listaJugadores: List<String>
    private lateinit var puntosTotales: IntArray
    private var puntosTurno = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPasarTurno.isEnabled = false//inhabilito el boton de pasar turno
        binding.imageView.isEnabled = false//inhabilito el dado


        val numJugadores=listOf("Selecciona jugadores","2","3","4")
        val numRondas=listOf("Selecciona rondas","2","4","6")

        //adapter del spinner de jugadores
       val adapterJugadores= ArrayAdapter(this,android.R.layout.simple_spinner_item,numJugadores)
        adapterJugadores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJugadores.adapter=adapterJugadores


        binding.spinnerJugadores.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{ //me situo en el spinner, lo pongo para que salga hacia abajo el menu
            //creo el objeto del adapter que me permite crear las siguientes funciones:
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {//posicion empieza en 0,1,2... para las posiciones del spinner
                if (position == 0) {
                    jugadoresSeleccionados = null//si seleccionan la primera opcion que es 0 hago que sea null para que no se inicie la partida
                    visibilidadJugadores(visible3 = false, visible4 = false)//no hago visible ni el jugador 3 ni 4
                    return
                }
                jugadoresSeleccionados=numJugadores[position].toIntOrNull()// coje el valor dentro del spinner y lo convierte a entero
                when(jugadoresSeleccionados){
                    2 -> visibilidadJugadores(visible3 = false, visible4 = false)
                    3 -> visibilidadJugadores(visible3 = true, visible4 = false)
                    4 -> visibilidadJugadores(visible3 = true, visible4 = true)
                    else -> visibilidadJugadores(visible3 = false, visible4 = false) //por defecto no se muestra nada

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        //adapter del spinner de rondas
        val adapterRondas= ArrayAdapter(this,android.R.layout.simple_spinner_item,numRondas)
        adapterRondas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRondas.adapter=adapterRondas


        binding.spinnerRondas.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0){
                    rondasSeleccionadas = null
                    return
                }
                rondasSeleccionadas = numRondas[position].toIntOrNull() //hacemos lo mismo que en el de jugadores
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.buttonIniciarPartida.setOnClickListener {
            if (jugadoresSeleccionados != null && rondasSeleccionadas != null) { //si no se han seleccionado ni jugadores ni rondas sale un aviso y no se inicia partida
                iniciarPartida(jugadoresSeleccionados!!, rondasSeleccionadas!!)
            }else{
                android.widget.Toast.makeText(this, "Por favor, selecciona jugadores y rondas", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun iniciarPartida(jugadores: Int, rondas: Int){//se inicia la partida con numero de rondas y de jugadores
        // Bloquea los spinners y el botón de inicio
        bloquear(binding.spinnerJugadores)
        bloquear(binding.spinnerRondas)
        bloquear(binding.buttonIniciarPartida)
        binding.textGanadores.visibility = View.GONE//el texto de sale de ganador se elimina si empiezas una nueva ronda
        binding.buttonPasarTurno.isEnabled = true//ahora si se puede pasar turno y tirar dado
        binding.imageView.isEnabled = true


        // Construye lista de jugadores según la seleccion de numero de jugadores
        listaJugadores = when (jugadores) {
            2 -> listOf("J1", "J2")
            3 -> listOf("J1", "J2", "J3")
            4 -> listOf("J1", "J2", "J3", "J4")
            else -> emptyList()
        }
        puntosTotales = IntArray(listaJugadores.size) { 0 }


        contadorRondas = 1 //se crea en el onCreate para que se reinicie la partida
        indiceJugador = 0 //se crea en el onCreate para que se reinicie la partida
        puntosTurno = 0 //se crea en el onCreate para que se reinicie la partida



        // Vuelve todo a 0 para iniciar una nueva partida
        binding.textNumeroRondaActual.text = contadorRondas.toString()
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador]
        binding.textPuntuacionActual2.text = "0"
        binding.textPuntuacionJ1.text = "0"
        binding.textPuntuacionJ2.text = "0"
        binding.textPuntuacionJ3.text = "0"
        binding.textPuntuacionJ4.text = "0"

        // se inician las rondas
        jugarRondas(jugadores, rondas)

    }

    private fun jugarRondas(jugadores:Int, rondas:Int){
        // se tira del dado pulsando en la imagen
        binding.imageView.setOnClickListener {
            val tirada = (1..6).random()//cada vez que pulsas el dado sale un valor random que va de 1 a 6

            // Actualiza la imagen del dado si tienes dado_n1..dado_n6
            when (tirada) {
                1 -> binding.imageView.setImageResource(R.drawable.dado_n1)
                2 -> binding.imageView.setImageResource(R.drawable.dado_n2)
                3 -> binding.imageView.setImageResource(R.drawable.dado_n3)
                4 -> binding.imageView.setImageResource(R.drawable.dado_n4)
                5 -> binding.imageView.setImageResource(R.drawable.dado_n5)
                6 -> binding.imageView.setImageResource(R.drawable.dado_n6)
                else -> {
                    //coloco un dado por defecto en caso de no ser un número valido aunque no puede serlo
                    binding.imageView.setImageResource(R.drawable.dado_cerdos)
                }
            }
            if (tirada == 1) {
                // pierde puntos del turno
                puntosTurno = 0
                binding.textPuntuacionActual2.text = "0"
                // pasa turno automáticamente
                avanzarTurno()
            } else {
                // suma y muestra puntos del turno
                puntosTurno += tirada//los puntos del turno son los equivalentes al número del dado
                binding.textPuntuacionActual2.text = puntosTurno.toString()//se muestra en el textview
            }
        }

        // Pasar turno (guardar puntos y avanzar)
        binding.buttonPasarTurno.setOnClickListener {
            // Suma puntos del turno al total del jugador actual
            puntosTotales[indiceJugador] += puntosTurno

            // Actualiza marcador total en su TextView
            when (indiceJugador) {
                0 -> binding.textPuntuacionJ1.text = puntosTotales[0].toString()
                1 -> binding.textPuntuacionJ2.text = puntosTotales[1].toString()
                2 -> binding.textPuntuacionJ3.text = puntosTotales[2].toString()
                3 -> binding.textPuntuacionJ4.text = puntosTotales[3].toString()
            }

            // Resetea puntos de turno
            puntosTurno = 0
            binding.textPuntuacionActual2.text = "0"

            // Avanza turno
            avanzarTurno()
        }
    }

    private fun avanzarTurno() {//le toca su turno a otro jugador si sale 1 o pasan turno

        // Siguiente jugador
       // indiceJugador = (indiceJugador + 1) % listaJugadores.size
        if (indiceJugador == listaJugadores.size - 1) {  //si el indice del jugador es igual a 0 entonces se vuelve a J1 y se suma una ronda
            indiceJugador = 0
            contadorRondas++
        } else {
            indiceJugador++ //sino, se pasa al siguiente jugador
        }


        val rondasMaximas=rondasSeleccionadas.toString().toInt()//hago fija la variable de las rondas para evitar usar el operador elvis que me sugiere el IDE

        if (contadorRondas > rondasMaximas) { //si el contador supera el número de rondas, se acaba la partida, por lo tanto se vuelve  ahabilitar los botones de seleccion
            binding.buttonPasarTurno.isEnabled = false
            binding.imageView.isEnabled = false
            // Ahora calculo los ganadores
            val maxPuntos = puntosTotales.maxOrNull()//max or null es una funcion que calcula el número máximo de la lista de totales
            val ganadores = mutableListOf<String>()//lista de los ganadores(en caso de que haya mas de uno)

            for (posicion in puntosTotales.indices) {//en el array de puntos totales se va comparando siempre con el maximo y si es igual se añade a la lista de ganadores
                if (puntosTotales[posicion] == maxPuntos) {//posicion de los puntos totales(si es 1,es el jugador es el que esta en posicion 1
                    ganadores.add(listaJugadores[posicion])//posicion de la lista de jugadores, es la misma posicion que en el array de puntos totales
                }
            }

            // muestro el texto de los ganadores y activo la visibilidad de ese texto
            val mensaje = if (ganadores.size == 1) {//creo una variable con el mensaje para el caso de un solo ganador
                "Ganador: ${ganadores[0]} con $maxPuntos puntos"//solo hay un ganador por lo tanto esta en la posicion 0 de la lista
            } else {
                "Empate entre: ${ganadores.joinToString(", ")} con $maxPuntos puntos"//creo una variable con el mensaje para el caso de un empate
                //en este caso estoy pasando la lista de ganadores a string y los une con una coma para postrarlos en pantalla
            }



            binding.textGanadores.visibility = View.VISIBLE//activo la visibilidad del texto de ganadores
            binding.textGanadores.text = mensaje//hago que en ese cuadro de texto aparezca el mensaje de ganadores


            // Vuelvo a habilitar lo spinner y el boton de iniciar partida
            binding.spinnerJugadores.isEnabled = true
            binding.spinnerJugadores.isClickable = true
            binding.spinnerJugadores.alpha = 1f

            binding.spinnerRondas.isEnabled = true
            binding.spinnerRondas.isClickable = true
            binding.spinnerRondas.alpha = 1f

            binding.buttonIniciarPartida.isEnabled = true
            binding.buttonIniciarPartida.isClickable = true
            binding.buttonIniciarPartida.alpha = 1f

            return
        }
        binding.textNumeroRondaActual.text = contadorRondas.toString()//muestro la ronda por la que se va

        // muestro a quien le corresponde el turno
        binding.textIdTurnoJugador.text = listaJugadores[indiceJugador]
    }
    private fun visibilidadJugadores(visible3: Boolean, visible4: Boolean) {
        binding.textJugador3.visibility = if (visible3) View.VISIBLE else View.GONE  // Establece la visibilidad del texto de jugador 3 si se selecciona la opcion de 3 jugadores
        binding.textPuntuacionJ3.visibility = if (visible3) View.VISIBLE else View.GONE // Establece la visibilidad del marcador del tercer jugador

        binding.textJugador4.visibility = if (visible4) View.VISIBLE else View.GONE // Establece la visibilidad del del texto del jugador 4 si se seleccionan 4 jugadores
        binding.textPuntuacionJ4.visibility = if (visible4) View.VISIBLE else View.GONE // Establece la visibilidad del marcador del cuarto jugador
    }

    private fun bloquear(v: View) { //para bloquear los spinner cuando ya se ha seleccionado el numero de jugadores y rondas y se ha dado al boton de iniciar partida
        v.isEnabled = false
        v.isClickable = false
        v.alpha = 0.6f//baja la opacidad
    }


}