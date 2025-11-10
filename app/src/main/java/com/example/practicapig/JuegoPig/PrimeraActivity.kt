package com.example.practicapig.JuegoPig

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityPrimeraBinding

class PrimeraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrimeraBinding
    private var jugadoresSeleccionados: Int? = null
    private var rondasSeleccionadas: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPrimeraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //creacion del spinner
        val numJugadores = listOf("Selecciona jugadores","2","3","4")
        val adapterJugadores = ArrayAdapter(this, R.layout.simple_spinner_item, numJugadores)
        adapterJugadores.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerJugadores.adapter = adapterJugadores
        //hasta aqui creacion spinner

        binding.spinnerJugadores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                //seleccionamos el numero de jugadores del spinner
                if (position == 0) {
                    jugadoresSeleccionados = null
                } else {
                    jugadoresSeleccionados = numJugadores[position].toInt()
                }

                //--------leo el número que ponen en las rondas-----
                val textoNumeroRondas = binding.NumeroRondas.text.toString().trim()
                if (textoNumeroRondas.isEmpty()) {
                    rondasSeleccionadas = null
                } else {
                    val numeroRondas = textoNumeroRondas.toIntOrNull()
                    //lanzo toast si ponen num rondas incorrecto
                    if (numeroRondas == null || (numeroRondas != 2 && numeroRondas != 4 && numeroRondas != 6)) {
                        rondasSeleccionadas = null
                        Toast.makeText(this@PrimeraActivity, "Número de rondas inválido. Usa 2, 4 o 6.", Toast.LENGTH_SHORT).show()
                    } else {
                        rondasSeleccionadas = numeroRondas
                    }
                }
                //----hasta aqui el text---------

                // si ambos campos son distintos de null, paso a la siguiente activity
                if (rondasSeleccionadas != null && jugadoresSeleccionados != null) {
                    val rondas = rondasSeleccionadas
                    val jugadores = jugadoresSeleccionados
                    val intent = Intent(this@PrimeraActivity, SegundaActivity::class.java)
                    intent.putExtra("rondas", rondas)
                    intent.putExtra("jugadores", jugadores)
                    startActivity(intent)
                    finish()
                }else{
                    if (binding.spinnerJugadores.selectedItemPosition != 0) {  //vuelvo a poner el spinner en la posicion 0 manualmente para forzar una nueva seleccion
                        binding.spinnerJugadores.setSelection(0)//si algo ha fallado vuelvo el spinner a 0 para evitar problemas de seleccion
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }


        }


    }


}



