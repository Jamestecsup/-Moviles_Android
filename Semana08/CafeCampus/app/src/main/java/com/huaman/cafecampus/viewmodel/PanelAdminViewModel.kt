package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.ui.components.inicioDelDia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Panel de control del ADMINISTRADOR (mockup 2).
 *
 * Todo sale de Flows de Room: cuando el estudiante confirma un pedido, los
 * contadores y las ventas del dia se actualizan solos, sin recargar nada.
 */
class PanelAdminViewModel(
    private val repo: CafeRepository
) : ViewModel() {

    /** Inicio del dia de HOY, en millis. */
    private val inicioDia = inicioDelDia()

    private val nuevosFlow = repo.observarCantidadPorEstado(EstadoPedido.RECIBIDO)

    /** Contadores de colores del panel. */
    val nuevos: StateFlow<Int> = nuevosFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0
    )

    val preparando: StateFlow<Int> =
        repo.observarCantidadPorEstado(EstadoPedido.PREPARANDO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val listos: StateFlow<Int> =
        repo.observarCantidadPorEstado(EstadoPedido.LISTO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Suma de los pedidos no cancelados de hoy. */
    val ventasHoy: StateFlow<Double> =
        repo.observarVentasHoy(inicioDia)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    /** Cantidad de pedidos de hoy (para el "21 pedidos" de la tarjeta). */
    val pedidosHoy: StateFlow<Int> =
        repo.observarPedidosHoy(inicioDia)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /**
     * Grafico de barras "Pedidos por hora".
     * Agrupa los horarios de recojo de hoy en las 5 horas de la cafeteria.
     */
    val pedidosPorHora: StateFlow<Map<Int, Int>> =
        repo.observarHorasRecojoHoy(inicioDia)
            .map { horarios -> agruparPorHora(horarios) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** Los pedidos que aun no entraron a cocina: la lista "Nuevos pedidos". */
    val pedidosNuevos: StateFlow<List<PedidoConDetalle>> =
        repo.observarPedidosPorEstado(EstadoPedido.RECIBIDO)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Boton "Preparar" del panel: avanza el pedido un paso. */
    fun preparar(pedido: PedidoConDetalle) {
        viewModelScope.launch {
            repo.cambiarEstado(
                pedido.pedido.id,
                pedido.pedido.estadoTipado.siguiente()
            )
        }
    }

    companion object {
        /** Horas que muestra el grafico, como en el mockup. */
        val HORAS_GRAFICO = listOf(8, 9, 10, 11, 12)

        /** Convierte "HH:mm" en la hora y cuenta cuantos pedidos hay en cada una. */
        fun agruparPorHora(horarios: List<String>): Map<Int, Int> {
            val conteo = mutableMapOf<Int, Int>()
            HORAS_GRAFICO.forEach { conteo[it] = 0 }

            horarios.forEach { texto ->
                val hora = texto.substringBefore(':').trim().toIntOrNull() ?: return@forEach
                conteo[hora] = (conteo[hora] ?: 0) + 1
            }
            return conteo
        }
    }
}