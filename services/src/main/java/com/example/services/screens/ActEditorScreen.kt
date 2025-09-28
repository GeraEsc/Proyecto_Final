package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core_data.model.Actividad
import com.example.core_data.viewmodel.GestorViewModel
import java.time.LocalDate
import java.util.UUID


//Pantalla para crear o editar una actividad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActEditorScreen(
    viewModel: GestorViewModel,
    actId: String? = null,
    onClose: () -> Unit
) {
    val myActs by viewModel.acts.collectAsState(initial = emptyList())
    val existing = remember(myActs, actId) { myActs.firstOrNull { it.id == actId } }
    val isEdit = existing != null
    val isLocal = existing?.id?.startsWith("local-") ?: true // nuevas también son "local"

    // Parse simple de "Lugar: xxx" en la primera línea de la descripcion
    fun splitDescripcion(desc: String?): Pair<String, String?> {
        if (desc.isNullOrBlank()) return "" to null
        val lines = desc.lines()
        val first = lines.firstOrNull()?.trim().orEmpty()
        val prefix = "Lugar:"
        return if (first.startsWith(prefix, ignoreCase = true)) {
            val lugar = first.removePrefix(prefix).trim()
            val rest = lines.drop(1).joinToString("\n").trim()
            rest to lugar.ifBlank { null }
        } else {
            desc to null
        }
    }

    val (desc0, lugar0) = splitDescripcion(existing?.descripcion)

    var titulo by rememberSaveable { mutableStateOf(existing?.titulo.orEmpty()) }
    var descripcion by rememberSaveable { mutableStateOf(desc0) }
    var fecha by rememberSaveable { mutableStateOf(existing?.date.orEmpty()) }  // YYYY-MM-DD
    var lugar by rememberSaveable { mutableStateOf(lugar0.orEmpty()) }

    //Validaciones
    val tituloError = titulo.isBlank()
    val fechaError = fecha.isNotBlank() && runCatching { LocalDate.parse(fecha) }.isFailure

    //Reconstruir descripción con posible lugar
    fun buildDescripcion(lugar: String, descripcion: String): String =
        buildString {
            if (lugar.isNotBlank()) append("Lugar: ${lugar.trim()}\n")
            append(descripcion.trim())
        }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar actividad" else "Nueva actividad") },
                navigationIcon = {
                    TextButton(onClick = onClose) { Text("Cerrar") }
                },
                actions = {
                    // Guardar (solo editar local o crear)
                    val canSave = !tituloError && !fechaError && (isLocal || !isEdit)
                    TextButton(
                        onClick = {
                            val act = Actividad(
                                id = existing?.id ?: "local-${UUID.randomUUID()}",
                                titulo = titulo.trim(),
                                descripcion = buildDescripcion(lugar, descripcion),
                                date = fecha.trim(),
                                calificaciones = existing?.calificaciones ?: 0.0
                            )
                            if (isEdit) viewModel.updateAct(act) else viewModel.insertAct(act)
                            onClose()
                        },
                        enabled = canSave
                    ) { Text("Guardar") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEdit && !isLocal) {
                //Bloque informativo para actividades de catalogo
                Text(
                    "Esta actividad proviene del catalogo y no puede editarse ni eliminarse.",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                isError = tituloError,
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                enabled = isLocal || !isEdit,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lugar,
                onValueChange = { lugar = it },
                label = { Text("Lugar") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                enabled = isLocal || !isEdit,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                isError = fechaError,
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next
                ),
                enabled = isLocal || !isEdit,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripcion") },
                minLines = 4,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                enabled = isLocal || !isEdit,
                modifier = Modifier.fillMaxWidth()
            )

            //Boton eliminar solo si es una actividad local creada manualmente
            if (isEdit && isLocal) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        existing?.let { viewModel.deleteAct(it) }
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Eliminar actividad") }
            }
        }
    }
}
