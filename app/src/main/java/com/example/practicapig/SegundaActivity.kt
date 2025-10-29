package com.example.practicapig

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityPrimeraBinding
import com.example.practicapig.databinding.ActivitySegundaBinding


private lateinit var binding: ActivityPrimeraBinding


class SegundaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rondasSeleccionadas = intent.getIntExtra("rondas", 0)
        val jugadoresSeleccionados = intent.getIntExtra("jugadores", 0)



    }

}