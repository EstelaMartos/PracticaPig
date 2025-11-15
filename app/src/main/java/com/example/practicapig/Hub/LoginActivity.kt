package com.example.practicapig.Hub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityLoginBinding
import com.example.practicapig.databinding.ActivitySegundaBinding

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



}