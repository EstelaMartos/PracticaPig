package com.example.practicapig.Librerias.galeria

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityVerFotoBinding
import java.io.File

class VerFotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityVerFotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ruta = intent.getStringExtra("ruta") ?: return
        binding.imagenCompleta.setImageURI(Uri.fromFile(File(ruta)))
    }
}