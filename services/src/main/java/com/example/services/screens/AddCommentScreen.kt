package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(onAdd: (String) -> Unit) {
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Nuevo comentario") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Escribe tu comentario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onAdd(comment) },
                enabled = comment.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar") }
        }
    }
}