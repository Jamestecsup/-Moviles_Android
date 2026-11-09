package com.huaman.cafecampus.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Formato de moneda peruano: "S/ 12.00"
 */
fun soles(monto: Double): String = String.format(Locale.US, "S/ %.2f", monto)

/**
 * "Hoy 10:30", "Ayer 09:15" o "12 oct 16:10".
 */
fun fechaHoraLegible(millis: Long): String {
    val ahora = Calendar.getInstance()
    val pedido = Calendar.getInstance().apply { timeInMillis = millis }

    val dias = diferenciaEnDias(pedido, ahora)
    val hora = SimpleDateFormat("HH:mm", Locale.US).format(Date(millis))

    return when {
        dias <= 0 -> "Hoy $hora"
        dias == 1 -> "Ayer $hora"
        dias < 7 -> "Hace $dias dias"
        else -> SimpleDateFormat("d MMM HH:mm", Locale.forLanguageTag("es-ES"))
            .format(Date(millis))
    }
}

/** Cantidad de dias calendario entre dos fechas (positivo si [desde] es anterior). */
private fun diferenciaEnDias(desde: Calendar, hasta: Calendar): Int {
    val a = desde.clone() as Calendar
    val b = hasta.clone() as Calendar
    listOf(a, b).forEach {
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
    val dias = (b.timeInMillis - a.timeInMillis) / 86_400_000L
    return dias.toInt()
}

/** Inicio del dia de hoy en millis. Lo usa el panel para las ventas del dia. */
fun inicioDelDia(): Long = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

/** Plural sencillo: 1 producto / 2 productos. */
fun plural(cantidad: Int, singular: String, pluralForma: String): String =
    if (cantidad == 1) "$cantidad $singular" else "$cantidad $pluralForma"

/** Resumen de lineas para las tarjetas: "2 x Menu del dia + 1 x Jugo de naranja". */
fun resumenLineas(nombres: List<String>, cantidades: List<Int>): String =
    nombres.mapIndexed { i, n -> "${cantidades.getOrElse(i) { 1 }} x $n" }
        .joinToString(" + ")