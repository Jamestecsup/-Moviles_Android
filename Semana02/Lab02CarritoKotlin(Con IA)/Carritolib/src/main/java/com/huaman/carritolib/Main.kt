package com.huaman.carritolib

fun main() {

    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val carrito = Carrito("Juan")

    carrito.agregarProducto(Producto("Laptop HP", 2500.0, 1))
    carrito.agregarProducto(Producto("Mouse Logitech", 45.5, 2))
    carrito.agregarProducto(Producto("Teclado Logitech", 120.0, 1))
    carrito.agregarProducto(Producto("Monitor Samsung", 850.0, 1))

    println("Cliente: ${carrito.nombreCliente}")
    println()

    carrito.mostrarProductos()

    carrito.mostrarDetalle()

    println()
    println("Cantidad de productos: ${carrito.cantidadProductos()}")

    println(String.format("Subtotal:      S/ %8.2f", carrito.calcularSubtotal()))
    println(String.format("IGV (18%%):     S/ %8.2f", carrito.calcularIGV()))
    println(String.format("TOTAL A PAGAR: S/ %8.2f", carrito.calcularTotal()))
}