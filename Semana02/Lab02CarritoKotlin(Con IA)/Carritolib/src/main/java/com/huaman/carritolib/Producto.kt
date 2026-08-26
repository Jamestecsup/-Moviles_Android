package com.huaman.carritolib

open class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {

    open fun calcularImporte(): Double {
        return precio * cantidad
    }

    open fun mostrarInformacion(): String {
        return "$nombre x$cantidad - S/ ${String.format("%.2f", calcularImporte())}"
    }
}