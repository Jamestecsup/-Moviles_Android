package com.huaman.cafecampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.navigation.NavCafe
import com.huaman.cafecampus.ui.theme.CafeTheme
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory

/**
 * Unica Activity de la app. Solo arma el grafo:
 *  - obtiene el Repository desde [CafeApp],
 *  - crea la fabrica de ViewModels,
 *  - pinta `CafeTheme(rol)` + `NavCafe`.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repositorio = cafeRepository
        val factory = CafeViewModelFactory(repositorio)

        setContent {
            // TODO (Paso 6): el rol y el modo oscuro salen de SesionViewModel.
            //   Ahora se fijan en cliente + modo del sistema.
            val rol = Rol.CLIENTE
            val modoOscuro = isSystemInDarkTheme()

            CafeTheme(rol = rol, darkTheme = modoOscuro) {
                NavCafe(factory = factory)
            }
        }
    }
}