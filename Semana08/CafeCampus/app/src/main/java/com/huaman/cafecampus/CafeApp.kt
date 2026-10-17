package com.huaman.cafecampus

import android.app.Application
import android.content.Context
import com.huaman.cafecampus.data.local.CafeDatabase
import com.huaman.cafecampus.data.local.DatosIniciales
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.data.repository.CafeRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application de CafeCampus. Registrada en el AndroidManifest.
 *
 * Hace dos cosas al arrancar:
 *  1. crea la base Room (singleton) y la registra en DataStore,
 *  2. siembra los datos iniciales la primera vez.
 */
class CafeApp : Application() {

    /** Ámbito para el trabajo de arranque (evita que la app muera). */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Repositorio unico de la app: lo consumen todos los ViewModels. */
    val repositorio: CafeRepository by lazy {
        CafeRepositoryRoom(CafeDatabase.obtener(this))
    }

    override fun onCreate() {
        super.onCreate()
        val db = CafeDatabase.obtener(this)
        appScope.launch(Dispatchers.IO) {
            DatosIniciales.sembrar(db)
        }
    }
}

/**
 * Atajo para llegar al repositorio desde cualquier Composable sin tener que
 * castear el contexto a mano.
 */
val Context.cafeApp: CafeApp
    get() = applicationContext as CafeApp

/** Repositorio de la app leido desde el contexto de la pantalla. */
val Context.cafeRepository: CafeRepository
    get() = cafeApp.repositorio