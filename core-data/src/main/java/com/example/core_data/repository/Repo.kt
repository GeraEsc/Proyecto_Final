package com.example.core_data.repository

import com.example.core_data.model.Actividad
import com.example.core_data.data.GestorDao
import com.example.core_data.firebase.FirebaseActService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class Repo(
    private val gestorDAO: GestorDao,
    private val firebaseService: FirebaseActService
) {


    fun getAllActs(): Flow<List<Actividad>> = gestorDAO.getAllActs()

    suspend fun insert(act: Actividad) {
        gestorDAO.insertAct(act)
        try {
            firebaseService.uploadAct(act)
        } catch (e: Exception) { android.util.Log.e("Repo", "Firebase error", e) }

    }

    suspend fun update(act: Actividad) {
        gestorDAO.updateAct(act)
        try {
            firebaseService.uploadAct(act)
        } catch (e: Exception) { android.util.Log.e("Repo", "Firebase error", e) }

    }

    suspend fun delete(act: Actividad) {
        gestorDAO.deleteAct(act)
        try {
            firebaseService.deleteAct(act)
        } catch (e: Exception) { android.util.Log.e("Repo", "Firebase error", e) }

    }

    suspend fun syncFromFirebase() = withContext(Dispatchers.IO) {
        // Obtener todas las actividades desde Firebase
        val remote = firebaseService
        val remoteActs = remote.getAllActs()
        // Opción A: reemplazar completamente lo local por lo remoto:
        gestorDAO.replaceAllTx(remoteActs)

    }
}