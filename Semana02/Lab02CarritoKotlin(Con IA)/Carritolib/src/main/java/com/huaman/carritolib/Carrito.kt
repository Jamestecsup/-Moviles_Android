package com.huaman.carritolib

class Carrito {

    private val productos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }

    fun mostrarProductos() {
        println()
        println("--------- DETALLE DEL CARRITO ---------")

        for ((indice, producto) in productos.withIndex()) {
            println("${indice + 1}. ${producto.mostrarInformacion()}")
        }
    }

    fun calcularSubtotal(): Double {
        return productos.sumOf { it.calcularImporte() }
    }

    fun calcularIGV(): Double {
        return calcularSubtotal() * 0.18
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularIGV()
    }

    fun cantidadProductos(): Int {
        return productos.size
    }
}