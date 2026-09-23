package com.huaman.tecsupfit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.huaman.tecsupfit.model.EstadoReserva
import com.huaman.tecsupfit.model.Reserva

@Composable
fun ReservaItem(
    reserva: Reserva,
    onCancelarClick: (() -> Unit)? = null
) {
    val esConfirmada = reserva.estado == EstadoReserva.CONFIRMADA
    val colorEstado = if (esConfirmada) Color(0xFF0F5132) else Color(0xFF757575)
    val textoEstado = if (esConfirmada) "Confirmada" else "Completada"
    val horaTexto = if (esConfirmada) "Hoy, ${reserva.clase.hora}" else "Ayer, ${reserva.clase.hora}"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(80.dp)
                    .background(colorEstado)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = reserva.clase.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = horaTexto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Surface(
                    color = colorEstado.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            if (esConfirmada && onCancelarClick != null) {
                TextButton(
                    onClick = onCancelarClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
