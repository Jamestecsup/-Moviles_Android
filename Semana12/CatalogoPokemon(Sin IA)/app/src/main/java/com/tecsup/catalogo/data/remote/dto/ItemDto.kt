package com.tecsup.catalogo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---------------------------------------------------------------------------
// GET /type/{tipo}
// Devuelve { "pokemon": [ { "slot": 1, "pokemon": { "name": "...", "url": "..." } } ] }
// ---------------------------------------------------------------------------
@Serializable
data class TipoRespuestaDto(
    val pokemon: List<EntradaTipoDto> = emptyList()
)

@Serializable
data class EntradaTipoDto(
    val slot: Int = 0,
    val pokemon: PokemonRefDto
)

@Serializable
data class PokemonRefDto(
    val name: String,
    val url: String
)

// ---------------------------------------------------------------------------
// GET /pokemon/{nombre}
// ---------------------------------------------------------------------------
@Serializable
data class PokemonDetalleDto(
    val id: Int = 0,
    val name: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val sprites: SpritesDto = SpritesDto(),
    val types: List<TipoSlotDto> = emptyList()
)

@Serializable
data class SpritesDto(
    @SerialName("front_default") val frontDefault: String? = null,
    val other: OtherSpritesDto? = null
)

@Serializable
data class OtherSpritesDto(
    @SerialName("official-artwork") val officialArtwork: OfficialArtworkDto? = null
)

@Serializable
data class OfficialArtworkDto(
    @SerialName("front_default") val frontDefault: String? = null
)

@Serializable
data class TipoSlotDto(
    val slot: Int = 0,
    val type: TipoDto = TipoDto()
)

@Serializable
data class TipoDto(
    val name: String = ""
)
