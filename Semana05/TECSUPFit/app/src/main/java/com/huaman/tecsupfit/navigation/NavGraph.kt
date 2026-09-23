package com.huaman.tecsupfit.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huaman.tecsupfit.components.BottomNavBar
import com.huaman.tecsupfit.data.DataSource
import com.huaman.tecsupfit.model.EstadoReserva
import com.huaman.tecsupfit.model.Reserva
import com.huaman.tecsupfit.screens.ConfirmacionScreen
import com.huaman.tecsupfit.screens.DetalleClaseScreen
import com.huaman.tecsupfit.screens.InicioScreen
import com.huaman.tecsupfit.screens.PerfilScreen
import com.huaman.tecsupfit.screens.ReservasScreen
import com.huaman.tecsupfit.screens.RutinasScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val listaClases = DataSource.clases
    val listaReservas = remember { mutableStateListOf(*DataSource.reservasIniciales.toTypedArray()) }

    val rutasConBottomBar = listOf(
        Screen.Inicio.route,
        Screen.Reservas.route,
        Screen.Rutinas.route,
        Screen.Perfil.route
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in rutasConBottomBar

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Inicio.route) {
                InicioScreen(
                    clases = listaClases,
                    onClaseClick = { claseId ->
                        navController.navigate(Screen.DetalleClase.createRoute(claseId))
                    }
                )
            }
            composable(Screen.Reservas.route) {
                ReservasScreen(reservas = listaReservas)
            }
            composable(Screen.Rutinas.route) {
                RutinasScreen()
            }
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    totalClases = listaReservas.size,
                    racha = 3
                )
            }
            composable(
                route = Screen.DetalleClase.route,
                arguments = listOf(navArgument("claseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val claseId = backStackEntry.arguments?.getString("claseId")
                val clase = listaClases.find { it.id == claseId } ?: listaClases.first()

                DetalleClaseScreen(
                    clase = clase,
                    onBackClick = { navController.popBackStack() },
                    onReservarClick = {
                        val nuevaReserva = Reserva(
                            id = "r_${System.currentTimeMillis()}",
                            clase = clase,
                            estado = EstadoReserva.CONFIRMADA
                        )
                        listaReservas.add(0, nuevaReserva)
                        navController.navigate(Screen.Confirmacion.createRoute(clase.id)) {
                            popUpTo(Screen.Inicio.route)
                        }
                    }
                )
            }
            composable(
                route = Screen.Confirmacion.route,
                arguments = listOf(navArgument("claseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val claseId = backStackEntry.arguments?.getString("claseId")
                val clase = listaClases.find { it.id == claseId } ?: listaClases.first()

                ConfirmacionScreen(
                    clase = clase,
                    onVerReservasClick = {
                        navController.navigate(Screen.Reservas.route) {
                            popUpTo(Screen.Inicio.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
