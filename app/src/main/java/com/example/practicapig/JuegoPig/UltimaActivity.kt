package com.example.practicapig.JuegoPig

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.LoginActivity
import com.example.practicapig.Hub.MenuActivity
import com.example.practicapig.databinding.ActivityUltimaBinding

class UltimaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUltimaBinding
    private lateinit var juego: Juego
    private lateinit var listaJugadores: ArrayList<Jugador>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUltimaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //-------------------------------INTENT------------------------------------------
        //-----------recojo los datos del intent

        val usuario = intent.getParcelableCompat<Usuario>("usuario")
        juego = intent.getParcelableCompat<Juego>("juego")!!
        listaJugadores =
            intent.getParcelableArrayListCompat<Jugador>("jugadoresFinales") ?: arrayListOf()

        // -------- calculo el mensaje final
        var maxPuntos = 0
        for (jugador in listaJugadores) {
            if (jugador.puntos > maxPuntos) {
                maxPuntos = jugador.puntos
            }
        }

        val ganadores = ArrayList<Jugador>()
        for (jugador in listaJugadores) {
            if (jugador.puntos == maxPuntos) {
                ganadores.add(jugador)
            }
        }

        val mensaje = if (ganadores.size == 1) {
            "Ganador: ${ganadores[0].nombre} con $maxPuntos puntos"
        } else {
            val nombres = ganadores.joinToString(", ") { it.nombre }
            "Empate entre: $nombres con $maxPuntos puntos"
        }


        // controlo d enuevo la visibilidad
        when (listaJugadores.size) {
            2 -> visibilidadJugadores(visible3 = false, visible4 = false)
            3 -> visibilidadJugadores(visible3 = true, visible4 = false)
            4 -> visibilidadJugadores(visible3 = true, visible4 = true)
        }


        // ----------pinto nombres y puntuaciones

        // jugador 1
        binding.textJugador1.text = listaJugadores[0].toString()


        // jugador 2
        binding.textJugador2.text = listaJugadores[1].toString()


        if (listaJugadores.size >= 3) {
            binding.textJugador3.visibility = View.VISIBLE
            binding.textJugador3.text = listaJugadores[2].toString()
        } else {
            binding.textJugador3.visibility = View.GONE
        }

        if (listaJugadores.size == 4) {
            binding.textJugador4.visibility = View.VISIBLE
            binding.textJugador4.text = listaJugadores[3].toString()
        } else {
            binding.textJugador4.visibility = View.GONE
        }



        // -------mensaje final

        binding.textMensajeFinal.text = mensaje

        //------------------------------------INTENT----------------------------------
        //intent vacio para colver al menu

        binding.pasarAMenu.setOnClickListener {
            val intent = Intent(this@UltimaActivity, MenuActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
            finish()
        }
    }


    private fun visibilidadJugadores(visible3: Boolean, visible4: Boolean) {

        binding.textJugador3.visibility = if (visible3) View.VISIBLE else View.GONE

        binding.textJugador4.visibility = if (visible4) View.VISIBLE else View.GONE

    }
}
