package com.huaman.cafecampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.huaman.cafecampus.ui.cliente.MenuScreen
import com.huaman.cafecampus.ui.cliente.SplashScreen
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory
import com.huaman.cafecampus.viewmodel.MenuViewModel

/**
 * Grafo de navegacion de CafeCampus.
 *
 * Los dos perfiles comparten el MISMO NavHost. Lo que cambia segun el rol es:
 *  - el tema `CafeTheme(rol)` (naranja / indigo),
 *  - el `NavigationBar` inferior (3 items cliente / 4 items admin),
 *  - la pantalla de inicio.
 *
 * Paso 6: Splash + Menu.
 */
@Composable
fun NavCafe(
    factory: CafeViewModelFactory,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route ?: Rutas.SPLASH

    // Pantallas SIN barra inferior (son "modales" o de entrada)
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
                // TODO (Paso 9/12): barra inferior segun el rol activo
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.SPLASH) {
                SplashScreen(
                    onListo = {
                        navController.navigate(Rutas.MENU) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // TODO (Paso 6): agregar el destino LOGIN

            composable(Rutas.MENU) {
                val vm: MenuViewModel = viewModel(factory = factory)
                MenuScreen(
                    vm = vm,
                    onProducto = { id ->
                        vm.abrirProducto(id)
                        navController.navigate(Rutas.detalleProducto(id))
                    }
                )
            }

            // TODO (Paso 7): agregar DETALLE_PRODUCTO
            // TODO (Paso 8): agregar CONFIRMAR_PEDIDO, SEGUIMIENTO, MIS_PEDIDOS, PERFIL
            // TODO (Paso 9): agregar PANEL, PEDIDOS_ADMIN, DETALLE_PEDIDO_ADMIN
            // TODO (Paso 10): agregar MENU_ADMIN, FORMULARIO_PRODUCTO, AJUSTES
        }
    }
}