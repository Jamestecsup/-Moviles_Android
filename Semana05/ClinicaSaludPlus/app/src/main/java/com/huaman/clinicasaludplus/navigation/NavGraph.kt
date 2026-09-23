package com.example.clinicasaludplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clinicasaludplus.model.Medico
import com.example.clinicasaludplus.ui.screens.InicioScreen
import com.example.clinicasaludplus.ui.screens.PerfilMedicoScreen
import com.example.clinicasaludplus.ui.screens.AgendarCitaScreen
import com.example.clinicasaludplus.ui.screens.ConfirmacionScreen
import com.example.clinicasaludplus.ui.screens.MisCitasScreen
import com.example.clinicasaludplus.ui.screens.HistorialScreen

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