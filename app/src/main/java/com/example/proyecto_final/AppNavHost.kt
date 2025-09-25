package com.example.proyecto_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.core_data.viewmodel.GestorViewModel
import com.example.proyecto_final.ui.theme.Proyecto_FinalTheme

class AppNavHost : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val factory = (application as MyApp).container.gestorVmFactory()
        val vm = ViewModelProvider(this, factory)[GestorViewModel::class.java]

        setContent {
            Proyecto_FinalTheme {

                val navController = rememberNavController()
                RootNavGraph( viewModel = vm, navController = navController)
            }
        }
    }
}
