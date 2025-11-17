package com.example.practicapig.Hub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.JuegoPig.PrimeraActivity
import com.example.practicapig.databinding.ActivityMenuBinding



private lateinit var binding: ActivityMenuBinding

class MenuActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //recoger intent
        val usuario = intent.getParcelableCompat<Usuario>("usuario")
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario no encontrado"


        binding.nombreUsuario.setOnClickListener {
            val intent = Intent(this, CContraseniaActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        binding.imagenJuego.setOnClickListener {
            val intent = Intent(this, PrimeraActivity::class.java)
            startActivity(intent)
        }



    }
}

inline fun <reified T : Parcelable> Intent.getParcelableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getParcelableExtra(key, T::class.java)
    else
        @Suppress("DEPRECATION") getParcelableExtra(key)