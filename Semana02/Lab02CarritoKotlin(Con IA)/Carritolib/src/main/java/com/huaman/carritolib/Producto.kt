package com.huaman.carritolib

class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {

    fun calcularImporte(): Double {
        return precio * cantidad
    }

    fun mostrarInformacion(): String {
        return "$nombre x$cantidad - S/ ${String.format("%.2f", calcularImporte())}"
    }
}