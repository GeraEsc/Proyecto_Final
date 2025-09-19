package com.example.proyecto_final.data

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Actividad::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GestorDatabase : RoomDatabase() {
    abstract fun gestorDao(): GestorDao
}
