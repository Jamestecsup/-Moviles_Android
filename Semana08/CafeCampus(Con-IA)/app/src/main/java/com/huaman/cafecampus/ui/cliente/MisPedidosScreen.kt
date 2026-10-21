package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.PedidoCard

/**
 * Mis pedidos (mockup 7): pestanas Activos / Historial.
 *
 * Activos   = RECIBIDO, PREPARANDO, LISTO
 * Historial = ENTREGADO, CANCELADO
 *
 * La separacion la hace el ViewModel (pedidosActivos / pedidosHistoricos),
 * no la pantalla.
 */
@Composable
fun MisPedidosScreen(
    activos: List<PedidoConDetalle>,
    historicos: List<PedidoConDetalle>,
    onPedido: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onRepetir: (PedidoConDetalle) -> Unit = {},
    onAtras: () -> Unit = {}
) {
    var pestañaActiva by remember { mutableStateOf(0) } // 0 = Activos, 1 = Historial

    val lista = if (pestañaActiva == 0) activos else historicos

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Mis pedidos", onAtras = onAtras)

        PestanaSimple(
            opciones = listOf("Activos", "Historial"),
            seleccionada = pestañaActiva,
            onSeleccion = { pestañaActiva = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (lista.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (pestañaActiva == 0) {
                        "No tienes pedidos activos"
                    } else {
                        "Todavia no tienes historial"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = lista.size, key = { lista[it].pedido.id }) { indice ->
                val pedido = lista[indice]
                TarjetaPedidoConAcciones(
                    pedido = pedido,
                    onPedido = { onPedido(pedido.pedido.id) },
                    onRepetir = { onRepetir(pedido) }
                )
            }
        }
    }
}

/**
 * Tarjeta de un pedido con su boton de "Repetir".
 *
 * Repetir vuelve a cargar los productos del pedido al carrito, ya con los
 * precios y las promociones de HOY.
 */
@Composable
private fun TarjetaPedidoConAcciones(
    pedido: PedidoConDetalle,
    onPedido: () -> Unit,
    onRepetir: () -> Unit
) {
    Column {
        PedidoCard(
            numero = pedido.pedido.id,
            resumen = pedido.resumen,
            horaRecojo = pedido.pedido.horaRecojo,
            total = pedido.pedido.total,
            estado = pedido.pedido.estadoTipado,
            pago = FormaPago.desde(pedido.pedido.pago).etiqueta,
            nota = pedido.pedido.nota.ifBlank { null },
            onClick = onPedido
        )

        // Un pedido cancelado no se puede repetir.
        if (pedido.pedido.estadoTipado != EstadoPedido.CANCELADO) {
            OutlinedButton(
                onClick = onRepetir,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "  Repetir pedido",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/** Pestanas segmentadas Activos / Historial. */
@Composable
fun PestanaSimple(
    opciones: List<String>,
    seleccionada: Int,
    onSeleccion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        opciones.forEachIndexed { indice, texto ->
            val activo = indice == seleccionada
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (activo) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onSeleccion(indice) }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = texto,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}