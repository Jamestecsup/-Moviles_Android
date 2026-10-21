package com.huaman.cafecampus.model

import com.huaman.cafecampus.data.local.entity.Producto

/**
 * Una linea del carrito del estudiante.
 *
 * No es una entidad de Room: vive solo en memoria mientras el usuario arma el
 * pedido. Al confirmar, el Repository la convierte en `DetallePedido`.
 *
 * Guarda el PRECIO FINAL (con la promocion ya aplicada) y el descuento, para
 * que el carrito muestre el precio real y el de lista tachado.
 */
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int,
    val nota: String = "",
    /** Precio unitario que se cobra (con descuento aplicado). */
    val precioUnitario: Double = producto.precio,
    /** Descuento que se esta aplicando, en porcentaje. */
    val descuentoPorcentaje: Double = 0.0
) {
    /** Precio de lista unitario (sin descuento). */
    val precioUnitarioOriginal: Double get() = producto.precio

    /** Subtotal que se cobra: precioUnitario x cantidad. */
    val subtotal: Double get() = precioUnitario * cantidad

    /** Subtotal de lista (sin descuento), para tachar. */
    val subtotalOriginal: Double get() = producto.precio * cantidad

    val tieneDescuento: Boolean get() = descuentoPorcentaje > 0.0
}