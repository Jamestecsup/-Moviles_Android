package com.huaman.clinicasaludplus.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val experiencia: String,
    val calificacion: Float,
    val resenas: Int,
    val descripcion: String
)

data class Cita(
    val id: Int,
    val medico: Medico,
    val fecha: String,
    val hora: String,
    val estado: EstadoCita
)

enum class EstadoCita {
    CONFIRMADA, COMPLETADA
}

val medicosMock = listOf(
    Medico(
        id = 1,
        nombre = "Dra. Ana Torres",
        especialidad = "Cardiología",
        experiencia = "12 años exp.",
        calificacion = 4.9f,
        resenas = 128,
        descripcion = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
    ),
    Medico(
        id = 2,
        nombre = "Dr. Luis Vega",
        especialidad = "Pediatría",
        experiencia = "8 años exp.",
        calificacion = 4.7f,
        resenas = 95,
        descripcion = "Especialista en cuidado pediátrico integral y preventivo."
    ),
    Medico(
        id = 3,
        nombre = "Dra. Rosa Diaz",
        especialidad = "Dermatología",
        experiencia = "10 años exp.",
        calificacion = 4.8f,
        resenas = 112,
        descripcion = "Especialista en dermatología médica y estética."
    )
)

val especialidades = listOf("Cardiología", "Pediatría", "Dermatología")

val horasMock = listOf("9:00", "10:30", "3:00")
val fechasMock = listOf("26", "27", "28")

val citasMock = listOf(
    Cita(
        id = 1,
        medico = medicosMock[0],
        fecha = "27",
        hora = "10:30",
        estado = EstadoCita.CONFIRMADA
    ),
    Cita(
        id = 2,
        medico = medicosMock[1],
        fecha = "15",
        hora = "15:00",
        estado = EstadoCita.COMPLETADA
    )
)
