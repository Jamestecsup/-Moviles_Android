package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Bandeja de pedidos del ADMINISTRADOR: los que aun no entraron a cocina
 * (mockup 3).
 *
 * El chip seleccionado decide que pedidos se ven. `flatMapLatest` cambia el
 * Flowunderlying cuando cambia el chip, sin recrear la pantalla.
 *
 * La regla de avanzar estados:
 *
 *     RECIBIDO -> PREPARANDO -> LISTO -> ENTREGADO
 *
 * vive en [EstadoPedido.siguiente] y aqui, NUNCA dentro de un Composable.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PedidosAdminViewModel(
    private val repo: CafeRepository
) : ViewModel() {

    private val _estadoSeleccionado = MutableStateFlow(EstadoPedido.RECIBIDO)
    val estadoSeleccionado: StateFlow<EstadoPedido> = _estadoSeleccionado.asStateFlow()

    /** Los chips de la pantalla. */
    val estadosVisibles = listOf(
        EstadoPedido.RECIBIDO,
        EstadoPedido.PREPARANDO,
        EstadoPedido.LISTO
    )

    /** Cambia de chip: la lista se recalcula sola. */
    val pedidos: StateFlow<List<PedidoConDetalle>> =
        _estadoSeleccionado.flatMapLatest { estado ->
            repo.observarPedidosPorEstado(estado)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun seleccionar(estado: EstadoPedido) {
        _estadoSeleccionado.value = estado
    }

    /** Avanza un paso: Recibido -> Preparando -> Listo -> Entregado. */
    fun avanzar(pedido: PedidoConDetalle) {
        viewModelScope.launch {
            repo.cambiarEstado(pedido.pedido.id, pedido.pedido.estadoTipado.siguiente())
        }
    }

    /** Cancela el pedido. */
    fun cancelar(pedido: PedidoConDetalle) {
        viewModelScope.launch {
            repo.cambiarEstado(pedido.pedido.id, EstadoPedido.CANCELADO)
        }
    }

    /** Texto del boton principal segun el estado (logica, no UI). */
    fun textoAvanzar(estado: EstadoPedido): String = when (estado) {
        EstadoPedido.RECIBIDO -> "Pasar a Preparando"
        EstadoPedido.PREPARANDO -> "Pasar a Listo"
        EstadoPedido.LISTO -> "Pasar a Entregado"
        else -> "Avanzar"
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    /** Un pedido abierto en detalle (0 = ninguno). */
    private val _pedidoAbierto = MutableStateFlow(0)
    val pedidoAbierto: StateFlow<PedidoConDetalle?> =
        _pedidoAbierto.flatMapLatest { id ->
            if (id == 0) flowOf(null) else repo.observarPedido(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun verPedido(id: Int) {
        _pedidoAbierto.value = id
    }
}