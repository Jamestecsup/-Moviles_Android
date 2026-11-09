package com.tecsup.mibodega.ui.cliente.modelo

/**
 * Lo que queda registrado cuando el usuario confirma:
 * se guarda en ClienteApp y lo lee la Confirmación.
 */
data class PedidoConfirmado(
    val numero: String,
    val total: Double,
    val metodoPago: String,
    val direccion: String,
    val referencia: String
)
