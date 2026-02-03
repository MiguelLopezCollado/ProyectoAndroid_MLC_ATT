package com.example.proyectoandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la aplicación usando Room.
 * Define las entidades y la versión de la base de datos.
 */
@Database(entities = [Contacto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_android_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
