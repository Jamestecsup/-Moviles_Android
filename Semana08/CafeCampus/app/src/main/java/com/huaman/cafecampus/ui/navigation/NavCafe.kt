package com.huaman.cafecampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.admin.PanelScreen
import com.huaman.cafecampus.ui.admin.PedidosAdminScreen
import com.huaman.cafecampus.ui.cliente.ConfirmarPedidoScreen
import com.huaman.cafecampus.ui.cliente.DetalleProductoScreen
import com.huaman.cafecampus.ui.cliente.LoginScreen
import com.huaman.cafecampus.ui.cliente.MisPedidosScreen
import com.huaman.cafecampus.ui.cliente.MenuScreen
import com.huaman.cafecampus.ui.cliente.PerfilScreen
import com.huaman.cafecampus.ui.cliente.SeguimientoScreen
import com.huaman.cafecampus.ui.cliente.SplashScreen
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory
import com.huaman.cafecampus.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/** Un item de la barra inferior. */
private data class ItemBarra(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

/** 3 items para el cliente. */
private val BARRA_CLIENTE = listOf(
    ItemBarra(Rutas.MENU, "Menu", Icons.Filled.Home),
    ItemBarra(Rutas.MIS_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

/** 4 items para el administrador. */
private val BARRA_ADMIN = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Filled.BarChart),
    ItemBarra(Rutas.PEDIDOS_ADMIN, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.MENU_ADMIN, "Menu", Icons.Filled.Restaurant),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

/**
 * Grafo de navegacion de CafeCampus.
 *
 * Los dos perfiles comparten el MISMO NavHost. Lo que cambia segun el rol es:
 *  - el tema (`CafeTheme(rol)`, aplicado en MainActivity),
 *  - el `NavigationBar` inferior (3 items cliente / 4 items admin),
 *  - la pantalla de inicio.
 */
@Composable
fun NavCafe(
    factory: CafeViewModelFactory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Los ViewModel se crean UNA vez aqui, a nivel del NavHost. Si se crearan
    // dentro de cada composable(), cada pantalla tendria su propia instancia y
    // el carrito se perderia al navegar.
    val menu = factory.menu
    val pedidosCliente = factory.pedidosCliente

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route ?: Rutas.SPLASH
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val rolElegido by sesion.rolSeleccionado.collectAsStateWithLifecycle()
    val rol = usuario?.rolTipado ?: rolElegido

    // Cliente conectado: se necesita su id para pedir y para el historial.
    LaunchedEffect(usuario?.id) {
        usuario?.let { pedidosCliente.cargar(it.id) }
    }

    // Pantallas SIN barra inferior (de entrada o de detalle a pantalla completa)
    val sinBarra = rutaActual in listOf(
        Rutas.SPLASH,
        Rutas.LOGIN,
        Rutas.DETALLE_PRODUCTO,
        Rutas.CONFIRMAR_PEDIDO,
        Rutas.SEGUIMIENTO,
        Rutas.FORMULARIO_PRODUCTO,
        Rutas.DETALLE_PEDIDO_ADMIN
    )

    val barra = if (rol == Rol.ADMIN) BARRA_ADMIN else BARRA_CLIENTE

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!sinBarra) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp
                ) {
                    barra.forEach { item ->
                        val activo = rutaActual == item.ruta
                        NavigationBarItem(
                            selected = activo,
                            onClick = {
                                if (!activo) {
                                    // Patron estandar de barra inferior:
                                    // guarda el estado de cada pestana y
                                    // vuelve a el al reabrirla.
                                    navController.navigate(item.ruta) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = {
                                Text(
                                    text = item.titulo,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.SPLASH) {
                val scope = rememberCoroutineScope()
                SplashScreen(
                    onListo = {
                        scope.launch {
                            // Si DataStore ya tiene perfil, se salta el login.
                            val guardada = sesion.restaurarSesion()
                            val destino = guardada?.let { pantallaPrincipal(it) } ?: Rutas.LOGIN
                            navController.navigate(destino) {
                                popUpTo(Rutas.SPLASH) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Rutas.LOGIN) {
                LoginScreen(
                    vm = sesion,
                    onIngresoExitoso = { rolIngresado ->
                        navController.navigate(pantallaPrincipalDeRol(rolIngresado)) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // -------------------------------------------------------------------
            // CLIENTE
            // -------------------------------------------------------------------

            composable(Rutas.MENU) {
                val unidades by menu.unidadesCarrito.collectAsStateWithLifecycle()
                MenuScreen(
                    vm = menu,
                    unidadesCarrito = unidades,
                    onCarrito = { navController.navigate(Rutas.CONFIRMAR_PEDIDO) },
                    onProducto = { id ->
                        menu.abrirProducto(id)
                        navController.navigate(Rutas.detalleProducto(id))
                    }
                )
            }

            composable(
                route = Rutas.DETALLE_PRODUCTO,
                arguments = listOf(navArgument(Rutas.ARG_PRODUCTO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PRODUCTO_ID) ?: 0

                // El producto se busca SIEMPRE en el Flow de productos: si el
                // administrador lo marca como agotado mientras esta abierta,
                // la pantalla se actualiza sola.
                val productos by menu.productos.collectAsStateWithLifecycle()
                val producto = productos.firstOrNull { it.id == id }

                if (producto == null) {
                    VolverA(navController, Rutas.MENU)
                } else {
                    val carrito by menu.carrito.collectAsStateWithLifecycle()
                    DetalleProductoScreen(
                        producto = producto,
                        cantidadEnCarrito = carrito.firstOrNull { it.producto.id == id }
                            ?.cantidad ?: 0,
                        onAgregar = { cantidad, nota ->
                            menu.agregar(producto, cantidad, nota)
                            navController.popBackStack()
                        },
                        onAtras = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.CONFIRMAR_PEDIDO) {
                val carrito by menu.carrito.collectAsStateWithLifecycle()
                ConfirmarPedidoScreen(
                    items = carrito,
                    onConfirmar = { hora, pago, nota ->
                        pedidosCliente.confirmar(hora, pago, nota) { idNuevo ->
                            navController.navigate(Rutas.seguimiento(idNuevo)) {
                                popUpTo(Rutas.CONFIRMAR_PEDIDO) { inclusive = true }
                            }
                        }
                    },
                    onSeguirPidiendo = { navController.popBackStack() },
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.SEGUIMIENTO,
                arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
                LaunchedEffect(id) { pedidosCliente.verPedido(id) }

                // Flow de Room: cambia solo, sin reiniciar la app.
                val pedido by pedidosCliente.pedido.collectAsStateWithLifecycle()
                SeguimientoScreen(
                    pedido = pedido,
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.MIS_PEDIDOS) {
                val activos by pedidosCliente.pedidosActivos.collectAsStateWithLifecycle()
                val historicos by pedidosCliente.pedidosHistoricos.collectAsStateWithLifecycle()
                MisPedidosScreen(
                    activos = activos,
                    historicos = historicos,
                    onPedido = { id -> navController.navigate(Rutas.seguimiento(id)) },
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.PERFIL) {
                PerfilScreen(
                    usuario = usuario,
                    unidades = menu.unidadesCarrito.collectAsStateWithLifecycle().value,
                    onCerrarSesion = {
                        sesion.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // -------------------------------------------------------------------
            // ADMINISTRADOR
            // -------------------------------------------------------------------

            composable(Rutas.PANEL) {
                PanelScreen(
                    vm = factory.panelAdmin,
                    onVerTodos = { navController.navigate(Rutas.PEDIDOS_ADMIN) },
                    onPedido = { id -> navController.navigate(Rutas.detallePedidoAdmin(id)) }
                )
            }

            composable(Rutas.PEDIDOS_ADMIN) {
                PedidosAdminScreen(
                    vm = factory.pedidosAdmin,
                    onPedido = { id -> navController.navigate(Rutas.detallePedidoAdmin(id)) }
                )
            }

            // TODO (Paso 11): agregar DETALLE_PEDIDO_ADMIN, MENU_ADMIN,
            //                  FORMULARIO_PRODUCTO y AJUSTES
        }
    }
}

/** Vuelve a una ruta cuando la pantalla ya no tiene datos que mostrar. */
@Composable
private fun VolverA(navController: NavHostController, ruta: String) {
    LaunchedEffect(Unit) {
        navController.popBackStack(ruta, inclusive = false)
    }
}

/** Pantalla principal segun el perfil del usuario. */
fun pantallaPrincipal(usuario: Usuario): String = pantallaPrincipalDeRol(usuario.rolTipado)

fun pantallaPrincipalDeRol(rol: Rol): String = when (rol) {
    Rol.CLIENTE -> Rutas.MENU
    Rol.ADMIN -> Rutas.PANEL
}

/**
 * Navega a la pantalla principal de un rol LIMPIANDO la pila.
 * Se usa al cambiar de perfil y al cerrar sesion.
 */
fun irAPantallaPrincipal(navController: NavHostController, rol: Rol) {
    navController.navigate(pantallaPrincipalDeRol(rol)) {
        popUpTo(0) { inclusive = true }   // limpia toda la pila
        launchSingleTop = true
    }
}