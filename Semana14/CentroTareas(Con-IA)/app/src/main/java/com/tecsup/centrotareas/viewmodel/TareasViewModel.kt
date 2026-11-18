package com.tecsup.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.ComparacionTiempos
import com.tecsup.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (Mejora 1: Flow desde Repository) -----
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private var trabajo: Job? = null

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    fun tareaLarga() {
        if (_corriendo.value) return

        trabajo = viewModelScope.launch {
            try {
                _corriendo.value = true
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."

                repo.ejecutarTareaConProgreso().collect { avance ->
                    _progreso.value = avance
                }

                _mensaje.value = "Terminó"
            } catch (e: CancellationException) {
                _mensaje.value = "Cancelado"
                throw e
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos (Mejora 2: Progreso en tiempo real) -----
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private val _progresoPrimos = MutableStateFlow(0f)
    val progresoPrimos: StateFlow<Float> = _progresoPrimos.asStateFlow()

    private var trabajoPrimos: Job? = null

    fun contar(hasta: Int) {
        if (hasta < 2) return

        trabajoPrimos?.cancel()
        _primos.value = PrimosUi.Calculando
        _progresoPrimos.value = 0f

        trabajoPrimos = viewModelScope.launch {
            try {
                val inicio = System.currentTimeMillis()
                val cantidad = repo.contarPrimosConProgreso(hasta) { avance ->
                    _progresoPrimos.value = avance
                }
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
        _primos.value = PrimosUi.Cancelado
    }

    // ----- Dos cargas (Mejora 3: Reintento automático inteligente y comparación de tiempos) -----
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private val _comparacion = MutableStateFlow(ComparacionTiempos())
    val comparacion: StateFlow<ComparacionTiempos> = _comparacion.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun limpiarComparacion() {
        _comparacion.value = ComparacionTiempos()
    }

    fun cargarUnaPorUna() {
        ejecutarCarga(modo = "Una por una", enParalelo = false)
    }

    fun cargarALaVez() {
        ejecutarCarga(modo = "A la vez", enParalelo = true)
    }

    fun reintentarCarga() {
        cargarALaVez()
    }

    private fun ejecutarCarga(modo: String, enParalelo: Boolean) {
        if (_carga.value is CargaUi.Cargando) return

        _carga.value = CargaUi.Cargando
        val simular = _simularError.value

        viewModelScope.launch {
            try {
                val inicio = System.currentTimeMillis()
                val (perfil, pedidos) = cargarDatosConReintentos(enParalelo, simular)
                val ms = System.currentTimeMillis() - inicio
                _comparacion.update { it.registrar(enParalelo, ms) }
                _carga.value = CargaUi.Exito(modo, perfil, pedidos.size, ms)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message ?: "Sin conexión"}")
            }
        }
    }

    private suspend fun cargarDatosConReintentos(
        enParalelo: Boolean,
        simularError: Boolean
    ): Pair<String, List<String>> {
        var reintentos = 0
        while (true) {
            try {
                return if (enParalelo) {
                    coroutineScope {
                        val perfilDeferred = async { repo.cargarPerfil() }
                        val pedidosDeferred = async { repo.cargarPedidos(simularError) }
                        Pair(perfilDeferred.await(), pedidosDeferred.await())
                    }
                } else {
                    val perfil = repo.cargarPerfil()
                    val pedidos = repo.cargarPedidos(simularError)
                    Pair(perfil, pedidos)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (reintentos >= ESPERAS_REINTENTO_MS.size) throw e
                delay(ESPERAS_REINTENTO_MS[reintentos])
                reintentos++
            }
        }
    }

    private companion object {
        val ESPERAS_REINTENTO_MS = longArrayOf(500L, 1000L, 2000L)
    }
}
