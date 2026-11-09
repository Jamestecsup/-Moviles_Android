package com.tecsup.catalogo.data.mapper

import com.tecsup.catalogo.data.local.ItemEntity
import com.tecsup.catalogo.data.remote.dto.PokemonDetalleDto
import com.tecsup.catalogo.data.remote.dto.PokemonRefDto
import com.tecsup.catalogo.domain.model.Item

private const val SPRITES_OFICIALES =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

/** Convierte lo que responde la API (DTO) en lo que se guarda en Room (Entity). */
fun PokemonRefDto.aEntity(tipo: String): ItemEntity {
    val numero = url.trimEnd('/').substringAfterLast('/')
    return ItemEntity(
        id = name,
        titulo = name.aTitulo(),
        subtitulo = tipo.aTitulo(),
        imagenUrl = "$SPRITES_OFICIALES$numero.png",
        esFavorito = false
    )
}

/** Convierte lo guardado en Room (Entity) en el modelo que usa la UI. */
fun ItemEntity.aModelo(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito
)

/** Convierte el detalle de la API (DTO) en el modelo que usa la UI. */
fun PokemonDetalleDto.aModelo(esFavorito: Boolean): Item {
    val imagen = sprites.other?.officialArtwork?.frontDefault
        ?: sprites.frontDefault
        ?: "$SPRITES_OFICIALES$id.png"
    val nombresTipos = types.map { it.type.name.aTitulo() }

    return Item(
        id = name,
        titulo = name.aTitulo(),
        subtitulo = nombresTipos.joinToString(" / "),
        imagenUrl = imagen,
        esFavorito = esFavorito,
        tipos = nombresTipos,
        altura = height,
        peso = weight
    )
}

/** "charizard-mega-x" -> "Charizard Mega X" */
fun String.aTitulo(): String =
    replace("-", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { palabra ->
            palabra.replaceFirstChar { letra -> letra.uppercase() }
        }
