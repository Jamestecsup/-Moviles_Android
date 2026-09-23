package com.huaman.navlab.model

data class Student(
    val id: String,
    val name: String,
    val career: String,
    val email: String,
    val faculty: String,
    val bio: String,
    val imageUrl: String
)

val sampleStudents = listOf(
    Student(
        id = "2024-0001",
        name = "Juan León",
        career = "Ingeniería de Sistemas",
        email = "juan.leon@example.com",
        faculty = "Ingeniería y Tecnología",
        bio = "Estudiante destacado con interés en desarrollo Android.",
        imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
    ),
    Student(
        id = "2024-0002",
        name = "Maria Garcia",
        career = "Arquitectura",
        email = "maria.garcia@example.com",
        faculty = "Arquitectura y Diseño",
        bio = "Apasionada por el diseño sostenible y modelado en 3D.",
        imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"
    ),
    Student(
        id = "2024-0003",
        name = "Carlos Perez",
        career = "Medicina",
        email = "carlos.perez@example.com",
        faculty = "Ciencias de la Salud",
        bio = "Interesado en la investigación pediátrica y salud pública.",
        imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150"
    ),
    Student(
        id = "2024-0004",
        name = "Ana Lopez",
        career = "Derecho",
        email = "ana.lopez@example.com",
        faculty = "Derecho y Ciencias Políticas",
        bio = "Especializándose en derecho digital y protección de datos.",
        imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150"
    ),
    Student(
        id = "2024-0005",
        name = "Luis Ramirez",
        career = "Administración",
        email = "luis.ramirez@example.com",
        faculty = "Ciencias Empresariales",
        bio = "Enfocado en innovación financiera y gestión de proyectos.",
        imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
    )
)
