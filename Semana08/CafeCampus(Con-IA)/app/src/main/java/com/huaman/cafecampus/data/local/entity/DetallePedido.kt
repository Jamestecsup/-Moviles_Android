package com.huaman.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Linea de un pedido (tabla `detalle_pedido`).
 *
 * Relacion N:1 con [Pedido] -> un pedido tiene N lineas.
 *
 * - [pedidoId] es clave foranea hacia `pedidos.id` con `onDelete = CASCADE`:
 *   si se borra el pedido, sus lineas se borran solas.
 * - CLAVE COMPUESTA / candidata: `(pedidoId, productoId)` es UNIQUE. Un producto
 *   no puede aparecer dos veces como dos lineas del mismo pedido, por eso el
 *   carrito SUMA cantidades en vez de duplicar la linea.
 *
 *  POR QUE se guardan [nombreProducto] y [precioUnitario]?
 *  Son una foto del momento. Si manana el administrador cambia el precio o
 *  elimina un producto, los pedidos antiguos no cambian ni se rompen.
 *  Por eso [productoId] NO es clave foranea: puede quedar apuntando a un
 *  producto que ya no existe, y eso es intencional.
 */
@Entity(
    tableName = "detalle_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("pedidoId"),
        Index(value = ["pedidoId", "productoId"], unique = true)
    ]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
) {
    /** Subtotal de la linea: precioUnitario x cantidad. */
    val subtotal: Double get() = precioUnitario * cantidad
}