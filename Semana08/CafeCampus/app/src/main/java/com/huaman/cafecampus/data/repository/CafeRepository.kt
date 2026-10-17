package com.huaman.cafecampus.data.repository

import com.huaman.cafecampus.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

/**
 * CONTRATO de acceso a datos de CafeCampus.
 *
 * La app solo conoce esta interface, nunca el DAO ni SQLite. Gracias a esto,
 * mas adelante el Repository se puede conectar a un servicio web (Semana 9) y
 * luego a la nube, SIN cambiar ViewModels ni pantallas.
 *
 * Implementacion local: [CafeRepositoryRoom] (SQLite / cafecampus.db).
 */
interface CafeRepository {

    // -----------------------------------------------------------------------
    // Productos (ejemplo resuelto que trae el proyecto base)
    // -----------------------------------------------------------------------

    /** Lectura que actualiza la pantalla -> Flow (sin `suspend`). */
    fun observarProductos(): Flow<List<Producto>>

    suspend fun guardarProducto(p: Producto)

    suspend fun eliminarProducto(p: Producto)

    suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean)

    // TODO (Paso 4): agregar Usuarios, Pedidos y Panel.

    // -----------------------------------------------------------------------
    // Usuarios - se completa en el Paso 4
    // -----------------------------------------------------------------------
    // suspend fun login(correo: String, clave: String): Usuario?

    // -----------------------------------------------------------------------
    // Pedidos - se completa en el Paso 4
    // -----------------------------------------------------------------------
    // suspend fun crearPedido(clienteId: Int, items: List<ItemCarrito>,
    //                          horaRecojo: String, pago: String, nota: String): Long
    // fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>>
    // fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>>
    // fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    // suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido)

    // -----------------------------------------------------------------------
    // Panel de control - se completa en el Paso 4
    // -----------------------------------------------------------------------
    // fun observarVentasHoy(inicioDia: Long): Flow<Double>
    // fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
}