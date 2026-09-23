package com.huaman.clinicasaludplus.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PurpuraPrimario,
    secondary = PurpuraSecundario,
    tertiary = Verde,
    background = Blanco,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = PurpuraPrimario,
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000)
)

@Composable
fun ClinicaSaludPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
