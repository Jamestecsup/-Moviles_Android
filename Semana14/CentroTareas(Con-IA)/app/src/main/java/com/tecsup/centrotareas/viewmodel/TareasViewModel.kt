package com.tecsup.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

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

    // ----- Dos cargas (Mejora 3: Tiempo máximo y reintento automático) -----
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return

        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            try {
                val inicio = System.currentTimeMillis()
                val perfil = repo.cargarPerfil()
                val pedidos = repo.cargarPedidos(_simularError.value)
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, ms)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message ?: "Sin conexión"}")
            }
        }
    }

    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return

        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            try {
                val inicio = System.currentTimeMillis()
                val (perfil, pedidos) = withTimeout(4000L) {
                    coroutineScope {
                        val p = async { repo.cargarPerfil() }
                        val o = async { repo.cargarPedidos(_simularError.value) }
                        Pair(p.await(), o.await())
                    }
                }
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, ms)
            } catch (e: TimeoutCancellationException) {
                _carga.value = CargaUi.Error("Tardó demasiado tiempo (Tiempo límite de 4s excedido)")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message ?: "Sin conexión"}")
            }
        }
    }

    // Mejora 3: Reintento automático inteligente con backoff progresivo (hasta 3 intentos)
    fun cargarConReintentoAutomatico(intentosMaximos: Int = 3) {
        if (_carga.value is CargaUi.Cargando) return

        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            var intentoActual = 1
            var ultimoError: Throwable? = null
            val inicio = System.currentTimeMillis()

            while (intentoActual <= intentosMaximos) {
                try {
                    val (perfil, pedidos) = withTimeout(4000L) {
                        coroutineScope {
                            val p = async { repo.cargarPerfil() }
                            val o = async { repo.cargarPedidos(_simularError.value) }
                            Pair(p.await(), o.await())
                        }
                    }
                    val ms = System.currentTimeMillis() - inicio
                    val detalleModo = if (intentoActual == 1) "A la vez" else "A la vez (Reintento #$intentoActual)"
                    _carga.value = CargaUi.Exito(detalleModo, perfil, pedidos.size, ms)
                    return@launch
                } catch (e: TimeoutCancellationException) {
                    ultimoError = e
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ultimoError = e
                }

                if (intentoActual < intentosMaximos) {
                    delay(intentoActual * 600L) // Backoff progresivo
                }
                intentoActual++
            }

            val mensajeFalla = if (ultimoError is TimeoutCancellationException) {
                "Tardó demasiado tiempo en los $intentosMaximos intentos"
            } else {
                "No se pudo cargar tras $intentosMaximos intentos: ${ultimoError?.message ?: "Sin conexión"}"
            }
            _carga.value = CargaUi.Error(mensajeFalla)
        }
    }

    fun reintentarCarga() {
        cargarConReintentoAutomatico()
    }
}
