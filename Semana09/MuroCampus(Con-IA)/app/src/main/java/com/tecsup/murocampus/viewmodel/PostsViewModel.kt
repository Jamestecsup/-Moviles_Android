package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(
    private val repo: PostRepository
) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _crearEstado = MutableStateFlow<UiState<Post>?>(null)
    val crearEstado: StateFlow<UiState<Post>?> = _crearEstado.asStateFlow()

    private val _eliminarEstado = MutableStateFlow<UiState<Unit>?>(null)
    val eliminarEstado: StateFlow<UiState<Unit>?> = _eliminarEstado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        filtrar(_usuarioFiltro.value)
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val resultado = if (userId == null) {
                repo.listar()
            } else {
                repo.porUsuario(userId)
            }
            resultado
                .onSuccess { posts ->
                    _lista.value = UiState.Exito(posts)
                }
                .onFailure { error ->
                    _lista.value = UiState.Error(mensajeDe(error))
                }
        }
    }

    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            try {
                coroutineScope {
                    val postDeferred = async { repo.detalle(id) }
                    val comentariosDeferred = async { repo.comentarios(id) }

                    val postResult = postDeferred.await()
                    val comentariosResult = comentariosDeferred.await()

                    if (postResult.isSuccess && comentariosResult.isSuccess) {
                        _detalle.value = UiState.Exito(
                            DetalleUi(
                                post = postResult.getOrThrow(),
                                comentarios = comentariosResult.getOrThrow()
                            )
                        )
                    } else {
                        val error = postResult.exceptionOrNull()
                            ?: comentariosResult.exceptionOrNull()
                            ?: Exception("Error desconocido")
                        _detalle.value = UiState.Error(mensajeDe(error))
                    }
                }
            } catch (e: Exception) {
                _detalle.value = UiState.Error(mensajeDe(e))
            }
        }
    }

    fun publicar(titulo: String, texto: String) {
        if (titulo.isBlank() || texto.isBlank()) return
        viewModelScope.launch {
            _crearEstado.value = UiState.Cargando
            val nuevo = NuevoPost(
                userId = 1,
                title = titulo.trim(),
                body = texto.trim()
            )
            repo.crear(nuevo)
                .onSuccess { creado ->
                    _crearEstado.value = UiState.Exito(creado)
                }
                .onFailure { error ->
                    _crearEstado.value = UiState.Error(mensajeDe(error))
                }
        }
    }

    fun limpiarCrearEstado() {
        _crearEstado.value = null
    }

    fun eliminar(id: Int, onExito: () -> Unit) {
        viewModelScope.launch {
            _eliminarEstado.value = UiState.Cargando
            repo.eliminar(id)
                .onSuccess {
                    _eliminarEstado.value = UiState.Exito(Unit)
                    // Eliminar la publicación en memoria local
                    val estadoActual = _lista.value
                    if (estadoActual is UiState.Exito) {
                        _lista.value = UiState.Exito(estadoActual.datos.filter { it.id != id })
                    }
                    onExito()
                }
                .onFailure { error ->
                    _eliminarEstado.value = UiState.Error(mensajeDe(error))
                }
        }
    }

    fun limpiarEliminarEstado() {
        _eliminarEstado.value = null
    }
}
