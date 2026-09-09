package com.huaman.registrodenotascon_ia.logic

import com.huaman.registrodenotascon_ia.model.Curso
import kotlin.math.roundToInt

class Evaluador(val cursos: List<Curso>, private val aplicarRedondeo: Boolean) {

    fun calcularPromedioPonderado(): Double {
        return cursos.sumOf { it.calcularAporte() }
    }

    fun calcularPromedioFinal(): Double {
        val ponderado = calcularPromedioPonderado()
        return if (aplicarRedondeo) ponderado.roundToInt().toDouble() else ponderado
    }

    fun obtenerObservacion(promedioFinal: Double): String {
        return when {
            promedioFinal >= 17.0 -> "EXCELENTE"
            promedioFinal >= 13.0 -> "APROBADO"
            promedioFinal >= 10.0 -> "EN RECUPERACIÓN"
            else -> "DESAPROBADO"
        }
    }

    fun obtenerColorHex(promedioFinal: Double): Long {
        return when {
            promedioFinal >= 17.0 -> 0xFF1B5E20
            promedioFinal >= 13.0 -> 0xFF2E7D32
            promedioFinal >= 10.0 -> 0xFFEF6C00
            else -> 0xFFC62828
        }
    }
}