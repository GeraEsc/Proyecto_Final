package com.example.core_data.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core_data.model.Actividad

// Aquí registras tus entidades y el DAO
@Database(
    entities = [Actividad::class], // 👈 puedes agregar más entidades si vas creando (Rating, Inscription…)
    version = 2,
    exportSchema = false
)
abstract class GestorDatabase : RoomDatabase() {

    abstract fun gestorDao(): GestorDao

    companion object {
        @Volatile
        private var INSTANCE: GestorDatabase? = null

        fun getDatabase(context: Context): GestorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GestorDatabase::class.java,
                    "gestor_db" // Nombre físico del archivo de la BD
                )
                    .fallbackToDestructiveMigration() // ⚠️ borra y recrea si cambias version (útil mientras aprendes)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}