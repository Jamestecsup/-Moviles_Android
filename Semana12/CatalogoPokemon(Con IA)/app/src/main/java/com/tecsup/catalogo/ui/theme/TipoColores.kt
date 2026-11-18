package com.tecsup.catalogo.ui.theme

import androidx.compose.ui.graphics.Color

/** Colores de fondo y de texto para la etiqueta de un tipo de Pokémon. */
data class ColoresDeTipo(
    val fondo: Color,
    val texto: Color
)

/**
 * Devuelve el color de cada tipo. Acepta los nombres en inglés que entrega la
 * API ("fire", "water", "grass"...) y también los nombres en español.
 */
fun coloresDeTipo(tipo: String): ColoresDeTipo = when (tipo.trim().lowercase()) {
    "fire", "fuego" -> ColoresDeTipo(Color(0xFFE53935), Color.White)
    "water", "agua" -> ColoresDeTipo(Color(0xFF1E88E5), Color.White)
    "grass", "planta" -> ColoresDeTipo(Color(0xFF43A047), Color.White)
    "ground", "tierra" -> ColoresDeTipo(Color(0xFF66BB6A), Color.White)
    "electric", "eléctrico", "electrico" ->
        ColoresDeTipo(Color(0xFFFDD835), Color(0xFF3E2723))
    "ice", "hielo" -> ColoresDeTipo(Color(0xFF4DD0E1), Color(0xFF00363A))
    "fighting", "lucha" -> ColoresDeTipo(Color(0xFFD84315), Color.White)
    "poison", "veneno" -> ColoresDeTipo(Color(0xFF8E24AA), Color.White)
    "flying", "volador" -> ColoresDeTipo(Color(0xFF64B5F6), Color(0xFF0D47A1))
    "psychic", "psíquico", "psiquico" -> ColoresDeTipo(Color(0xFFEC407A), Color.White)
    "bug", "bicho" -> ColoresDeTipo(Color(0xFF7CB342), Color.White)
    "rock", "roca" -> ColoresDeTipo(Color(0xFF6D4C41), Color.White)
    "ghost", "fantasma" -> ColoresDeTipo(Color(0xFF5E35B1), Color.White)
    "dragon", "dragón" -> ColoresDeTipo(Color(0xFF3949AB), Color.White)
    "dark", "siniestro" -> ColoresDeTipo(Color(0xFF424242), Color.White)
    "steel", "acero" -> ColoresDeTipo(Color(0xFF78909C), Color.White)
    "fairy", "hada" -> ColoresDeTipo(Color(0xFFF06292), Color.White)
    else -> ColoresDeTipo(Color(0xFF9E9E9E), Color.White)
}
