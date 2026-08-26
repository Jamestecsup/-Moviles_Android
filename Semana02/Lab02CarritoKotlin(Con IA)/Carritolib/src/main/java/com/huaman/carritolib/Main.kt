package com.huaman.carritolib

fun main() {

    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val nombreCliente = "Juan"

    val carrito = Carrito()

    carrito.agregarProducto(ProductoFisico("Laptop HP", 2500.0, 1, 2.5))
    carrito.agregarProducto(ProductoFisico("Mouse Logitech", 45.5, 2, 0.2))
    carrito.agregarProducto(ProductoFisico("Teclado Logitech", 120.0, 1, 0.8))
    carrito.agregarProducto(ProductoFisico("Monitor Samsung", 850.0, 1, 4.0))
    carrito.agregarProducto(ProductoDigital("Licencia Windows", 350.0, 1, 5.0))

    println("Cliente: $nombreCliente")

    carrito.mostrarProductos()

    println()
    println("Cantidad de productos: ${carrito.cantidadProductos()}")

    val subtotal = carrito.calcularSubtotal()
    val descuento = carrito.calcularDescuento()
    val subtotalConDescuento = carrito.calcularSubtotalConDescuento()
    val igv = carrito.calcularIGV()
    val total = carrito.calcularTotal()

    println(String.format("Subtotal:             S/ %8.2f", subtotal))
    println(String.format("Descuento:            S/ %8.2f", descuento))
    println(String.format("Subtotal con descuento: S/ %8.2f", subtotalConDescuento))
    println(String.format("IGV (18%%):            S/ %8.2f", igv))
    println(String.format("TOTAL A PAGAR:        S/ %8.2f", total))
}