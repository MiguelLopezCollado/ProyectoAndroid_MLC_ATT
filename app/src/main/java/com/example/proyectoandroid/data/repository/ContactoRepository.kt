package com.example.proyectoandroid.data.repository

import com.example.proyectoandroid.data.local.Contacto
import com.example.proyectoandroid.data.local.ContactoDao
import com.example.proyectoandroid.data.remote.RandomUserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio que gestiona la fuente de datos de Contactos.
 * Coordina la obtención de datos desde la API remota y su almacenamiento local.
 *
 * @property contactoDao DAO para acceso a la base de datos local.
 * @property api Cliente de Retrofit para acceso a la API remota.
 */
class ContactoRepository(
    private val contactoDao: ContactoDao,
    private val api: RandomUserApi
) {

    /**
     * Obtiene el flujo de contactos almacenados localmente.
     * Fuente única de verdad (Single Source of Truth).
     */
    val contactos: Flow<List<Contacto>> = contactoDao.getAllContactos()

    /**
     * Importa nuevos contactos desde la API y los guarda en la base de datos local.
     * Esta operación se ejecuta en el despachador de IO.
   **/
    suspend fun importarContactos(cantidad: Int = 10) {
        withContext(Dispatchers.IO) {
            // 1. Obtener datos de la API
            val response = api.getUsers(results = cantidad)
            
            // 2. Mapear DTOs a Entidades
            val nuevosContactos = response.results.map { dto ->
                Contacto(
                    name = "${dto.name.first} ${dto.name.last}",
                    email = dto.email,
                    phone = dto.phone,
                    picture = dto.picture.large // Usamos la imagen grande
                )
            }

            // 3. Insertar en base de datos local (reemplaza si hay conflictos no manejados por ID, pero aquí son nuevos insert)
            // 3. Insertar en base de datos local
            contactoDao.insertAll(nuevosContactos)
        }
    }

    suspend fun deleteContacto(contacto: Contacto) {
        contactoDao.delete(contacto)
    }
}
