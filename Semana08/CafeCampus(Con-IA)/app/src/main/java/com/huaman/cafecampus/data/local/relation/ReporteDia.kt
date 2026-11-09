package com.huaman.cafecampus.data.local.relation

/**
 * Resultado de una consulta agregada: el total de una forma de pago.
 *
 * Room lo devuelve como una fila de la consulta, asi que se declara como
 * una clase suelta (no una entidad: no tiene tabla propia).
 */
data class TotalPorPago(
    val pago: String,
    val total: Double,
    val pedidos: Int
)

/**
 * El producto mas vendido del dia, con cuantas unidades se vendieron.
 */
data class ProductoVendido(
    val nombreProducto: String,
    val unidades: Int,
    val total: Double
)
