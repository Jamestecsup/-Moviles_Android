package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.relation.ProductoVendido
import com.huaman.cafecampus.data.local.relation.TotalPorPago
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.ui.components.inicioDelDia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Reporte del dia del administrador (Fase 2, mejora 3).
 *
 * Todo sale de consultas agregadas en Room: el producto mas vendido y el
 * total por forma de pago. No hay datos fijos.
 */
class ReporteViewModel(private val repo: CafeRepository) : ViewModel() {

    private val inicioDia = inicioDelDia()

    /** Ranking de productos mas vendidos del dia. */
    val masVendidos: StateFlow<List<ProductoVendido>> =
        repo.observarProductosMasVendidos(inicioDia)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Total y cantidad de pedidos por forma de pago (Caja / Yape). */
    val porFormaDePago: StateFlow<List<TotalPorPago>> =
        repo.observarTotalesPorFormaDePago(inicioDia)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** El producto mas vendido (el primero del ranking), o null si no hay ventas. */
    val productoMasVendido: StateFlow<ProductoVendido?> =
        masVendidos
            .map { it.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Total vendido en el dia (suma de todas las formas de pago). */
    val totalDelDia: StateFlow<Double> =
        porFormaDePago
            .map { lista -> lista.sumOf { it.total } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    /** Cantidad de pedidos del dia. */
    val pedidosDelDia: StateFlow<Int> =
        porFormaDePago
            .map { lista -> lista.sumOf { it.pedidos } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
