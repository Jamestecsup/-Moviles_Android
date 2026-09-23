package com.huaman.tecsupfit.model

enum class EstadoReserva {
    CONFIRMADA,
    COMPLETADA
}

data class Reserva(
    val id: String,
    val clase: ClaseGym,
    val estado: EstadoReserva
)
