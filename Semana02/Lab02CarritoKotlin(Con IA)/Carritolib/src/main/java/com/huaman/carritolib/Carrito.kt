package com.huaman.carritolib

class Carrito(
    val nombreCliente: String
) {
    private val productos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }

    fun mostrarDetalle() {
        println()
        println("--------- DETALLE DEL CARRITO ---------")

        var i = 1

        for (producto in productos) {
            val importe = producto.precio * producto.cantidad

            println(
                String.format(
                    "%d. %-20s x%d S/ %8.2f",
                    i,
                    producto.nombre,
                    producto.cantidad,
                    importe
                )
            )

            i++
        }
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.0

        for (producto in productos) {
            subtotal += producto.precio * producto.cantidad
        }

        return subtotal
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