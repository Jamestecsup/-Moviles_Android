package com.example.clinicasaludplus.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val LightColorScheme = lightColorScheme(
        primary = PurpuraPrimario,
        secondary = PurpuraSecundario,
        tertiary = Verde,
        background = Blanco,
        surface = Blanco,
        onPrimary = Blanco,
        onSecondary = PurpuraPrimario,
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000),
        )

@Composable
fun ClinicaSaludPlusTheme(
        content: @Composable () -> Unit
) {
MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
)
}