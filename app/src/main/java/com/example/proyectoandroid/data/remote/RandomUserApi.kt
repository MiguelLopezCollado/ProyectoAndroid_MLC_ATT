package com.example.proyectoandroid.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para interactuar con la API de RandomUser.
 */
interface RandomUserApi {

    //Número de resultados a obtener. Por defecto 10.
    //Respuesta de la API conteniendo los resultados.

    @GET("api/")
    suspend fun getUsers(@Query("results") results: Int = 10): RandomUserResponse
}

/**
 * Modelo de respuesta raíz de la API.
 */
data class RandomUserResponse(
    val results: List<UserDto>
)

/**
 * Data Transfer Object (DTO) que representa un usuario de la API.
 */
data class UserDto(
    val name: NameDto,
    val email: String,
    val phone: String,
    val picture: PictureDto
)

/**
 * DTO para el nombre del usuario.
 */
data class NameDto(
    val first: String,
    val last: String
)

/**
 * DTO para las imágenes del usuario.
 */
data class PictureDto(
    val large: String,
    val medium: String,
    val thumbnail: String
)
