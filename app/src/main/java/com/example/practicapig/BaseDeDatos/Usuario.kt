package com.example.practicapig.BaseDeDatos

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "usuarios")
data class Usuario(
    //esta va a ser la palabra clabe de la base de datos, debe ser única
    @PrimaryKey val nombre : String,
    //las columnas de la base de datos
    //@ColumnInfo sirve para personalizar el nombre de una columna en la base de datos
    @ColumnInfo(name = "contraseña") val contraseña : String,
    @ColumnInfo(name = "fecha_nacimiento") val fecha_nacimiento : String,
): Parcelable