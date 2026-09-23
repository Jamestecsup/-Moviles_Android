package com.tecsup.tecsupfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Cuarto destino del bottomBar. La tarea no pide requisitos funcionales
 * específicos para esta pantalla (solo que exista como destino navegable
 * con su ícono resaltado), así que se deja como contenido simple.
 */
@Composable
fun RutinasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.List,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Rutinas",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Próximamente: tus rutinas personalizadas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}