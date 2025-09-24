package com.example.services

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.services.ui.theme.Proyecto_FinalTheme


class ActScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proyecto_FinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNotasRapidas(object : AccionesNota {
                        override fun mostrarNota(nota: Nota) {}
                    })
                }
            }

        }
    }
}


// ---------------------------
// MODELO DE DATOS
// ---------------------------

// Clase de datos que representa una nota
// Contiene un título y un contenido
data class Nota(val titulo: String, val contenido: String)

// ---------------------------
// INTERFAZ PARA ACCIONES
// ---------------------------

// Define que acciones se pueden realizar con una nota
// En este caso, solo mostrar una nota seleccionada
interface AccionesNota {
    fun mostrarNota(nota: Nota)
}

// ---------------------------
// OBJETO GESTOR DE NOTAS
// ---------------------------

// Objeto "singleton" que maneja todas las notas de la aplicación
object GestorNotas {

    // Lista donde se almacenan todas las notas
    val listaNotas = mutableListOf<Nota>()

    // Función para agregar una nueva nota a la lista
    // Retorna true si se pudo agregar, false si no
    fun agregarNota(titulo: String, contenido: String): Boolean {
        return try {
            // Valida que no estén vacíos
            if (titulo.isNotBlank() && contenido.isNotBlank()) {
                listaNotas.add(Nota(titulo, contenido))
                true
            } else false
        } catch (e: Exception) {
            // Manejo de errores con try/catch
            false
        }
    }

    // Función que filtra notas según una búsqueda en el título
    // Uso de función de orden superior (filter con lambda)
    fun filtrarNotas(query: String): List<Nota> =
        listaNotas.filter { it.titulo.contains(query, ignoreCase = true) }
}

// ---------------------------
// ACTIVIDAD PRINCIPAL
// ---------------------------

class MainActivity : ComponentActivity(), AccionesNota {

    // Función que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aquí se define la interfaz usando Jetpack Compose
        setContent { AppNotasRapidas(this) }
    }

    // Implementación de la acción definida en la interfaz
    // Aquí solo imprime en consola el título de la nota seleccionada
    override fun mostrarNota(nota: Nota) {
        println("Nota seleccionada: ${nota.titulo}")
    }
}

// ---------------------------
// INTERFAZ DE USUARIO (Jetpack Compose)
// ---------------------------




@Preview (showBackground = true)
@Composable
fun PreviewAppNotasRapidas() {
    AppNotasRapidas(object : AccionesNota {
        override fun mostrarNota(nota: Nota) {}
    })
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNotasRapidas(acciones: AccionesNota) {

    //Variables de estado reactivas que actualizan la UI automaticamente
    var titulo by remember { mutableStateOf("") }      // Texto del campo título
    var contenido by remember { mutableStateOf("") }   // Texto del campo contenido
    var notas by remember { mutableStateOf(GestorNotas.listaNotas.toList()) } // Lista de notas

    //Scaffold es la estructura base de la pantalla (barra superior, contenido, etc.)
    Scaffold(topBar = { TopAppBar(title = { Text("Notas Rapidas") }) }) { padding ->

        //Contenedor principal en columna
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            //Campo de texto para el titulo de la nota
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it }, //Actualiza el estado
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            //Campo de texto para el contenido de la nota
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it }, //Actualiza el estado
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            //Boton para agregar la nota
            Button(
                onClick = {
                    //Si se pudo agregar, se actualiza la lista y se limpian los campos
                    if (GestorNotas.agregarNota(titulo, contenido)) {
                        notas = GestorNotas.listaNotas.toList()
                        titulo = ""
                        contenido = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Agregar Nota")
            }

            //Lista de notas (se renderiza solo lo visible → eficiente)
            LazyColumn {
                items(notas) { nota ->

                    //Cada nota se muestra en una tarjeta (Card)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { acciones.mostrarNota(nota) } // Acción al dar click
                    ) {
                        // Contenido dentro de la tarjeta
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(nota.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(nota.contenido, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            }
        }
}