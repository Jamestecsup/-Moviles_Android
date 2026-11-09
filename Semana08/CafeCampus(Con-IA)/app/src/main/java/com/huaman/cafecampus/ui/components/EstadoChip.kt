package com.huaman.cafecampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.theme.EstadoCancelado
import com.huaman.cafecampus.ui.theme.EstadoCanceladoContenedor
import com.huaman.cafecampus.ui.theme.EstadoEntregado
import com.huaman.cafecampus.ui.theme.EstadoEntregadoContenedor
import com.huaman.cafecampus.ui.theme.EstadoListo
import com.huaman.cafecampus.ui.theme.EstadoListoContenedor
import com.huaman.cafecampus.ui.theme.EstadoPreparando
import com.huaman.cafecampus.ui.theme.EstadoPreparandoContenedor
import com.huaman.cafecampus.ui.theme.EstadoRecibido
import com.huaman.cafecampus.ui.theme.EstadoRecibidoContenedor

/**
 * Colores de estado COMUNES a los dos perfiles (seccion 7 del laboratorio):
 *
 *   Recibido azul | Preparando ambar | Listo verde
 *   Entregado gris | Cancelado rojo
 *
 * Son los unicos lugares donde se permite un color fijo: son tokens de estado,
 * no colores de marca, y deben verse igual en el panel del admin y en el
 * seguimiento del estudiante.
 */
object EstadoColores {
    fun texto(estado: EstadoPedido): Color = when (estado) {
        EstadoPedido.RECIBIDO -> EstadoRecibido
        EstadoPedido.PREPARANDO -> EstadoPreparando
        EstadoPedido.LISTO -> EstadoListo
        EstadoPedido.ENTREGADO -> EstadoEntregado
        EstadoPedido.CANCELADO -> EstadoCancelado
    }

    fun contenedor(estado: EstadoPedido): Color = when (estado) {
        EstadoPedido.RECIBIDO -> EstadoRecibidoContenedor
        EstadoPedido.PREPARANDO -> EstadoPreparandoContenedor
        EstadoPedido.LISTO -> EstadoListoContenedor
        EstadoPedido.ENTREGADO -> EstadoEntregadoContenedor
        EstadoPedido.CANCELADO -> EstadoCanceladoContenedor
    }
}

/**
 * Chip de estado del pedido: "Recibido", "Preparando", "Listo", ...
 * Se usa igual en el menu, en Mis pedidos, en Seguimiento y en el panel.
 */
@Composable
fun EstadoChip(
    estado: EstadoPedido,
    modifier: Modifier = Modifier,
    texto: String = estado.etiqueta
) {
    Box(
        modifier = modifier
            .background(
                color = EstadoColores.contenedor(estado),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = EstadoColores.texto(estado)
        )
    }
}

/**
 * Chip de disponibilidad del producto: "Disponible" en verde o "Agotado" en
 * rojo. Es distinto del estado del pedido, por eso vive aqui y no en
 * [EstadoChip].
 */
@Composable
fun DisponibilidadChip(
    disponible: Boolean,
    modifier: Modifier = Modifier
) {
    val estado = if (disponible) EstadoPedido.LISTO else EstadoPedido.CANCELADO
    Box(
        modifier = modifier
            .background(
                color = EstadoColores.contenedor(estado),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = if (disponible) "Disponible" else "Agotado",
            style = MaterialTheme.typography.labelMedium,
            color = EstadoColores.texto(estado)
        )
    }
}