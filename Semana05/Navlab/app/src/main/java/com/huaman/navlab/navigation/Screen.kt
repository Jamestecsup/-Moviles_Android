package com.huaman.navlab.navigation

sealed class Screen(val route: String) {

    object Login : Screen(route = "login")

    object Home : Screen(route = "home")

    object List : Screen(route = "list")

    object Profile : Screen(route = "profile")

    object Detail : Screen(route = "detail/{studentId}") {
        fun createRoute(studentId: String): String = "detail/$studentId"
    }
}
