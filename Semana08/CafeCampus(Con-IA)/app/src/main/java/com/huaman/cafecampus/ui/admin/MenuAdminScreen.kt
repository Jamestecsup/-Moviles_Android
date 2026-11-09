package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.DisponibilidadChip
import com.huaman.cafecampus.ui.components.ImagenProducto
import com.huaman.cafecampus.ui.components.soles
import com.huaman.cafecampus.viewmodel.MenuAdminViewModel

/**
 * Gestion del menu del administrador (mockup 5).
 *
 * Lista con icono, categoria, precio, chip Disponible/Agotado y botones de
 * editar y eliminar. El eliminar pide confirmacion con AlertDialog.
 * Abajo, el FAB para agregar.
 */
@Composable
fun MenuAdminScreen(
    vm: MenuAdminViewModel,
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val productos by vm.productosVisibles.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    var porEliminar by remember { mutableStateOf<Producto?>(null) }

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            AppTopBar(titulo = "Gestion del menu")

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vm.categorias) { cat ->
                    FilterChip(
                        selected = cat == categoria,
                        onClick = { vm.seleccionarCategoria(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = cat == categoria,
                            borderColor = MaterialTheme.colorScheme.outlineVariant,
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos.size, key = { productos[it].id }) { indice ->
                    val producto = productos[indice]
                    FilaProductoAdmin(
                        producto = producto,
                        onEditar = { onEditar(producto.id) },
                        onEliminar = { porEliminar = producto },
                        onAlternar = { vm.alternarDisponibilidad(producto) }
                    )
                }
            }
        }

        // FAB para agregar
        FloatingActionButton(
            onClick = onNuevo,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(22.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Nuevo producto")
        }
    }

    // Confirmacion de borrado
    porEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar producto") },
            text = {
                Text("Vas a eliminar \"${producto.nombre}\". Los pedidos antiguos conservan su precio.")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(producto)
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

/** Fila de la carta administrable. */
@Composable
private fun FilaProductoAdmin(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onAlternar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenProducto(
                producto = producto,
                modifier = Modifier.size(56.dp),
                tamanio = 28
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = "${producto.categoria} - ${soles(producto.precio)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAlternar)
                ) {
                    // Tocar el chip alterna Disponible / Agotado
                    DisponibilidadChip(disponible = producto.disponible)
                }
            }

            IconButton(onClick = onEditar) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar ${producto.nombre}",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar ${producto.nombre}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}