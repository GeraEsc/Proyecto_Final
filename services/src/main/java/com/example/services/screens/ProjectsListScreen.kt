package com.example.services.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.services.data.ProjectRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListScreen(
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit
) {
    val projects = remember { mutableStateOf(ProjectRepository.getProjects()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("🌍 Proyectos de Voluntariado") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProjectClick) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proyecto")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(projects.value) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onProjectClick(project.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(project.title, style = MaterialTheme.typography.titleMedium)
                        Text(project.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}