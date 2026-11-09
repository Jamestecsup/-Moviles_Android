package com.huaman.cafecampus.ui.cliente

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.EstadoChip
import com.huaman.cafecampus.ui.components.EstadoVacio
import com.huaman.cafecampus.viewmodel.PromocionesViewModel

/**
 * Promociones.
 *
 * El ESTUDIANTE solo ve las activas (las que se aplican).
 * El ADMINISTRADOR ademas puede crear, editar, habilitar/deshabilitar y
 * eliminar.
 */
@Composable
fun PromocionesScreen(
    vm: PromocionesViewModel,
    esAdmin: Boolean,
    modifier: Modifier = Modifier,
    onNueva: () -> Unit = {},
    onEditar: (Int) -> Unit = {},
    onAtras: () -> Unit = {}
) {
    val todas by vm.todas.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val mensajeError = error

    val activas = remember(todas) { todas.filter { it.activo } }
    val deshabilitadas = remember(todas) { todas.filter { !it.activo } }
    var porEliminar by remember { mutableStateOf<Promocion?>(null) }

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(titulo = "Promociones", onAtras = onAtras)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = if (esAdmin) 96.dp else 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (mensajeError != null) {
                    item {
                        Text(
                            text = mensajeError,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (todas.isEmpty()) {
                    item {
                        EstadoVacio(
                            texto = "No hay promociones",
                            subtitulo = if (esAdmin) {
                                "Crea una con el boton + para ubicar un producto o una categoria"
                            } else {
                                "Cuando la cafeteria ofrezca ofertas, apareceran aqui"
                            },
                            icono = Icons.Filled.LocalOffer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (activas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Activas",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(activas, key = { it.id }) { p ->
                        TarjetaPromocion(
                            promocion = p,
                            esAdmin = esAdmin,
                            onEditar = { onEditar(p.id) },
                            onAlternar = {
                                if (p.activo) vm.desactivar(p) else vm.activar(p)
                            },
                            onEliminar = { porEliminar = p }
                        )
                    }
                }

                // El admin tambien ve las que deshabilito, para poder
                // volver a habilitarlas.
                if (esAdmin && deshabilitadas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Deshabilitadas",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(deshabilitadas, key = { it.id }) { p ->
                        TarjetaPromocion(
                            promocion = p,
                            esAdmin = esAdmin,
                            onEditar = { onEditar(p.id) },
                            onAlternar = { vm.activar(p) },
                            onEliminar = { porEliminar = p }
                        )
                    }
                }
            }
        }

        // FAB para crear una promocion nueva (solo admin)
        if (esAdmin) {
            FloatingActionButton(
                onClick = onNueva,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(22.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Nueva promocion"
                )
            }
        }
    }

    // Confirmacion antes de eliminar
    porEliminar?.let { promocion ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar promocion") },
            text = {
                Text("Vas a eliminar la promocion de ${promocion.valorReferencia}.")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(promocion)
                    porEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaPromocion(
    promocion: Promocion,
    esAdmin: Boolean,
    onEditar: () -> Unit,
    onAlternar: () -> Unit,
    onEliminar: () -> Unit
) {
    val fechaFin = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        .format(java.util.Date(promocion.fechaFin))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (promocion.activo) {
                MaterialTheme.colorScheme.surfaceContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = promocion.descripcion(),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        color = if (promocion.activo) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
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
                    estado = if (promocion.activo) {
                        com.huaman.cafecampus.model.EstadoPedido.LISTO
                    } else {
                        com.huaman.cafecampus.model.EstadoPedido.CANCELADO
                    }
                )
            }

            // Acciones del administrador: solo iconos, con su descripcion
            // (para TalkBack). El texto al lado era redundante.
            if (esAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onAlternar) {
                        Icon(
                            imageVector = if (promocion.activo) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (promocion.activo) {
                                "Deshabilitar"
                            } else {
                                "Habilitar"
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onEditar) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onEliminar) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}