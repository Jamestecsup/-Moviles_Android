package com.huaman.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Usuario de la app: estudiante o administrador.
 *
 * Tabla `usuarios` (1 usuario -> N pedidos).
 *
 * - PK autogenerada [id]
 * - clave candidata: [correo] (UNIQUE) -> es la clave de login
 * - [rol] se guarda como String con [com.huaman.cafecampus.model.Rol.name]
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: String
) {
    /** El rol tipado, ya convertido desde el String de la base. */
    val rolTipado: com.huaman.cafecampus.model.Rol
        get() = com.huaman.cafecampus.model.Rol.entries.firstOrNull { it.name == rol }
            ?: com.huaman.cafecampus.model.Rol.CLIENTE
}