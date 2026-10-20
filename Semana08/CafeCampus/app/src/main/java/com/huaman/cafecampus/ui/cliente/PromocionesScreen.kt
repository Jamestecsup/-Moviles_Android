package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.EstadoChip
import com.huaman.cafecampus.viewmodel.PromocionesViewModel

@Composable
fun PromocionesScreen(
    vm: PromocionesViewModel,
    rol: String,
    onEditar: (Int) -> Unit = {},
    onEliminar: (Int) -> Unit = {},
    onNuevo: () -> Unit = {},
    modifier: Modifier = Modifier,
    onAtras: () -> Unit = {}
) {
    val promos by vm.promociones.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(titulo = "Promociones", onAtras = onAtras)

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Promociones activas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (promos.isEmpty()) {
                Text(
                    text = "No hay promociones activas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                items(promos, key = { it.id }) { p ->
                    TarjetaPromocion(promocion = p, rol = rol)
                }
            }
        }
    }
}

@Composable
private fun TarjetaPromocion(promocion: Promocion, rol: String) {
    val fechaFin = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        .format(java.util.Date(promocion.fechaFin))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = promocion.descripcion(),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = "${promocion.tipo} · ${promocion.descuentoPorcentaje.toInt()}% descuento",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Valido hasta: $fechaFin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            EstadoChip(
                estado = if (promocion.esValida) {
                    com.huaman.cafecampus.model.EstadoPedido.LISTO
                } else {
                    com.huaman.cafecampus.model.EstadoPedido.CANCELADO
                }
            )
        }
    }
}