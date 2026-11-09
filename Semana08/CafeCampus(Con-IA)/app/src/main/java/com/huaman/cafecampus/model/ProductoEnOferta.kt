package com.huaman.cafecampus.model

import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.local.entity.Promocion

/**
 * Un producto con la promocion vigente ya aplicada.
 *
 * La entidad [Producto] NO se modifica: guarda el precio de lista. Este
 * modelo es solo de presentacion, y dice cuanto se paga de verdad.
 */
data class ProductoEnOferta(
    val producto: Producto,
    val descuentoPorcentaje: Double = 0.0,
    val precioFinal: Double = producto.precio
) {
    val id: Int get() = producto.id
    val nombre: String get() = producto.nombre
    val categoria: String get() = producto.categoria

    /** Precio de lista (sin descuento). */
    val precioOriginal: Double get() = producto.precio

    val tieneDescuento: Boolean get() = descuentoPorcentaje > 0.0

    /** Precio unitario con descuento, por la cantidad pedida. */
    fun precioDe(cantidad: Int): Double = precioFinal * cantidad

    /** Precio de lista por la cantidad pedida (para tachar). */
    fun precioOriginalDe(cantidad: Int): Double = producto.precio * cantidad
}

/**
 * Reglas de aplicacion de las promociones.
 *
 * Una promocion puede aplicar a:
 *  - UN producto   (tipo = PRODUCTO, valorReferencia = nombre del producto)
 *  - UNA categoria (tipo = CATEGORIA, valorReferencia = nombre de la categoria)
 *
 * Si un producto tiene AMBOS (una por producto y otra por categoria),
 * gana la del PRODUCTO: es mas especifica.
 */
object CalculoPromociones {

    const val TIPO_PRODUCTO = "PRODUCTO"
    const val TIPO_CATEGORIA = "CATEGORIA"

    /** La promocion que aplica a un producto, o null si no tiene ninguna. */
    fun promocionDe(producto: Producto, promociones: List<Promocion>): Promocion? {
        val vigentes = promociones.filter { it.esValida }
        if (vigentes.isEmpty()) return null

        // 1) La del producto tiene prioridad
        val deProducto = vigentes.firstOrNull {
            it.tipo == TIPO_PRODUCTO &&
                it.valorReferencia.trim().equals(producto.nombre, ignoreCase = true)
        }
        if (deProducto != null) return deProducto

        // 2) Si no, la de su categoria
        return vigentes.firstOrNull {
            it.tipo == TIPO_CATEGORIA &&
                it.valorReferencia.trim().equals(producto.categoria, ignoreCase = true)
        }
    }

    /** Calcula el precio final de un producto segun las promociones vigentes. */
    fun ofertaDe(producto: Producto, promociones: List<Promocion>): ProductoEnOferta {
        val promo = promocionDe(producto, promociones) ?: return ProductoEnOferta(producto)
        val descuento = promo.descuentoPorcentaje.coerceIn(0.0, 100.0)
        val final = producto.precio * (1.0 - descuento / 100.0)
        return ProductoEnOferta(producto, descuento, final)
    }
}