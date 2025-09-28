package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core_data.model.Actividad
import com.example.core_data.viewmodel.GestorViewModel
import com.example.ratings.screens.AverageRatingRow
import com.example.services.data.FeaturedActs



//Detalle de una actividad (tanto del catalogo como inscrita/creada manualmente)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActDetailScreen(
    actId: String,
    viewModel: GestorViewModel,
    onBack: () -> Unit,
    onEditLocal: () -> Unit,
    onSeeReviews: () -> Unit
) {
    val myActs by viewModel.acts.collectAsState(initial = emptyList())

    //Si esta en catalogo = es una actividad destacada
    val featured = remember(actId) { FeaturedActs.items.firstOrNull { it.id == actId } }
    //Si está en Room = está inscrita o creada manualmente
    val local = remember(myActs, actId) { myActs.firstOrNull { it.id == actId } }

    val isEnrolled = local != null
    //Si no esta en catalogo pero si en Room = creada manualmente
    val isManual = local != null && featured == null

    val titulo = featured?.titulo ?: local?.titulo ?: "Actividad"
    val descripcion = featured?.descripcion ?: local?.descripcion.orEmpty()
    val date = featured?.date ?: local?.date.orEmpty()
    val imageUrl = featured?.imageUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atras") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
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
            featured?.let {
                Text(
                    text = "${it.categoria} • ${it.ubicacion}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (date.isNotBlank()) Text("Fecha: $date")
            Spacer(Modifier.height(12.dp))
            Text(descripcion)

            //Valoracion media y boton "Ver todas las valoraciones"
            Spacer(Modifier.height(16.dp))
            AverageRatingRow(
                actId = actId,
                onSeeAll = onSeeReviews
            )

            Spacer(Modifier.height(16.dp))

            //Inscribirse / Cancelar inscripcion
            Button(
                onClick = {
                    if (isEnrolled) {
                        //Cancelar inscripcion
                        viewModel.deleteAct(local!!)
                    } else {
                        //Inscribirse
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
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEnrolled) "Cancelar inscripcion" else "Inscribirme")
            }

            //Si es creada manualmente, mostrar botones Editar y Eliminar
            if (isManual) {
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedButton(
                        onClick = onEditLocal,
                        modifier = Modifier.weight(1f)
                    ) { Text("Editar") }

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.deleteAct(local!!) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Eliminar") }
                }
            }
        }
    }
}
