package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.PostApi
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.EditarPost
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun eliminar(id: Int): Result<Unit>
    suspend fun editar(id: Int, nuevoTitulo: String): Result<Post>
}

class PostRepositoryRemote(
    private val api: PostApi
) : PostRepository {

    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: CancellationException) {
        throw e // nunca se traga la cancelación
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun listar(): Result<List<Post>> = llamar {
        api.listar().map { CampusContenido.adaptarPost(it) }
    }

    override suspend fun detalle(id: Int): Result<Post> = llamar {
        CampusContenido.adaptarPost(api.detalle(id))
    }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> = llamar {
        api.comentarios(id).map { CampusContenido.adaptarComentario(it) }
    }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> = llamar {
        api.porUsuario(userId).map { CampusContenido.adaptarPost(it) }
    }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = llamar {
        api.crear(nuevo)
    }

    override suspend fun eliminar(id: Int): Result<Unit> = try {
        val respuesta = api.eliminar(id)
        if (respuesta.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(respuesta))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun editar(id: Int, nuevoTitulo: String): Result<Post> = llamar {
        CampusContenido.adaptarPost(api.editar(id, EditarPost(title = nuevoTitulo)))
    }
}
