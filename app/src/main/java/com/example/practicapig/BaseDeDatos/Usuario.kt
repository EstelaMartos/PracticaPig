package com.example.practicapig.BaseDeDatos

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val nombre : String,
    @ColumnInfo(name = "contraseña") val contraseña : String,
    @ColumnInfo(name = "fecha_nacimiento") val fecha_nacimiento : String
): Parcelable