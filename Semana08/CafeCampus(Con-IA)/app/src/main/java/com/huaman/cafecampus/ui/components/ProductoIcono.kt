package com.huaman.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Iconos vectoriales de la carta, seleccionados por la columna `icono` del
 * producto: cafe | plato | jugo | pan | botella | dulce.
 *
 * Este es el ICONO DE RESPALDO: si la foto del producto no existe en assets,
 * [ImagenProducto] cae aqui y la app sigue viéndose bien.
 */
object ProductoIconos {
    val cafe: ImageVector = Icons.Filled.Coffee
    val plato: ImageVector = Icons.Filled.DinnerDining
    val jugo: ImageVector = Icons.Filled.LocalDrink
    val pan: ImageVector = Icons.Filled.LunchDining
    val botella: ImageVector = Icons.Filled.LocalCafe
    val dulce: ImageVector = Icons.Filled.Cookie

    /** Resuelve el vector a partir del String guardado en la base. */
    fun de(icono: String): ImageVector = when (icono.lowercase()) {
        "cafe" -> cafe
        "plato" -> plato
        "jugo" -> jugo
        "pan" -> pan
        "botella" -> botella
        "dulce" -> dulce
        else -> plato
    }
}

/**
 * Icono vectorial del producto sobre un contenedor redondeado del color del
 * perfil. Es el fallback de [ImagenProducto].
 */
@Composable
fun ProductoIcono(
    icono: String,
    modifier: Modifier = Modifier,
    tamanio: Int = 56,
    colorIcono: Color? = null,
    colorContenedor: Color? = null
) {
    Box(
        modifier = modifier
            .size(tamanio.dp)
            .background(
                color = colorContenedor ?: androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ProductoIconos.de(icono),
            contentDescription = null,
            modifier = Modifier.size((tamanio * 0.52).dp),
            tint = colorIcono ?: androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}