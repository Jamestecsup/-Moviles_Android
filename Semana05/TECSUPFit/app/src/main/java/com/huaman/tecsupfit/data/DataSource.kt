package com.huaman.tecsupfit.data

import com.huaman.tecsupfit.model.ClaseGym
import com.huaman.tecsupfit.model.EstadoReserva
import com.huaman.tecsupfit.model.Reserva

object DataSource {
    val clases = listOf(
        ClaseGym(
            id = "1",
            nombre = "CrossFit Pro",
            categoria = "Fuerza",
            hora = "08:00 AM",
            sala = "Sala A",
            duracionMin = 60,
            descripcion = "Entrenamiento de alta intensidad enfocado en fuerza y resistencia muscular.",
            cuposDisponibles = 4,
            cuposTotal = 15
        ),
        ClaseGym(
            id = "2",
            nombre = "Spinning Cardio",
            categoria = "Cardio",
            hora = "09:30 AM",
            sala = "Sala B",
            duracionMin = 45,
            descripcion = "Clase de ciclismo de interior con música motivadora para quemar calorías.",
            cuposDisponibles = 2,
            cuposTotal = 20
        ),
        ClaseGym(
            id = "3",
            nombre = "Yoga Relax",
            categoria = "Yoga",
            hora = "11:00 AM",
            sala = "Sala C",
            duracionMin = 50,
            descripcion = "Sesión de estiramiento y relajación para mejorar la flexibilidad y postura.",
            cuposDisponibles = 8,
            cuposTotal = 12
        ),
        ClaseGym(
            id = "4",
            nombre = "Functional Training",
            categoria = "Fuerza",
            hora = "04:00 PM",
            sala = "Sala A",
            duracionMin = 60,
            descripcion = "Movimientos funcionales cotidianos para mejorar agilidad y tono muscular.",
            cuposDisponibles = 5,
            cuposTotal = 15
        )
    )

    val reservasIniciales = listOf(
        Reserva(
            id = "r1",
            clase = clases[0],
            estado = EstadoReserva.CONFIRMADA
        ),
        Reserva(
            id = "r2",
            clase = clases[1],
            estado = EstadoReserva.COMPLETADA
        )
    )
}
