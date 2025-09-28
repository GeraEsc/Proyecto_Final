package com.example.ratings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// ------------------------------
// Modelo de datos para una calificación/reseña
// ------------------------------
private data class RatingDoc(
    val id: String = "",
    val userId: String? = null,
    val userName: String? = null,
    val stars: Int = 0,
    val comment: String = "",
    val createdAt: Long = 0L
)

// ------------------------------
// Extrae "stars" de forma robusta
// ------------------------------
private fun starsFrom(d: DocumentSnapshot): Int {
    d.getLong("stars")?.let { return it.toInt().coerceIn(0, 5) }
    d.getDouble("stars")?.let { return it.roundToInt().coerceIn(0, 5) }
    d.get("stars")?.toString()?.toIntOrNull()?.let { return it.coerceIn(0, 5) }
    return 0
}

//Pantalla principal de calificaciones y reseñas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    actId: String,
    onBack: () -> Unit,
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid
    val currentUserName = currentUser?.displayName ?: currentUser?.email ?: "Anónimo"

    var ratings by remember { mutableStateOf(listOf<RatingDoc>()) }
    var myStars by remember { mutableStateOf(0) }
    var myComment by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    // Suscripcion en vivo a ratings de la actividad
    DisposableEffect(actId) {
        val ref = db.collection("activities")
            .document(actId)
            .collection("ratings")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snap, _ ->
            val list = snap?.documents?.map { d ->
                RatingDoc(
                    id = d.id,
                    userId = d.getString("userId"),
                    userName = d.getString("userName"),
                    stars = starsFrom(d),                       // <--- USAR HELPER
                    comment = d.getString("comment") ?: "",
                    createdAt = d.getLong("createdAt") ?: 0L
                )
            } ?: emptyList()

            ratings = list

            // precarga la reseña del usuario logueado (si existe)
            if (currentUserId != null) {
                list.firstOrNull { it.id == currentUserId }?.let { mine ->
                    myStars = mine.stars
                    myComment = mine.comment
                }
            }
        }
        onDispose { listener.remove() }
    }

    val avg = remember(ratings) {
        if (ratings.isEmpty()) 0.0 else ratings.map { it.stars }.average()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas y calificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            //Resumen de promedio
            Card(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StaticStars(avg = avg) // promedio con posible media estrella
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("${"%.1f".format(avg)} / 5.0", style = MaterialTheme.typography.titleMedium)
                        Text("${ratings.size} opiniones", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Formulario para calificar (una reseña por usuario)
            Text("Tu calificacion", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            SelectableStars(selected = myStars, onSelect = { myStars = it })

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = myComment,
                onValueChange = { myComment = it },
                label = { Text("Comentario") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (currentUserId == null) return@Button // exige login
                    if (myStars in 1..5 && myComment.isNotBlank() && !isSending) {
                        isSending = true
                        submitRating(
                            db = db,
                            actId = actId,
                            userId = currentUserId,
                            userName = currentUserName,
                            stars = myStars,
                            comment = myComment.trim(),
                        ) { isSending = false }
                    }
                },
                enabled = (myStars in 1..5) && myComment.isNotBlank() && !isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                val alreadyRated = currentUserId != null && ratings.any { it.id == currentUserId }
                Text(if (alreadyRated) "Actualizar reseña" else "Enviar reseña")
            }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            // Lista de reseñas
            Text("Reseñas de la comunidad", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(ratings, key = { it.id }) { r ->
                    RatingItem(r)
                    Divider()
                }
            }
        }
    }
}

//Envía o actualiza la calificacion/reseña de un usuario
private fun submitRating(
    db: FirebaseFirestore,
    actId: String,
    userId: String,
    userName: String,
    stars: Int,
    comment: String,
    onDone: () -> Unit
) {
    val data = hashMapOf(
        "userId" to userId,
        "userName" to userName,
        "stars" to stars,
        "comment" to comment,
        "createdAt" to System.currentTimeMillis()
    )
    db.collection("activities")
        .document(actId)
        .collection("ratings")
        .document(userId)
        .set(data)
        .addOnCompleteListener { onDone() }
}

//selector de estrellas (1..5)
@Composable
private fun SelectableStars(selected: Int, onSelect: (Int) -> Unit) {
    Row {
        (1..5).forEach { i ->
            IconButton(onClick = { onSelect(i) }) {
                val icon = if (i <= selected) Icons.Filled.Star else Icons.Outlined.Star
                Icon(icon, contentDescription = "$i estrellas")
            }
        }
    }
}

//promedio (puede tener medias estrellas)
@Composable
private fun StaticStars(avg: Double) {
    val full = avg.toInt().coerceIn(0, 5)
    val hasHalf = (avg - full) >= 0.25 && (avg - full) < 0.75
    val empty = 5 - full - if (hasHalf) 1 else 0

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(full) { Icon(Icons.Filled.Star, contentDescription = null) }
        if (hasHalf) Icon(Icons.Rounded.StarHalf, contentDescription = null)
        repeat(empty) { Icon(Icons.Outlined.Star, contentDescription = null) }
    }
}

//estrellas fijas enteras (para cada reseña)
@Composable
private fun FixedStars(stars: Int) {
    val s = stars.coerceIn(0, 5)
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(s) { Icon(Icons.Filled.Star, contentDescription = null) }
        repeat(5 - s) { Icon(Icons.Outlined.Star, contentDescription = null) }
    }
}

//ítem de reseña individual
@Composable
private fun RatingItem(r: RatingDoc) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FixedStars(stars = r.stars)
            Spacer(Modifier.width(8.dp))
            Text(r.userName ?: "Anónimo", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(8.dp))
            Text(
                dateString(r.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (r.comment.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(r.comment)
        }
    }
}

private fun dateString(ms: Long): String {
    if (ms <= 0) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(ms))
}


@Composable
fun AverageRatingRow(
    actId: String,
    onSeeAll: () -> Unit,
) {
    val db = remember { FirebaseFirestore.getInstance() }
    var ratings by remember { mutableStateOf(listOf<RatingDoc>()) }

    DisposableEffect(actId) {
        val ref = db.collection("activities").document(actId).collection("ratings")
        val l = ref.addSnapshotListener { snap, _ ->
            ratings = snap?.documents?.map { d ->
                RatingDoc(
                    id = d.id,
                    userId = d.getString("userId"),
                    userName = d.getString("userName"),
                    stars = starsFrom(d),                     // <--- USAR HELPER AQUÍ TAMBIÉN
                    comment = d.getString("comment") ?: "",
                    createdAt = d.getLong("createdAt") ?: 0L
                )
            } ?: emptyList()
        }
        onDispose { l.remove() }
    }

    val avg = remember(ratings) { if (ratings.isEmpty()) 0.0 else ratings.map { it.stars }.average() }

    Row(verticalAlignment = Alignment.CenterVertically) {
        StaticStars(avg)
        Spacer(Modifier.width(8.dp))
        Text("${"%.1f".format(avg)} (${ratings.size})", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = onSeeAll) { Text("Ver reseñas") }
    }
}
