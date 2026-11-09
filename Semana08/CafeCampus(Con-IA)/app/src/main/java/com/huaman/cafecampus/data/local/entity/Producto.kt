package com.huaman.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Producto de la carta. Este es el "vertical" resuelto que trae el proyecto
 * base: entidad -> DAO -> Repository -> ViewModel -> MenuScreen.
 *
 * Tabla `productos`.
 * - PK autoincremental [id]
 * - clave candidata: [nombre] (UNIQUE), no hay dos productos con el mismo nombre
 * - [imagenRuta]: ruta a una imagen de la galería guardada en el dispositivo.
 *   Si es null, se usa el icono vectorial definido por [icono].
 */
@Entity(
    tableName = "productos",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Double,
    val disponible: Boolean = true,
    /** cafe | plato | jugo | pan | botella | dulce */
    val icono: String = "plato",
    /** Ruta absoluta a una imagen de galería guardada en el dispositivo, o null */
    val imagenRuta: String? = null
)