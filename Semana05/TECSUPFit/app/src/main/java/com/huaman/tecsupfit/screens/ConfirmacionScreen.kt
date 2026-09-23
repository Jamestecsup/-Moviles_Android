package com.huaman.tecsupfit.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huaman.tecsupfit.model.ClaseGym

@Composable
fun ConfirmacionScreen(
    clase: ClaseGym,
    onVerReservasClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
        }
        Text("¡Cupo reservado!", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
        Text(clase.nombre, style = MaterialTheme.typography.titleMedium)
        Text(
            "Hoy, ${clase.hora} · ${clase.sala}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onVerReservasClick, modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) {
            Text("Ver mis reservas")
        }
    }
}
