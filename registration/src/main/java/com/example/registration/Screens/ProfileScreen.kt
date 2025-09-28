// registration/src/main/java/com/example/registration/Screens/ProfileScreen.kt
package com.example.registration.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,          // el host (app) decide adónde navegar tras cerrar sesión
    modifier: Modifier = Modifier
) {
    val user = remember { FirebaseAuth.getInstance().currentUser }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // Foto (si hay) – requiere coil-compose
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(12.dp))
            Text(user?.displayName ?: "Usuario", style = MaterialTheme.typography.titleMedium)
            Text(user?.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!user?.uid.isNullOrBlank()) {
                Text("UID: ${user!!.uid}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
