package com.tecsup.avisoscampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(
    primary = Color(0xFF15549C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E7FF),
    onPrimaryContainer = Color(0xFF093565),
    background = Color(0xFFF6F8FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFE4EBF4),
    outline = Color(0xFF68788C)
)

private val Oscuro = darkColorScheme(
    primary = Color(0xFFA4CAFF),
    onPrimary = Color(0xFF08305C),
    primaryContainer = Color(0xFF14487D),
    onPrimaryContainer = Color(0xFFD6E7FF),
    background = Color(0xFF10151B),
    surface = Color(0xFF19212A)
)

@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Oscuro else Claro, content = content)
}
