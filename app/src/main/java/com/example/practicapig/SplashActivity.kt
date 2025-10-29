package com.example.practicapig

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity  : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Agrega un temporizador para simular la carga
        val splashTimer = object : Thread() {
            override fun run() {
                try {
                    sleep(3000) // Tiempo en milisegundos
                    val intent = Intent(this@SplashActivity, PrimeraActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        splashTimer.start()
    }

}