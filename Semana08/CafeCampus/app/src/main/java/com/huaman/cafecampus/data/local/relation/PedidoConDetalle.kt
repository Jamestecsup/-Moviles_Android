package com.huaman.cafecampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.huaman.cafecampus.data.local.entity.DetallePedido
import com.huaman.cafecampus.data.local.entity.Pedido
import com.huaman.cafecampus.data.local.entity.Usuario

/**
 * Un pedido con TODAS sus lineas y el cliente que lo hizo.
 *
 * Room necesita resolver en dos pasos (primero la cabecera, despues las
 * lineas), asi que por eso toda consulta que devuelva esta clase debe estar
 * anotada con `@Transaction`.
 *
 * - `@Embedded pedido`  -> las columnas de la tabla `pedidos`
 * - `@Relation`         -> la lista de `detalle_pedido` del pedido
 * - `@Relation`         -> el `Usuario` dueño del pedido (clave foranea
 *                          `clienteId`)
 */
data class PedidoConDetalle(
    @Embedded
    val pedido: Pedido,

    @Relation(parentColumn = "id", entityColumn = "pedidoId")
    val detalles: List<DetallePedido>,

    @Relation(parentColumn = "clienteId", entityColumn = "id")
    val cliente: Usuario
) {
    /** Total de lineas (unidades) del pedido. */
    val unidades: Int get() = detalles.sumOf { it.cantidad }

    /** Numero de lineas distintas. */
    val lineas: Int get() = detalles.size

    /** "2 x Menu del dia + 1 x Jugo de naranja" */
    val resumen: String
        get() = detalles.joinToString(" + ") { "${it.cantidad} x ${it.nombreProducto}" }

    /** Suma real de las lineas (por si el total guardado quedara viejo). */
    val totalCalculado: Double get() = detalles.sumOf { it.subtotal }
}