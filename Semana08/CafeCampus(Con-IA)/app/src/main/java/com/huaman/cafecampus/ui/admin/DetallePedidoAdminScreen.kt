package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.EstadoChip
import com.huaman.cafecampus.ui.components.ImagenProducto
import com.huaman.cafecampus.ui.components.LineaSeparadora
import com.huaman.cafecampus.ui.components.soles

/**
 * Detalle del pedido para el administrador (mockup 4).
 *
 * Cliente, lineas, total y la nota del cliente en una tarjeta ambar, mas el
 * selector de estado y los botones de avanzar / cancelar.
 *
 * La pantalla NO decide el estado: recibe el pedido por Flow y avisa al
 * ViewModel.
 */
@Composable
fun DetallePedidoAdminScreen(
    pedido: PedidoConDetalle?,
    onCambiarEstado: (EstadoPedido) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    onAtras: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Detalle del pedido", onAtras = onAtras)

        if (pedido == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pedido no encontrado",
                    style = MaterialTheme.typography.bodyMedium,
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

            // ------------------------------------------------ cabecera
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pedido #${pedido.pedido.id.toString().padStart(4, '0')}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    EstadoChip(estado = estado)
                }
            }

            // ------------------------------------------------ cliente + lineas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {

                    Text(
                        text = "Cliente",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${pedido.cliente.nombre} - ${pedido.cliente.rolTipado.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    pedido.detalles.forEach { linea ->
                        // El producto pudo ser borrado: la linea guarda una
                        // foto del nombre, asi que se muestra sin icono.
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = linea.nombreProducto,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "x${linea.cantidad}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = soles(linea.subtotal),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    LineaSeparadora(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = soles(pedido.pedido.total),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Pago: ${pedido.pedido.pago} - Recojo: ${pedido.pedido.horaRecojo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // ------------------------------------------------ nota del cliente
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = com.huaman.cafecampus.ui.theme.EstadoPreparandoContenedor
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Nota del cliente",
                        style = MaterialTheme.typography.labelMedium,
                        color = com.huaman.cafecampus.ui.theme.EstadoPreparando
                    )
                    Text(
                        text = pedido.pedido.nota.ifBlank { "Sin notas" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ------------------------------------------------ cambiar estado
            Text(
                text = "Cambiar estado",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )

            SelectorEstadoAdmin(
                estado = estado,
                onSeleccion = onCambiarEstado
            )

            Button(
                onClick = { onCambiarEstado(estado.siguiente()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = MaterialTheme.shapes.small,
                enabled = estado != EstadoPedido.ENTREGADO && estado != EstadoPedido.CANCELADO,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Pasar a ${estado.siguiente().etiqueta}",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp),
                shape = MaterialTheme.shapes.small,
                enabled = estado != EstadoPedido.CANCELADO && estado != EstadoPedido.ENTREGADO,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Cancelar pedido",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}