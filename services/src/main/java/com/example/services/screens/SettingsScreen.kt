package com.example.services.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    darkModeEnabled: Boolean,
    language: String,
    onToggleDarkMode: (Boolean) -> Unit,
    onChangeLanguage: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Modo oscuro", modifier = Modifier.weight(1f))
            Switch(checked = darkModeEnabled, onCheckedChange = onToggleDarkMode)
        }

        Spacer(Modifier.height(20.dp))
        Text("Idioma actual: $language", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onChangeLanguage(if (language == "ES") "EN" else "ES") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambiar a ${if (language == "ES") "EN" else "ES"}")
        }
    }
}