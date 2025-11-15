package com.example.practicapig.BaseDeDatos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practicapig.Hub.Usuario

@Database(entities = [Usuario::class], version = 1, exportSchema = false)
abstract class DatabaseUsuarios : RoomDatabase() {

    abstract fun usuarioDao(): DatabaseDAO

    companion object {
        @Volatile
        private var INSTANCE: DatabaseUsuarios? = null

        fun getDatabase(context: Context): DatabaseUsuarios {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseUsuarios::class.java,
                    "bd_emg"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


