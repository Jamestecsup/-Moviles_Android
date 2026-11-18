package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.IconoContador
import com.huaman.cafecampus.ui.components.EstadoColores
import com.huaman.cafecampus.ui.components.soles
import com.huaman.cafecampus.ui.components.plural
import com.huaman.cafecampus.viewmodel.PanelAdminViewModel

/**
 * Panel de control del administrador (mockup 2).
 *
 * 3 contadores de colores, tarjeta de ventas del dia, grafico de "Pedidos
 * por hora" y el resumen de pedidos nuevos con su boton "Preparar".
 */
@Composable
fun PanelScreen(
    vm: PanelAdminViewModel,
    onVerTodos: () -> Unit,
    onPedido: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onVerReporte: () -> Unit = {},
    onVerPedidos: () -> Unit = {},
    onPreparar: (PedidoConDetalle) -> Unit = { vm.preparar(it) }
) {
    val nuevos by vm.nuevos.collectAsStateWithLifecycle()
    val preparando by vm.preparando.collectAsStateWithLifecycle()
    val listos by vm.listos.collectAsStateWithLifecycle()
    val ventas by vm.ventasHoy.collectAsStateWithLifecycle()
    val pedidosHoy by vm.pedidosHoy.collectAsStateWithLifecycle()
    val porHora by vm.pedidosPorHora.collectAsStateWithLifecycle()
    val nuevosPedidos by vm.pedidosNuevos.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(
            titulo = "Panel de control",
            accion = {
                IconoContador(
                    cantidad = nuevos,
                    icon = Icons.Filled.Notifications,
                    descripcion = "Pedidos nuevos",
                    onClick = onVerPedidos
                )
            }
        )

        Text(
            text = "Cafeteria - Hoy",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // -------------------------------------------------- 3 contadores
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ContadorCard(
                        valor = nuevos,
                        etiqueta = "Nuevos",
                        color = com.huaman.cafecampus.ui.theme.EstadoRecibido,
                        modifier = Modifier.weight(1f)
                    )
                    ContadorCard(
                        valor = preparando,
                        etiqueta = "Preparando",
                        color = com.huaman.cafecampus.ui.theme.EstadoPreparando,
                        modifier = Modifier.weight(1f)
                    )
                    ContadorCard(
                        valor = listos,
                        etiqueta = "Listos",
                        color = com.huaman.cafecampus.ui.theme.EstadoListo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // -------------------------------------------------- ventas del dia
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ventas del dia",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = soles(ventas),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Text(
                            text = plural(pedidosHoy, "pedido", "pedidos"),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // -------------------------------------------------- pedidos por hora
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Pedidos por hora",
                            style = MaterialTheme.typography.titleSmall
                        )
                        GraficoBarras(
                            datos = porHora,
                            horas = PanelAdminViewModel.HORAS_GRAFICO,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        )
                    }
                }
            }

            // -------------------------------------------------- reporte del dia
            item {
                Card(
                    onClick = onVerReporte,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 14.dp)
                        ) {
                            Text(
                                text = "Reporte del dia",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Producto mas vendido y total por forma de pago",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // -------------------------------------------------- nuevos pedidos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nuevos pedidos",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onVerTodos) {
                        Text(
                            text = "Ver todos",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (nuevosPedidos.isEmpty()) {
                item {
                    Text(
                        text = "No hay pedidos nuevos. Todo al dia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            items(
                count = nuevosPedidos.size,
                key = { nuevosPedidos[it].pedido.id }
            ) { indice ->
                val pedido = nuevosPedidos[indice]
                TarjetaPedidoNuevo(
                    pedido = pedido,
                    onPreparar = { onPreparar(pedido) },
                    onClick = { onPedido(pedido.pedido.id) }
                )
            }
        }
    }
}

/** Contador de colores: un numero grande y su etiqueta. */
@Composable
private fun ContadorCard(
    valor: Int,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

/**
 * Grafico de barras por hora. Es una composicion simple de Cajas: no hace
 * falta ninguna libreria de graficos.
 */
@Composable
private fun GraficoBarras(
    datos: Map<Int, Int>,
    horas: List<Int>,
    modifier: Modifier = Modifier
) {
    val maximo = (datos.values.maxOrNull() ?: 0).coerceAtLeast(1)

    Column(modifier = modifier) {
        // Barras
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            horas.forEach { hora ->
                val valor = datos[hora] ?: 0
                val esPico = valor == maximo && valor > 0

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = valor.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(
                                // Proporcion de la barra: 0.15 minimo para que
                                // se vea aunque valga 0.
                                (0.15f + 0.85f * valor / maximo).coerceIn(0f, 1f)
                            )
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (esPico) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                }
                            )
                    )
                }
            }
        }

        // Eje de horas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            horas.forEach { hora ->
                Text(
                    text = "${hora}h",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Fila de "Nuevos pedidos" con el boton Preparar. */
@Composable
private fun TarjetaPedidoNuevo(
    pedido: PedidoConDetalle,
    onPreparar: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${pedido.pedido.id.toString().padStart(4, '0')}  ${pedido.cliente.nombre}",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = "${pedido.resumen} - ${pedido.pedido.horaRecojo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Button(
                onClick = onPreparar,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Preparar", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}