package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.CirculoNumerado
import com.huaman.cafecampus.ui.components.EstadoColores

/**
 * Selector de estado del administrador (mockup 4):
 * Recibido / Preparando / Listo / Entregado.
 *
 * Los pasos completados salen en el color del estado; el actual, resaltado.
 */
@Composable
fun SelectorEstadoAdmin(
    estado: EstadoPedido,
    onSeleccion: (EstadoPedido) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones = listOf(
        EstadoPedido.RECIBIDO,
        EstadoPedido.PREPARANDO,
        EstadoPedido.LISTO,
        EstadoPedido.ENTREGADO
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEachIndexed { indice, opcion ->
            val activo = opcion == estado
            val completado = orden(opcion) < orden(estado)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onSeleccion(opcion) }
                    .border(
                        BorderStroke(
                            1.dp,
                            if (activo) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CirculoNumerado(numero = indice + 1, activo = completado || activo)
                    Text(
                        text = opcion.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = if (activo) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            EstadoColores.texto(opcion)
                        },
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
    }
}

private fun orden(estado: EstadoPedido): Int = when (estado) {
    EstadoPedido.RECIBIDO -> 0
    EstadoPedido.PREPARANDO -> 1
    EstadoPedido.LISTO -> 2
    EstadoPedido.ENTREGADO -> 3
    EstadoPedido.CANCELADO -> -1
}