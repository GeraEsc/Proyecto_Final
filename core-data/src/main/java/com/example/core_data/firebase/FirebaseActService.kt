package com.example.core_data.firebase

import com.example.core_data.model.Actividad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirebaseActService (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("Actividades")

    suspend fun uploadAct(act: Actividad) {
        collection.document(act.id).set(act.toMap()).await()
    }

    suspend fun deleteAct(act: Actividad) {
        collection.document(act.id).delete().await()
    }

    suspend fun getAllActs(): List<Actividad> {
        val snapshot = collection.get().await()
        return snapshot.documents.mapNotNull {
            it.data?.let { data -> Actividad.fromMap(data)}
        }
    }
}
