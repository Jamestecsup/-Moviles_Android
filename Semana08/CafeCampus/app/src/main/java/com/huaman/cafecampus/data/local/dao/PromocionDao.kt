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

    @Query("SELECT * FROM promociones WHERE activo = 1 ORDER BY fechaInicio DESC")
    fun observarTodos(): Flow<List<Promocion>>

    @Query("SELECT * FROM promociones WHERE id = :id")
    suspend fun porId(id: Int): Promocion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(p: Promocion): Long

    @Update
    suspend fun actualizar(p: Promocion)

    @Delete
    suspend fun eliminar(p: Promocion)

    @Query("UPDATE promociones SET activo = 0 WHERE id = :id")
    suspend fun desactivar(id: Int)
}