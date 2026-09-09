package com.huaman.registrodenotascon_ia.model

class CursoFundamentos : Curso("Fundamentos de Programación", 0.20, "(20%)") {
    override fun obtenerDetalle() = "• Fundamentos: $nota × 20% = ${calcularAporte()}"
}

class CursoPOO : Curso("Programación Orientada a Objetos", 0.25, "(25%)") {
    override fun obtenerDetalle() = "• POO: $nota × 25% = ${calcularAporte()}"
}

class CursoMoviles : Curso("Programación en Móviles", 0.30, "(30%)") {
    override fun obtenerDetalle() = "• Móviles: $nota × 30% = ${calcularAporte()}"
}

class CursoBaseDatos : Curso("Base de Datos", 0.25, "(25%)") {
    override fun obtenerDetalle() = "• Base de Datos: $nota × 25% = ${calcularAporte()}"
}