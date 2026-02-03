package com.example.proyectoandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contactos")
data class Contacto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val picture: String
)
