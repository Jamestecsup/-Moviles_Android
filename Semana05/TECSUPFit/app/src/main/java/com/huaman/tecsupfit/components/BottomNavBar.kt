package com.huaman.tecsupfit.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.huaman.tecsupfit.navigation.Screen

sealed class BottomBarItem(val route: String, val title: String, val icon: ImageVector) {
    object Inicio : BottomBarItem(Screen.Inicio.route, "Inicio", Icons.Filled.Home)
    object Reservas : BottomBarItem(Screen.Reservas.route, "Reservas", Icons.Filled.CalendarToday)
    object Rutinas : BottomBarItem(Screen.Rutinas.route, "Rutinas", Icons.Filled.List)
    object Perfil : BottomBarItem(Screen.Perfil.route, "Perfil", Icons.Filled.Person)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomBarItem.Inicio,
        BottomBarItem.Reservas,
        BottomBarItem.Rutinas,
        BottomBarItem.Perfil
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Inicio.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
