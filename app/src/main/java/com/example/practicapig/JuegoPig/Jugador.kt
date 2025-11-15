package com.example.practicapig.JuegoPig
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Jugador(val nombre: String, var puntos: Int): Parcelable{
    override fun toString(): String {
        return "$nombre: $puntos "
    }
}