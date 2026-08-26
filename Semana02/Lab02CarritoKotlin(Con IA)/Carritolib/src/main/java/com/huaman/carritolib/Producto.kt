package com.huaman.carritolib

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {
    fun calcularImporte(): Double {
        return precio * cantidad
    }
}