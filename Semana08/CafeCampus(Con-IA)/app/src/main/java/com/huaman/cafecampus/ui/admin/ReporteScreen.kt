package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.relation.ProductoVendido
import com.huaman.cafecampus.data.local.relation.TotalPorPago
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.soles
import com.huaman.cafecampus.viewmodel.ReporteViewModel

/**
 * Reporte del dia (Fase 2, mejora 3).
 *
 * Muestra el producto mas vendido y el total por forma de pago. Todo se
 * calcula con consultas agregadas en Room: si no hay pedidos, se avisa.
 */
@Composable
fun ReporteScreen(
    vm: ReporteViewModel,
    modifier: Modifier = Modifier,
    onAtras: () -> Unit = {}
) {
    val masVendidos by vm.masVendidos.collectAsStateWithLifecycle()
    val porPago by vm.porFormaDePago.collectAsStateWithLifecycle()
    val masVendido by vm.productoMasVendido.collectAsStateWithLifecycle()
    val totalDia by vm.totalDelDia.collectAsStateWithLifecycle()
    val pedidosDia by vm.pedidosDelDia.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Reporte del dia", onAtras = onAtras)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Resumen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaResumen(
                    titulo = "Ventas",
                    valor = soles(totalDia),
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Pedidos",
                    valor = pedidosDia.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Producto mas vendido
            Text(
                text = "Producto mas vendido",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )

            if (masVendido == null) {
                VacioReporte(texto = "Todavia no hay ventas hoy")
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.padding(start = 14.dp)) {
                            Text(
                                text = masVendido!!.nombreProducto,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${masVendido!!.unidades} unidad(es) · ${soles(masVendido!!.total)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Ranking
            if (masVendidos.size > 1) {
                Text(
                    text = "Ranking del dia",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        masVendidos.forEachIndexed { indice, producto ->
                            FilaProductoVendido(
                                posicion = indice + 1,
                                producto = producto,
                                maximo = masVendidos.maxOf { it.unidades }
                            )
                        }
                    }
                }
            }

            // Total por forma de pago
            Text(
                text = "Total por forma de pago",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )

            if (porPago.isEmpty()) {
                VacioReporte(texto = "Sin pedidos registrados hoy")
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        porPago.forEach { fila ->
                            FilaFormaDePago(
                                fila = fila,
                                maximo = porPago.maxOf { it.total }.coerceAtLeast(1.0)
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

/** Tarjeta pequena de resumen (ventas / pedidos). */
@Composable
private fun TarjetaResumen(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Fila del ranking: posicion, nombre, unidades y barra proporcional. */
@Composable
private fun FilaProductoVendido(posicion: Int, producto: ProductoVendido, maximo: Int) {
    Column(modifier = Modifier.padding(vertical = 7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$posicion",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = producto.nombreProducto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Text(
                text = "${producto.unidades}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        // Barra proporcional a las unidades
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (maximo > 0) producto.unidades.toFloat() / maximo else 0f
                    )
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/** Fila del total por forma de pago, con su barra proporcional. */
@Composable
private fun FilaFormaDePago(fila: TotalPorPago, maximo: Double) {
    val etiqueta = if (fila.pago == "CAJA") "Pagar en caja" else "Yape / Plin"
    Column(modifier = Modifier.padding(vertical = 7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Payments,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "  $etiqueta",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${fila.pedidos} pedido(s)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "  ${soles(fila.total)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((fila.total / maximo).toFloat().coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/** Estado vacio del reporte. */
@Composable
private fun VacioReporte(texto: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
