
package com.example.proyecto_final

import android.app.Application
import com.example.proyecto_final.di.AppContainer

//Aplicacion para inicializar el contenedor de dependencias
class MyApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
