package com.huaman.tecsupfit.model

data class ClaseGym(
    val id: String,
    val nombre: String,
    val categoria: String,
    val hora: String,
    val sala: String,
    val duracionMin: Int,
    val descripcion: String,
    val cuposDisponibles: Int,
    val cuposTotal: Int
)
