package com.example.proyecto_final.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//CRUD - Create, Read, Update y Delete
@Dao
interface GestorDao {

    @Query("SELECT * FROM Actividades ORDER BY date DESC")
    fun getAllActs(): Flow<List<Actividad>>

    //POST
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAct(act: Actividad)

    //PUT
    @Update
    suspend fun updateAct(act: Actividad)

    //DELETE
    @Delete
    suspend fun deleteAct(act: Actividad)
}