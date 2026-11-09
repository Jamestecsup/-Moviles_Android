package com.tecsup.catalogo.domain.model

/**
 * Modelo que usa la interfaz (UI).
 * Para Pokémon el identificador es el nombre, por eso es String.
 */
data class Item(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String? = null,
    val esFavorito: Boolean = false,
    val tipos: List<String> = emptyList(),
    val altura: Int? = null,
    val peso: Int? = null
)
