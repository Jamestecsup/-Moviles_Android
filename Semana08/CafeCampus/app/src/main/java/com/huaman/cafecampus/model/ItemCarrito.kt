package com.huaman.cafecampus.model

import com.huaman.cafecampus.data.local.entity.Producto

/**
 * Una linea del carrito del estudiante.
 *
 * No es una entidad de Room: vive solo en memoria mientras el usuario arma el
 * pedido. Al confirmar, el Repository la convierte en `DetallePedido`.
 */
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int,
    val nota: String = ""
) {
    /** Precio de esta linea = precioUnitario x cantidad. */
    val subtotal: Double get() = producto.precio * cantidad
}