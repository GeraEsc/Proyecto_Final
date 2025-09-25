package com.example.core_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

//Estructura de la tabla "Actividades" en la base de datos Room
//Mapear para exponer

@Entity(tableName = "Actividades")
data class Actividad(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val titulo: String,
    val descripcion: String,
    val date: String,
    val calificaciones: Double


//    @ColumnInfo(name = "title") val title: String,
//    @ColumnInfo(name = "content") val content: String,
//    @ColumnInfo(name = "date") val date: LocalDateTime
){

    //Convertir a map para firebase
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "titulo" to titulo,
        "descripcion" to descripcion,
        "date" to date,
        "calificaciones" to calificaciones,
    )



    // Construir desde Firebase
    companion object {

        fun fromMap(map: Map<String, Any?>): Actividad = Actividad(
            id = map["id"] as String,
            titulo = map["titulo"] as String,
            descripcion = map["descripcion"] as String,
            date = map["date"] as String,
            calificaciones = (map["calificaciones"] as Number).toDouble()
        )
    }
}