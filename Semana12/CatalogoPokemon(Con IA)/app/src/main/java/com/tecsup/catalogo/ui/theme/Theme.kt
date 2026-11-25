package com.tecsup.catalogo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = RojoPokemon,
    onPrimary = Color.White,
    primaryContainer = RojoContenedor,
    onPrimaryContainer = RojoOscuro,
    secondary = AmarilloPokemon,
    onSecondary = Color(0xFF3B2F00),
    secondaryContainer = AmarilloContenedor,
    onSecondaryContainer = Color(0xFF3B2F00),
    background = FondoClaro,
    onBackground = Color(0xFF201A1A),
    surface = FondoClaro,
    onSurface = Color(0xFF201A1A),
    surfaceVariant = Color(0xFFF4DEDC),
    onSurfaceVariant = Color(0xFF534341),
    outline = Color(0xFF857370),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFFFB3B1),
    onPrimary = Color(0xFF68000F),
    primaryContainer = RojoOscuro,
    onPrimaryContainer = RojoContenedor,
    secondary = Color(0xFFE6C34A),
    onSecondary = Color(0xFF3B2F00),
    background = FondoOscuro,
    onBackground = Color(0xFFEDE0DF),
    surface = FondoOscuro,
    onSurface = Color(0xFFEDE0DF),
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = Color(0xFFD8C2BF),
    outline = Color(0xFFA08C89),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun CatalogoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
