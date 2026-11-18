package com.tecsup.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.campusgps.data.LugaresRepositoryLocal
import com.tecsup.campusgps.data.UbicacionRepositoryGoogle

class CampusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            val ubicacionRepo = UbicacionRepositoryGoogle(context.applicationContext)
            val lugaresRepo = LugaresRepositoryLocal()
            return MapaViewModel(ubicacionRepo, lugaresRepo) as T
        }
        throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
    }
}
