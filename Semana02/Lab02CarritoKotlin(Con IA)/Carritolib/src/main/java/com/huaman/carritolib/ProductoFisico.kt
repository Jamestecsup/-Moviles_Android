package com.huaman.carritolib

class ProductoFisico(
    nombre: String,
    precio: Double,
    cantidad: Int,
    val peso: Double
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        return (precio + peso * 2) * cantidad
    }

    override fun mostrarInformacion(): String {
        return "$nombre x$cantidad - Peso: ${peso}kg - S/ ${String.format("%.2f", calcularImporte())}"
    }
}