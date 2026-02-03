package com.example.proyectoandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactoDao {

    //Obtiene todos los contactos almacenados en la base de datos.

    @Query("SELECT * FROM contactos ORDER BY name ASC")
    fun getAllContactos(): Flow<List<Contacto>>


      //Inserta una lista de contactos en la base de datos.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contactos: List<Contacto>)
    
    /**
     * Elimina todos los contactos de la base de datos.
     */
    @Query("DELETE FROM contactos")
    suspend fun deleteAll()
}
