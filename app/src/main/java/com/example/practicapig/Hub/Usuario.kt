package com.example.practicapig.Hub

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
class Usuario(
    @PrimaryKey val nombre : String,
    @ColumnInfo(name = "contraseña") val contraseña : String,
    @ColumnInfo(name = "correo") val correo : String,
    @ColumnInfo(name = "fecha_nacimiento") val fecha_nacimiento : String
)

