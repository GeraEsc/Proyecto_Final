package com.example.core_data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.example.core_data.data.GestorDatabase
import com.example.core_data.repository.Repo
import com.example.core_data.ui.theme.Proyecto_FinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            GestorDatabase::class.java,
            "Gestor_db"
        ).build()

//        val repository = Repo(database.gestorDao())

        setContent {
            Proyecto_FinalTheme {

            }
        }
    }
}