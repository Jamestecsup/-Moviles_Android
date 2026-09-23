package com.tecsup.tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.tecsup.tecsupfit.navigation.AppRoot
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupFitApp()
        }
    }
}

/**
 * Envuelve toda la app con el theme. AppRoot (en navigation/NavGraph.kt)
 * contiene el estado compartido y el NavHost.
 */
@Composable
fun TecsupFitApp() {
    TecsupFitTheme {
        AppRoot()
    }
}