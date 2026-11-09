package com.huaman.cafecampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.navigation.NavCafe
import com.huaman.cafecampus.ui.theme.CafeTheme
import com.huaman.cafecampus.viewmodel.CafeViewModelFactory
import com.huaman.cafecampus.viewmodel.SesionViewModel

/**
 * Unica Activity de la app. Solo arma el grafo:
 *  - obtiene el Repository y las Preferencias desde [CafeApp],
 *  - crea la fabrica de ViewModels,
 *  - pinta `CafeTheme(rol, modoOscuro)` + `NavCafe`.
 *
 * El rol y el modo oscuro salen de [SesionViewModel], que los lee de DataStore:
 * por eso cambian al instante y se recuerdan al reabrir la app.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = CafeViewModelFactory(
            repo = cafeRepository,
            prefs = cafePreferencias
        )

        setContent {
            // Un solo SesionViewModel para toda la app (mismo Activity).
            val sesion: SesionViewModel = viewModel(factory = factory)
            val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
            val rolElegido by sesion.rolSeleccionado.collectAsStateWithLifecycle()
            val modoOscuro by sesion.modoOscuro.collectAsStateWithLifecycle()

            // Antes de iniciar sesion manda el rol ELEGIDO en el selector, para
            // que el login del administrador ya se vea indigo.
            val rol: Rol = usuario?.rolTipado ?: rolElegido

            CafeTheme(rol = rol, darkTheme = modoOscuro) {
                NavCafe(factory = factory, sesion = sesion)
            }
        }
    }
}