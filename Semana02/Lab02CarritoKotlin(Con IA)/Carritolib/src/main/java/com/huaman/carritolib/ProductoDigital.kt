package com.huaman.carritolib

class ProductoDigital(
    nombre: String,
    precio: Double,
    cantidad: Int,
    val tamanoGB: Double
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        return precio * cantidad
    }

    override fun mostrarInformacion(): String {
        return "$nombre x$cantidad - Digital: ${tamanoGB}GB - S/ ${String.format("%.2f", calcularImporte())}"
    }
}