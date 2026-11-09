package com.huaman.cafecampus.model

/**
 * Estados posibles de un pedido.
 *
 * Se guarda en la tabla `pedidos` siempre como [EstadoPedido.name].
 *
 * ```
 * RECIBIDO -> PREPARANDO -> LISTO -> ENTREGADO
 *     \                              \
 *      +---------- CANCELADO ---------+
 * ```
 *
 * [siguiente] es la UNICA fuente de la regla de avance: la logica de negocio
 * vive aqui y en el ViewModel, nunca dentro de un Composable.
 */
enum class EstadoPedido {
    RECIBIDO,
    PREPARANDO,
    LISTO,
    ENTREGADO,
    CANCELADO;

    /** Siguiente estado en la cadena de preparacion. */
    fun siguiente(): EstadoPedido = when (this) {
        RECIBIDO -> PREPARANDO
        PREPARANDO -> LISTO
        LISTO -> ENTREGADO
        ENTREGADO -> ENTREGADO
        CANCELADO -> CANCELADO
    }

    /** Un pedido "activo" todavia esta en la cafeteria. */
    val activo: Boolean
        get() = this == RECIBIDO || this == PREPARANDO || this == LISTO

    /** Un pedido "historico" ya se entrego o se cancelo. */
    val historico: Boolean get() = !activo

    /** Texto que ve el usuario (el enum guarda el nombre tecnico en ingles). */
    val etiqueta: String
        get() = when (this) {
            RECIBIDO -> "Recibido"
            PREPARANDO -> "Preparando"
            LISTO -> "Listo"
            ENTREGADO -> "Entregado"
            CANCELADO -> "Cancelado"
        }

    /** Descripcion corta que aparece bajo el paso en el seguimiento. */
    val descripcion: String
        get() = when (this) {
            RECIBIDO -> "Tu pedido llego a la cocina"
            PREPARANDO -> "Estan cocinando tu pedido"
            LISTO -> "Puedes pasar a recogerlo"
            ENTREGADO -> "Buen provecho!"
            CANCELADO -> "El pedido fue cancelado"
        }

    companion object {
        /** Convierte el String de la base en enum, con un valor por defecto seguro. */
        fun desde(valor: String?): EstadoPedido =
            entries.firstOrNull { it.name == valor } ?: RECIBIDO
    }
}