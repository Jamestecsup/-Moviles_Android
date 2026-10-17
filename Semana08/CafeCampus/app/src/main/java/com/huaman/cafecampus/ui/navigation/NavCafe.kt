package com.huaman.cafecampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.cliente.DetalleProductoScreen
import com.huaman.cafecampus.ui.cliente.LoginScreen
import com.huaman.cafecampus.ui.cliente.MenuScreen
import com.huaman.cafecampus.ui.cliente.SplashScreen
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory
import com.huaman.cafecampus.viewmodel.MenuViewModel
import com.huaman.cafecampus.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/**
 * Grafo de navegacion de CafeCampus.
 *
 * Los dos perfiles comparten el MISMO NavHost. Lo que cambia segun el rol es
 * el tema (`CafeTheme(rol)`, en MainActivity), el `NavigationBar` inferior
 * (3 items cliente / 4 items admin) y la pantalla de inicio.
 *
 * Implementado: Splash y Login (Paso 6), Menu (proyecto base).
 */
@Composable
fun NavCafe(
    factory: CafeViewModelFactory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route ?: Rutas.SPLASH
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val rol = usuario?.rolTipado ?: Rol.CLIENTE

    // Pantallas SIN barra inferior (son de entrada o de detalle)
    val sinBarra = rutaActual in listOf(
        Rutas.SPLASH,
        Rutas.LOGIN,
        Rutas.DETALLE_PRODUCTO,
        Rutas.CONFIRMAR_PEDIDO,
        Rutas.SEGUIMIENTO,
        Rutas.FORMULARIO_PRODUCTO,
        Rutas.DETALLE_PEDIDO_ADMIN
    )

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!sinBarra) {
                // TODO (Paso 9/12): NavigationBar con 3 items (cliente)
                // o 4 items (administrador).
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
                            val destino = if (guardada == null) {
                                Rutas.LOGIN
                            } else {
                                pantallaPrincipal(guardada)
                            }
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

            composable(Rutas.MENU) {
                val vm: MenuViewModel = viewModel(factory = factory)
                val unidades by vm.unidadesCarrito.collectAsStateWithLifecycle()

                MenuScreen(
                    vm = vm,
                    unidadesCarrito = unidades,
                    onProducto = { id ->
                        vm.abrirProducto(id)
                        navController.navigate(Rutas.detalleProducto(id))
                    }
                )
            }

            composable(
                route = Rutas.DETALLE_PRODUCTO,
                arguments = listOf(navArgument(Rutas.ARG_PRODUCTO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PRODUCTO_ID) ?: 0
                val vm: MenuViewModel = viewModel(factory = factory)

                // El producto se busca SIEMPRE en el Flow de productos: si el
                // administrador lo marca como agotado mientras esta abierta,
                // la pantalla se actualiza sola.
                val productos by vm.productos.collectAsStateWithLifecycle()
                val producto = productos.firstOrNull { it.id == id }

                if (producto == null) {
                    // El producto ya no existe (lo borraron): se vuelve al menu.
                    VolverAlMenu(navController)
                } else {
                    DetalleProductoScreen(
                        producto = producto,
                        cantidadEnCarrito = vm.carrito.collectAsStateWithLifecycle().value
                            .firstOrNull { it.producto.id == id }?.cantidad ?: 0,
                        onAgregar = { cantidad, nota ->
                            vm.agregar(producto, cantidad, nota)
                            navController.popBackStack()
                        },
                        onAtras = { navController.popBackStack() }
                    )
                }
            }

            // TODO (Paso 8): agregar CONFIRMAR_PEDIDO, SEGUIMIENTO, MIS_PEDIDOS, PERFIL
            // TODO (Paso 9): agregar PANEL, PEDIDOS_ADMIN, DETALLE_PEDIDO_ADMIN
            // TODO (Paso 10): agregar MENU_ADMIN, FORMULARIO_PRODUCTO, AJUSTES
        }
    }
}

/** Vuelve al menu cuando el producto ya no existe. */
@Composable
private fun VolverAlMenu(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.popBackStack(Rutas.MENU, inclusive = false)
    }
}

/** Pantalla principal segun el perfil del usuario. */
fun pantallaPrincipal(usuario: Usuario): String = pantallaPrincipalDeRol(usuario.rolTipado)

fun pantallaPrincipalDeRol(rol: Rol): String = when (rol) {
    Rol.CLIENTE -> Rutas.MENU
    // TODO (Paso 9): cuando exista el panel, el admin entra a PANEL.
    Rol.ADMIN -> Rutas.MENU
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