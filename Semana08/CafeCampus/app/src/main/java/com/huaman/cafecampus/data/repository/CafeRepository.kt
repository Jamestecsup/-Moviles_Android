package com.huaman.cafecampus.data.repository

import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.data.local.relation.PedidoConDetalle
import com.huaman.cafecampus.model.EstadoPedido
import com.huaman.cafecampus.model.ItemCarrito
import com.huaman.cafecampus.model.Rol
import kotlinx.coroutines.flow.Flow

/**
 * CONTRATO de acceso a datos de CafeCampus.
 *
 * La app solo conoce esta interface, nunca el DAO ni SQLite. Gracias a esto,
 * mas adelante el Repository se puede conectar a un servicio web (Semana 9) y
 * luego a la nube, SIN cambiar ViewModels ni pantallas.
 *
 * Implementacion local: [CafeRepositoryRoom] (SQLite / cafecampus.db).
 *
 * REGLA DE ORO
 *  - lo que debe ACTUALIZAR la pantalla -> Flow (sin `suspend`)
 *  - lo que cambia datos UNA sola vez  -> `suspend`
 *  No se reemplazan, se complementan.
 */
interface CafeRepository {

    // -----------------------------------------------------------------------
    // Usuarios
    // -----------------------------------------------------------------------

    /** Login. Devuelve el Usuario o null si el correo/clave no coinciden. */
    suspend fun login(correo: String, clave: String): Usuario?

    /**
     * Recupera el usuario de un rol. Lo usa el Splash para reconstruir la
     * sesion: DataStore solo recuerda el ROL, no el id del usuario.
     */
    suspend fun usuarioDelRol(rol: Rol): Usuario?

    // -----------------------------------------------------------------------
    // Productos
    // -----------------------------------------------------------------------

    /** Lectura que actualiza la pantalla -> Flow (sin `suspend`). */
    fun observarProductos(): Flow<List<Producto>>

    /** Inserta si es nuevo (id == 0) o actualiza si ya existe. */
    suspend fun guardarProducto(p: Producto)

    suspend fun eliminarProducto(p: Producto)

    suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean)

    // -----------------------------------------------------------------------
    // Pedidos
    // -----------------------------------------------------------------------

    /**
     * Crea un pedido a partir del carrito: convierte cada [ItemCarrito] en una
     * linea `DetallePedido` copiando nombre y precio del producto, calcula el
     * total y llama al DAO transaccional.
     *
     * @return id del pedido creado.
     */
    suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long

    /** Historial del estudiante. */
    fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>>

    /** Bandeja del administrador, filtrada por estado. */
    fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>>

    /** Un pedido concreto, para la pantalla de seguimiento. */
    fun observarPedido(id: Int): Flow<PedidoConDetalle?>

    /** El administrador avanza (o cancela) el estado de un pedido. */
    suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido)

    // -----------------------------------------------------------------------
    // Panel de control
    // -----------------------------------------------------------------------

    /** Suma de los pedidos no cancelados desde el inicio del dia. */
    fun observarVentasHoy(inicioDia: Long): Flow<Double>

    /** Cuantos pedidos hay en un estado (contadores del panel). */
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>

    /** Cuantos pedidos no cancelados se hicieron desde [inicioDia]. */
    fun observarPedidosHoy(inicioDia: Long): Flow<Int>

    /** Horarios de recojo de hoy, para el grafico de barras del panel. */
    fun observarHorasRecojoHoy(inicioDia: Long): Flow<List<String>>
}