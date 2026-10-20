package com.huaman.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "promociones",
    indices = [Index(value = ["tipo", "valorReferencia"], unique = true)]
)
data class Promocion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,             // "PRODUCTO" | "CATEGORIA"
    val valorReferencia: String,  // nombre del producto o nombre de la categoría
    val descuentoPorcentaje: Double, // ej. 20.0 = 20% de descuento
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFin: Long,
    val activo: Boolean = true
) {
    val esValida: Boolean
        get() = activo && fechaInicio <= System.currentTimeMillis() && System.currentTimeMillis() <= fechaFin

    fun descripcion(): String = when (tipo) {
        "PRODUCTO" -> "Producto: $valorReferencia"
        "CATEGORIA" -> "Categoría: $valorReferencia"
        else -> "Desconocido"
    }
}