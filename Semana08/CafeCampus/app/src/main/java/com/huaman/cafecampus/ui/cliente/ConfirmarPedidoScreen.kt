package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.model.ItemCarrito
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField
import com.huaman.cafecampus.ui.components.ImagenProducto
import com.huaman.cafecampus.ui.components.LineaSeparadora
import com.huaman.cafecampus.ui.components.soles

/**
 * Confirmar pedido (mockup 5).
 *
 * Resumen de lineas, chips de hora de recojo, seleccion de pago (Caja /
 * Yape-Plin), subtotal y total, y los botones "Confirmar pedido" y
 * "Seguir pidiendo".
 */
@Composable
fun ConfirmarPedidoScreen(
    items: List<ItemCarrito>,
    onConfirmar: (horaRecojo: String, pago: String, nota: String) -> Unit,
    modifier: Modifier = Modifier,
    onSeguirPidiendo: () -> Unit = {},
    onAtras: () -> Unit = {}
) {
    var horaRecojo by remember { mutableStateOf(HORAS_RECOJO[1]) }
    var pago by remember { mutableStateOf(FormaPago.CAJA) }
    var nota by remember { mutableStateOf("") }

    val subtotal = items.sumOf { it.subtotal }
    val servicio = 0.0
    val total = subtotal + servicio

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Confirmar pedido", onAtras = onAtras)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Lineas del pedido
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    items.forEach { item ->
                        LineaResumen(item)
                    }
                }
            }

            // Hora de recojo
            Text(
                text = "Hora de recojo",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HORAS_RECOJO.forEach { hora ->
                    ChipSeleccion(
                        texto = hora,
                        activo = hora == horaRecojo,
                        onClick = { horaRecojo = hora },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Forma de pago
            Text(
                text = "Forma de pago",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(FormaPago.CAJA, FormaPago.YAPE).forEach { opcion ->
                    ChipBoton(
                        texto = opcion.etiqueta,
                        activo = opcion == pago,
                        onClick = { pago = opcion },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Nota
            Text(
                text = "Nota (opcional)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 22.dp, bottom = 8.dp)
            )
            CafeTextField(
                valor = nota,
                onValorChange = { nota = it },
                etiqueta = "",
                marcador = "Sin aji, por favor",
                multilinea = true,
                minLineas = 2
            )

            // Totales
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FilaTotal("Subtotal", soles(subtotal))
                    FilaTotal("Servicio", soles(servicio))
                    LineaSeparadora(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = soles(total),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            CafeButton(
                texto = "Confirmar pedido",
                onClick = { onConfirmar(horaRecojo, pago.valor, nota) },
                modifier = Modifier.padding(top = 20.dp),
                habilitado = items.isNotEmpty()
            )

            CafeButtonSecundario(
                texto = "Seguir pidiendo",
                onClick = onSeguirPidiendo,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )
        }
    }
}

/** Una linea del resumen: foto/icono, nombre, cantidad y subtotal. */
@Composable
private fun LineaResumen(item: ItemCarrito) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImagenProducto(
            producto = item.producto,
            modifier = Modifier.size(48.dp),
            tamanio = 24
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = item.producto.nombre,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            Text(
                text = "x${item.cantidad}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (item.nota.isNotBlank()) {
                Text(
                    text = "Nota: ${item.nota}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        Text(
            text = soles(item.subtotal),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** Fila etiqueta / valor del bloque de totales. */
@Composable
private fun FilaTotal(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/** Chip redondeado para la hora de recojo. */
@Composable
fun ChipSeleccion(
    texto: String,
    activo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (activo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = if (activo) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )
    }
}

/** Chip con borde, para las formas de pago del diseno. */
@Composable
fun ChipBoton(
    texto: String,
    activo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
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
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = if (activo) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )
    }
}

/** Formas de pago: se guardan como String en `pedidos.pago`. */
enum class FormaPago(val etiqueta: String, val valor: String) {
    CAJA("Pagar en caja", "CAJA"),
    YAPE("Yape / Plin", "YAPE");

    companion object {
        fun desde(valor: String): FormaPago =
            entries.firstOrNull { it.valor == valor } ?: CAJA
    }
}

/** Horarios de recojo que ofrece la pantalla. */
val HORAS_RECOJO = listOf("Ahora", "10:30", "11:00", "11:30")