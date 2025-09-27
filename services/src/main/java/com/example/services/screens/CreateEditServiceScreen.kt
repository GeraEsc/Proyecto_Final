package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateEditServiceScreen(onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(title, description) }) { Text("Guardar") }
    }
}