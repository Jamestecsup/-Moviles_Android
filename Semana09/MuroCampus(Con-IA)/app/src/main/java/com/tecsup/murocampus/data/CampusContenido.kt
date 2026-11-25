package com.tecsup.murocampus.data

import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post

object CampusContenido {

    private val publicacionesCampus = listOf(
        Pair(
            "Convocatoria abierta para el Hackathon Anual 2026",
            "Forma tu equipo de hasta 4 integrantes y participa en el desarrollo de soluciones móviles para ciudades sostenibles. Habrá mentorías especializadas y premios tecnológicos."
        ),
        Pair(
            "Taller práctico: Arquitectura MVVM y Corrutinas en Android",
            "Este viernes a las 3:00 PM en el Laboratorio 302. Aprenderemos StateFlow, Retrofit y buenas prácticas con Jetpack Compose. ¡Entrada libre para todos los estudiantes!"
        ),
        Pair(
            "Horarios extendidos en Biblioteca por semana de evaluaciones",
            "Durante las próximas dos semanas, las salas de estudio individual y grupal de la biblioteca central permanecerán abiertas hasta las 10:00 PM."
        ),
        Pair(
            "Feria de Empleabilidad y Pasantías Tecnológicas",
            "Más de 20 empresas líderes del sector TI estarán en el patio central del campus entrevistando a estudiantes de 4to a 6to ciclo. Trae tu CV impreso o digital."
        ),
        Pair(
            "Torneo Relámpago de Fútbol y Vóley Intercarreras",
            "Inscripciones abiertas en la oficina de Bienestar Estudiantil. Los encuentros deportivos se jugarán los sábados por la tarde en las losas del campus."
        ),
        Pair(
            "Club de Robótica e Inteligencia Artificial: Convocatoria",
            "Invitamos a todos los apasionados por visión artificial, drones y sistemas embebidos a la primera reunión informativa este miércoles a las 5:00 PM en el FabLab."
        ),
        Pair(
            "Actualización de herramientas de desarrollo en laboratorios",
            "Se ha completado la actualización de Android Studio, JDK 21 y SDKs en todas las estaciones de trabajo del departamento de tecnología digital."
        ),
        Pair(
            "Asesorías académicas gratuitas entre pares (Peer Tutoring)",
            "Si necesitas reforzar conceptos en estructuras de datos, desarrollo móvil o bases de datos, agenda tu sesión personalizada con estudiantes tutores en el portal."
        ),
        Pair(
            "Charla magistral: Ciberseguridad en Aplicaciones Móviles",
            "Acompáñanos este jueves en el auditorio principal con ingenieros invitados de la industria. Se otorgará constancia de participación digital a los asistentes."
        ),
        Pair(
            "Concurso de Diseño de Interfaces de Usuario (UI/UX)",
            "Presenta tu prototipo interactivo enfocado en accesibilidad e impacto social antes del fin de mes. Los mejores proyectos recibirán mención de honor."
        ),
        Pair(
            "Campaña de donación de libros y material de estudio",
            "Dona textos técnicos o guías de laboratorio en buen estado en el módulo de atención estudiantil. Ayuda a enriquecer el banco de préstamos de la biblioteca."
        ),
        Pair(
            "Mantenimiento programado de servidores y campus virtual",
            "Este domingo de 02:00 AM a 06:00 AM se realizará mantenimiento preventivo en la plataforma académica. Toma precauciones con tus entregas de tareas."
        ),
        Pair(
            "Nuevo club de debate y habilidades blandas para ingenieros",
            "Aprende técnicas de oratoria, comunicación asertiva y negociación efectiva para entrevistas de trabajo y sustentaciones de tesis. ¡Súmate este martes!"
        ),
        Pair(
            "Visita técnica a centro de datos de alta disponibilidad",
            "Conoce la infraestructura en la nube, servidores y redes de telecomunicaciones de primer nivel. Cupos limitados para estudiantes del último ciclo."
        ),
        Pair(
            "Exposición anual de proyectos de fin de carrera",
            "Acompáñanos en la presentación de tesis y aplicaciones desarrolladas por la promoción de graduados en el auditorio de innovación tecnológica."
        )
    )

    private val comentariosCampus = listOf(
        Pair("carlos.mendoza@tecsup.edu.pe", "¿El evento incluirá constancia digital de participación para los asistentes?"),
        Pair("ana.paredes@campus.pe", "¡Excelente iniciativa! Ya coordiné con mis compañeros de grupo para asistir."),
        Pair("docente.guia@tecsup.edu.pe", "Recuerden revisar el repositorio previo antes de comenzar con la sesión práctica."),
        Pair("lucia.ramos@estudiantes.pe", "¿En qué enlace o carpeta compartida publicarán las diapositivas y el código fuente?"),
        Pair("jorge.quispe@campus.pe", "Muchas gracias por compartir la información, resulta de gran ayuda para nuestro proyecto final.")
    )

    fun adaptarPost(post: Post): Post {
        val indice = (post.id - 1).coerceAtLeast(0) % publicacionesCampus.size
        val (tituloCampus, cuerpoCampus) = publicacionesCampus[indice]

        val tituloFinal = if (esTextoEnLatin(post.title)) tituloCampus else post.title
        val cuerpoFinal = if (esTextoEnLatin(post.body)) cuerpoCampus else post.body

        return post.copy(
            title = tituloFinal,
            body = cuerpoFinal
        )
    }

    fun adaptarComentario(comentario: Comentario): Comentario {
        val indice = (comentario.id - 1).coerceAtLeast(0) % comentariosCampus.size
        val (emailEstudiante, cuerpoComentario) = comentariosCampus[indice]

        val cuerpoFinal = if (esTextoEnLatin(comentario.body)) cuerpoComentario else comentario.body

        return comentario.copy(
            email = emailEstudiante,
            body = cuerpoFinal
        )
    }

    private fun esTextoEnLatin(texto: String): Boolean {
        val palabrasLatinas = setOf(
            "lorem", "ipsum", "dolor", "sunt", "aut", "qui", "est", "quia",
            "eum", "repellat", "laudantium", "voluptatem", "architecto", "nesciunt",
            "provident", "fugiat", "consequatur", "repudiandae", "corporis", "molestiae",
            "ullam", "laboriosam", "perspiciatis", "rerum", "blanditiis", "harum"
        )
        val palabras = texto.lowercase().split(Regex("[^a-zA-ZáéíóúÁÉÍÓÚñÑ]+"))
        return palabras.any { it in palabrasLatinas }
    }
}
