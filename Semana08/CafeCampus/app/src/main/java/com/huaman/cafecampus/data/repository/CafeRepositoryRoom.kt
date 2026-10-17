package com.huaman.cafecampus.data.repository

import com.huaman.cafecampus.data.local.CafeDatabase
import com.huaman.cafecampus.data.local.dao.ProductoDao
import com.huaman.cafecampus.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Implementacion LOCAL de [CafeRepository] sobre SQLite (Room).
 *
 * Esta clase es la UNICA que conoce los DAOs. Ni los ViewModels ni las
 * pantallas saben que la base es SQLite: por eso mas adelante se puede cambiar
 * por un servicio web sin tocar nada mas.
 *
 * Regla de oro:
 *   - lo que debe ACTUALIZAR la pantalla -> Flow (no `suspend`)
 *   - lo que cambia datos UNA sola vez  -> `suspend`
 */
class CafeRepositoryRoom(
    private val productoDao: ProductoDao
) : CafeRepository {

    constructor(db: CafeDatabase) : this(db.productoDao())

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

    // TODO (Paso 4): implementar Usuarios, Pedidos y Panel.
}