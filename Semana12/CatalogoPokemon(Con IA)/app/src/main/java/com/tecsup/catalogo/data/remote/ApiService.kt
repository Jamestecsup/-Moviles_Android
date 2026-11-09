package com.tecsup.catalogo.data.remote

import com.tecsup.catalogo.data.remote.dto.PokemonDetalleDto
import com.tecsup.catalogo.data.remote.dto.TipoRespuestaDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    /** Lista los Pokémon de un tipo: fire, water o grass. */
    @GET("type/{tipo}")
    suspend fun listar(@Path("tipo") tipo: String): TipoRespuestaDto

    /** Detalle de un Pokémon por su nombre. */
    @GET("pokemon/{nombre}")
    suspend fun detalle(@Path("nombre") nombre: String): PokemonDetalleDto
}
