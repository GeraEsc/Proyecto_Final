package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core_data.model.Actividad
import com.example.core_data.viewmodel.GestorViewModel
import com.example.services.data.FeaturedActs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActDetailScreen(
    actId: String,
    viewModel: GestorViewModel,
    onBack: () -> Unit,
    onEditLocal: () -> Unit
) {
    val myActs by viewModel.acts.collectAsState(initial = emptyList())
    val featured = remember(actId) { FeaturedActs.items.firstOrNull { it.id == actId } }
    val local = remember(myActs, actId) { myActs.firstOrNull { it.id == actId } }
    val isEnrolled = local != null

    val titulo = featured?.titulo ?: local?.titulo ?: "Actividad"
    val descripcion = featured?.descripcion ?: local?.descripcion.orEmpty()
    val date = featured?.date ?: local?.date.orEmpty()
    val imageUrl = featured?.imageUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            imageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            Text(titulo, style = MaterialTheme.typography.headlineSmall)
            featured?.let { Text("${it.categoria} • ${it.ubicacion}", color = MaterialTheme.colorScheme.primary) }
            if (date.isNotBlank()) Text("Fecha: $date")
            Spacer(Modifier.height(12.dp))
            Text(descripcion)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isEnrolled) {
                        // Cancelar
                        viewModel.deleteAct(local!!)
                    } else {
                        // Inscribir
                        val entity = featured?.let { f ->
                            Actividad(
                                id = f.id,
                                titulo = f.titulo,
                                descripcion = f.descripcion,
                                date = f.date,
                                calificaciones = 0.0
                            )
                        } ?: Actividad(
                            id = actId,
                            titulo = titulo,
                            descripcion = descripcion,
                            date = date,
                            calificaciones = 0.0
                        )
                        viewModel.insertAct(entity)
                    }
                }
            ) {
                Text(if (isEnrolled) "Cancelar inscripción" else "Inscribirme")
            }
        }
    }
}

