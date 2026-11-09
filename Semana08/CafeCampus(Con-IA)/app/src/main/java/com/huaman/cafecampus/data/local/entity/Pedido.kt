package com.huaman.cafecampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cabecera de un pedido (tabla `pedidos`).
 *
 * Relacion N:1 con [Usuario] -> un usuario tiene N pedidos.
 *
 * - [clienteId] es la clave foranea hacia `usuarios.id`
 * - [estado] se guarda como String con
 *   [com.huaman.cafecampus.model.EstadoPedido.name]
 * - [fecha] es `System.currentTimeMillis()` al crear el pedido
 *
 * El indice sobre [clienteId] es OBLIGATORIO: Room avisa (y en modo estricto
 * falla) cuando una columna de clave foranea no tiene indice.
 */
@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("clienteId")]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int,
    val estado: String,
    val horaRecojo: String,
    /** CAJA | YAPE */
    val pago: String,
    val nota: String,
    val total: Double,
    val fecha: Long = System.currentTimeMillis()
) {
    /** El estado tipado, ya convertido desde el String de la base. */
    val estadoTipado: com.huaman.cafecampus.model.EstadoPedido
        get() = com.huaman.cafecampus.model.EstadoPedido.desde(estado)
}