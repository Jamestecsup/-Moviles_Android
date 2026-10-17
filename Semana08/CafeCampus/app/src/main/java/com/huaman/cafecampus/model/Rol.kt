package com.huaman.cafecampus.model

/**
 * Perfil de la app. Los dos perfiles conviven en el MISMO dispositivo y
 * comparten la misma base Room.
 *
 * Se guarda en la base y en DataStore siempre como [Rol.name] (String).
 */
enum class Rol {
    CLIENTE,
    ADMIN;

    val esAdmin: Boolean get() = this == ADMIN

    /** Texto que ve el usuario. */
    val etiqueta: String
        get() = when (this) {
            CLIENTE -> "Estudiante"
            ADMIN -> "Administrador"
        }
}