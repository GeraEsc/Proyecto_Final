// services/src/main/java/com/example/services/screens/ActsScreen.kt
package com.example.services.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.core_data.model.Actividad
import com.example.core_data.viewmodel.GestorViewModel
import com.example.services.data.FeaturedAct
import com.example.services.data.FeaturedActs
import com.example.services.ui.components.ActCard
import java.time.LocalDate


//Pantalla principal con listado de actividades
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActsScreen(
    navController: NavController,
    viewModel: GestorViewModel,
    onNavigateToAuth: (() -> Unit)? = null,
    onCreateAct: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val myActs by viewModel.acts.collectAsState(initial = emptyList())
    val catalogo = remember { FeaturedActs.items }

    // --- Filtros ---
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedLocation by rememberSaveable { mutableStateOf<String?>(null) }
    var startDate by rememberSaveable { mutableStateOf("") } // YYYY-MM-DD
    var endDate by rememberSaveable { mutableStateOf("") }   // YYYY-MM-DD

    val categories = remember(catalogo) { catalogo.map { it.categoria }.distinct().sorted() }
    val locations  = remember(catalogo) { catalogo.map { it.ubicacion }.distinct().sorted() }

    fun parseDate(s: String): LocalDate? = runCatching { LocalDate.parse(s.trim()) }.getOrNull()
    val from = remember(startDate) { parseDate(startDate) }
    val to   = remember(endDate)   { parseDate(endDate) }

    fun matchesQuery(a: FeaturedAct) =
        query.isBlank() || a.titulo.contains(query, true) || a.descripcion.contains(query, true)
    fun matchesCategory(a: FeaturedAct) =
        selectedCategory?.let { a.categoria == it } ?: true
    fun matchesLocation(a: FeaturedAct) =
        selectedLocation?.let { a.ubicacion == it } ?: true
    fun matchesDate(a: FeaturedAct): Boolean {
        val d = parseDate(a.date) ?: return true
        if (from != null && d.isBefore(from)) return false
        if (to   != null && d.isAfter(to))    return false
        return true
    }
    val disponibles = remember(catalogo, query, selectedCategory, selectedLocation, from, to) {
        catalogo.filter { a -> matchesQuery(a) && matchesCategory(a) && matchesLocation(a) && matchesDate(a) }
    }

    // Inscripción
    fun isEnrolled(id: String): Boolean = myActs.any { it.id == id }
    fun toggleEnrollmentFor(featured: FeaturedAct) {
        val current = myActs.firstOrNull { it.id == featured.id }
        if (current != null) {
            viewModel.deleteAct(current)
        } else {
            val entity = Actividad(
                id = featured.id,
                titulo = featured.titulo,
                descripcion = featured.descripcion,
                date = featured.date,
                calificaciones = 0.0
            )
            viewModel.insertAct(entity)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        //Cabecera
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text("Gestor de Voluntariado", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Actividades disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            //icono configuración
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(Icons.Rounded.Settings, contentDescription = "Configuracion")
            }
            //icono perfil
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = "Perfil")
            }
        }

        Spacer(Modifier.height(12.dp))

        // ------------------ Búsqueda ------------------
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar por título o descripcion") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        //Filtros de categoría y ubicacion
        Row(verticalAlignment = Alignment.CenterVertically) {
            var expCat by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expCat,
                onExpandedChange = { expCat = !expCat },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                OutlinedTextField(
                    value = selectedCategory ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    placeholder = { Text("Todas") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expCat) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expCat, onDismissRequest = { expCat = false }) {
                    DropdownMenuItem(text = { Text("Todas") },
                        onClick = { selectedCategory = null; expCat = false })
                    categories.forEach { c ->
                        DropdownMenuItem(text = { Text(c) },
                            onClick = { selectedCategory = c; expCat = false })
                    }
                }
            }

            var expLoc by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expLoc,
                onExpandedChange = { expLoc = !expLoc },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                OutlinedTextField(
                    value = selectedLocation ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ubicación") },
                    placeholder = { Text("Todas") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expLoc) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expLoc, onDismissRequest = { expLoc = false }) {
                    DropdownMenuItem(text = { Text("Todas") },
                        onClick = { selectedLocation = null; expLoc = false })
                    locations.forEach { l ->
                        DropdownMenuItem(text = { Text(l) },
                            onClick = { selectedLocation = l; expLoc = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        //Filtros de fecha
        Row {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Desde (YYYY-MM-DD)") },
                isError = startDate.isNotBlank() && parseDate(startDate) == null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("Hasta (YYYY-MM-DD)") },
                isError = endDate.isNotBlank() && parseDate(endDate) == null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Coincidencias: ${disponibles.size}", modifier = Modifier.weight(1f))
            TextButton(onClick = {
                query = ""; selectedCategory = null; selectedLocation = null
                startDate = ""; endDate = ""
            }) { Text("Limpiar filtros") }
        }

        Spacer(Modifier.height(8.dp))

        // ------------------ Carrusel destacadas ------------------
        if (disponibles.isNotEmpty()) {
            Text("Destacadas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(disponibles, key = { it.id }) { f ->
                    FeaturedCard(
                        featured = f,
                        isEnrolled = isEnrolled(f.id),
                        onToggleEnroll = { toggleEnrollmentFor(f) },
                        onVerDetalle = { navController.navigate("act_detail/${f.id}") }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        } else {
            Text("No hay actividades con esos filtros.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
        }

        Divider()

        //Mis inscripciones
        Spacer(Modifier.width(8.dp))
        Text("Mis inscripciones", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))

        if (myActs.isEmpty()) {
            Text("Aun no te has inscrito en ninguna actividad.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(myActs, key = { it.id }) { act ->
                    val f = catalogo.firstOrNull { it.id == act.id }
                    ActCard(
                        act = act,
                        category = f?.categoria,
                        location = f?.ubicacion,
                        isEnrolled = true,
                        onToggleEnroll = { /* si solo deseas cancelar desde detalle, navega: */
                            // navController.navigate("act_detail/${act.id}")
                            viewModel.deleteAct(act)
                        },
                        onClick = { navController.navigate("act_detail/${act.id}") },
                        imagePainter = f?.imageUrl?.let { rememberAsyncImagePainter(it) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { onCreateAct?.invoke() ?: navController.navigate("act_editor") }) {
            Text("Crear actividad")
        }

        if (onNavigateToAuth != null) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = onNavigateToAuth, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Login / Registro")
            }
        }
    }
}


//Tarjeta para actividad destacada en el carrusel
@Composable
private fun FeaturedCard(
    featured: FeaturedAct,
    isEnrolled: Boolean,
    onToggleEnroll: () -> Unit,
    onVerDetalle: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
    ) {
        Box {
            AsyncImage(
                model = featured.imageUrl,
                contentDescription = featured.titulo,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xAA000000))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(featured.titulo, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("${featured.categoria} • ${featured.ubicacion}", color = Color.White)
                Text("Fecha: ${featured.date}", color = Color.White)

                Spacer(Modifier.height(6.dp))
                Row {
                    OutlinedButton(onClick = onToggleEnroll) {
                        Text(
                            if (isEnrolled) "Ya inscrito" else "Inscribirme",
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = onVerDetalle) { Text("Ver detalles") }
                }
            }
        }
    }
}
