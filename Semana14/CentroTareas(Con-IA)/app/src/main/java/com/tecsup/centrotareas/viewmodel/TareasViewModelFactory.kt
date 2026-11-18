package com.tecsup.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.centrotareas.data.TareasRepository
import com.tecsup.centrotareas.data.TareasRepositoryLocal

class TareasViewModelFactory(
    private val repo: TareasRepository = TareasRepositoryLocal()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            return TareasViewModel(repo) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
