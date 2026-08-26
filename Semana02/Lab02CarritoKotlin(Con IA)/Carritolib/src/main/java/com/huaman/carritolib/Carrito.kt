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

    fun calcularDescuento(): Double {
        val subtotal = calcularSubtotal()

        return when {
            subtotal >= 3000 -> subtotal * 0.10
            subtotal >= 1500 -> subtotal * 0.05
            else -> 0.0
        }
    }

    fun calcularSubtotalConDescuento(): Double {
        return calcularSubtotal() - calcularDescuento()
    }

    fun calcularIGV(): Double {
        return calcularSubtotalConDescuento() * 0.18
    }

    fun calcularTotal(): Double {
        return calcularSubtotalConDescuento() + calcularIGV()
    }

    fun cantidadProductos(): Int {
        return productos.size
    }
}