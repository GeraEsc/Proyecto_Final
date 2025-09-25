package com.example.core_data.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core_data.model.Actividad

@Database(
    entities = [Actividad::class],
    version = 1,
    exportSchema = false
)
abstract class GestorDatabase : RoomDatabase() {

    abstract fun gestorDao(): GestorDao

    companion object {
        @Volatile private var INSTANCE: GestorDatabase? = null

        fun getInstance(context: Context): GestorDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GestorDatabase::class.java,
                    "gestor.db"
                )
                    // .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
