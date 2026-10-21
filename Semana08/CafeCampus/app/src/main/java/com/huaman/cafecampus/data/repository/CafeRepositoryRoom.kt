package com.huaman.cafecampus.data.repository

import com.huaman.cafecampus.data.local.CafeDatabase
import com.huaman.cafecampus.data.local.dao.PedidoDao
import com.huaman.cafecampus.data.local.dao.PromocionDao
import com.huaman.cafecampus.data.local.dao.ProductoDao
import com.huaman.cafecampus.data.local.dao.UsuarioDao
import com.huaman.cafecampus.data.local.entity.DetallePedido
import com.huaman.cafecampus.data.local.entity.Pedido
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.model.ItemCarrito
import com.huaman.cafecampus.model.Rol
import kotlinx.coroutines.flow.Flow

/**
 * Implementacion LOCAL de [CafeRepository] sobre SQLite (Room).
 *
 * Esta clase es la UNICA que conoce los DAOs. Ni los ViewModels ni las
 * pantallas saben que la base es SQLite: por eso mas adelante se puede cambiar
 * por un servicio web sin tocar nada mas.
 *
 * - lo que debe ACTUALIZAR la pantalla -> Flow (sin `suspend`)
 * - lo que cambia datos UNA sola vez  -> `suspend`
 */
class CafeRepositoryRoom(
    private val productoDao: ProductoDao,
    private val usuarioDao: UsuarioDao,
    private val pedidoDao: PedidoDao,
    private val promocionDao: PromocionDao
) : CafeRepository {

    constructor(db: CafeDatabase) : this(
        db.productoDao(),
        db.usuarioDao(),
        db.pedidoDao(),
        db.promocionDao()
    )

    // -----------------------------------------------------------------------
    // Usuarios
    // -----------------------------------------------------------------------

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.login(correo.trim(), clave)

    override suspend fun usuarioDelRol(rol: Rol): Usuario? =
        usuarioDao.porRol(rol.name)

    // -----------------------------------------------------------------------
    // Productos
    // -----------------------------------------------------------------------

    override fun observarProductos(): Flow<List<Producto>> =
        productoDao.observarTodos()

    override suspend fun guardarProducto(p: Producto) {
        if (p.id == 0) productoDao.insertar(p) else productoDao.actualizar(p)
    }

    override suspend fun eliminarProducto(p: Producto) {
        productoDao.eliminar(p)
    }

    override suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean) {
        productoDao.cambiarDisponibilidad(id, disponible)
    }

    // -----------------------------------------------------------------------
    // Pedidos
    // -----------------------------------------------------------------------

    /**
     * Convierte el carrito en lineas `DetallePedido`.
     *
     * IMPORTANTE: se COPIA el nombre y el PRECIO FINAL (con la promocion ya
     * aplicada) en la linea. Es una foto del momento: si manana el administrador
     * cambia el precio, el precio o la promocion, este pedido no se altera ni
     * se rompe.
     */
    override suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long {
        val detalles: List<DetallePedido> = items.map { item ->
            DetallePedido(
                pedidoId = 0, // lo asigna crearPedido() dentro de la transaccion
                productoId = item.producto.id,
                nombreProducto = item.producto.nombre,
                cantidad = item.cantidad,
                precioUnitario = item.precioUnitario
            )
        }

        val total = detalles.sumOf { it.subtotal }

        val pedido = Pedido(
            clienteId = clienteId,
            estado = EstadoPedido.RECIBIDO.name,
            horaRecojo = horaRecojo,
            pago = pago,
            nota = nota,
            total = total,
            fecha = System.currentTimeMillis()
        )

        return pedidoDao.crearPedido(pedido, detalles)
    }

    override fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorCliente(clienteId)

    override fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorEstado(estado)

    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> =
        pedidoDao.observarUno(id)

    /** El estado se guarda SIEMPRE como `EstadoPedido.name` (String). */
    override suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido) {
        pedidoDao.cambiarEstado(pedidoId, estado.name)
    }

    // -----------------------------------------------------------------------
    // Panel de control
    // -----------------------------------------------------------------------

    override fun observarVentasHoy(inicioDia: Long): Flow<Double> =
        pedidoDao.ventasDesde(inicioDia)

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> =
        pedidoDao.cantidadPorEstado(estado)

    override fun observarPedidosHoy(inicioDia: Long): Flow<Int> =
        pedidoDao.pedidosDesde(inicioDia)

    override fun observarHorasRecojoHoy(inicioDia: Long): Flow<List<String>> =
        pedidoDao.horasRecojoDesde(inicioDia)

    // -----------------------------------------------------------------------
    // Promociones
    // -----------------------------------------------------------------------

    override fun observarPromociones(): Flow<List<Promocion>> =
        promocionDao.observarActivas()

    override fun observarTodasPromociones(): Flow<List<Promocion>> =
        promocionDao.observarTodas()

    override suspend fun promocionPorId(id: Int): Promocion? =
        promocionDao.porId(id)

    override suspend fun guardarPromocion(p: Promocion): Unit {
        promocionDao.insertar(p)
    }

    override suspend fun eliminarPromocion(p: Promocion) =
        promocionDao.eliminar(p)

    override suspend fun desactivarPromocion(id: Int) =
        promocionDao.desactivar(id)

    override suspend fun activarPromocion(id: Int) =
        promocionDao.activar(id)
}