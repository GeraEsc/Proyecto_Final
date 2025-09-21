package com.example.core_data.data

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.core_data.model.Actividad

@Database(
    entities = [Actividad::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GestorDatabase : RoomDatabase() {
    abstract fun gestorDao(): GestorDao
}
