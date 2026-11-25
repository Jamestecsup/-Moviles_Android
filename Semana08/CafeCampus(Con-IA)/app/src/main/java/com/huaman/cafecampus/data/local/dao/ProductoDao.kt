package com.huaman.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huaman.cafecampus.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a la tabla `productos`. Es el ejemplo resuelto del laboratorio:
 *
 *   LECTURA  -> Flow   (la pantalla se actualiza sola)
 *   ESCRITURA -> suspend (cambia datos una sola vez)
 *
 * No se reemplazan, se complementan.
 */
@Dao
interface ProductoDao {

    /** Lectura: el Flow emite de nuevo cada vez que la tabla cambia. */
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodos(): Flow<List<Producto>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun porId(id: Int): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(p: Producto): Long

    @Update
    suspend fun actualizar(p: Producto)

    @Delete
    suspend fun eliminar(p: Producto)

    /** El administrador marca un producto como agotado / disponible. */
    @Query("UPDATE productos SET disponible = :d WHERE id = :id")
    suspend fun cambiarDisponibilidad(id: Int, d: Boolean)
}