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
    val icono: String
)