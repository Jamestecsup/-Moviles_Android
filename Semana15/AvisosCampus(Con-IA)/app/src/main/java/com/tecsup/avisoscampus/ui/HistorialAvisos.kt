package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    TarjetaAvisos("Historial") {
        if (avisos.isEmpty()) Text("Sin avisos todavía")
        avisos.take(20).forEach { aviso ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EtiquetaOrigen(aviso.origen)
                        Text(aviso.hora, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(
                        aviso.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(aviso.texto, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val (fondo, tinta) = when (origen) {
        Origen.BOTON -> Color(0xFFD9EBFF) to Color(0xFF0B487D)
        Origen.RECORDATORIO -> Color(0xFFFFE7AA) to Color(0xFF644600)
        Origen.MODO_AVION -> Color(0xFFEEE1FF) to Color(0xFF51317C)
        Origen.CARGADOR -> Color(0xFFD5F3E4) to Color(0xFF14533A)
    }
    Surface(shape = RoundedCornerShape(50), color = fondo, contentColor = tinta) {
        Text(
            text = origen.texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
