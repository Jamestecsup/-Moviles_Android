package com.huaman.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.huaman.cafecampus.data.local.entity.DetallePedido
import com.huaman.cafecampus.data.local.entity.Pedido
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a `pedidos` y `detalle_pedido`.
 *
 * LECTURA -> Flow + `@Transaction` (porque devuelven PedidoConDetalle).
 * ESCRITURA -> suspend.
 */
@Dao
interface PedidoDao {

    // -----------------------------------------------------------------------
    // Escritura
    // -----------------------------------------------------------------------

    @Insert
    suspend fun insertarPedido(p: Pedido): Long

    @Insert
    suspend fun insertarDetalles(lista: List<DetallePedido>)

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: String)

    /**
     * Crea el pedido completo de una sola vez: inserta la cabecera, le pasa el
     * id generado a cada linea y despues inserta las lineas.
     *
     * Si algo falla a mitad, `@Transaction` revierte TODO: no quedan pedidos
     * sin lineas.
     */
    @Transaction
    suspend fun crearPedido(pedido: Pedido, detalles: List<DetallePedido>): Long {
        val id = insertarPedido(pedido)
        insertarDetalles(detalles.map { it.copy(pedidoId = id.toInt()) })
        return id
    }

    // -----------------------------------------------------------------------
    // Lectura (con @Transaction porque devuelven PedidoConDetalle)
    // -----------------------------------------------------------------------

    /** Historial del cliente, del mas reciente al mas antiguo. */
    @Transaction
    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId ORDER BY fecha DESC")
    fun observarPorCliente(clienteId: Int): Flow<List<PedidoConDetalle>>

    /** Bandeja del administrador: pedidos de un estado, mas recientes primero. */
    @Transaction
    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY fecha DESC")
    fun observarPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>>

    /** Un solo pedido, para la pantalla de seguimiento. */
    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    fun observarUno(id: Int): Flow<PedidoConDetalle?>

    /** Version puntual (no observable) del pedido: la usa el Repository al crear. */
    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun uno(id: Int): PedidoConDetalle?

    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun contar(): Int

    // -----------------------------------------------------------------------
    // Panel de control
    // -----------------------------------------------------------------------

    /**
     * Ventas desde [inicio] (inicio del dia en millis), sin contar cancelados.
     * `COALESCE(SUM(total), 0.0)` evita que devuelva null cuando no hay
     * pedidos: el panel siempre necesita un numero.
     */
    @Query(
        """
        SELECT COALESCE(SUM(total), 0.0) FROM pedidos
        WHERE fecha >= :inicio AND estado <> 'CANCELADO'
        """
    )
    fun ventasDesde(inicio: Long): Flow<Double>

    /** Cuantos pedidos hay en cada estado, para los contadores del panel. */
    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    fun cantidadPorEstado(estado: EstadoPedido): Flow<Int>

    /** Version puntual del conteo (para "nuevos pedidos" al abrir). */
    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    suspend fun cantidadPorEstadoUnaVez(estado: EstadoPedido): Int
}