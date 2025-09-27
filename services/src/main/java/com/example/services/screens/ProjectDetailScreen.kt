package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectName: String,
    description: String,
    onJoinClick: (() -> Unit)? = null,
    onViewComments: (() -> Unit)? = null
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(projectName) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Descripción", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onJoinClick?.invoke() }, modifier = Modifier.fillMaxWidth()) {
                Text("Participar")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { onViewComments?.invoke() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ver comentarios") }
        }
    }
}