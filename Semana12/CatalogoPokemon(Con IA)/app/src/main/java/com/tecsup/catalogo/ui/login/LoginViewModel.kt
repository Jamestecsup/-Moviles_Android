package com.tecsup.catalogo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.catalogo.data.auth.AuthRepository
import com.tecsup.catalogo.domain.model.Rol
import com.tecsup.catalogo.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            _sesion.value = runCatching { repo.sesionActual() }.getOrNull()
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Ingresa tu correo y tu contraseña"
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            repo.iniciarSesion(correo.trim(), clave)
                .onSuccess { _sesion.value = it }
                .onFailure { _error.value = mensajeDe(it) }

            _cargando.value = false
        }
    }

    fun cerrarSesion() {
        repo.cerrarSesion()
        _sesion.value = null
    }

    fun limpiarError() {
        _error.value = null
    }

    private fun mensajeDe(e: Throwable): String {
        val texto = e.localizedMessage ?: ""
        return when {
            texto.contains("password", ignoreCase = true) ||
                texto.contains("credential", ignoreCase = true) ||
                texto.contains("INVALID_LOGIN", ignoreCase = true) ->
                "Correo o contraseña incorrectos"
            texto.contains("network", ignoreCase = true) ->
                "No hay conexión a internet"
            texto.isBlank() -> "No se pudo iniciar sesión. Revisa tu conexión."
            else -> texto
        }
    }
}
