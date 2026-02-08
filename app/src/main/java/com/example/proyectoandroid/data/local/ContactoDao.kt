package com.example.proyectoandroid.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactoDao {

    //Obtiene todos los contactos almacenados en la base de datos.

    @Query("SELECT * FROM contactos ORDER BY name ASC")
    fun getAllContactos(): Flow<List<Contacto>>


      //Inserta una lista de contactos en la base de datos.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contactos: List<Contacto>)

    @Delete
    suspend fun delete(contacto: Contacto)

    @androidx.room.Update
    suspend fun update(contacto: Contacto)


    
}
