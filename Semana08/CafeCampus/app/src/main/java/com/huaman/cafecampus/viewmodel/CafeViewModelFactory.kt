package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huaman.cafecampus.data.prefs.PreferenciasStore
import com.huaman.cafecampus.data.repository.CafeRepository

/**
 * Fabrica unica de ViewModels.
 *
 * Los ViewModels reciben el REPOSITORY (interface) y las PREFERENCIAS, nunca
 * el DAO. Esta fabrica es la que hace esa conexion, y es el unico lugar que
 * conoce las dos cosas.
 */
class CafeViewModelFactory(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MenuViewModel::class.java) ->
            MenuViewModel(repo) as T

        modelClass.isAssignableFrom(SesionViewModel::class.java) ->
            SesionViewModel(repo, prefs) as T

        // TODO (Paso 9/10/11): registrar PedidosClienteViewModel,
        // PanelAdminViewModel, PedidosAdminViewModel y MenuAdminViewModel.

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}