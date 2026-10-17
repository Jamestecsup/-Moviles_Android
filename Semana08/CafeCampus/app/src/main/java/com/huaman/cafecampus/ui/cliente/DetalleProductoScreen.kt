package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeTextField
import com.huaman.cafecampus.ui.components.DisponibilidadChip
import com.huaman.cafecampus.ui.components.ImagenProductoGrande
import com.huaman.cafecampus.ui.components.soles

/**
 * Detalle del producto (mockup 4).
 *
 * Muestra la foto (o el icono de respaldo), el nombre, el precio, si esta
 * disponible, un selector de cantidad (+ / -, minimo 1), la nota para la
 * cocina y el total. El boton "Agregar al pedido" deshabilitado si el
 * producto esta AGOTADO.
 */
@Composable
fun DetalleProductoScreen(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: (cantidad: Int, nota: String) -> Unit,
    modifier: Modifier = Modifier,
    onAtras: () -> Unit = {}
) {
    var cantidad by remember(producto.id) { mutableIntStateOf(1) }
    var nota by remember(producto.id) { mutableStateOf("") }

    val agotado = !producto.disponible
    val total = producto.precio * cantidad

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Detalle del producto", onAtras = onAtras)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Foto grande
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.9f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                ImagenProductoGrande(
                    producto = producto,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Nombre y precio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = soles(producto.precio),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Estado + tiempo de preparacion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DisponibilidadChip(disponible = producto.disponible)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " 10 - 15 min",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Descripcion
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 14.dp)
            )

            // Cantidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                SelectorCantidad(
                    cantidad = cantidad,
                    onCambio = { cantidad = it.coerceAtLeast(1) },
                    habilitado = !agotado
                )
            }

            // Nota para la cocina
            Text(
                text = "Nota para la cocina",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
            )
            CafeTextField(
                valor = nota,
                onValorChange = { nota = it },
                etiqueta = "",
                marcador = "Sin aji, por favor",
                multilinea = true,
                minLineas = 2,
                habilitado = !agotado
            )

            // Total
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = soles(total),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (cantidadEnCarrito > 0 && !agotado) {
                Text(
                    text = "Ya tienes $cantidadEnCarrito en el pedido",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            CafeButton(
                texto = "Agregar al pedido",
                onClick = { onAgregar(cantidad, nota) },
                modifier = Modifier.padding(top = 20.dp, bottom = 24.dp),
                habilitado = !agotado
            )
        }
    }
}

/**
 * Selector "- N +" del diseno: el menos es un circulo delineado y el mas un
 * circulo lleno del color del perfil. El minimo es 1.
 */
@Composable
fun SelectorCantidad(
    cantidad: Int,
    onCambio: (Int) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotonRedondo(
            habilitado = habilitado && cantidad > 1,
            relleno = false,
            onClick = { onCambio(cantidad - 1) }
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Quitar uno",
                modifier = Modifier.size(18.dp),
                tint = if (habilitado && cantidad > 1) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }

        Text(
            text = cantidad.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        BotonRedondo(
            habilitado = habilitado,
            relleno = habilitado,
            onClick = { onCambio(cantidad + 1) }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Agregar uno",
                modifier = Modifier.size(18.dp),
                tint = if (habilitado) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        }
    }
}

/** Circulo delineado o relleno del selector de cantidad. */
@Composable
private fun BotonRedondo(
    habilitado: Boolean,
    relleno: Boolean,
    onClick: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (relleno && habilitado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        contenido()
    }
}

/** Altura estandar del area de imagen; se usa en el Carrito tambien. */
val AlturaImagenDetalle = 220.dp