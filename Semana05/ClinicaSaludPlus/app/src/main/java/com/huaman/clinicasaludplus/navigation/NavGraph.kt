package com.huaman.clinicasaludplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.huaman.clinicasaludplus.model.Medico
import com.huaman.clinicasaludplus.screens.AgendarCitaScreen
import com.huaman.clinicasaludplus.screens.ConfirmacionScreen
import com.huaman.clinicasaludplus.screens.HistorialScreen
import com.huaman.clinicasaludplus.screens.InicioScreen
import com.huaman.clinicasaludplus.screens.MisCitasScreen
import com.huaman.clinicasaludplus.screens.PerfilMedicoScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onNavigateToDrawer: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            InicioScreen(
                navController = navController,
                onNavigateToDrawer = onNavigateToDrawer
            )
        }

        composable(
            route = "perfil_medico/{medicoId}",
            arguments = listOf(
                androidx.navigation.navArgument("medicoId") {
                    type = androidx.navigation.NavType.IntType
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            PerfilMedicoScreen(
                navController = navController,
                medicoId = medicoId
            )
        }

        composable(
            route = "agendar_cita/{medicoId}",
            arguments = listOf(
                androidx.navigation.navArgument("medicoId") {
                    type = androidx.navigation.NavType.IntType
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            AgendarCitaScreen(
                navController = navController,
                medicoId = medicoId
            )
        }

        composable(
            route = "confirmacion/{medicoId}/{fecha}/{hora}",
            arguments = listOf(
                androidx.navigation.navArgument("medicoId") {
                    type = androidx.navigation.NavType.IntType
                },
                androidx.navigation.navArgument("fecha") {
                    type = androidx.navigation.NavType.StringType
                },
                androidx.navigation.navArgument("hora") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            ConfirmacionScreen(
                navController = navController,
                medicoId = medicoId,
                fecha = fecha,
                hora = hora
            )
        }

        composable("mis_citas") {
            MisCitasScreen(
                navController = navController,
                onNavigateToDrawer = onNavigateToDrawer
            )
        }

        composable("historial") {
            HistorialScreen(
                navController = navController,
                onNavigateToDrawer = onNavigateToDrawer
            )
        }
    }
}
