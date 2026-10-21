package com.huaman.cafecampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.admin.AjustesScreen
import com.huaman.cafecampus.ui.admin.DetallePedidoAdminScreen
import com.huaman.cafecampus.ui.admin.FormularioProductoScreen
import com.huaman.cafecampus.ui.admin.MenuAdminScreen
import com.huaman.cafecampus.ui.admin.PanelScreen
import com.huaman.cafecampus.ui.admin.PedidosAdminScreen
import com.huaman.cafecampus.ui.admin.ReporteScreen
import com.huaman.cafecampus.ui.admin.FormularioPromocionScreen
import com.huaman.cafecampus.ui.cliente.ConfirmarPedidoScreen
import com.huaman.cafecampus.ui.cliente.DetalleProductoScreen
import com.huaman.cafecampus.ui.cliente.LoginScreen
import com.huaman.cafecampus.ui.cliente.MisPedidosScreen
import com.huaman.cafecampus.ui.cliente.MenuScreen
import com.huaman.cafecampus.ui.cliente.PerfilScreen
import com.huaman.cafecampus.ui.cliente.PromocionesScreen
import com.huaman.cafecampus.ui.cliente.RegistroScreen
import com.huaman.cafecampus.ui.cliente.SeguimientoScreen
import com.huaman.cafecampus.ui.cliente.SplashScreen
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory
import com.huaman.cafecampus.viewmodel.PromocionesViewModel
import com.huaman.cafecampus.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/** Un item de la barra inferior. */
private data class ItemBarra(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

/** 4 items para el cliente (incluye Promociones). */
private val BARRA_CLIENTE = listOf(
    ItemBarra(Rutas.MENU, "Menu", Icons.Filled.Home),
    ItemBarra(Rutas.PROMOCIONES, "Promociones", Icons.Filled.LocalOffer),
    ItemBarra(Rutas.MIS_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

/** 5 items para el administrador (incluye Promociones). */
private val BARRA_ADMIN = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Filled.BarChart),
    ItemBarra(Rutas.PEDIDOS_ADMIN, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.PROMOCIONES, "Promociones", Icons.Filled.LocalOffer),
    ItemBarra(Rutas.MENU_ADMIN, "Menu", Icons.Filled.Restaurant),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

/**
 * Grafo de navegacion de CafeCampus.
 *
 * Los dos perfiles comparten el MISMO NavHost. Lo que cambia segun el rol es:
 *  - el tema (`CafeTheme(rol)`, aplicado en MainActivity),
 *  - el `NavigationBar` inferior (3 items cliente / 4 items admin),
 *  - la pantalla de inicio.
 */
@Composable
fun NavCafe(
    factory: CafeViewModelFactory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Los ViewModel se crean UNA vez aqui, a nivel del NavHost. Si se crearan
    // dentro de cada composable(), cada pantalla tendria su propia instancia y
    // el carrito se perderia al navegar.
    val menu = factory.menu
    val pedidosCliente = factory.pedidosCliente

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route ?: Rutas.SPLASH
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val rolElegido by sesion.rolSeleccionado.collectAsStateWithLifecycle()
    val rol = usuario?.rolTipado ?: rolElegido

    // Cliente conectado: se necesita su id para pedir y para el historial.
    LaunchedEffect(usuario?.id) {
        usuario?.let { pedidosCliente.cargar(it.id) }
    }

    // Pantallas SIN barra inferior (de entrada o de detalle a pantalla completa)
    val sinBarra = rutaActual in listOf(
        Rutas.SPLASH,
        Rutas.LOGIN,
        Rutas.REGISTRO,
        Rutas.DETALLE_PRODUCTO,
        Rutas.CONFIRMAR_PEDIDO,
        Rutas.SEGUIMIENTO,
        Rutas.FORMULARIO_PRODUCTO,
        Rutas.FORMULARIO_PROMOCION,
        Rutas.DETALLE_PEDIDO_ADMIN
    )

    val barra = if (rol == Rol.ADMIN) BARRA_ADMIN else BARRA_CLIENTE

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!sinBarra) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp
                ) {
                    barra.forEach { item ->
                        val activo = rutaActual == item.ruta
                        NavigationBarItem(
                            selected = activo,
                            onClick = {
                                if (!activo) {
                                    // Patron estandar de barra inferior:
                                    // guarda el estado de cada pestana y
                                    // vuelve a el al reabrirla.
                                    navController.navigate(item.ruta) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = {
                                Text(
                                    text = item.titulo,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
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
                            val destino = guardada?.let { pantallaPrincipal(it) } ?: Rutas.LOGIN
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
                            // Limpia toda la pila: el Login no debe quedar
                            // atras (si no, "atras" vuelve a la pantalla de
                            // entrada).
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onIrAlRegistro = {
                        navController.navigate(Rutas.REGISTRO) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    vm = sesion,
                    onRegistrado = { nuevo ->
                        // La cuenta ya esta creada: se inicia sesion y se
                        // entra directo al menu del estudiante.
                        sesion.ingresarCon(nuevo)
                        navController.navigate(pantallaPrincipalDeRol(Rol.CLIENTE)) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onVolverAlLogin = { navController.popBackStack() }
                )
            }

            // -------------------------------------------------------------------
            // CLIENTE
            // -------------------------------------------------------------------

            composable(Rutas.MENU) {
                val unidades by menu.unidadesCarrito.collectAsStateWithLifecycle()
                val total by menu.totalCarrito.collectAsStateWithLifecycle()
                val totalOriginal by menu.totalCarritoOriginal.collectAsStateWithLifecycle()
                MenuScreen(
                    vm = menu,
                    unidadesCarrito = unidades,
                    totalCarrito = total,
                    totalCarritoOriginal = totalOriginal,
                    nombreUsuario = usuario?.nombre,
                    onCarrito = { navController.navigate(Rutas.CONFIRMAR_PEDIDO) },
                    onProducto = { id ->
                        navController.navigate(Rutas.detalleProducto(id))
                    }
                )
            }

            composable(
                route = Rutas.DETALLE_PRODUCTO,
                arguments = listOf(navArgument(Rutas.ARG_PRODUCTO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PRODUCTO_ID) ?: 0

                // El producto se busca SIEMPRE en el Flow de ofertas: si el
                // administrador lo marca como agotado o crea una promocion
                // mientras esta abierta, la pantalla se actualiza sola.
                val ofertas by menu.ofertas.collectAsStateWithLifecycle()
                val oferta = ofertas.firstOrNull { it.id == id }

                if (oferta == null) {
                    VolverA(navController, Rutas.MENU)
                } else {
                    val carrito by menu.carrito.collectAsStateWithLifecycle()
                    DetalleProductoScreen(
                        producto = oferta.producto,
                        descuentoPorcentaje = oferta.descuentoPorcentaje,
                        precioFinal = oferta.precioFinal,
                        cantidadEnCarrito = carrito.firstOrNull { it.producto.id == id }
                            ?.cantidad ?: 0,
                        onAgregar = { cantidad, nota ->
                            menu.agregar(
                                producto = oferta.producto,
                                cantidad = cantidad,
                                nota = nota,
                                precioUnitario = oferta.precioFinal,
                                descuentoPorcentaje = oferta.descuentoPorcentaje
                            )
                            navController.popBackStack()
                        },
                        onAtras = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.CONFIRMAR_PEDIDO) {
                val carrito by menu.carrito.collectAsStateWithLifecycle()
                val recibePedidos by pedidosCliente.recibirPedidos.collectAsStateWithLifecycle()
                val mensaje by pedidosCliente.mensaje.collectAsStateWithLifecycle()

                ConfirmarPedidoScreen(
                    items = carrito,
                    recibePedidos = recibePedidos,
                    mensaje = mensaje,
                    onConfirmar = { hora, pago, nota ->
                        pedidosCliente.confirmar(hora, pago, nota) { idNuevo ->
                            navController.navigate(Rutas.seguimiento(idNuevo)) {
                                popUpTo(Rutas.CONFIRMAR_PEDIDO) { inclusive = true }
                            }
                        }
                    },
                    onSeguirPidiendo = { navController.popBackStack() },
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.SEGUIMIENTO,
                arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
                LaunchedEffect(id) { pedidosCliente.verPedido(id) }

                // Flow de Room: cambia solo, sin reiniciar la app.
                val pedido by pedidosCliente.pedido.collectAsStateWithLifecycle()
                SeguimientoScreen(
                    pedido = pedido,
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.MIS_PEDIDOS) {
                val activos by pedidosCliente.pedidosActivos.collectAsStateWithLifecycle()
                val historicos by pedidosCliente.pedidosHistoricos.collectAsStateWithLifecycle()
                val mensaje by pedidosCliente.mensaje.collectAsStateWithLifecycle()
                MisPedidosScreen(
                    activos = activos,
                    historicos = historicos,
                    onPedido = { id -> navController.navigate(Rutas.seguimiento(id)) },
                    onRepetir = { pedido ->
                        pedidosCliente.repetirPedido(pedido)
                    },
                    onIrAlMenu = {
                        navController.navigate(Rutas.MENU) {
                            popUpTo(Rutas.MENU) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.PERFIL) {
                PerfilScreen(
                    usuario = usuario,
                    unidades = menu.unidadesCarrito.collectAsStateWithLifecycle().value,
                    onCerrarSesion = {
                        sesion.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // -------------------------------------------------------------------
            // ADMINISTRADOR
            // -------------------------------------------------------------------

            composable(Rutas.PANEL) {
                PanelScreen(
                    vm = factory.panelAdmin,
                    onVerTodos = { navController.navigate(Rutas.PEDIDOS_ADMIN) },
                    onPedido = { id -> navController.navigate(Rutas.detallePedidoAdmin(id)) },
                    onVerReporte = { navController.navigate(Rutas.REPORTE) }
                )
            }

            composable(Rutas.PEDIDOS_ADMIN) {
                PedidosAdminScreen(
                    vm = factory.pedidosAdmin,
                    onPedido = { id -> navController.navigate(Rutas.detallePedidoAdmin(id)) }
                )
            }

            composable(
                route = Rutas.DETALLE_PEDIDO_ADMIN,
                arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PEDIDO_ID) ?: 0
                LaunchedEffect(id) { factory.pedidosAdmin.verPedido(id) }

                val pedido by factory.pedidosAdmin.pedidoAbierto.collectAsStateWithLifecycle()
                DetallePedidoAdminScreen(
                    pedido = pedido,
                    onCambiarEstado = { nuevo ->
                        pedido?.let { p ->
                            factory.pedidosAdmin.cambiarA(p, nuevo)
                        }
                    },
                    onCancelar = { pedido?.let { factory.pedidosAdmin.cancelar(it) } },
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.MENU_ADMIN) {
                MenuAdminScreen(
                    vm = factory.menuAdmin,
                    onNuevo = { navController.navigate(Rutas.formularioProducto()) },
                    onEditar = { id -> navController.navigate(Rutas.formularioProducto(id)) }
                )
            }

            composable(
                route = Rutas.FORMULARIO_PRODUCTO,
                arguments = listOf(
                    navArgument(Rutas.ARG_PRODUCTO_ID) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PRODUCTO_ID) ?: 0
                val vm = factory.menuAdmin

                val productos by vm.productos.collectAsStateWithLifecycle()
                val error by vm.error.collectAsStateWithLifecycle()

                FormularioProductoScreen(
                    producto = productos.firstOrNull { it.id == id },
                    categorias = vm.categoriasFormulario,
                    iconos = vm.iconos,
                    error = error,
                    onGuardar = { idForm, nombre, desc, cat, prec, ico, disp, imagenRuta ->
                        vm.guardar(idForm, nombre, desc, cat, prec, ico, disp, imagenRuta) {
                            navController.popBackStack()
                        }
                    },
                    onCancelar = { navController.popBackStack() },
                    onAtras = { navController.popBackStack() },
                    onLimpiarError = vm::limpiarError
                )
            }

            composable(Rutas.PROMOCIONES) {
                PromocionesScreen(
                    vm = factory.promociones,
                    esAdmin = rol == Rol.ADMIN,
                    onNueva = { navController.navigate(Rutas.formularioPromocion()) },
                    onEditar = { id -> navController.navigate(Rutas.formularioPromocion(id)) }
                )
            }

            composable(
                route = Rutas.FORMULARIO_PROMOCION,
                arguments = listOf(
                    navArgument(Rutas.ARG_PROMOCION_ID) {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { entry ->
                val id = entry.arguments?.getInt(Rutas.ARG_PROMOCION_ID) ?: 0
                val vm = factory.promociones

                val todas by vm.todas.collectAsStateWithLifecycle()
                val productos by vm.productos.collectAsStateWithLifecycle()
                val error by vm.error.collectAsStateWithLifecycle()

                FormularioPromocionScreen(
                    promocion = todas.firstOrNull { it.id == id },
                    productos = productos,
                    categorias = vm.categorias,
                    error = error,
                    onGuardar = { idForm, tipo, valor, desc, fechaFin ->
                        vm.guardar(idForm, tipo, valor, desc, fechaFin) {
                            navController.popBackStack()
                        }
                    },
                    onCancelar = { navController.popBackStack() },
                    onAtras = { navController.popBackStack() },
                    onLimpiarError = vm::limpiarError
                )
            }

            composable(Rutas.REPORTE) {
                ReporteScreen(
                    vm = factory.reporte,
                    onAtras = { navController.popBackStack() }
                )
            }

            composable(Rutas.AJUSTES) {
                val modoOscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
                val notificaciones by sesion.notificaciones.collectAsStateWithLifecycle()
                val recibePedidos by sesion.recibirPedidos.collectAsStateWithLifecycle()

                AjustesScreen(
                    usuario = usuario,
                    modoOscuro = modoOscuro,
                    notificaciones = notificaciones,
                    recibePedidos = recibePedidos,
                    onModoOscuro = sesion::guardarModoOscuro,
                    onNotificaciones = sesion::guardarNotificaciones,
                    onRecibePedidos = sesion::guardarRecibirPedidos,
                    onCambiarPerfil = {
                        // Modo practica: se guarda el otro perfil en DataStore
                        // y se navega a su grafo LIMPIANDO la pila.
                        val otro = if (rol == Rol.ADMIN) Rol.CLIENTE else Rol.ADMIN
                        sesion.cambiarPerfil(otro)
                        irAPantallaPrincipal(navController, otro)
                    },
                    onCerrarSesion = {
                        sesion.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

/** Vuelve a una ruta cuando la pantalla ya no tiene datos que mostrar. */
@Composable
private fun VolverA(navController: NavHostController, ruta: String) {
    LaunchedEffect(Unit) {
        navController.popBackStack(ruta, inclusive = false)
    }
}

/** Pantalla principal segun el perfil del usuario. */
fun pantallaPrincipal(usuario: Usuario): String = pantallaPrincipalDeRol(usuario.rolTipado)

fun pantallaPrincipalDeRol(rol: Rol): String = when (rol) {
    Rol.CLIENTE -> Rutas.MENU
    Rol.ADMIN -> Rutas.PANEL
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