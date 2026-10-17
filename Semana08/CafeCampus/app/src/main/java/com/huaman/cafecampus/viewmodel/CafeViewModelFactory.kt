package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huaman.cafecampus.data.repository.CafeRepository

/**
 * Fabrica unica de ViewModels.
 *
 * Los ViewModels reciben el REPOSITORY (interface), nunca el DAO. Esta fabrica
 * es la que hace esa conexion, y es el unico lugar que conoce las dos cosas.
 */
class CafeViewModelFactory(
    private val repo: CafeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MenuViewModel::class.java) ->
            MenuViewModel(repo) as T

        // TODO (Paso 6/9/10/11): registrar aqui los demas ViewModels.

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}