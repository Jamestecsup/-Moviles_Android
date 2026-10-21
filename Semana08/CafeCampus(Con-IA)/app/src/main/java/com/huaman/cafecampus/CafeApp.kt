package com.huaman.cafecampus

import android.app.Application
import android.content.Context
import com.huaman.cafecampus.data.local.CafeDatabase
import com.huaman.cafecampus.data.local.DatosIniciales
import com.huaman.cafecampus.data.prefs.PreferenciasStore
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.data.repository.CafeRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application de CafeCampus. Registrada en el AndroidManifest.
 *
 * Hace tres cosas al arrancar:
 *  1. crea la base Room (singleton),
 *  2. siembra los datos iniciales la primera vez,
 *  3. expone el Repository y las Preferencias para toda la app.
 */
class CafeApp : Application() {

    /** Ámbito para el trabajo de arranque (evita que la app muera). */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Repositorio unico de la app: lo consumen todos los ViewModels. */
    val repositorio: CafeRepository by lazy {
        CafeRepositoryRoom(CafeDatabase.obtener(this))
    }

    /** Preferencias (DataStore): perfil activo, modo oscuro, etc. */
    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    override fun onCreate() {
        super.onCreate()
        val db = CafeDatabase.obtener(this)
        appScope.launch(Dispatchers.IO) {
            DatosIniciales.sembrar(db)
        }
    }
}

/** Atajo para llegar al repositorio desde cualquier pantalla. */
val Context.cafeRepository: CafeRepository
    get() = (applicationContext as CafeApp).repositorio

/** Atajo para llegar a las preferencias desde cualquier pantalla. */
val Context.cafePreferencias: PreferenciasStore
    get() = (applicationContext as CafeApp).preferencias