package com.example.core_data.repository

import com.example.core_data.model.Actividad
import com.example.core_data.data.GestorDao
import com.example.core_data.firebase.FirebaseActService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class Repo(
    private val gestorDAO: GestorDao,
    private val firebaseService: FirebaseActService
) {


    fun getAllActs(): Flow<List<Actividad>> = gestorDAO.getAllActs()

    suspend fun insert(act: Actividad) {
        gestorDAO.insertAct(act)
        try {
            firebaseService.uploadAct(act)
        } catch (_: Exception) {


        }
    }

    suspend fun update(act: Actividad) {
        gestorDAO.updateAct(act)
        try {
            firebaseService.uploadAct(act)
        } catch (_: Exception) {


        }
    }

    suspend fun delete(act: Actividad) {
        gestorDAO.deleteAct(act)
        try {
            firebaseService.deleteAct(act)
        } catch (_: Exception) {


        }
    }

    suspend fun syncFromFirebase() {
        try {
            val remoteActs = firebaseService.getAllActs()
            val localActs = gestorDAO.getAllActs().first()

            remoteActs.forEach { remote ->
                if (localActs.none { it.id == remote.id }) {
                    gestorDAO.insertAct(remote)
                }
            }
        } catch (_: Exception) {


        }
    }

    // TODO --- REMOVE --- Tests
    suspend fun insertFakeData() {
        val fakeActs = listOf(
            Actividad(
                titulo = "Correr",
                descripcion = "Correr en el parque",
                fecha = "2024-06-01",
                //calificaciones = 3.5
            ),
            Actividad(
                titulo = "Nadar",
                descripcion = "Nadar en la piscina",
                fecha = "2024-06-02",
                //calificaciones = 4.0
            ),
            Actividad(
                titulo = "Ciclismo",
                descripcion = "Paseo en bicicleta",
                fecha = "2024-06-03",
                //calificaciones = 5.0
            )

        )


        fakeActs.forEach {
            gestorDAO.insertAct(it)
            try {
                firebaseService.uploadAct(it)
            } catch (_: Exception) {}
        }

    }
}