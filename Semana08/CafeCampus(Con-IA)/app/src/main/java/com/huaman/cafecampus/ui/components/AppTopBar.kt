package com.huaman.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Barra superior comun a todas las pantallas.
 *
 * - [titulo] centrado (diseno del mockup)
 * - [onAtras] opcional: si es null no muestra la flecha
 * - [accion] opcional: usually un icono a la derecha
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    modifier: Modifier = Modifier,
    onAtras: (() -> Unit)? = null,
    accion: (@Composable () -> Unit)? = null,
    contenedorColor: Color = MaterialTheme.colorScheme.background
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onAtras != null) {
                IconButton(onClick = onAtras) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            } else {
                Box(modifier = Modifier.size(8.dp))
            }
        },
        actions = {
            // OJO: este Box NO debe usar fillMaxWidth(), porque se comeria el
            // ancho del titulo y lo dejaria invisible.
            Box(
                modifier = Modifier.padding(end = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                accion?.invoke()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = contenedorColor
        )
    )
}

/**
 * Icono de notificacion con contador, tal como aparece en el menu del cliente.
 *
 * Antes era un boton que no hacia nada: ahora [onClick] es obligatorio y
 * cada pantalla decide a donde lleva (el cliente, a sus pedidos; el
 * administrador, a la bandeja de pedidos).
 */
@Composable
fun IconoContador(
    cantidad: Int,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = descripcion)
        }
        if (cantidad > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 2.dp)
                    .size(18.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cantidad.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/** Row de acciones con el padding estandar de 16.dp del diseno. */
@Composable
fun FilaAcciones(
    modifier: Modifier = Modifier,
    contenido: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = contenido
    )
}

/** Padding estandar de la app (16.dp). */
val PaddingCafe: PaddingValues = PaddingValues(16.dp)