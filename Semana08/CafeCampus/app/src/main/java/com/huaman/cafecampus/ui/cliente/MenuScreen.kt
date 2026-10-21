package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// OJO: `items` de LazyRow y de LazyVerticalGrid se llaman igual. Importamos la
// de LazyRow y, en la grilla, usamos la sobrecarga por `count`.
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.Categoria
import com.huaman.cafecampus.ui.components.IconoContador
import com.huaman.cafecampus.ui.components.ProductoCard
import com.huaman.cafecampus.ui.components.plural
import com.huaman.cafecampus.ui.components.soles
import com.huaman.cafecampus.viewmodel.MenuViewModel

/**
 * Menu de la cafeteria (mockup 3) - pantalla principal del ESTUDIANTE.
 *
 * La View SOLO observa y avisa: pide los productos al ViewModel con
 * `collectAsStateWithLifecycle()` y le devuelve los clics. No toca la base.
 */
@Composable
fun MenuScreen(
    vm: MenuViewModel,
    onProducto: (Int) -> Unit,
    modifier: Modifier = Modifier,
    unidadesCarrito: Int = 0,
    totalCarrito: Double = 0.0,
    totalCarritoOriginal: Double = 0.0,
    nombreUsuario: String? = null,
    onCarrito: () -> Unit = {}
) {
    val productos by vm.productos.collectAsStateWithLifecycle()
    val visibles by vm.productosVisibles.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }

    val filtrados = remember(visibles, busqueda) {
        if (busqueda.isBlank()) visibles
        else visibles.filter { it.nombre.contains(busqueda, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // Saludo y campana
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // "Juan Perez" -> "Juan"
    val nombreCorto = nombreUsuario
        ?.trim()
        ?.split(" ")
        ?.firstOrNull()
        ?.takeIf { it.isNotBlank() }

    Text(
                text = if (nombreCorto == null) "Hola!" else "Hola, $nombreCorto!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
                Text(
                    text = "Que te antoja hoy?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconoContador(
                cantidad = 0,
                icon = Icons.Filled.Notifications,
                descripcion = "Notificaciones"
            )
        }

        // Bolsa del pedido, con contador de unidades
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCarrito() }
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tu pedido",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    if (unidadesCarrito == 0) {
                        Text(
                            text = "Vacio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = soles(totalCarrito),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (totalCarrito < totalCarritoOriginal) {
                            Text(
                                text = soles(totalCarritoOriginal),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        } else {
                            Text(
                                text = plural(unidadesCarrito, "producto", "productos"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Buscador
        BuscadorCafe(
            valor = busqueda,
            onValorChange = { busqueda = it },
            modifier = Modifier.padding(top = 14.dp)
        )

        // Filtros por categoria
        LazyRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.categorias) { cat ->
                FilterChip(
                    selected = cat == categoria,
                    onClick = { vm.seleccionarCategoria(cat) },
                    label = { Text(text = cat, style = MaterialTheme.typography.labelMedium) },
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

        // Titulo de la seccion
        Text(
            text = if (categoria == Categoria.TODAS) "Populares hoy" else categoria,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
        )

        // Grilla de productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtrados.size, key = { filtrados[it].id }) { indice ->
                val oferta = filtrados[indice]
                ProductoCard(
                    producto = oferta.producto,
                    onClick = { onProducto(oferta.id) },
                    onAgregar = { onProducto(oferta.id) },
                    descuentoPorcentaje = oferta.descuentoPorcentaje,
                    precioFinal = oferta.precioFinal
                )
            }
        }
    }
}

/** Campo de busqueda redondeado, tal como el mockup. */
@Composable
private fun BuscadorCafe(
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Buscar en el menu...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

/** Subtitulo auxiliar con el total del pedido en curso. */
@Composable
fun ResumenCarrito(
    cantidad: Int,
    total: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$cantidad producto(s)",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = soles(total),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}