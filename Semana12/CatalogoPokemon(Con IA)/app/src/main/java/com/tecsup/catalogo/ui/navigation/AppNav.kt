package com.tecsup.catalogo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.catalogo.AppContainer
import com.tecsup.catalogo.domain.model.Rol
import com.tecsup.catalogo.ui.admin.AdminScreen
import com.tecsup.catalogo.ui.admin.AdminViewModel
import com.tecsup.catalogo.ui.detalle.DetalleScreen
import com.tecsup.catalogo.ui.detalle.DetalleViewModel
import com.tecsup.catalogo.ui.favoritos.FavoritosScreen
import com.tecsup.catalogo.ui.favoritos.FavoritosViewModel
import com.tecsup.catalogo.ui.lista.ListaScreen
import com.tecsup.catalogo.ui.lista.ListaViewModel
import com.tecsup.catalogo.ui.login.LoginScreen
import com.tecsup.catalogo.ui.login.LoginViewModel

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()

    val factory = remember(container) {
        viewModelFactory {
            initializer { LoginViewModel(container.authRepository) }
            initializer { ListaViewModel(container.itemRepository, container.notificador) }
            initializer { DetalleViewModel(container.itemRepository) }
            initializer { FavoritosViewModel(container.itemRepository) }
            initializer { AdminViewModel(container.itemRepository, container.notificador) }
        }
    }

    val loginVm: LoginViewModel = viewModel(factory = factory)
    val sesion by loginVm.sesion.collectAsStateWithLifecycle()
    val rol = sesion?.rol ?: Rol.USUARIO

    val cerrarSesion: () -> Unit = {
        loginVm.cerrarSesion()
        nav.navigate("login") {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                vm = loginVm,
                onSesionIniciada = {
                    nav.navigate("lista") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("lista") {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                vm = vm,
                rol = rol,
                onDetalle = { id -> nav.navigate("detalle/$id") },
                onFavoritos = { nav.navigate("favoritos") },
                onAdmin = { nav.navigate("admin") },
                onCerrarSesion = cerrarSesion
            )
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entrada ->
            val vm: DetalleViewModel = viewModel(factory = factory)
            DetalleScreen(
                vm = vm,
                id = entrada.arguments?.getString("id").orEmpty(),
                onVolver = { nav.popBackStack() }
            )
        }

        composable("favoritos") {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(
                vm = vm,
                onDetalle = { id -> nav.navigate("detalle/$id") },
                onVolver = { nav.popBackStack() }
            )
        }

        composable("admin") {
            if (rol == Rol.ADMIN) {
                val vm: AdminViewModel = viewModel(factory = factory)
                AdminScreen(
                    vm = vm,
                    onVolver = { nav.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    nav.popBackStack()
                }
            }
        }
    }
}
