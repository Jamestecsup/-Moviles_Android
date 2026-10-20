package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.data.repository.CafeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PromocionesViewModel(private val repo: CafeRepository) : ViewModel() {

    val promociones: StateFlow<List<Promocion>> =
        repo.observarPromociones()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun guardar(
        tipo: String,
        valorReferencia: String,
        descuentoPorcentaje: Double,
        fechaFinMillis: Long,
        alGuardar: () -> Unit
    ) {
        viewModelScope.launch {
            if (valorReferencia.isBlank()) {
                _error.value = "Debe seleccionar producto o categoria"
                return@launch
            }
            if (descuentoPorcentaje <= 0.0 || descuentoPorcentaje > 100.0) {
                _error.value = "El descuento debe ser entre 1 y 100"
                return@launch
            }

            repo.guardarPromocion(
                Promocion(
                    tipo = tipo,
                    valorReferencia = valorReferencia.trim(),
                    descuentoPorcentaje = descuentoPorcentaje,
                    fechaFin = fechaFinMillis,
                    activo = true
                )
            )
            _error.value = null
            alGuardar()
        }
    }

    fun eliminar(promocion: Promocion) {
        viewModelScope.launch { repo.eliminarPromocion(promocion) }
    }

    fun desactivar(id: Int) {
        viewModelScope.launch { repo.desactivarPromocion(id) }
    }
}