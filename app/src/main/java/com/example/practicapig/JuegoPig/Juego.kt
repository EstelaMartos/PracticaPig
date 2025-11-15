package com.example.practicapig.JuegoPig
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Juego(val numJugadores: Int, val numRondas: Int) : Parcelable