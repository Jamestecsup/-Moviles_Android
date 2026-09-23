package com.huaman.tecsupfit.data

import com.huaman.tecsupfit.model.ClaseGym
import com.huaman.tecsupfit.model.EstadoReserva
import com.huaman.tecsupfit.model.Reserva

object DataSource {
    val clases = listOf(
        ClaseGym(
            id = "1",
            nombre = "Yoga funcional",
            categoria = "Yoga",
            hora = "7:00 am",
            sala = "Sala 2",
            duracionMin = 60,
            descripcion = "Yoga enfocado en movilidad y fuerza funcional.",
            cuposDisponibles = 5,
            cuposTotal = 15
        ),
        ClaseGym(
            id = "2",
            nombre = "Cross Training",
            categoria = "Fuerza",
            hora = "6:00 pm",
            sala = "Sala 1",
            duracionMin = 45,
            descripcion = "Entrenamiento funcional de alta intensidad. Cupos limitados.",
            cuposDisponibles = 8,
            cuposTotal = 12
        ),
        ClaseGym(
            id = "3",
            nombre = "Spinning",
            categoria = "Cardio",
            hora = "7:30 pm",
            sala = "Sala 3",
            duracionMin = 50,
            descripcion = "Clase de ciclismo indoor de alta quema calórica.",
            cuposDisponibles = 3,
            cuposTotal = 20
        )
    )

    val reservasIniciales = listOf(
        Reserva(
            id = "r1",
            clase = clases[1], // Cross Training
            estado = EstadoReserva.CONFIRMADA
        ),
        Reserva(
            id = "r2",
            clase = clases[0], // Yoga funcional
            estado = EstadoReserva.COMPLETADA
        )
    )
}
