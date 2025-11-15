package com.example.practicapig.Hub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityRegistroBinding

private lateinit var binding: ActivityRegistroBinding
class RegistroActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRegistroBinding.inflate(layoutInflater)
    setContentView(binding.root)
    }


}


