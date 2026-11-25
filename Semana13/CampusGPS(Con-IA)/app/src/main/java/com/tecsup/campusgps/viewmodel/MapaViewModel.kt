package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.campusgps.data.GpsDesactivadoException
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.Posicion
import com.tecsup.campusgps.model.TipoError
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // Mejora 1: Filtro por categoría
    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    val categorias: List<String> = listOf("Todas", "Estudio", "Comida", "Eventos", "Servicios")

    // Mejora 2: Radio de cercanía
    private val _radioCercania = MutableStateFlow<Float?>(null)
    val radioCercania: StateFlow<Float?> = _radioCercania.asStateFlow()

    val radiosDisponibles: List<Float?> = listOf(null, 100f, 200f, 500f)

    val lugaresFiltrados: StateFlow<List<LugarConDistancia>> = combine(
        _lugares,
        _categoriaSeleccionada
    ) { lista, cat ->
        if (cat == null || cat == "Todas") {
            lista
        } else {
            lista.filter { it.lugar.categoria.equals(cat, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    val masCercano: StateFlow<Lugar?> = lugaresFiltrados.map { lista ->
        lista.firstOrNull { it.metros != null }?.lugar
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun seleccionarCategoria(categoria: String?) {
        _categoriaSeleccionada.value = if (categoria == "Todas") null else categoria
    }

    fun seleccionarRadio(radio: Float?) {
        _radioCercania.value = radio
    }

    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                mensaje = "Este dispositivo no tiene Google Play Services.",
                tipo = TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it)
                }
                .onFailure {
                    _estado.value = UbicacionUi.Error(
                        mensaje = mensajeDe(it),
                        tipo = tipoErrorDe(it)
                    )
                }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        val lugaresActuales = lugaresRepo.obtenerLugares()
        val listaConDistancias = lugaresActuales.map { lugar ->
            val metros = distanciaMetros(pos, lugar)
            LugarConDistancia(lugar, metros)
        }.sortedBy { it.metros }
        _lugares.value = listaConDistancias
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            mensaje = "Sin permiso de ubicación no puedo mostrar dónde estás",
            tipo = TipoError.SIN_PERMISO
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    private fun tipoErrorDe(e: Throwable): TipoError = when (e) {
        is SecurityException -> TipoError.SIN_PERMISO
        is GpsDesactivadoException -> TipoError.GPS_APAGADO
        else -> TipoError.OTRO
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is SecurityException -> "Sin permiso de ubicación no puedo mostrar dónde estás"
        is GpsDesactivadoException -> e.message ?: "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo"
        else -> e.message ?: "No se pudo obtener la ubicación"
    }
}
