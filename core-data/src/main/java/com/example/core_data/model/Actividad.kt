package com.example.core_data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

//Estructura de la tabla "Actividades" en la base de datos Room
//Mapear para exponer

@Entity(tableName = "Actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val date: LocalDateTime
)