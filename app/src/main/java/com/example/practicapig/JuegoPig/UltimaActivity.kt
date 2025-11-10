package com.example.practicapig.JuegoPig

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityUltimaBinding

class UltimaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUltimaBinding
    private lateinit var nombresOrden: ArrayList<String>

    // arrays recibidos por Intent: nombres y puntos

    private lateinit var puntosOrden: IntArray
    private var jugadoresSeleccionados: Int = 0
    private var mensajeFinal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUltimaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //-----------recojo los datos del intent

        nombresOrden = intent.getStringArrayListExtra("nombresOrden") ?: arrayListOf()
        puntosOrden  = intent.getIntArrayExtra("puntosOrden") ?: IntArray(0)
        mensajeFinal = intent.getStringExtra("mensajeFinal") ?: ""

        // numero de jugadores = tamaño de los arrays
        jugadoresSeleccionados = nombresOrden.size


        // controlo d enuevo la visibilidad
        when (jugadoresSeleccionados) {
            2 -> visibilidadJugadores(visible3 = false, visible4 = false)
            3 -> visibilidadJugadores(visible3 = true,  visible4 = false)
            4 -> visibilidadJugadores(visible3 = true,  visible4 = true)
        }


        // ----------pinto nombres y puntuaciones

        // jugador 1
        binding.textJugador1.text = nombresOrden[0]
        binding.textPuntuacionJ1.text = puntosOrden[0].toString()

        // jugador 2
        binding.textJugador2.text = nombresOrden[1]
        binding.textPuntuacionJ2.text = puntosOrden[1].toString()

        // jugador 3
        if (jugadoresSeleccionados >= 3) {
            binding.textJugador3.text = nombresOrden[2]
            binding.textPuntuacionJ3.text = puntosOrden[2].toString()
        }

        // jugador 4
        if (jugadoresSeleccionados == 4) {
            binding.textJugador4.text = nombresOrden[3]
            binding.textPuntuacionJ4.text = puntosOrden[3].toString()
        }


        // -------mensaje final

        binding.textMensajeFinal.visibility = View.VISIBLE
        binding.textMensajeFinal.text = mensajeFinal
    }


    private fun visibilidadJugadores(visible3: Boolean, visible4: Boolean) {

        binding.textJugador3.visibility     = if (visible3) View.VISIBLE else View.GONE
        binding.textPuntuacionJ3.visibility = if (visible3) View.VISIBLE else View.GONE


        binding.textJugador4.visibility     = if (visible4) View.VISIBLE else View.GONE
        binding.textPuntuacionJ4.visibility = if (visible4) View.VISIBLE else View.GONE
    }
}
