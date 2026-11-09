package com.tecsup.catalogo.data.repository

import com.tecsup.catalogo.data.local.ItemDao
import com.tecsup.catalogo.data.mapper.aEntity
import com.tecsup.catalogo.data.mapper.aModelo
import com.tecsup.catalogo.data.remote.ApiService
import com.tecsup.catalogo.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {

    override val items: Flow<List<Item>> =
        dao.todos().map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = try {
        // Los favoritos se leen antes del Upsert para no perderlos.
        val favoritos = dao.idsFavoritos().toSet()

        val nuevos = TIPOS.flatMap { tipo ->
            api.listar(tipo).pokemon.map { entrada -> entrada.pokemon.aEntity(tipo) }
        }
            .distinctBy { it.id }
            .map { entity -> entity.copy(esFavorito = entity.id in favoritos) }

        dao.guardarTodos(nuevos)
        Result.success(nuevos.size)
    } catch (e: IOException) {
        Result.failure(e) // sin internet
    } catch (e: HttpException) {
        Result.failure(e) // 404, 500...
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = try {
        val dto = api.detalle(id)
        val esFavorito = dao.esFavorito(dto.name) ?: false
        Result.success(dto.aModelo(esFavorito))
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: String) = dao.alternarFavorito(id)

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() = dao.borrarTodo()

    private companion object {
        /** Variantes del tema Pokémon asignadas en el laboratorio. */
        val TIPOS = listOf("fire", "water", "grass")
    }
}
