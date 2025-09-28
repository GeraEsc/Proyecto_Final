package com.example.core_data.data

import androidx.core.view.VelocityTrackerCompat.clear
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.core_data.model.Actividad
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

    // Insertar o actualizar una lista de actividades
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(acts: List<Actividad>)

    @Query("DELETE FROM Actividades")
    suspend fun clear()

    @Transaction
    suspend fun replaceAllTx(acts: List<Actividad>) {
        // Eliminar todas las actividades existentes
        clear()
        // Insertar las nuevas actividades
        if (acts.isNotEmpty()) insertAll(acts)
    }
}