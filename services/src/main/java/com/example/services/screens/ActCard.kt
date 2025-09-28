package com.example.services.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core_data.model.Actividad


//Tarjeta para mostrar una actividad con opciones para inscribirse y ver detalles
@Composable
fun ActCard(
    act: Actividad,
    category: String?,
    location: String?,
    isEnrolled: Boolean,
    onToggleEnroll: (() -> Unit)?,
    onClick: () -> Unit,
    imagePainter: Painter? = null,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            if (imagePainter != null) {
                Image(
                    painter = imagePainter,
                    contentDescription = act.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(act.titulo, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            if (!category.isNullOrBlank() || !location.isNullOrBlank()) {
                Text(
                    listOfNotNull(category, location).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (act.date.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text("Fecha: ${act.date}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(act.descripcion, maxLines = 3, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onToggleEnroll != null) {
                    OutlinedButton(onClick = onToggleEnroll) {
                        Text(if (isEnrolled) "Ya inscrito" else "Inscribirme")
                    }
                }
                Button(onClick = onClick) { Text("Ver detalles") }
            }
        }
    }
}
