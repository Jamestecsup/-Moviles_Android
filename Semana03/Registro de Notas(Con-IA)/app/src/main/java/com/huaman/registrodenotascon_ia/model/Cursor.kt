package com.huaman.registrodenotascon_ia.model

abstract class Curso(val nombre: String, val peso: Double, val pesoTexto: String) {
    var nota: Int = 0

    open fun calcularAporte(): Double {
        return nota * peso
    }

    abstract fun obtenerDetalle(): String
}