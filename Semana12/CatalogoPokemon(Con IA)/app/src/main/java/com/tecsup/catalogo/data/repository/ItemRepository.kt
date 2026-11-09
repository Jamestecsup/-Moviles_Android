package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    /** La lista sale siempre de Room. */
    val items: Flow<List<Item>>

    /** Solo los elementos marcados como favoritos. */
    val favoritos: Flow<List<Item>>

    /** Retrofit -> Room. Devuelve cuántos elementos guardó. */
    suspend fun refrescar(): Result<Int>

    suspend fun obtenerDetalle(id: String): Result<Item>

    suspend fun alternarFavorito(id: String)

    suspend fun cantidadGuardada(): Int

    suspend fun cantidadFavoritos(): Int

    suspend fun limpiarCache()
}
