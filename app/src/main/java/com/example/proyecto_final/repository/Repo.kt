package com.example.proyecto_final.repository

import com.example.proyecto_final.data.Actividad
import com.example.proyecto_final.data.GestorDao
import kotlinx.coroutines.flow.Flow

class Repo(private val gestorDAO: GestorDao) {


    fun getAllActs(): Flow<List<Actividad>> = gestorDAO.getAllActs()

    suspend fun insert(act: Actividad) {
        gestorDAO.insertAct(act)
    }

    suspend fun update(act: Actividad) {
        gestorDAO.updateAct(act)
    }

    suspend fun delete(act: Actividad) {
        gestorDAO.deleteAct(act)
    }
}