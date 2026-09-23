package com.huaman.navlab.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.huaman.navlab.screens.DetailScreen
import com.huaman.navlab.screens.HomeScreen
import com.huaman.navlab.screens.ListScreen
import com.huaman.navlab.screens.LoginScreen
import com.huaman.navlab.screens.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.List.route) {
            ListScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.StringType
                    defaultValue = "2024-0001"
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "2024-0001"
            DetailScreen(navController, studentId)
        }
    }
}
