package com.huaman.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.model.EstadoPedido

/**
 * Linea de tiempo del seguimiento del pedido (mockup 6):
 * Recibido -> Preparando -> Listo -> Entregado.
 *
 * La pantalla NO decide que paso esta completo: solo recibe el estado actual
 * y el componente dibuja. El estado llega por Flow desde Room, asi que cambia
 * solo cuando el administrador lo cambia, sin reiniciar la app.
 */
@Composable
fun StepperEstado(
    estadoActual: EstadoPedido,
    modifier: Modifier = Modifier
) {
    // Un pedido cancelado no avanza: se marca solo el primer paso.
    val cancelado = estadoActual == EstadoPedido.CANCELADO

    Column(modifier = modifier.fillMaxWidth()) {
        listOf(
            EstadoPedido.RECIBIDO,
            EstadoPedido.PREPARANDO,
            EstadoPedido.LISTO,
            EstadoPedido.ENTREGADO
        ).forEachIndexed { indice, paso ->
            val esActual = paso == estadoActual
            val completado = orden(paso) < orden(estadoActual)

            FilaStepper(
                paso = paso,
                titulo = paso.etiqueta,
                descripcion = paso.descripcion,
                esActual = esActual,
                completado = completado,
                cancelado = cancelado,
                esUltimo = indice == 3,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

private fun orden(estado: EstadoPedido): Int = when (estado) {
    EstadoPedido.RECIBIDO -> 0
    EstadoPedido.PREPARANDO -> 1
    EstadoPedido.LISTO -> 2
    EstadoPedido.ENTREGADO -> 3
    EstadoPedido.CANCELADO -> -1
}

@Composable
private fun FilaStepper(
    paso: EstadoPedido,
    titulo: String,
    descripcion: String,
    esActual: Boolean,
    completado: Boolean,
    cancelado: Boolean,
    esUltimo: Boolean,
    modifier: Modifier = Modifier
) {
    val activo = esActual || completado

    Row(modifier = modifier.fillMaxWidth()) {

        // Columna del marcador y la linea vertical
        Column(
            modifier = Modifier.width(34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CirculoEstado(
                lleno = activo,
                modifier = Modifier.size(26.dp)
            ) {
                if (activo) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(
                            if (completado) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                )
            }
        }

        // Titulo y descripcion
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                color = if (activo) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Circulo de estado numerado usado en el selector del administrador
 * (mockup 4: "Cambiar estado -> Recibido / Preparando / Listo / Entregado").
 */
@Composable
fun CirculoNumerado(
    numero: Int,
    activo: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                if (activo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = numero.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (activo) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/** Franja de alto fijo, usada como separador vertical flexible. */
@Composable
fun LineaSeparadora(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

/** Rellena el alto disponible (util dentro de filas). */
@Composable
fun Relleno(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxHeight())
}