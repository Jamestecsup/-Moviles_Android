package com.huaman.tecsupfit.navigation

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Reservas : Screen("reservas")
    object Perfil : Screen("perfil")
    object Rutinas : Screen("rutinas")
    object DetalleClase : Screen("detalle_clase/{claseId}") {
        fun createRoute(claseId: String) = "detalle_clase/$claseId"
    }
    object Confirmacion : Screen("confirmacion/{claseId}") {
        fun createRoute(claseId: String) = "confirmacion/$claseId"
    }
}
