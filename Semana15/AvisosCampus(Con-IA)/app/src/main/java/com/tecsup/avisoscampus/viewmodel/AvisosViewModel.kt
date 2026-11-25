package com.tecsup.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.model.PermisoUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {
    val avisos = repo.avisos

    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos = _eventos.asSharedFlow()

    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso = _permiso.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan = _faltan.asStateFlow()

    private val _escuchandoAvion = MutableStateFlow(false)
    val escuchandoAvion = _escuchandoAvion.asStateFlow()

    private val _escuchandoCargador = MutableStateFlow(false)
    val escuchandoCargador = _escuchandoCargador.asStateFlow()

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
        if (concedido) _mensaje.value = null
    }

    fun avisarAhora() {
        viewModelScope.launch {
            registrarYEmitir("Tarea lista", "Tu entrega fue registrada", Origen.BOTON)
        }
    }

    private suspend fun registrarYEmitir(titulo: String, texto: String, origen: Origen) {
        val aviso = repo.registrar(titulo, texto, origen)
        // Al rotar puede haber un instante sin pantalla que reciba el evento.
        _eventos.subscriptionCount.first { it > 0 }
        _eventos.emit(aviso)
    }

    fun recordarEn(segundos: Int = 10) {
        if (segundos < 1 || trabajo != null) return
        trabajo = viewModelScope.launch {
            try {
                for (restantes in segundos downTo 1) {
                    _faltan.value = restantes
                    delay(1_000)
                }
                registrarYEmitir("Recordatorio", "Revisa tu laboratorio pendiente", Origen.RECORDATORIO)
            } finally {
                _faltan.value = null
                trabajo = null
            }
        }
    }

    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }

    fun cambiarEscucha(valor: Boolean) {
        _escuchandoAvion.value = valor
    }

    fun alCambiarModoAvion(activo: Boolean) {
        if (!_escuchandoAvion.value) return
        viewModelScope.launch {
            registrarYEmitir("Modo avión", if (activo) "Activado" else "Desactivado", Origen.MODO_AVION)
        }
    }

    fun cambiarEscuchaCargador(valor: Boolean) {
        _escuchandoCargador.value = valor
    }

    fun alCambiarCargador(conectado: Boolean) {
        if (!_escuchandoCargador.value) return
        viewModelScope.launch {
            registrarYEmitir("Cargador", if (conectado) "Conectado" else "Desconectado", Origen.CARGADOR)
        }
    }

    fun noSePudoMostrar() {
        _mensaje.value = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
    }
}
