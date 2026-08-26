package com.huaman.carritolib

class Carrito(
    val nombreCliente: String
) {

    private val productos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }

    fun mostrarProductos() {
        for (producto in productos) {
            println("Producto agregado: ${producto.nombre}")
        }
    }

    fun mostrarDetalle() {
        println()
        println("--------- DETALLE DEL CARRITO ---------")

        var i = 1

        for (producto in productos) {
            println(
                String.format(
                    "%d. %-20s x%d S/ %8.2f",
                    i,
                    producto.nombre,
                    producto.cantidad,
                    producto.calcularImporte()
                )
            )

            i++
        }
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.0

        for (producto in productos) {
            subtotal += producto.calcularImporte()
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