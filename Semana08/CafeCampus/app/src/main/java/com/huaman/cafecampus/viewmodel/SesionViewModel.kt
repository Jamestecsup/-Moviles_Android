package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.prefs.PreferenciasStore
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Sesion de la app: login con selector de rol y perfil activo.
 *
 * Solo conoce el [CafeRepository] y el [PreferenciasStore]. No sabe nada de
 * Composables ni de Navigation.
 */
class SesionViewModel(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _rolSeleccionado = MutableStateFlow(Rol.CLIENTE)
    val rolSeleccionado: StateFlow<Rol> = _rolSeleccionado.asStateFlow()

    private val _modoOscuro = MutableStateFlow(false)
    val modoOscuro: StateFlow<Boolean> = _modoOscuro.asStateFlow()

    private val _recibirPedidos = MutableStateFlow(true)
    val recibirPedidos: StateFlow<Boolean> = _recibirPedidos.asStateFlow()

    private val _notificaciones = MutableStateFlow(true)
    val notificaciones: StateFlow<Boolean> = _notificaciones.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        // El tema y el interruptor de Ajustes leen de DataStore, por eso se
        // recogen aqui y se exponen como StateFlow.
        viewModelScope.launch {
            prefs.modoOscuro.collect { _modoOscuro.value = it }
        }
        viewModelScope.launch {
            prefs.recibirPedidos.collect { _recibirPedidos.value = it }
        }
        viewModelScope.launch {
            prefs.notificaciones.collect { _notificaciones.value = it }
        }
    }

    /** Elige el perfil del formulario de login. */
    fun elegirRol(rol: Rol) {
        _rolSeleccionado.value = rol
        _error.value = null
    }

    /**
     * Intenta iniciar sesion.
     *
     * - Campos vacios            -> "Campos vacios"
     * - Credenciales incorrectas -> "Correo o contrasena incorrectos"
     * - El rol del usuario no es el seleccionado -> mensaje especifico
     * - Exito -> guarda el perfil en DataStore
     */
    fun login(correo: String, clave: String, rol: Rol) {
        if (_cargando.value) return
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                if (correo.isBlank() || clave.isBlank()) {
                    _error.value = "Campos vacios"
                    return@launch
                }

                val usuario = repo.login(correo, clave)
                if (usuario == null) {
                    _error.value = "Correo o contrasena incorrectos"
                    return@launch
                }

                val rolDelUsuario = Rol.entries.firstOrNull { it.name == usuario.rol }
                    ?: Rol.CLIENTE

                if (rolDelUsuario != rol) {
                    _error.value = if (rolDelUsuario == Rol.ADMIN) {
                        "Ese correo es de administrador, no de estudiante"
                    } else {
                        "Ese correo es de estudiante, no de administrador"
                    }
                    return@launch
                }

                _usuarioActual.value = usuario
                _rolSeleccionado.value = rolDelUsuario
                prefs.guardarPerfil(rolDelUsuario)
            } finally {
                _cargando.value = false
            }
        }
    }

    /**
     * Lo llama el Splash: si DataStore tiene un perfil, se salta el login y
     * devuelve el Usuario para ir directo a su pantalla principal.
     * Devuelve null si no hay sesion guardada.
     */
    suspend fun restaurarSesion(): Usuario? {
        val nombre = prefs.perfilActivo.first()
        val rol = Rol.entries.firstOrNull { it.name == nombre } ?: return null

        val usuario = repo.usuarioDelRol(rol) ?: return null
        _usuarioActual.value = usuario
        _rolSeleccionado.value = rol
        return usuario
    }

    /** Cierra sesion: borra el perfil de DataStore y vuelve al login. */
    fun cerrarSesion() {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            _error.value = null
        }
    }

    /** Cambia de perfil en modo practica, sin volver a loguearse. */
    fun cambiarPerfil(rol: Rol) {
        viewModelScope.launch {
            val usuario = repo.usuarioDelRol(rol)
            if (usuario != null) {
                _usuarioActual.value = usuario
                _rolSeleccionado.value = rol
                prefs.guardarPerfil(rol)
            }
        }
    }

    fun guardarModoOscuro(oscuro: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(oscuro) }
    }

    fun guardarRecibirPedidos(acepta: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirPedidos(acepta) }
    }

    fun guardarNotificaciones(activa: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(activa) }
    }

    /** Limpia el error cuando el usuario vuelve a escribir. */
    fun limpiarError() {
        _error.value = null
    }
}