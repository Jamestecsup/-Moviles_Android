package com.tecsup.campusgps.util

import java.util.Locale

fun formatearDistancia(metros: Float): String {
    return if (metros < 1000f) {
        "${metros.toInt()} m"
    } else {
        val km = metros / 1000f
        String.format(Locale.US, "%.1f km", km)
    }
}
