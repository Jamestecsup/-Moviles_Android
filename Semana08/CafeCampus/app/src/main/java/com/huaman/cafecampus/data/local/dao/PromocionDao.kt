package com.huaman.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huaman.cafecampus.data.local.entity.Promocion
import kotlinx.coroutines.flow.Flow

@Dao
interface PromocionDao {

    /** Solo las activas: es lo que ve el estudiante. */
    @Query("SELECT * FROM promociones WHERE activo = 1 ORDER BY fechaInicio DESC")
    fun observarActivas(): Flow<List<Promocion>>

    /** Todas, activas y deshabilitadas: es lo que gestiona el administrador. */
    @Query("SELECT * FROM promociones ORDER BY activo DESC, fechaInicio DESC")
    fun observarTodas(): Flow<List<Promocion>>

    @Query("SELECT * FROM promociones WHERE id = :id")
    suspend fun porId(id: Int): Promocion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(p: Promocion): Long

    @Update
    suspend fun actualizar(p: Promocion)

    @Delete
    suspend fun eliminar(p: Promocion)

    /** Deshabilita la promocion: sigue en la base pero deja de aplicarse. */
    @Query("UPDATE promociones SET activo = 0 WHERE id = :id")
    suspend fun desactivar(id: Int)

    /** Vuelve a habilitar una promocion. */
    @Query("UPDATE promociones SET activo = 1 WHERE id = :id")
    suspend fun activar(id: Int)
}