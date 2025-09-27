package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServiceDetailScreen(serviceName: String, description: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = serviceName, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}