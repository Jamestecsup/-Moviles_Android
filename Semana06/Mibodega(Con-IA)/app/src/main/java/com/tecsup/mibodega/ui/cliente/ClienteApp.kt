package com.tecsup.mibodega.ui.cliente

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.mibodega.ui.cliente.modelo.ItemCarrito
import com.tecsup.mibodega.ui.cliente.modelo.DatosCuenta
import com.tecsup.mibodega.ui.cliente.modelo.PedidoConfirmado
import com.tecsup.mibodega.ui.cliente.modelo.Producto
import com.tecsup.mibodega.ui.cliente.modelo.listaProductosFake
import com.tecsup.mibodega.ui.cliente.screens.bienvenida.BienvenidaScreen
import com.tecsup.mibodega.ui.cliente.screens.carrito.CarritoScreen
import com.tecsup.mibodega.ui.cliente.screens.confirmacion.ConfirmacionScreen
import com.tecsup.mibodega.ui.cliente.screens.detalle.DetalleProductoScreen
import com.tecsup.mibodega.ui.cliente.screens.entrega.DatosEntregaScreen
import com.tecsup.mibodega.ui.cliente.screens.favoritos.FavoritosScreen
import com.tecsup.mibodega.ui.cliente.screens.inicio.InicioScreen
import com.tecsup.mibodega.ui.cliente.screens.login.LoginScreen
import com.tecsup.mibodega.ui.cliente.screens.pedidos.MisPedidosScreen
import com.tecsup.mibodega.ui.cliente.screens.registro.RegistroScreen

private const val COSTO_DELIVERY = 4.00

/**
 * "Director de orquesta" de la app cliente:
 * - Tiene el NavHost con las rutas de cada pantalla.
 * - Tiene el estado del carrito (List<ItemCarrito>), que se reparte
 *   hacia abajo a Inicio, Detalle, Carrito y Entrega.
 * Ninguna Screen navega sola ni modifica el carrito directamente:
 * todas reciben funciones (lambdas) desde aquí (state hoisting).
 */
private object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val DETALLE = "detalle/{productoId}"
    const val CARRITO = "carrito"
    const val PEDIDOS = "pedidos"
    const val FAVORITOS = "favoritos"
    const val ENTREGA = "entrega"
    const val CONFIRMACION = "confirmacion"

    fun detalle(productoId: Int) = "detalle/$productoId"
}

@Composable
fun ClienteApp() {
    val navController = rememberNavController()

    // El carrito vive aquí arriba, no en ninguna Screen.
    var carrito by remember { mutableStateOf<List<ItemCarrito>>(emptyList()) }
    // Lo que se guardó del registro, para prellenar la entrega.
    var nombreRegistro by remember { mutableStateOf("") }
    var telefonoRegistro by remember { mutableStateOf("") }
    var direccionRegistro by remember { mutableStateOf("") }
    // La cuenta que se muestra en Perfil: sale del registro
    // o del teléfono con el que se ingresó.
    var cuenta by remember { mutableStateOf<DatosCuenta?>(null) }
    // Los ids marcados con el corazón, los lee Favoritos.
    val favoritosIds = remember { mutableStateSetOf<Int>() }
    // El último pedido confirmado, lo lee la Confirmación.
    var ultimoPedido by remember { mutableStateOf<PedidoConfirmado?>(null) }
    // El historial que muestra Mis pedidos.
    val historialPedidos = remember { mutableStateListOf<PedidoConfirmado>() }
    val totalPedido = carrito.sumOf { it.producto.precio * it.cantidad } + COSTO_DELIVERY

    NavHost(
        navController = navController,
        startDestination = Rutas.BIENVENIDA
    ) {
        composable(Rutas.BIENVENIDA) {
            BienvenidaScreen(
                onRegistrarse = { navController.navigate(Rutas.REGISTRO) },
                onIniciarSesion = { navController.navigate(Rutas.LOGIN) },
                onTerminos = { /* TODO: abrir términos y condiciones */ }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onVolver = { navController.popBackStack() },
                onIngresar = { telefono ->
                    if (cuenta?.telefono != telefono) {
                        cuenta = DatosCuenta(
                            nombre = "Invitado",
                            telefono = telefono,
                            direccion = "Sin dirección registrada"
                        )
                    }
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                    }
                },
                onCrearCuenta = { navController.navigate(Rutas.REGISTRO) }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onCrearCuenta = { nombre, telefono, direccion, _ ->
                    nombreRegistro = nombre
                    telefonoRegistro = telefono
                    direccionRegistro = direccion
                    cuenta = DatosCuenta(
                        nombre = nombre,
                        telefono = telefono,
                        direccion = direccion
                    )
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.INICIO) {
            InicioScreen(
                cantidadCarrito = carrito.sumOf { it.cantidad },
                cuenta = cuenta,
                favoritosIds = favoritosIds,
                onVerCarrito = { navController.navigate(Rutas.CARRITO) },
                onVerPedidos = { navController.navigate(Rutas.PEDIDOS) },
                onVerFavoritos = { navController.navigate(Rutas.FAVORITOS) },
                onProductoClick = { producto ->
                    navController.navigate(Rutas.detalle(producto.id))
                },
                onAgregarProducto = { producto ->
                    carrito = agregarOSumarProducto(carrito, producto, 1)
                },
                onFavoritoClick = { productoId ->
                    if (productoId in favoritosIds) {
                        favoritosIds.remove(productoId)
                    } else {
                        favoritosIds.add(productoId)
                    }
                }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            val producto = listaProductosFake.first { it.id == productoId }

            DetalleProductoScreen(
                producto = producto,
                onVolver = { navController.popBackStack() },
                onAgregarAlCarrito = { productoSeleccionado, cantidad ->
                    carrito = agregarOSumarProducto(carrito, productoSeleccionado, cantidad)
                    navController.popBackStack()
                },
                esFavorito = producto.id in favoritosIds,
                onFavoritoClick = {
                    if (producto.id in favoritosIds) {
                        favoritosIds.remove(producto.id)
                    } else {
                        favoritosIds.add(producto.id)
                    }
                }
            )
        }

        composable(Rutas.CARRITO) {
            CarritoScreen(
                carrito = carrito,
                onVolver = { navController.popBackStack() },
                onIncrementar = { producto ->
                    carrito = carrito.map {
                        if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + 1) else it
                    }
                },
                onDecrementar = { producto ->
                    carrito = carrito.mapNotNull {
                        when {
                            it.producto.id != producto.id -> it
                            it.cantidad > 1 -> it.copy(cantidad = it.cantidad - 1)
                            else -> null // si llega a 0, se elimina de la lista
                        }
                    }
                },
                onEliminar = { producto ->
                    carrito = carrito.filterNot { it.producto.id == producto.id }
                },
                onContinuarPedido = { navController.navigate(Rutas.ENTREGA) }
            )
        }

        composable(Rutas.ENTREGA) {
            DatosEntregaScreen(
                total = totalPedido,
                nombreInicial = nombreRegistro,
                telefonoInicial = telefonoRegistro,
                direccionInicial = direccionRegistro,
                onVolver = { navController.popBackStack() },
                onConfirmarPedido = { direccion, referencia, metodoPago ->
                    ultimoPedido = PedidoConfirmado(
                        numero = (1000..9999).random().toString(),
                        total = totalPedido,
                        metodoPago = metodoPago,
                        direccion = direccion,
                        referencia = referencia
                    )
                    ultimoPedido?.let { historialPedidos.add(it) }
                    carrito = emptyList()
                    navController.navigate(Rutas.CONFIRMACION) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        composable(Rutas.PEDIDOS) {
            MisPedidosScreen(
                pedidos = historialPedidos,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.FAVORITOS) {
            FavoritosScreen(
                favoritos = listaProductosFake.filter { it.id in favoritosIds },
                onAgregarProducto = { producto ->
                    carrito = agregarOSumarProducto(carrito, producto, 1)
                },
                onQuitarFavorito = { productoId ->
                    favoritosIds.remove(productoId)
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.CONFIRMACION) {
            ultimoPedido?.let { pedido ->
                ConfirmacionScreen(
                    pedido = pedido,
                    onVolverAlInicio = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Si el producto ya está en el carrito, le suma la cantidad;
 * si no, lo agrega como un ItemCarrito nuevo.
 */
private fun agregarOSumarProducto(
    carrito: List<ItemCarrito>,
    producto: Producto,
    cantidad: Int
): List<ItemCarrito> {
    val itemExistente = carrito.find { it.producto.id == producto.id }
    return if (itemExistente != null) {
        carrito.map {
            if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + cantidad) else it
        }
    } else {
        carrito + ItemCarrito(producto = producto, cantidad = cantidad)
    }
}