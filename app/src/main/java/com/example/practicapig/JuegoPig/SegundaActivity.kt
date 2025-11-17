package com.example.practicapig.JuegoPig

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicapig.databinding.ActivitySegundaBinding

class SegundaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaBinding

    // array que guarda el nombre elegido por cada jugador
    private lateinit var arrayNombresSeleccionados: Array<Jugador?>

    // listas con los recicler y sus respectivos adapters
    private val recyclerViewsJugadores: MutableList<RecyclerView> = mutableListOf() //es la lista de los recicler views que se muestran
    private val adaptersNombrePorJugador: MutableList<NombreAdapter> = mutableListOf()

    // -------datos que llegan desde la primera activiti
    private var rondasSeleccionadas: Int = 0
    private var jugadoresSeleccionados: Int = 0
    private lateinit var juego: Juego



    //lista de los nombres como objetos
    private val nombresDisponibles = listOf(
        Jugador("Aitor Tilla",0), Jugador("Ana Conda",0),
        Jugador("Armando Broncas",0), Jugador("Aurora Boreal",0),
        Jugador("Bartolo Mesa",0),
        Jugador("Carmen Mente",0), Jugador("Enrique Cido",0),
        Jugador("Esteban Dido",0), Jugador("Elba Lazo",0),
        Jugador("Fermin Tado",0),
        Jugador("Lola Mento",0), Jugador("Luz Cuesta",0),
        Jugador("Paco Tilla",0), Jugador("Pere Gil",0),
        Jugador("Salvador Tumbado",0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //--------recojo los datos del intent

        juego = intent.getParcelableCompat<Juego>("juego")!!
        rondasSeleccionadas = juego.numRondas
        jugadoresSeleccionados = juego.numJugadores

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
    fun gestionarSeleccionDeNombre(nombreSeleccionado: Jugador, jugadorQueClico: Int) {

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
        val nombresYaTomados: List<Jugador> = arrayNombresSeleccionados.filterNotNull()

        var indiceJugador = 0
        while (indiceJugador < adaptersNombrePorJugador.size) {
            val adapter = adaptersNombrePorJugador[indiceJugador]
            val miSeleccion = arrayNombresSeleccionados[indiceJugador]

            // creo una lista filtrada con su nombre actual (si lo tiene) + nombres libres
            val listaFiltrada = ArrayList<Jugador>()
            for (nombre in nombresDisponibles) {
                val esMiSeleccion = (miSeleccion != null && miSeleccion == nombre)
                val estaTomado = nombresYaTomados.contains(nombre)
                if (esMiSeleccion || !estaTomado) {
                    listaFiltrada.add(nombre)
                }
            }

            adapter.actualizarDatos(listaFiltrada, miSeleccion)
            indiceJugador++
        }
    }


    //una vez elegidos todos los nombres, paso a la siguiente activity

    private fun pasarASiguienteActivity() {
        //---------------------hago los intent
        //creo un alista de jugadores(objetos)
        val jugadoresFinales = ArrayList<Jugador>()
        for (indiceJugador in 0 until jugadoresSeleccionados) {
            val jugadorSeleccionado = arrayNombresSeleccionados[indiceJugador]
            jugadoresFinales.add(Jugador(jugadorSeleccionado?.nombre ?: "Sin nombre", 0))
        }
        val intentJuego = Intent(this, MainActivity::class.java)
        //paso el objeto juego
        intentJuego.putExtra("juego", juego)
        //paso la lista de jugadores
        intentJuego.putParcelableArrayListExtra("jugadoresLista", jugadoresFinales)
        //paso a la siguiente activity
        startActivity(intentJuego)
        finish()
    }


    // controlo la visibilidad de los jugadores 3 y 4
    private fun adaptoVisibilidad(visibleJ3: Boolean, visibleJ4: Boolean) {
        binding.textNombre3.visibility = if (visibleJ3) View.VISIBLE else View.GONE //visibilidad texto
        binding.nombreJ3.visibility = if (visibleJ3) View.VISIBLE else View.GONE //visibilidad del recicler

        binding.textNombre4.visibility = if (visibleJ4) View.VISIBLE else View.GONE //visibilidad texto
        binding.nombreJ4.visibility = if (visibleJ4) View.VISIBLE else View.GONE //visibilidad recicler
    }

    inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            getParcelableExtra(key, T::class.java)
        else
            @Suppress("DEPRECATION") getParcelableExtra(key)
}
