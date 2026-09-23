package com.huaman.tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.huaman.tecsupfit.navigation.AppRoot
import com.huaman.tecsupfit.ui.theme.TECSUPFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupFitApp()
        }
    }
}

@Composable
fun TecsupFitApp() {
    TECSUPFitTheme {
        AppRoot()
    }
}
