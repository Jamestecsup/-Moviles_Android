package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.Categoria
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.repository.CafeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromocionesViewModel(private val repo: CafeRepository) : ViewModel() {

    /** Todas las promociones: activas y deshabilitadas. Las gestiona el admin. */
    val todas: StateFlow<List<Promocion>> =
        repo.observarTodasPromociones()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Solo las activas: es lo que aplica al estudiante. */
    val activas: StateFlow<List<Promocion>> =
        repo.observarPromociones()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Productos de la carta, Straight desde el Flow de Room.
     *
     * Por eso el selector del formulario se actualiza SOLO: si el
     * administrador crea un producto nuevo en "Menu", aparece aqui sin
     * tocar nada.
     */
    val productos: StateFlow<List<Producto>> =
        repo.observarProductos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Categorias disponibles para una promocion de categoria entera. */
    val categorias: List<String> = Categoria.paraFormulario

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Id de la promocion que se esta editando (0 = crear una nueva). */
    private val _enEdicion = MutableStateFlow<Promocion?>(null)
    val enEdicion: StateFlow<Promocion?> = _enEdicion.asStateFlow()

    fun editar(promocion: Promocion) {
        _enEdicion.value = promocion
    }

    fun cerrarEdicion() {
        _enEdicion.value = null
    }

    /**
     * Crea una promocion nueva o actualiza la que ya existe.
     *
     * @param id 0 para crear una nueva, o el id de la que se edita.
     */
    fun guardar(
        id: Int,
        tipo: String,
        valorReferencia: String,
        descuentoTexto: String,
        fechaFinMillis: Long,
        alGuardar: () -> Unit
    ) {
        viewModelScope.launch {
            if (valorReferencia.isBlank()) {
                _error.value = "Debe seleccionar producto o categoria"
                return@launch
            }

            val descuento = descuentoTexto.trim().replace(",", ".").toDoubleOrNull()
            if (descuento == null || descuento <= 0.0 || descuento > 100.0) {
                _error.value = "El descuento debe ser un numero entre 1 y 100"
                return@launch
            }

            val existente = if (id == 0) null else repo.promocionPorId(id)

            repo.guardarPromocion(
                Promocion(
                    id = id,
                    tipo = tipo,
                    valorReferencia = valorReferencia.trim(),
                    descuentoPorcentaje = descuento,
                    // al editar se conserva la fecha de inicio original
                    fechaInicio = existente?.fechaInicio ?: System.currentTimeMillis(),
                    fechaFin = fechaFinMillis,
                    // al editar se conserva si estaba activa o deshabilitada
                    activo = existente?.activo ?: true
                )
            )

            _error.value = null
            _enEdicion.value = null
            alGuardar()
        }
    }

    /** Elimina la promocion de la base. */
    fun eliminar(promocion: Promocion) {
        viewModelScope.launch { repo.eliminarPromocion(promocion) }
    }

    /** Deshabilita: sigue en la base pero deja de aplicarse. */
    fun desactivar(promocion: Promocion) {
        viewModelScope.launch { repo.desactivarPromocion(promocion.id) }
    }

    /** Vuelve a habilitar una promocion deshabilitada. */
    fun activar(promocion: Promocion) {
        viewModelScope.launch { repo.activarPromocion(promocion.id) }
    }

    fun limpiarError() {
        _error.value = null
    }
}