package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.EstadoChip
import com.huaman.cafecampus.ui.components.EstadoVacio
import com.huaman.cafecampus.ui.components.PedidoCard
import com.huaman.cafecampus.ui.components.soles
import com.huaman.cafecampus.viewmodel.PedidosAdminViewModel

/**
 * Pedidos por estado (mockup 3): la bandeja de trabajo de la cafeteria.
 *
 * FilterChips de Nuevos / Preparando / Listos. El chip manda: la lista cambia
 * porque el ViewModel usa `flatMapLatest` sobre el estado seleccionado.
 */
@Composable
fun PedidosAdminScreen(
    vm: PedidosAdminViewModel,
    onPedido: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onAtras: (() -> Unit)? = null
) {
    val pedidos by vm.pedidos.collectAsStateWithLifecycle()
    val estado by vm.estadoSeleccionado.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Pedidos", onAtras = onAtras)

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.estadosVisibles) { e ->
                FilterChip(
                    selected = e == estado,
                    onClick = { vm.seleccionar(e) },
                    label = { Text(e.etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = e == estado,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        if (pedidos.isEmpty()) {
            EstadoVacio(
                texto = "No hay pedidos en ${estado.etiqueta.lowercase()}",
                subtitulo = "Los pedidos nuevos apareceran aqui apenas el estudiante confirme",
                icono = Icons.AutoMirrored.Filled.ReceiptLong,
                modifier = Modifier.fillMaxSize()
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pedidos.size, key = { pedidos[it].pedido.id }) { indice ->
                val pedido = pedidos[indice]

                PedidoCard(
                    numero = pedido.pedido.id,
                    resumen = pedido.resumen,
                    horaRecojo = pedido.pedido.horaRecojo,
                    total = pedido.pedido.total,
                    estado = pedido.pedido.estadoTipado,
                    nombreCliente = pedido.cliente.nombre,
                    pago = com.huaman.cafecampus.ui.cliente.FormaPago
                        .desde(pedido.pedido.pago).etiqueta,
                    nota = pedido.pedido.nota.ifBlank { null },
                    onClick = { onPedido(pedido.pedido.id) }
                )

                // Acciones del pedido
                FilaAccionesPedido(
                    pedido = pedido,
                    textoAvanzar = vm.textoAvanzar(pedido.pedido.estadoTipado),
                    onAvanzar = { vm.avanzar(pedido) },
                    onCancelar = { vm.cancelar(pedido) }
                )
            }
        }
    }
}

/**
 * Fila de acciones: "Pasar a <siguiente>" y "Cancelar".
 * Los botones NO deciden el estado: solo avisan al ViewModel.
 */
@Composable
private fun FilaAccionesPedido(
    pedido: PedidoConDetalle,
    textoAvanzar: String,
    onAvanzar: () -> Unit,
    onCancelar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onAvanzar,
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(textoAvanzar, style = MaterialTheme.typography.labelMedium)
        }

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(vertical = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cancelar", style = MaterialTheme.typography.labelMedium)
        }
    }
}