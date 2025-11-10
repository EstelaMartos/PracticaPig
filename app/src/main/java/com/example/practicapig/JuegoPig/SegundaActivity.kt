package com.example.practicapig.JuegoPig

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicapig.databinding.ActivitySegundaBinding

class SegundaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaBinding

    // array que guarda el nombre elegido por cada jugador
    private lateinit var arrayNombresSeleccionados: Array<String?>

    // listas con los recicler y sus respectivos adapters
    private val recyclerViewsJugadores: MutableList<RecyclerView> = mutableListOf() //es la lista de los recicler views que se muestran
    private val adaptersNombrePorJugador: MutableList<NombreAdapter> = mutableListOf()

    // -------datos que llegan desde la primera activiti
    private var rondasSeleccionadas: Int = 0
    private var jugadoresSeleccionados: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //--------recojo los datos del intent

        rondasSeleccionadas = intent.getIntExtra("rondas", 0)
        jugadoresSeleccionados = intent.getIntExtra("jugadores", 0)

        //-------hasta aqui los intent

        // creo el array con tantas posiciones como jugadores hayan seleccionado
        arrayNombresSeleccionados = arrayOfNulls(jugadoresSeleccionados)
        //es un array de nulos en inicio porque al empezar no se ha seleccionado nada


        // ------muestro solo los jugadores seleccionados

        when (jugadoresSeleccionados) {
            2 -> adaptoVisibilidad(false, false)
            3 -> adaptoVisibilidad(true, false)
            4 -> adaptoVisibilidad(true, true)
        }


        // preparo los recicler

        recyclerViewsJugadores.clear()//se limpia la lista, si en un futuro se reinicia la partida esto viene bien
        recyclerViewsJugadores.add(binding.nombreJ1) //los reclicler 1 y dos simepre estan en la lista
        recyclerViewsJugadores.add(binding.nombreJ2)
        // si hay 3 jugadores añado el recicler del jugador 3
        if (jugadoresSeleccionados >= 3) {
            recyclerViewsJugadores.add(binding.nombreJ3)
        }

        // si hay 4 jugadores añado también el del jugador 4
        if (jugadoresSeleccionados == 4) {
            recyclerViewsJugadores.add(binding.nombreJ4)
        }

        // creo los adapters y los asigno

        adaptersNombrePorJugador.clear()//antes de llenar la lista de los nombres la vacio,por si en un futuro la partida se reanuda
        var indiceJugadorActual = 0//inicio la variable
        while (indiceJugadorActual < recyclerViewsJugadores.size) {//si el indice es menos al numero de los recicler, asigno
            val recyclerViewJugador = recyclerViewsJugadores[indiceJugadorActual]//recicler actual asignado al indice del jugador actual
            recyclerViewJugador.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            // dibujo los elementos del recicler en una lista vertical, con el primer elemento arriba y el último abajo
            recyclerViewJugador.setHasFixedSize(true)// para que el recicler no cambie su tamaño

            // creo el adapter del recicler de este jugador y lo guardo
            val adapterJugador = NombreAdapter(this, indiceJugadorActual)//creo un adapter nuevo para el jugador número indiceJugadorActual
            recyclerViewJugador.adapter = adapterJugador//se encarga de llenar el recicler con el item nombre
            adaptersNombrePorJugador.add(adapterJugador)

            indiceJugadorActual++//paso al siguiente jugador
        }
        // ------cargo todos los nombres actualizados
        actualizarListas()
    }


    //   se ejecuta cuando un jugador pulsa un nombre en su lista
    // el adapter llama directamente a esta función
    fun gestionarSeleccionDeNombre(nombreSeleccionado: String, jugadorQueClico: Int) {

        val nombreActual = arrayNombresSeleccionados[jugadorQueClico]

        // si el jugador pulsa de nuevo su mismo nombre, no hago nada
        // pero seguimos con el flujo para comprobar si todos han elegido
        var cambioDeNombre = true
        if (nombreActual == nombreSeleccionado) {
            cambioDeNombre = false
        }
        // si ha cambiado su elección, la guardamos y actualizamos las listas
        if (cambioDeNombre) {
            arrayNombresSeleccionados[jugadorQueClico] = nombreSeleccionado
            actualizarListas()
        }

        // compruebo si todos los jugadores ya han elegido
        var cantidadJugadoresConNombre = 0
        var indice = 0
        while (indice < jugadoresSeleccionados) {
            if (arrayNombresSeleccionados[indice] != null) {
                cantidadJugadoresConNombre++
            }
            indice++
        }

        // si todos han elegido, paso a la siguiente activity
        if (cantidadJugadoresConNombre == jugadoresSeleccionados) {
            pasarASiguienteActivity()
        }
    }


    // actualizo todas las listas (oculto los nombres que ya fueron elegidos)
    //se llama cada vez que un jugador elige un nombre
    //muestra solo los nombres disponibles
    //resalta en negrita el nombre que cada jugador ha elegido
    private fun actualizarListas() {
        val nombresYaTomados = arrayNombresSeleccionados.filterNotNull() //son los nombres que ya estan cogidos por los demas jugadores

        var indiceJugador = 0
        while (indiceJugador < adaptersNombrePorJugador.size) { //recorro los adapters
            val adapter = adaptersNombrePorJugador[indiceJugador] //obtenemos el adapter del jugador actual
            val miSeleccion = arrayNombresSeleccionados[indiceJugador] //recupero el nombre que eligió ese jugador

            // creo una lista filtrada con:
            // su nombre actual en negrita
            // los nombres que no estén elegidos por otros
            val listaFiltrada = ArrayList<String>() //lista que contiene los nombres sin los nombres selccionados por otros
            for (nombre in NOMBRES) { //recorro la lista d elos nombres
                var esMiSeleccion = false
                if (miSeleccion != null) { //si mi seleccion no es null
                    if (miSeleccion == nombre) { //si mi seleccion es un mobre
                        esMiSeleccion = true //es true que es mi seleccion
                    }
                }

                //si el nombre no esta tomado, se muestra, si esta tomado pero es el que he elegido, se muestra en negrita,
                // si ya esta seleccionado por otro, no s epuede seleccionar
                val estaTomado = nombresYaTomados.contains(nombre)
                if (esMiSeleccion || !estaTomado) { //si mi seleccion no esta elegida por otro
                    listaFiltrada.add(nombre)//a la lista filtrada, añado mi eleccion
                }
            }

            adapter.actualizarDatos(listaFiltrada, miSeleccion)
            indiceJugador++
        }
    }


    //una vez elegidos todos los nombres, paso a la siguiente activity

    private fun pasarASiguienteActivity() {
        // creo un array de Strings con el tamaño del numero de los jugadores seleccionados
        val nombresFinales = Array(jugadoresSeleccionados) { "" }

        // relleno el array
        for (indiceJugador in 0 until jugadoresSeleccionados) { //
            nombresFinales[indiceJugador] = arrayNombresSeleccionados[indiceJugador] ?: ""
        }

        //---------------------hago los intent
        val intentJuego = Intent(this, MainActivity::class.java)
        intentJuego.putExtra("jugadores", jugadoresSeleccionados)
        intentJuego.putExtra("rondas", rondasSeleccionadas)
        intentJuego.putExtra("nombresSeleccionados", nombresFinales)
        startActivity(intentJuego)
        finish() // cierro esta activity
    }


    // controlo la visibilidad de los jugadores 3 y 4
    private fun adaptoVisibilidad(visibleJ3: Boolean, visibleJ4: Boolean) {
        binding.textNombre3.visibility = if (visibleJ3) View.VISIBLE else View.GONE //visibilidad texto
        binding.nombreJ3.visibility = if (visibleJ3) View.VISIBLE else View.GONE //visibilidad del recicler

        binding.textNombre4.visibility = if (visibleJ4) View.VISIBLE else View.GONE //visibilidad texto
        binding.nombreJ4.visibility = if (visibleJ4) View.VISIBLE else View.GONE //visibilidad recicler
    }
}


