package com.tecsup.mibodega.ui.cliente.modelo

/**
 * La cuenta que se muestra en Perfil: sale del registro
 * o del teléfono con el que se ingresó.
 */
data class DatosCuenta(
    val nombre: String,
    val telefono: String,
    val direccion: String
)
