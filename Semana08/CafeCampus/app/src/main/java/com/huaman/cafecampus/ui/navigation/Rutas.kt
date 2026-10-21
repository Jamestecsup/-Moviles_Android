package com.huaman.cafecampus.ui.navigation

/**
 * Todas las rutas de la app, en un solo lugar.
 *
 * Los dos perfiles (cliente y administrador) conviven en el MISMO NavHost:
 * lo que cambia es el `NavigationBar` de abajo (3 items para el cliente,
 * 4 para el admin) y el tema `CafeTheme(rol)`.
 */
object Rutas {

    // -----------------------------------------------------------------------
    // Comunes
    // -----------------------------------------------------------------------
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // -----------------------------------------------------------------------
    // Cliente (3 items en la barra inferior: Menu, Pedidos, Perfil)
    // -----------------------------------------------------------------------
    const val MENU = "menu"
    const val DETALLE_PRODUCTO = "detalleProducto/{productoId}"
    const val CONFIRMAR_PEDIDO = "confirmarPedido"
    const val SEGUIMIENTO = "seguimiento/{pedidoId}"
    const val MIS_PEDIDOS = "misPedidos"
    const val PERFIL = "perfil"
    const val PROMOCIONES = "promociones"
    const val FORMULARIO_PROMOCION = "formularioPromocion?promocionId={promocionId}"

    // -----------------------------------------------------------------------
    // Administrador (4 items: Panel, Pedidos, Menu, Ajustes)
    // -----------------------------------------------------------------------
    const val PANEL = "panel"
    const val PEDIDOS_ADMIN = "pedidosAdmin"
    const val DETALLE_PEDIDO_ADMIN = "detallePedidoAdmin/{pedidoId}"
    const val MENU_ADMIN = "menuAdmin"
    const val FORMULARIO_PRODUCTO = "formularioProducto?productoId={productoId}"
    const val AJUSTES = "ajustes"

    // -----------------------------------------------------------------------
    // Helpers: construyen la ruta real con sus argumentos
    // -----------------------------------------------------------------------

    fun detalleProducto(productoId: Int) = "detalleProducto/$productoId"

    fun seguimiento(pedidoId: Int) = "seguimiento/$pedidoId"

    fun detallePedidoAdmin(pedidoId: Int) = "detallePedidoAdmin/$pedidoId"

    /** `productoId` vacio = crear uno nuevo; con id = editar. */
    fun formularioProducto(productoId: Int? = null) =
        if (productoId == null) "formularioProducto?productoId=0" else "formularioProducto?productoId=$productoId"

    const val ARG_PRODUCTO_ID = "productoId"
    const val ARG_PEDIDO_ID = "pedidoId"

    fun promociones() = "promociones"

    /** `promocionId` vacio = crear una nueva; con id = editar. */
    fun formularioPromocion(promocionId: Int? = null) =
        if (promocionId == null) "formularioPromocion?promocionId=0"
        else "formularioPromocion?promocionId=$promocionId"

    const val ARG_PROMOCION_ID = "promocionId"
}