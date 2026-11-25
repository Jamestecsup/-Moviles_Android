package com.tecsup.catalogo.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    /** Texto del buscador. Solo filtra la vista, no modifica Room. */
    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    /** Última lista completa recibida; se conserva para poder limpiar el filtro. */
    private var listaCompleta: List<Item> = emptyList()

    private var refrescoTerminado = false

    init {
        viewModelScope.launch {
            repo.items.collect { lista ->
                if (lista.isNotEmpty() || refrescoTerminado) {
                    publicar(lista)
                }
            }
        }
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            val resultado = repo.refrescar()
            refrescoTerminado = true

            resultado.onSuccess { n ->
                if (_estado.value is UiState.Cargando) {
                    publicar(emptyList())
                }
                _aviso.value = null
                notificador.mostrar(
                    "Datos actualizados",
                    "Se guardaron $n elementos"
                )
            }.onFailure { e ->
                if (repo.cantidadGuardada() == 0) {
                    _estado.value = UiState.Error(mensajeDe(e))
                } else {
                    _aviso.value =
                        "Sin conexión: se muestran los datos guardados en el teléfono"
                }
            }
        }
    }

    /** Aplica el texto del buscador sobre la lista que ya está guardada. */
    fun buscar(texto: String) {
        _consulta.value = texto
        val actual = _estado.value
        if (actual is UiState.Exito) {
            _estado.value = UiState.Exito(filtrar(listaCompleta, texto))
        }
    }

    fun limpiarBusqueda() = buscar("")

    fun alternarFavorito(id: String) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    fun limpiarAviso() {
        _aviso.value = null
    }

    private fun publicar(lista: List<Item>) {
        listaCompleta = lista
        _estado.value = UiState.Exito(filtrar(lista, _consulta.value))
    }

    /**
     * Busca por nombre o por tipo. En la lista que sale de Room el tipo está en
     * el subtítulo; en el detalle viaja además dentro de [Item.tipos].
     */
    private fun filtrar(lista: List<Item>, texto: String): List<Item> {
        val consulta = texto.trim().lowercase()
        if (consulta.isEmpty()) return lista

        return lista.filter { item ->
            item.titulo.lowercase().contains(consulta) ||
                item.subtitulo.lowercase().contains(consulta) ||
                item.tipos.any { tipo -> tipo.lowercase().contains(consulta) }
        }
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException ->
            "No hay conexión a internet. Revisa tu red e inténtalo de nuevo."
        is HttpException ->
            "El servidor respondió con error ${e.code()}. Inténtalo más tarde."
        else ->
            "Ocurrió un error inesperado. Vuelve a intentarlo."
    }
}
