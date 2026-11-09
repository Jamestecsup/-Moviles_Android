package com.tecsup.catalogo.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.notification.Notificador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class Totales(
    val guardados: Int = 0,
    val favoritos: Int = 0
)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _totales = MutableStateFlow(Totales())
    val totales: StateFlow<Totales> = _totales.asStateFlow()

    private val _trabajando = MutableStateFlow(false)
    val trabajando: StateFlow<Boolean> = _trabajando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        cargarTotales()
    }

    fun cargarTotales() {
        viewModelScope.launch {
            _totales.value = Totales(
                guardados = repo.cantidadGuardada(),
                favoritos = repo.cantidadFavoritos()
            )
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _trabajando.value = true

            repo.refrescar()
                .onSuccess { n ->
                    notificador.mostrar(
                        "Datos actualizados",
                        "Se guardaron $n elementos"
                    )
                    _mensaje.value = "Se guardaron $n elementos"
                }
                .onFailure { e ->
                    _mensaje.value = mensajeDe(e)
                }

            cargarTotales()
            _trabajando.value = false
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            _trabajando.value = true
            repo.limpiarCache()
            _totales.value = Totales()
            _mensaje.value = "Caché eliminada"
            _trabajando.value = false
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}
