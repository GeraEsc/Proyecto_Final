package com.example.proyecto_final.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.core_data.firebase.FirebaseActService
import com.example.core_data.repository.Repo
import com.example.core_data.data.GestorDao
import com.example.core_data.data.GestorDatabase
import com.example.core_data.viewmodel.GestorViewModel

class AppContainer(app: Application) {

    init {

        if (FirebaseApp.getApps(app).isEmpty()) {
            FirebaseApp.initializeApp(app)
            FirebaseFirestore.setLoggingEnabled(true)


        }
    }

    //Room
    private val gestorDao: GestorDao by lazy {
        GestorDatabase.getInstance(app).gestorDao()
    }

    //Firebase
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseService by lazy { FirebaseActService(firestore) }

    //Repo
    private val repo by lazy { Repo(gestorDao, firebaseService) }



    //ViewModel Factory
    fun gestorVmFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GestorViewModel::class.java)) {
                return GestorViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}

