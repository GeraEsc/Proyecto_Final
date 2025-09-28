
package com.example.proyecto_final

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    onFinished: () -> Unit,
    showProgress: Boolean = true,
    durationMillis: Int = 1100
) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        label = "logo-scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(durationMillis.toLong())
        onFinished()
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Logo “GV”
                LogoBadge(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale)
                )
                Spacer(Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Gestor de Voluntariado",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (showProgress) {
                    Spacer(Modifier.height(18.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LogoBadge(
    modifier: Modifier = Modifier
) {
    // Círculo con gradiente + siglas GV
    val grad = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
    Box(
        modifier = modifier
            .background(grad, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "GV",
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
