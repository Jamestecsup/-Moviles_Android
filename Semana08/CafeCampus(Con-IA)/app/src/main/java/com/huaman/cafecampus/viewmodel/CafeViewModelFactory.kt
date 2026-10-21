package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huaman.cafecampus.data.prefs.PreferenciasStore
import com.huaman.cafecampus.data.repository.CafeRepository

/**
 * Fabrica unica de ViewModels.
 *
 * Los ViewModels reciben el REPOSITORY (interface) y las PREFERENCIAS, nunca
 * el DAO. Esta fabrica es la que hace esa conexion, y es el unico lugar que
 * conoce las dos cosas.
 *
 * OJO CON LAS INSTANCIAS COMPARTIDAS: el carrito vive en [MenuViewModel] y lo
 * necesita tambien [PedidosClienteViewModel] para leerlo y vaciarlo al
 * confirmar. Si la fabrica creara dos MenuViewModel distintos, el carrito de la
 * pantalla de confirmacion seria SIEMPRE vacio. Por eso las instancias se
 * guardan con `by lazy` y se devuelven siempre las mismas.
 */
class CafeViewModelFactory(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    /** Carrito y productos del menu. Compartido con PedidosClienteViewModel. */
    val menu: MenuViewModel by lazy { MenuViewModel(repo) }

    val sesion: SesionViewModel by lazy { SesionViewModel(repo, prefs) }

    val pedidosCliente: PedidosClienteViewModel by lazy { PedidosClienteViewModel(repo, menu, prefs) }

    val panelAdmin: PanelAdminViewModel by lazy { PanelAdminViewModel(repo) }

    val pedidosAdmin: PedidosAdminViewModel by lazy { PedidosAdminViewModel(repo) }

    val menuAdmin: MenuAdminViewModel by lazy { MenuAdminViewModel(repo) }

    val reporte: ReporteViewModel by lazy { ReporteViewModel(repo) }

    val promociones: PromocionesViewModel by lazy { PromocionesViewModel(repo) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MenuViewModel::class.java) -> menu as T

        modelClass.isAssignableFrom(SesionViewModel::class.java) -> sesion as T

        modelClass.isAssignableFrom(PedidosClienteViewModel::class.java) -> pedidosCliente as T

        modelClass.isAssignableFrom(PanelAdminViewModel::class.java) -> panelAdmin as T

        modelClass.isAssignableFrom(PedidosAdminViewModel::class.java) -> pedidosAdmin as T

        modelClass.isAssignableFrom(MenuAdminViewModel::class.java) -> menuAdmin as T

        modelClass.isAssignableFrom(ReporteViewModel::class.java) -> reporte as T

        modelClass.isAssignableFrom(PromocionesViewModel::class.java) -> promociones as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}