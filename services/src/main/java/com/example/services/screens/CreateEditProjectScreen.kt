package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.services.data.ProjectRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditProjectScreen(
    projectId: Int? = null,  // null = crear, no null = editar
    onSave: () -> Unit
) {
    val project = projectId?.let { ProjectRepository.getProjectById(it) }
    var title by remember { mutableStateOf(project?.title ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (project == null) "Nuevo Proyecto" else "Editar Proyecto") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del Proyecto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (project == null) {
                        ProjectRepository.addProject(title, description)
                    } else {
                        ProjectRepository.updateProject(project.id, title, description)
                    }
                    onSave()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}