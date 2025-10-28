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
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()//mata el oncreate no espera ningun estado mas
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        splashTimer.start()
    }

}