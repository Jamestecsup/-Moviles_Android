package com.huaman.carritolib

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

data class Cuota(
    val numero: Int,
    val fecha: LocalDate,
    val monto: Double,
    val pagoMensual: Double,
    var restaPago: Double,
    var estado: String
)

fun main() {
    val catalogo = mutableListOf<Producto>()

    while (true) {
        println()
        println("=========================================")
        println("       SISTEMA DE TIENDA TECSUP")
        println("=========================================")
        println("1. Ingresar como Administrador")
        println("2. Ingresar como Cliente")
        println("3. Salir")
        print("Seleccione una opción: ")

        when (readln().toInt()) {
            1 -> administrador(catalogo)
            2 -> cliente(catalogo)
            3 -> {
                println("Programa finalizado.")
                break
            }
            else -> println("Opción no válida.")
        }
    }
}

fun administrador(catalogo: MutableList<Producto>) {
    println()
    println("--------- ADMINISTRADOR ---------")

    print("¿Cuántos productos desea ingresar? ")
    val cantidadProductos = readln().toInt()

    for (i in 1..cantidadProductos) {
        println()
        println("--------- PRODUCTO $i ---------")

        print("Nombre del producto: ")
        val nombre = readln()

        print("Cantidad: ")
        val cantidad = readln().toInt()

        print("Precio: ")
        val precio = readln().toDouble()

        catalogo.add(Producto(nombre, precio, cantidad))
    }

    println()
    println("Productos registrados correctamente.")
}

fun cliente(catalogo: MutableList<Producto>) {
    if (catalogo.isEmpty()) {
        println()
        println("No hay productos registrados.")
        return
    }

    val carrito = mutableListOf<Producto>()

    while (true) {
        println()
        println("--------- CATÁLOGO ---------")

        for (i in catalogo.indices) {
            val producto = catalogo[i]

            println(
                "${i + 1}. ${producto.nombre} - S/ %.2f - Stock: %d".format(
                    producto.precio,
                    producto.cantidad
                )
            )
        }

        print("Seleccione un producto (0 para terminar): ")
        val opcion = readln().toInt()

        if (opcion == 0) {
            break
        }

        if (opcion < 1 || opcion > catalogo.size) {
            println("Producto no válido.")
            continue
        }

        val producto = catalogo[opcion - 1]

        print("Cantidad: ")
        val cantidad = readln().toInt()

        if (cantidad <= 0 || cantidad > producto.cantidad) {
            println("Cantidad no disponible.")
            continue
        }

        carrito.add(
            Producto(
                producto.nombre,
                producto.precio,
                cantidad
            )
        )

        println("Producto agregado al carrito.")

        print("¿Desea agregar otro producto? (SI/NO): ")
        val continuar = readln().uppercase()

        if (continuar != "SI") {
            break
        }
    }

    if (carrito.isEmpty()) {
        println("No se seleccionaron productos.")
        return
    }

    mostrarDetalle(carrito)

    val montoInicial = calcularSubtotal(carrito)

    println()
    println("Monto inicial: S/ %.2f".format(montoInicial))

    println()
    println("FORMA DE PAGO")
    println("1. 6 cuotas  - 20% de interés")
    println("2. 12 cuotas - 40% de interés")
    println("3. 24 cuotas - 60% de interés")

    print("Seleccione una opción: ")
    val opcionCuotas = readln().toInt()

    val numeroCuotas: Int
    val porcentajeInteres: Double

    when (opcionCuotas) {
        1 -> {
            numeroCuotas = 6
            porcentajeInteres = 0.20
        }
        2 -> {
            numeroCuotas = 12
            porcentajeInteres = 0.40
        }
        3 -> {
            numeroCuotas = 24
            porcentajeInteres = 0.60
        }
        else -> {
            println("Opción no válida.")
            return
        }
    }

    val interes = montoInicial * porcentajeInteres
    val montoPagar = montoInicial + interes
    val pagoMensual = montoPagar / numeroCuotas

    println()
    println("Monto inicial: S/ %.2f".format(montoInicial))
    println("Interés: S/ %.2f".format(interes))
    println("Monto a pagar: S/ %.2f".format(montoPagar))
    println("Número de cuotas: $numeroCuotas")
    println("Pago mensual: S/ %.2f".format(pagoMensual))

    procesarPagos(
        montoPagar,
        pagoMensual,
        numeroCuotas
    )
}

fun procesarPagos(
    montoPagar: Double,
    pagoMensual: Double,
    numeroCuotas: Int
) {
    print("\n¿Cuánto dinero tiene disponible? S/ ")
    var dineroDisponible = readln().toDouble()

    var restaPago = montoPagar
    var fecha = LocalDate.now()

    val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val cuotas = mutableListOf<Cuota>()

    while (cuotas.size < numeroCuotas && restaPago > 0.01) {
        val numeroCuota = cuotas.size + 1
        val pagoActual = minOf(pagoMensual, restaPago)

        mostrarTablaCuotas(cuotas)

        println()
        println("CUOTA $numeroCuota")
        println("Fecha: ${fecha.format(formato)}")
        println("Monto pendiente: S/ %.2f".format(restaPago))
        println("Pago mensual: S/ %.2f".format(pagoActual))

        if (dineroDisponible < pagoActual) {
            println()
            println("No tiene suficiente dinero para pagar la cuota.")
            println("Dinero disponible: S/ %.2f".format(dineroDisponible))
            println("Cuota pendiente: S/ %.2f".format(pagoActual))

            println()
            println("1. Ingresar dinero y continuar")
            println("2. Salir de la operación")
            print("Seleccione una opción: ")

            when (readln().toInt()) {
                1 -> {
                    print("¿Cuánto dinero desea agregar? S/ ")
                    val nuevoDinero = readln().toDouble()

                    if (nuevoDinero > 0) {
                        dineroDisponible += nuevoDinero
                        println(
                            "Dinero disponible: S/ %.2f".format(
                                dineroDisponible
                            )
                        )
                    } else {
                        println("Cantidad no válida.")
                    }
                }

                2 -> {
                    println()
                    println("Operación finalizada.")
                    println("Deuda pendiente: S/ %.2f".format(restaPago))
                    return
                }

                else -> println("Opción no válida.")
            }

            continue
        }

        print("\n¿Quiere pagar la siguiente cuota? (SI/NO): ")
        val respuesta = readln().uppercase()

        if (respuesta == "NO") {
            println()
            println("Pago detenido.")
            println("Deuda pendiente: S/ %.2f".format(restaPago))
            mostrarTablaCuotas(cuotas)
            return
        }

        if (respuesta != "SI") {
            println("Respuesta no es valida.")
            continue
        }

        dineroDisponible -= pagoActual
        restaPago -= pagoActual

        if (restaPago < 0.01) {
            restaPago = 0.0
        }

        val cuota = Cuota(
            numero = numeroCuota,
            fecha = fecha,
            monto = montoPagar,
            pagoMensual = pagoActual,
            restaPago = restaPago,
            estado = "PAGADO"
        )

        cuotas.add(cuota)

        println()
        println("Cuota $numeroCuota pagada.")
        println("Dinero disponible: S/ %.2f".format(dineroDisponible))
        println("Resta pago: S/ %.2f".format(restaPago))

        fecha = fecha.plusMonths(1)
    }

    mostrarTablaCuotas(cuotas)

    if (restaPago <= 0.01) {
        println()
        println("Deuda Cancelada")
        println("Todas las cuotas fueron pagadas.")
        println("Dinero restante: S/ %.2f".format(dineroDisponible))
    }
}

fun mostrarTablaCuotas(cuotas: List<Cuota>) {
    if (cuotas.isEmpty()) {
        return
    }

    val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    println()
    println(
        "%-3s | %-10s | %-12s | %-14s | %-12s | %-10s".format(
            "N°",
            "FECHA",
            "MONTO",
            "PAGO MENSUAL",
            "RESTA PAGO",
            "ESTADO"
        )
    )

    for (cuota in cuotas) {
        println(
            "%-3d | %-10s | S/ %9.2f | S/ %11.2f | S/ %9.2f | %-10s".format(
                cuota.numero,
                cuota.fecha.format(formato),
                cuota.monto,
                cuota.pagoMensual,
                cuota.restaPago,
                cuota.estado
            )
        )
    }

}

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0

    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }

    return subtotal
}

fun mostrarDetalle(productos: List<Producto>) {
    println()
    println("--------- DETALLE DEL CARRITO ---------")

    var i = 1

    for (p in productos) {
        val importe = p.precio * p.cantidad

        println(
            String.format(
                "%d. %-20s x%d S/ %8.2f",
                i,
                p.nombre,
                p.cantidad,
                importe
            )
        )

        i++
    }
}