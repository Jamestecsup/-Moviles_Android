package com.huaman.cafecampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.model.Rol

/**
 * Formas del diseno: tarjetas de 12.dp y botones de 10.dp
 * (seccion 7 del laboratorio: "Tarjetas con RoundedCornerShape(12.dp) y borde
 * fino, botones de 10.dp, margenes de 16.dp").
 */
private val CafeShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// ---------------------------------------------------------------------------
// Esquemas claros: uno por perfil.
// El resto de colores (secundario, terciario, superficies) se derivan del
// primario para mantener el tono de cada marca.
// ---------------------------------------------------------------------------
private val EsquemaCliente = lightColorScheme(
    primary = NaranjaPrimario,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = NaranjaContenedor,
    onPrimaryContainer = NaranjaPrimario,
    secondary = NaranjaPrimario,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = NaranjaContenedor,
    onSecondaryContainer = NaranjaPrimario,
    tertiary = NaranjaPrimario,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = NaranjaFondo,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1F1309),
    surface = NaranjaFondo,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1F1309),
    surfaceVariant = NaranjaContenedor,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF6B4A32),
    surfaceContainer = androidx.compose.ui.graphics.Color(0xFFFFF4EC),
    surfaceContainerHigh = androidx.compose.ui.graphics.Color(0xFFFDEDE0),
    outline = androidx.compose.ui.graphics.Color(0xFFE4C4AC),
    outlineVariant = NaranjaContenedor,
    error = EstadoCancelado,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = EstadoCanceladoContenedor,
    onErrorContainer = EstadoCancelado
)

private val EsquemaAdmin = lightColorScheme(
    primary = IndigoPrimario,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = IndigoContenedor,
    onPrimaryContainer = IndigoPrimario,
    secondary = IndigoPrimario,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = IndigoContenedor,
    onSecondaryContainer = IndigoPrimario,
    tertiary = IndigoPrimario,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = IndigoFondo,
    onBackground = androidx.compose.ui.graphics.Color(0xFF14142B),
    surface = IndigoFondo,
    onSurface = androidx.compose.ui.graphics.Color(0xFF14142B),
    surfaceVariant = IndigoContenedor,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF4A4A7A),
    surfaceContainer = androidx.compose.ui.graphics.Color(0xFFF0F0FE),
    surfaceContainerHigh = androidx.compose.ui.graphics.Color(0xFFE7E7FC),
    outline = androidx.compose.ui.graphics.Color(0xFFC7C7F0),
    outlineVariant = IndigoContenedor,
    error = EstadoCancelado,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = EstadoCanceladoContenedor,
    onErrorContainer = EstadoCancelado
)

// ---------------------------------------------------------------------------
// Modo oscuro: se activa desde Ajustes y se guarda en DataStore.
// El tono (naranja / indigo) se mantiene, solo cambian los tonos.
// ---------------------------------------------------------------------------
private fun esquemaOscuro(
    primario: androidx.compose.ui.graphics.Color,
    onPrimario: androidx.compose.ui.graphics.Color,
    contenedor: androidx.compose.ui.graphics.Color,
    onContenedor: androidx.compose.ui.graphics.Color
) = darkColorScheme(
    primary = primario,
    onPrimary = onPrimario,
    primaryContainer = contenedor,
    onPrimaryContainer = onContenedor,
    secondary = primario,
    onSecondary = onPrimario,
    secondaryContainer = contenedor,
    onSecondaryContainer = onContenedor,
    tertiary = primario,
    onTertiary = onPrimario,
    background = OscuroFondo,
    onBackground = androidx.compose.ui.graphics.Color(0xFFF3EAE4),
    surface = OscuroFondo,
    onSurface = androidx.compose.ui.graphics.Color(0xFFF3EAE4),
    surfaceVariant = OscuroSuperficie,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFD7C3B6),
    surfaceContainer = OscuroSuperficie,
    surfaceContainerHigh = OscuroSuperficie,
    outline = androidx.compose.ui.graphics.Color(0xFF8C857E),
    outlineVariant = OscuroSuperficie,
    error = EstadoCanceladoContenedor,
    onError = EstadoCancelado,
    errorContainer = androidx.compose.ui.graphics.Color(0xFF7F1D1D),
    onErrorContainer = EstadoCanceladoContenedor
)

/**
 * `CafeTheme(rol)` entrega un ColorScheme segun el perfil, tal como pide la
 * seccion 7 del laboratorio. El rol determina el color primario:
 * naranja para el cliente, indigo para el administrador.
 *
 * En las pantallas se usa SIEMPRE `MaterialTheme.colorScheme.primary`,
 * nunca un color fijo.
 */
@Composable
fun CafeTheme(
    rol: Rol = Rol.CLIENTE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val scheme = when {
        darkTheme && rol == Rol.ADMIN -> esquemaOscuro(
            primario = androidx.compose.ui.graphics.Color(0xFFA5B4FC),
            onPrimario = androidx.compose.ui.graphics.Color(0xFF1E1B4B),
            contenedor = androidx.compose.ui.graphics.Color(0xFF312E81),
            onContenedor = androidx.compose.ui.graphics.Color(0xFFE0E7FF)
        )

        darkTheme -> esquemaOscuro(
            primario = OscuroPrimario,
            onPrimario = androidx.compose.ui.graphics.Color(0xFF3B1500),
            contenedor = OscuroPrimarioContenedor,
            onContenedor = NaranjaContenedor
        )

        rol == Rol.ADMIN -> EsquemaAdmin
        else -> EsquemaCliente
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = CafeShapes,
        content = content
    )
}