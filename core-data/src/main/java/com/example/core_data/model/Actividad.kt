package com.example.core_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Actividades")
data class Actividad(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val fecha: String
)