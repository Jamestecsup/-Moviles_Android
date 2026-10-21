package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.data.prefs.PreferenciasStore
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Flujo de pedido del ESTUDIANTE: confirmar, seguir el estado y ver el
 * historial.
 *
 * El carrito vive en [MenuViewModel] (es el unico que lo posee); esta clase lo
 * usa para leer sus items y para vaciarlo al confirmar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PedidosClienteViewModel(
    private val repo: CafeRepository,
    private val menu: MenuViewModel,
    private val prefs: PreferenciasStore
) : ViewModel() {

    /**
     * Si la cafeteria acepta pedidos. Lo decide el administrador en Ajustes y
     * se guarda en DataStore, asi que el cliente lo ve al instante.
     */
    private val _recibirPedidos = MutableStateFlow(true)
    val recibirPedidos: StateFlow<Boolean> = _recibirPedidos.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.recibirPedidos.collect { _recibirPedidos.value = it }
        }
    }

    /** Id del cliente conectado. Lo fija la pantalla al entrar. */
    private val _clienteId = MutableStateFlow(0)
    val clienteId: StateFlow<Int> = _clienteId.asStateFlow()

    /**
     * Historial del estudiante. Cambia SOLO cuando la base cambia, porque es
     * un Flow de Room.
     */
    val misPedidos: StateFlow<List<PedidoConDetalle>> =
        _clienteId.flatMapLatest { id ->
            if (id == 0) flowOf(emptyList()) else repo.observarPedidosCliente(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Pedidos que siguen "en la cafeteria": RECIBIDO, PREPARANDO, LISTO. */
    val pedidosActivos: StateFlow<List<PedidoConDetalle>> =
        misPedidos.map { lista -> lista.filter { it.pedido.estadoTipado.activo } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Entregados y cancelados. */
    val pedidosHistoricos: StateFlow<List<PedidoConDetalle>> =
        misPedidos.map { lista -> lista.filter { it.pedido.estadoTipado.historico } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Pedido abierto en la pantalla de seguimiento (0 = ninguno). */
    private val _pedidoAbierto = MutableStateFlow(0)

    /** Un solo pedido, observado: la pantalla de seguimiento. */
    val pedido: StateFlow<PedidoConDetalle?> =
        _pedidoAbierto.flatMapLatest { id ->
            if (id == 0) flowOf(null) else repo.observarPedido(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargar(clienteId: Int) {
        _clienteId.value = clienteId
    }

    /** Abre un pedido en la pantalla de seguimiento. */
    fun verPedido(id: Int) {
        _pedidoAbierto.value = id
    }

    /**
     * Confirma el pedido:
     *  1. lo crea en Room con estado RECIBIDO,
     *  2. vacia el carrito,
     *  3. avisa con el id nuevo para navegar a su seguimiento.
     */
    fun confirmar(
        horaRecojo: String,
        pago: String,
        nota: String,
        onCreado: (Int) -> Unit
    ) {
        val cliente = _clienteId.value
        if (cliente == 0) {
            _mensaje.value = "Sesion no iniciada"
            return
        }

        // El administrador puede cerrar la cafeteria desde Ajustes.
        if (!_recibirPedidos.value) {
            _mensaje.value = "La cafeteria esta cerrada"
            return
        }

        val items = menu.carrito.value
        if (items.isEmpty()) {
            _mensaje.value = "El pedido esta vacio"
            return
        }

        viewModelScope.launch {
            val id = repo.crearPedido(
                clienteId = cliente,
                items = items,
                horaRecojo = horaRecojo,
                pago = pago,
                nota = nota
            )
            menu.vaciar()
            _mensaje.value = null
            _pedidoAbierto.value = id.toInt()
            onCreado(id.toInt())
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
