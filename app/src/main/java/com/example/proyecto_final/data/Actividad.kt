package com.example.proyecto_final.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

    //Estructura de la tabla "Actividades" en la base de datos Room
@Entity(tableName = "Actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val date: LocalDateTime
)