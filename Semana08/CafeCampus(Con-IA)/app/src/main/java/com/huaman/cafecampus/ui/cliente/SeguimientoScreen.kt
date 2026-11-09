package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.EstadoChip
import com.huaman.cafecampus.ui.components.StepperEstado
import com.huaman.cafecampus.ui.components.soles

/**
 * Seguimiento del pedido (mockup 6).
 *
 * Tarjeta de cabecera + StepperEstado.
 *
 * IMPORTANTE: la pantalla NO calcula el estado. Recibe el pedido que llega
 * por Flow desde Room, asi que se actualiza SOLO cuando el administrador
 * cambia el estado, sin reiniciar la app.
 */
@Composable
fun SeguimientoScreen(
    pedido: PedidoConDetalle?,
    modifier: Modifier = Modifier,
    onAtras: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Seguimiento", onAtras = onAtras)

        if (pedido == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = "No encontramos ese pedido",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }

        val estado = pedido.pedido.estadoTipado

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Cabecera: numero de pedido, recojo y tiempo estimado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (estado == EstadoPedido.CANCELADO) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #${pedido.pedido.id.toString().padStart(4, '0')}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (estado == EstadoPedido.CANCELADO) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                    Text(
                        text = "Recojo a las ${pedido.pedido.horaRecojo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (estado == EstadoPedido.CANCELADO) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Tiempo estimado: ${tiempoEstimado(estado)} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (estado == EstadoPedido.CANCELADO) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (estado == EstadoPedido.CANCELADO) {
                                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (estado == EstadoPedido.CANCELADO) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                }
            }

            // Si esta cancelado, aviso claro
            if (estado == EstadoPedido.CANCELADO) {
                Text(
                    text = "La cafeteria cancelo este pedido. Habla con el personal para mas detalles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            // Stepper
            Box(modifier = Modifier.padding(top = 18.dp)) {
                StepperEstado(estadoActual = estado)
            }

            // Resumen del pedido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pedido.resumen,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    EstadoChip(
                        estado = estado,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Text(
                    text = "Total ${soles(pedido.pedido.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Minutos que faltan, calculados a partir del estado.
 * Es una ayuda visual, no una regla de negocio.
 */
private fun tiempoEstimado(estado: EstadoPedido): Int = when (estado) {
    EstadoPedido.RECIBIDO -> 10
    EstadoPedido.PREPARANDO -> 6
    EstadoPedido.LISTO -> 0
    EstadoPedido.ENTREGADO -> 0
    EstadoPedido.CANCELADO -> 0
}