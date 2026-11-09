package com.tecsup.catalogo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.tecsup.catalogo.ui.navigation.AppNav
import com.tecsup.catalogo.ui.theme.CatalogoTheme

class MainActivity : ComponentActivity() {

    private val container by lazy { AppContainer(applicationContext) }

    private val pedirNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Si la persona niega el permiso, la app sigue funcionando. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CatalogoTheme {
                AppNav(container)
            }
        }

        pedirPermisoDeNotificaciones()
    }

    private fun pedirPermisoDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
